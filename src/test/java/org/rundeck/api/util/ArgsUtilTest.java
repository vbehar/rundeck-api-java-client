package org.rundeck.api.util;

import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test the {@link ArgsUtil}
 * 
 * @author Vincent Behar
 */
public class ArgsUtilTest {

    @Test
    public void generateArgString() throws Exception {
        Assert.assertNull(ArgsUtil.generateArgString(null));
        Assert.assertEquals("", ArgsUtil.generateArgString(new Properties()));

        Properties options = new Properties();
        options.put("key1", "value1");
        options.put("key2", "value 2 with spaces");
        String argString = ArgsUtil.generateArgString(options);
        if (argString.startsWith("-key1")) {
            Assert.assertEquals("-key1 value1 -key2 'value 2 with spaces'", argString);
        } else {
            Assert.assertEquals("-key2 'value 2 with spaces' -key1 value1", argString);
        }
    }

}
