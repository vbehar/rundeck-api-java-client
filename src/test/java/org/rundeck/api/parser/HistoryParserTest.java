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
import org.rundeck.api.domain.RundeckHistory;
import org.rundeck.api.domain.RundeckEvent.EventStatus;

/**
 * Test the {@link HistoryParser}
 * 
 * @author Vincent Behar
 */
public class HistoryParserTest {

    @Test
    public void parseHistory() throws Exception {
        InputStream input = getClass().getResourceAsStream("history.xml");
        Document document = ParserHelper.loadDocument(input);

        RundeckHistory history = new HistoryParser("result/events").parseXmlNode(document);

        Assert.assertEquals(2, history.getCount());
        Assert.assertEquals(4, history.getTotal());
        Assert.assertEquals(2, history.getMax());
        Assert.assertEquals(0, history.getOffset());
        Assert.assertEquals(2, history.getEvents().size());

        RundeckEvent event1 = history.getEvents().get(0);
        Assert.assertFalse(event1.isAdhoc());
        Assert.assertEquals("job-name", event1.getTitle());
        Assert.assertEquals(EventStatus.SUCCEEDED, event1.getStatus());
        Assert.assertEquals("ps", event1.getSummary());
        Assert.assertEquals(2, event1.getNodeSummary().getSucceeded());
        Assert.assertEquals(0, event1.getNodeSummary().getFailed());
        Assert.assertEquals(2, event1.getNodeSummary().getTotal());
        Assert.assertEquals("admin", event1.getUser());
        Assert.assertEquals("test", event1.getProject());
        Assert.assertEquals(new Date(1311946495646L), event1.getStartedAt());
        Assert.assertEquals(new Date(1311946557618L), event1.getEndedAt());
        Assert.assertEquals("1", event1.getJobId());
        Assert.assertEquals(new Long(2), event1.getExecutionId());

        RundeckEvent event2 = history.getEvents().get(1);
        Assert.assertTrue(event2.isAdhoc());
        Assert.assertEquals("adhoc", event2.getTitle());
        Assert.assertEquals(EventStatus.FAILED, event2.getStatus());
        Assert.assertEquals("ls $HOME", event2.getSummary());
        Assert.assertEquals(1, event2.getNodeSummary().getSucceeded());
        Assert.assertEquals(1, event2.getNodeSummary().getFailed());
        Assert.assertEquals(2, event2.getNodeSummary().getTotal());
        Assert.assertEquals("admin", event2.getUser());
        Assert.assertEquals("test", event2.getProject());
        Assert.assertEquals(new Date(1311945953547L), event2.getStartedAt());
        Assert.assertEquals(new Date(1311945963467L), event2.getEndedAt());
        Assert.assertEquals(null, event2.getJobId());
        Assert.assertEquals(new Long(1), event2.getExecutionId());
    }

}
