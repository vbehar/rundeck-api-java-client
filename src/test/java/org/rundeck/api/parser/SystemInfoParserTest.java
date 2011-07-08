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
package org.rundeck.api.parser;

import java.io.InputStream;
import java.util.Date;
import org.dom4j.Document;
import org.junit.Assert;
import org.junit.Test;
import org.rundeck.api.domain.RundeckSystemInfo;

/**
 * Test the {@link SystemInfoParser}
 * 
 * @author Vincent Behar
 */
public class SystemInfoParserTest {

    @Test
    public void parseProject() throws Exception {
        InputStream input = getClass().getResourceAsStream("system-info.xml");
        Document document = ParserHelper.loadDocument(input);

        RundeckSystemInfo info = new SystemInfoParser("result/system").parseXmlNode(document);

        Assert.assertEquals(new Date(1310051857605L), info.getDate());
        Assert.assertEquals("1.2.1", info.getVersion());
        Assert.assertEquals("1.2.1-1", info.getBuild());
        Assert.assertEquals("strongbad", info.getNode());
        Assert.assertEquals("/opt/rundeck/rundeck-1.2.1", info.getBaseDir());
        Assert.assertEquals("i386", info.getOsArch());
        Assert.assertEquals("Linux", info.getOsName());
        Assert.assertEquals("2.6.35-30-generic-pae", info.getOsVersion());
        Assert.assertEquals("Java HotSpot(TM) Server VM", info.getJvmName());
        Assert.assertEquals("Sun Microsystems Inc.", info.getJvmVendor());
        Assert.assertEquals("19.1-b02", info.getJvmVersion());
        Assert.assertEquals(new Date(1310032513574L), info.getStartDate());
        Assert.assertEquals(new Long(19344031), info.getUptimeInMillis());
        Assert.assertEquals("5 hours 22 minutes 24 seconds", info.getUptime());
        Assert.assertEquals("0.1 %", info.getCpuLoadAverage());
        Assert.assertEquals(new Long(954466304), info.getMaxMemoryInBytes());
        Assert.assertEquals(new Long(159576592), info.getFreeMemoryInBytes());
        Assert.assertEquals(new Long(271384576), info.getTotalMemoryInBytes());
        Assert.assertEquals(new Integer(0), info.getRunningJobs());
        Assert.assertEquals(new Integer(25), info.getActiveThreads());
    }

}
