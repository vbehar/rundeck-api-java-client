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
import org.dom4j.Document;
import org.junit.Assert;
import org.junit.Test;
import org.rundeck.api.domain.RundeckAbort;
import org.rundeck.api.domain.RundeckExecution;
import org.rundeck.api.domain.RundeckAbort.AbortStatus;
import org.rundeck.api.domain.RundeckExecution.ExecutionStatus;

/**
 * Test the {@link AbortParser}
 * 
 * @author Vincent Behar
 */
public class AbortParserTest {

    @Test
    public void parsePendingAbort() throws Exception {
        InputStream input = getClass().getResourceAsStream("abort-pending.xml");
        Document document = ParserHelper.loadDocument(input);

        RundeckAbort abort = new AbortParser("result/abort").parseXmlNode(document);
        RundeckExecution execution = abort.getExecution();

        Assert.assertEquals(AbortStatus.PENDING, abort.getStatus());

        Assert.assertEquals(new Long(1), execution.getId());
        Assert.assertEquals(ExecutionStatus.RUNNING, execution.getStatus());
    }

    @Test
    public void parseFailedAbort() throws Exception {
        InputStream input = getClass().getResourceAsStream("abort-failed.xml");
        Document document = ParserHelper.loadDocument(input);

        RundeckAbort abort = new AbortParser("result/abort").parseXmlNode(document);
        RundeckExecution execution = abort.getExecution();

        Assert.assertEquals(AbortStatus.FAILED, abort.getStatus());

        Assert.assertEquals(new Long(1), execution.getId());
        Assert.assertEquals(ExecutionStatus.SUCCEEDED, execution.getStatus());
    }

}
