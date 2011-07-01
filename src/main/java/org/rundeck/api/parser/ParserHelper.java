package org.rundeck.api.parser;

import java.io.IOException;
import java.io.InputStream;
import org.apache.http.HttpResponse;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.rundeck.api.RundeckApiException;

/**
 * Helper for parsing RunDeck responses
 * 
 * @author Vincent Behar
 */
public class ParserHelper {

    /**
     * Load an XML {@link Document} from the given RunDeck {@link HttpResponse}.
     * 
     * @param httpResponse from an API call to RunDeck
     * @return an XML {@link Document}
     * @throws RundeckApiException if we failed to read the response, or if the response is an error
     * @see #loadDocument(InputStream)
     */
    public static Document loadDocument(HttpResponse httpResponse) throws RundeckApiException {
        InputStream inputStream = null;

        try {
            inputStream = httpResponse.getEntity().getContent();
        } catch (IllegalStateException e) {
            throw new RundeckApiException("Failed to read RunDeck reponse", e);
        } catch (IOException e) {
            throw new RundeckApiException("Failed to read RunDeck reponse", e);
        }

        return loadDocument(inputStream);
    }

    /**
     * Load an XML {@link Document} from the given {@link InputStream}
     * 
     * @param inputStream from an API call to RunDeck
     * @return an XML {@link Document}
     * @throws RundeckApiException if we failed to read the response, or if the response is an error
     * @see #loadDocument(HttpResponse)
     */
    public static Document loadDocument(InputStream inputStream) throws RundeckApiException {
        SAXReader reader = new SAXReader();
        reader.setEncoding("UTF-8");

        Document document;
        try {
            document = reader.read(inputStream);
        } catch (DocumentException e) {
            throw new RundeckApiException("Failed to read RunDeck reponse", e);
        }
        document.setXMLEncoding("UTF-8");

        Node result = document.selectSingleNode("result");
        if (result != null) {
            Boolean failure = Boolean.valueOf(result.valueOf("@error"));
            if (failure) {
                throw new RundeckApiException(result.valueOf("error/message"));
            }
        }

        return document;
    }

}
