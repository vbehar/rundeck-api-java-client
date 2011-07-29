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
import org.rundeck.api.domain.RundeckEvent;
import org.rundeck.api.domain.RundeckEvent.EventStatus;

/**
 * Test the {@link EventParser}
 * 
 * @author Vincent Behar
 */
public class EventParserTest {

    @Test
    public void parseSucceededEvent() throws Exception {
        InputStream input = getClass().getResourceAsStream("event-succeeded.xml");
        Document document = ParserHelper.loadDocument(input);

        RundeckEvent event = new EventParser("event").parseXmlNode(document);

        Assert.assertFalse(event.isAdhoc());
        Assert.assertEquals("job-name", event.getTitle());
        Assert.assertEquals(EventStatus.SUCCEEDED, event.getStatus());
        Assert.assertEquals("ps", event.getSummary());
        Assert.assertEquals(2, event.getNodeSummary().getSucceeded());
        Assert.assertEquals(0, event.getNodeSummary().getFailed());
        Assert.assertEquals(2, event.getNodeSummary().getTotal());
        Assert.assertEquals("admin", event.getUser());
        Assert.assertEquals("test", event.getProject());
        Assert.assertEquals(new Date(1311946495646L), event.getStartedAt());
        Assert.assertEquals(new Date(1311946557618L), event.getEndedAt());
        Assert.assertEquals("1", event.getJobId());
        Assert.assertEquals(new Long(2), event.getExecutionId());
    }

    @Test
    public void parseAdhocEvent() throws Exception {
        InputStream input = getClass().getResourceAsStream("event-adhoc.xml");
        Document document = ParserHelper.loadDocument(input);

        RundeckEvent event = new EventParser("event").parseXmlNode(document);

        Assert.assertTrue(event.isAdhoc());
        Assert.assertEquals("adhoc", event.getTitle());
        Assert.assertEquals(EventStatus.FAILED, event.getStatus());
        Assert.assertEquals("ls $HOME", event.getSummary());
        Assert.assertEquals(1, event.getNodeSummary().getSucceeded());
        Assert.assertEquals(1, event.getNodeSummary().getFailed());
        Assert.assertEquals(2, event.getNodeSummary().getTotal());
        Assert.assertEquals("admin", event.getUser());
        Assert.assertEquals("test", event.getProject());
        Assert.assertEquals(new Date(1311945953547L), event.getStartedAt());
        Assert.assertEquals(new Date(1311945963467L), event.getEndedAt());
        Assert.assertEquals(null, event.getJobId());
        Assert.assertEquals(new Long(1), event.getExecutionId());
    }

}
