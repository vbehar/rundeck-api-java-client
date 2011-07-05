package org.rundeck.api.util;

import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test the {@link ParametersUtil}
 * 
 * @author Vincent Behar
 */
public class ParametersUtilTest {

    @Test
    public void generateArgString() throws Exception {
        Assert.assertNull(ParametersUtil.generateArgString(null));
        Assert.assertEquals("", ParametersUtil.generateArgString(new Properties()));

        Properties options = new Properties();
        options.put("key1", "value1");
        options.put("key2", "value 2 with spaces");
        String argString = ParametersUtil.generateArgString(options);
        if (argString.startsWith("-key1")) {
            Assert.assertEquals("-key1 value1 -key2 'value 2 with spaces'", argString);
        } else {
            Assert.assertEquals("-key2 'value 2 with spaces' -key1 value1", argString);
        }
    }

    @Test
    public void generateNodeFiltersString() throws Exception {
        Assert.assertNull(ParametersUtil.generateNodeFiltersString(null));
        Assert.assertEquals("", ParametersUtil.generateNodeFiltersString(new Properties()));

        Properties filters = new Properties();
        filters.put("tags", "appserv+front");
        filters.put("exclude-tags", "qa,dev");
        filters.put("os-family", "unix");
        String result = ParametersUtil.generateNodeFiltersString(filters);
        Assert.assertEquals("os-family=unix&exclude-tags=qa%2Cdev&tags=appserv%2Bfront", result);
    }

}
