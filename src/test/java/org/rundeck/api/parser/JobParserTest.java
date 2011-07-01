package org.rundeck.api.parser;

import java.io.InputStream;
import org.dom4j.Document;
import org.junit.Assert;
import org.junit.Test;
import org.rundeck.api.domain.RundeckJob;

/**
 * Test the {@link JobParser}
 * 
 * @author Vincent Behar
 */
public class JobParserTest {

    @Test
    public void parseNode() throws Exception {
        InputStream input = getClass().getResourceAsStream("job.xml");
        Document document = ParserHelper.loadDocument(input);

        RundeckJob job = new JobParser("joblist/job").parseNode(document);

        Assert.assertEquals("1", job.getId());
        Assert.assertEquals("job-name", job.getName());
        Assert.assertEquals("job description", job.getDescription());
        Assert.assertEquals("group-name", job.getGroup());
        Assert.assertEquals("project-name", job.getProject());
    }

}
