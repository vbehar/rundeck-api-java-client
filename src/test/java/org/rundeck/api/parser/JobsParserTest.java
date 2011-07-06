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
import java.util.List;
import org.dom4j.Document;
import org.junit.Assert;
import org.junit.Test;
import org.rundeck.api.domain.RundeckJob;

/**
 * Test the {@link JobsParser}
 * 
 * @author Vincent Behar
 */
public class JobsParserTest {

    @Test
    public void parseNode() throws Exception {
        InputStream input = getClass().getResourceAsStream("jobs.xml");
        Document document = ParserHelper.loadDocument(input);

        List<RundeckJob> jobs = new JobsParser("result/jobs/job").parseNode(document);
        Assert.assertEquals(2, jobs.size());

        RundeckJob job1 = jobs.get(0);
        Assert.assertEquals("1", job1.getId());
        Assert.assertEquals("ls", job1.getName());
        Assert.assertEquals("list files", job1.getDescription());
        Assert.assertEquals("system", job1.getGroup());
        Assert.assertEquals("test", job1.getProject());

        RundeckJob job2 = jobs.get(1);
        Assert.assertEquals("2", job2.getId());
        Assert.assertEquals("ps", job2.getName());
        Assert.assertEquals("list processes", job2.getDescription());
        Assert.assertEquals("system", job2.getGroup());
        Assert.assertEquals("test", job2.getProject());
    }

}
