package org.rundeck.api.parser;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import org.dom4j.Document;
import org.junit.Assert;
import org.junit.Test;
import org.rundeck.api.domain.RundeckExecution;
import org.rundeck.api.domain.RundeckJob;
import org.rundeck.api.domain.RundeckExecution.ExecutionStatus;

/**
 * Test the {@link ExecutionsParser}
 * 
 * @author Vincent Behar
 */
public class ExecutionsParserTest {

    @Test
    public void parseNode() throws Exception {
        InputStream input = getClass().getResourceAsStream("executions.xml");
        Document document = ParserHelper.loadDocument(input);

        List<RundeckExecution> executions = new ExecutionsParser("result/executions/execution").parseNode(document);
        Assert.assertEquals(2, executions.size());

        RundeckExecution exec1 = executions.get(0);
        Assert.assertEquals(new Long(1), exec1.getId());
        Assert.assertEquals("http://localhost:4440/execution/follow/1", exec1.getUrl());
        Assert.assertEquals(ExecutionStatus.SUCCEEDED, exec1.getStatus());
        Assert.assertEquals("admin", exec1.getStartedBy());
        Assert.assertEquals(new Date(1308322895104L), exec1.getStartedAt());
        Assert.assertEquals(new Date(1308322959420L), exec1.getEndedAt());
        Assert.assertEquals(null, exec1.getAbortedBy());
        Assert.assertEquals("ls ${option.dir}", exec1.getDescription());

        RundeckExecution exec2 = executions.get(1);
        Assert.assertEquals(new Long(2), exec2.getId());
        Assert.assertEquals("http://localhost:4440/execution/follow/2", exec2.getUrl());
        Assert.assertEquals(ExecutionStatus.SUCCEEDED, exec2.getStatus());
        Assert.assertEquals("admin", exec2.getStartedBy());
        Assert.assertEquals(new Date(1309524165388L), exec2.getStartedAt());
        Assert.assertEquals(new Date(1309524174635L), exec2.getEndedAt());
        Assert.assertEquals(null, exec2.getAbortedBy());
        Assert.assertEquals("ls ${option.dir}", exec2.getDescription());

        RundeckJob job1 = exec1.getJob();
        Assert.assertEquals("1", job1.getId());
        Assert.assertEquals("ls", job1.getName());
        Assert.assertEquals("system", job1.getGroup());
        Assert.assertEquals("test", job1.getProject());
        Assert.assertEquals("list files", job1.getDescription());

        RundeckJob job2 = exec2.getJob();
        Assert.assertEquals("1", job2.getId());
        Assert.assertEquals("ls", job2.getName());
        Assert.assertEquals("system", job2.getGroup());
        Assert.assertEquals("test", job2.getProject());
        Assert.assertEquals("list files", job2.getDescription());

        Assert.assertEquals(job1, job2);
    }

}
