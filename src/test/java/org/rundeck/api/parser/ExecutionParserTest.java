package org.rundeck.api.parser;

import java.io.InputStream;
import java.util.Date;
import org.dom4j.Document;
import org.junit.Assert;
import org.junit.Test;
import org.rundeck.api.domain.RundeckExecution;
import org.rundeck.api.domain.RundeckJob;
import org.rundeck.api.domain.RundeckExecution.ExecutionStatus;

/**
 * Test the {@link ExecutionParser}
 * 
 * @author Vincent Behar
 */
public class ExecutionParserTest {

    @Test
    public void parseRunningNode() throws Exception {
        InputStream input = getClass().getResourceAsStream("execution-running.xml");
        Document document = ParserHelper.loadDocument(input);

        RundeckExecution execution = new ExecutionParser("result/executions/execution").parseNode(document);
        RundeckJob job = execution.getJob();

        Assert.assertEquals(new Long(1), execution.getId());
        Assert.assertEquals("http://localhost:4440/execution/follow/1", execution.getUrl());
        Assert.assertEquals(ExecutionStatus.RUNNING, execution.getStatus());
        Assert.assertEquals("admin", execution.getStartedBy());
        Assert.assertEquals(new Date(1302183830082L), execution.getStartedAt());
        Assert.assertEquals(null, execution.getEndedAt());
        Assert.assertEquals(null, execution.getAbortedBy());
        Assert.assertEquals("ls ${option.dir}", execution.getDescription());

        Assert.assertEquals("1", job.getId());
        Assert.assertEquals("ls", job.getName());
        Assert.assertEquals("system", job.getGroup());
        Assert.assertEquals("test", job.getProject());
        Assert.assertEquals("list files", job.getDescription());
    }

    @Test
    public void parseSucceededNode() throws Exception {
        InputStream input = getClass().getResourceAsStream("execution-succeeded.xml");
        Document document = ParserHelper.loadDocument(input);

        RundeckExecution execution = new ExecutionParser("result/executions/execution").parseNode(document);
        RundeckJob job = execution.getJob();

        Assert.assertEquals(new Long(1), execution.getId());
        Assert.assertEquals("http://localhost:4440/execution/follow/1", execution.getUrl());
        Assert.assertEquals(ExecutionStatus.SUCCEEDED, execution.getStatus());
        Assert.assertEquals("admin", execution.getStartedBy());
        Assert.assertEquals(new Date(1308322895104L), execution.getStartedAt());
        Assert.assertEquals(new Date(1308322959420L), execution.getEndedAt());
        Assert.assertEquals(null, execution.getAbortedBy());
        Assert.assertEquals("ls ${option.dir}", execution.getDescription());

        Assert.assertEquals("1", job.getId());
        Assert.assertEquals("ls", job.getName());
        Assert.assertEquals("system", job.getGroup());
        Assert.assertEquals("test", job.getProject());
        Assert.assertEquals("list files", job.getDescription());
    }

}
