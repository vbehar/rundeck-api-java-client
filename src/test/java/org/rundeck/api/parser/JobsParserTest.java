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
