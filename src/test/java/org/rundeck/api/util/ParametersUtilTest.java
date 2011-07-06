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
