package org.rundeck.api.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.Map.Entry;
import org.apache.commons.lang.StringUtils;

/**
 * Utility class for RunDeck arguments
 * 
 * @author Vincent Behar
 */
public class ArgsUtil {

    /**
     * Generates and url-encode a RunDeck "argString" representing the given options. Format of the argString is
     * <code>"-key1 value1 -key2 'value 2 with spaces'"</code>
     * 
     * @param options to be converted
     * @return an url-encoded string. null if options is null, empty if there are no valid options.
     * @see #generateArgString(Properties)
     */
    public static String generateUrlEncodedArgString(Properties options) {
        String argString = generateArgString(options);
        if (StringUtils.isBlank(argString)) {
            return argString;
        }

        try {
            return URLEncoder.encode(argString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates a RunDeck "argString" representing the given options. Format of the argString is
     * <code>"-key1 value1 -key2 'value 2 with spaces'"</code>
     * 
     * @param options to be converted
     * @return a string. null if options is null, empty if there are no valid options.
     * @see #generateUrlEncodedArgString(Properties)
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

}
