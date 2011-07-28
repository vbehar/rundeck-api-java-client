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
import org.rundeck.api.domain.RundeckJob;
import org.rundeck.api.domain.RundeckJobsImportResult;

/**
 * Test the {@link JobsImportResultParser}
 * 
 * @author Vincent Behar
 */
public class JobsImportResultParserTest {

    @Test
    public void parseResult() throws Exception {
        InputStream input = getClass().getResourceAsStream("jobs-import.xml");
        Document document = ParserHelper.loadDocument(input);

        RundeckJobsImportResult result = new JobsImportResultParser("result").parseXmlNode(document);

        Assert.assertEquals(2, result.getSucceededJobs().size());
        Assert.assertEquals(0, result.getSkippedJobs().size());
        Assert.assertEquals(1, result.getFailedJobs().size());

        Assert.assertEquals("job-one", result.getSucceededJobs().get(0).getName());
        Assert.assertEquals("job-two", result.getSucceededJobs().get(1).getName());

        RundeckJob failedJob = result.getFailedJobs().keySet().iterator().next();
        Assert.assertEquals("job-three", failedJob.getName());
        Assert.assertEquals("Error message", result.getFailedJobs().get(failedJob));
    }

}
