package org.rundeck.api.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Map.Entry;
import org.apache.commons.lang.StringUtils;

/**
 * Utility class for API parameters that should be passed in URLs.
 * 
 * @author Vincent Behar
 */
public class ParametersUtil {

    /**
     * URL-encode the given string
     * 
     * @param input string to be encoded
     * @return an url-encoded string
     */
    public static String urlEncode(String input) {
        if (StringUtils.isBlank(input)) {
            return input;
        }
        try {
            return URLEncoder.encode(input, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates a RunDeck "argString" representing the given options. Format of the argString is
     * <code>"-key1 value1 -key2 'value 2 with spaces'"</code>. You might want to url-encode this string...
     * 
     * @param options to be converted
     * @return a string. null if options is null, empty if there are no valid options.
     */
    public static String generateArgString(Properties options) {
        if (options == null) {
            return null;
        }

        StringBuilder argString = new StringBuilder();
        for (Entry<Object, Object> option : options.entrySet()) {
            String key = String.valueOf(option.getKey());
            String value = String.valueOf(option.getValue());

            if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
                if (argString.length() > 0) {
                    argString.append(" ");
                }
                argString.append("-").append(key);
                argString.append(" ");
                if (value.indexOf(" ") >= 0
                    && !(0 == value.indexOf("'") && (value.length() - 1) == value.lastIndexOf("'"))) {
                    argString.append("'").append(value).append("'");
                } else {
                    argString.append(value);
                }
            }
        }
        return argString.toString();
    }

    /**
     * Generates an url-encoded string representing the given nodeFilters. Format of the string is
     * <code>"filter1=value1&filter2=value2"</code>.
     * 
     * @param nodeFilters to be converted
     * @return an url-encoded string. null if nodeFilters is null, empty if there are no valid filters.
     */
    public static String generateNodeFiltersString(Properties nodeFilters) {
        if (nodeFilters == null) {
            return null;
        }

        List<String> filters = new ArrayList<String>();
        for (Entry<Object, Object> filter : nodeFilters.entrySet()) {
            String key = String.valueOf(filter.getKey());
            String value = String.valueOf(filter.getValue());

            if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
                try {
                    filters.add(URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return StringUtils.join(filters, "&");
    }

}
