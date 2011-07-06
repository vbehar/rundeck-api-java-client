/*
 * Copyright 2011 Vincent Behar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.rundeck.api;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.rundeck.api.RundeckApiException.RundeckApiLoginException;
import org.rundeck.api.parser.NodeParser;
import org.rundeck.api.parser.ParserHelper;
import org.rundeck.api.util.AssertUtil;

/**
 * Class responsible for making the HTTP API calls
 * 
 * @author Vincent Behar
 */
class ApiCall {

    private final RundeckClient client;

    /**
     * Build a new instance, linked to the given RunDeck client
     * 
     * @param client holding the RunDeck url and the credentials
     * @throws IllegalArgumentException if client is null
     */
    public ApiCall(RundeckClient client) throws IllegalArgumentException {
        super();
        this.client = client;
        AssertUtil.notNull(client, "The RunDeck Client must not be null !");
    }

    /**
     * Try to "ping" the RunDeck instance to see if it is alive
     * 
     * @throws RundeckApiException if the ping fails
     */
    public void ping() throws RundeckApiException {
        HttpClient httpClient = instantiateHttpClient();
        try {
            HttpResponse response = httpClient.execute(new HttpGet(client.getUrl()));
            if (response.getStatusLine().getStatusCode() / 100 != 2) {
                throw new RundeckApiException("Invalid HTTP response '" + response.getStatusLine() + "' when pinging "
                                              + client.getUrl());
            }
        } catch (IOException e) {
            throw new RundeckApiException("Failed to ping RunDeck instance at " + client.getUrl(), e);
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }

    /**
     * Test the credentials (login/password) on the RunDeck instance
     * 
     * @throws RundeckApiLoginException if the login fails
     */
    public void testCredentials() throws RundeckApiLoginException {
        HttpClient httpClient = instantiateHttpClient();
        try {
            login(httpClient);
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }

    /**
     * Execute an HTTP GET request to the RunDeck instance, on the given path. We will login first, and then execute the
     * API call. At the end, the given parser will be used to convert the response to a more useful result object.
     * 
     * @param apiPath on which we will make the HTTP request
     * @param parser used to parse the response
     * @return the result of the call, as formatted by the parser
     * @throws RundeckApiException in case of error when calling the API
     * @throws RundeckApiLoginException if the login fails
     */
    public <T> T get(String apiPath, NodeParser<T> parser) throws RundeckApiException, RundeckApiLoginException {
        String apiUrl = client.getUrl() + RundeckClient.API_ENDPOINT + apiPath;

        HttpClient httpClient = instantiateHttpClient();
        try {
            login(httpClient);

            // execute the HTTP request
            HttpResponse response = null;
            try {
                response = httpClient.execute(new HttpGet(apiUrl));
            } catch (IOException e) {
                throw new RundeckApiException("Failed to execute an HTTP GET on url : " + apiUrl, e);
            }
            if (response.getStatusLine().getStatusCode() / 100 != 2) {
                throw new RundeckApiException("Invalid HTTP response '" + response.getStatusLine() + "' for " + apiUrl);
            }
            if (response.getEntity() == null) {
                throw new RundeckApiException("Empty RunDeck response ! HTTP status line is : "
                                              + response.getStatusLine());
            }

            // read and parse the response
            Document xmlDocument = ParserHelper.loadDocument(response);
            T result = parser.parseNode(xmlDocument);

            // release the connection
            try {
                EntityUtils.consume(response.getEntity());
            } catch (IOException e) {
                throw new RundeckApiException("Failed to consume entity (release connection)", e);
            }
            return result;
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }

    /**
     * Do the actual work of login, using the given {@link HttpClient} instance. You'll need to re-use this instance
     * when making API calls (such as running a job).
     * 
     * @param httpClient pre-instantiated
     * @throws RundeckApiLoginException if the login failed
     */
    private void login(HttpClient httpClient) throws RundeckApiLoginException {
        String location = client.getUrl() + "/j_security_check";

        while (true) {
            HttpPost postLogin = new HttpPost(location);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("j_username", client.getLogin()));
            params.add(new BasicNameValuePair("j_password", client.getPassword()));
            params.add(new BasicNameValuePair("action", "login"));

            HttpResponse response = null;
            try {
                postLogin.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                response = httpClient.execute(postLogin);
            } catch (IOException e) {
                throw new RundeckApiLoginException("Failed to post login form on " + location, e);
            }

            if (response.getStatusLine().getStatusCode() / 100 == 3) {
                // HTTP client refuses to handle redirects (code 3xx) for POST, so we have to do it manually...
                location = response.getFirstHeader("Location").getValue();
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    throw new RundeckApiLoginException("Failed to consume entity (release connection)", e);
                }
                continue;
            }
            if (response.getStatusLine().getStatusCode() / 100 != 2) {
                throw new RundeckApiLoginException("Invalid HTTP response '" + response.getStatusLine() + "' for "
                                                   + location);
            }
            try {
                String content = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
                if (StringUtils.contains(content, "j_security_check")) {
                    throw new RundeckApiLoginException("Login failed for user " + client.getLogin());
                }
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    throw new RundeckApiLoginException("Failed to consume entity (release connection)", e);
                }
            } catch (IOException io) {
                throw new RundeckApiLoginException("Failed to read RunDeck result", io);
            } catch (ParseException p) {
                throw new RundeckApiLoginException("Failed to parse RunDeck response", p);
            }
            break;
        }
    }

    /**
     * Instantiate a new {@link HttpClient} instance, configured to accept all SSL certificates
     * 
     * @return an {@link HttpClient} instance - won't be null
     */
    private HttpClient instantiateHttpClient() {
        SSLSocketFactory socketFactory = null;
        try {
            socketFactory = new SSLSocketFactory(new TrustStrategy() {

                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        } catch (UnrecoverableKeyException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }

        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory));
        return httpClient;
    }

}
