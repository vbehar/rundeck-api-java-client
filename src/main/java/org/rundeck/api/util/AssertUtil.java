package org.rundeck.api.util;

import org.apache.commons.lang.StringUtils;

/**
 * Utility class for assertions
 * 
 * @author Vincent Behar
 */
public class AssertUtil {

    /**
     * Test if the given {@link Object} is null
     * 
     * @param object
     * @param errorMessage to be used if the object is null
     * @throws IllegalArgumentException if the given object is null
     */
    public static void notNull(Object object, String errorMessage) throws IllegalArgumentException {
        if (object == null) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    /**
     * Test if the given {@link String} is blank (null, empty or only whitespace)
     * 
     * @param input string
     * @param errorMessage to be used if the string is blank
     * @throws IllegalArgumentException if the given string is blank
     */
    public static void notBlank(String input, String errorMessage) throws IllegalArgumentException {
        if (StringUtils.isBlank(input)) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

}
