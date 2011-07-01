package org.rundeck.api.parser;

import java.io.InputStream;
import java.util.List;
import org.dom4j.Document;
import org.junit.Assert;
import org.junit.Test;
import org.rundeck.api.domain.RundeckProject;

/**
 * Test the {@link ProjectsParser}
 * 
 * @author Vincent Behar
 */
public class ProjectsParserTest {

    @Test
    public void parseNode() throws Exception {
        InputStream input = getClass().getResourceAsStream("projects.xml");
        Document document = ParserHelper.loadDocument(input);

        List<RundeckProject> projects = new ProjectsParser("result/projects/project").parseNode(document);
        Assert.assertEquals(2, projects.size());

        RundeckProject project1 = projects.get(0);
        Assert.assertEquals("test", project1.getName());
        Assert.assertEquals("test project", project1.getDescription());

        RundeckProject project2 = projects.get(1);
        Assert.assertEquals("other", project2.getName());
        Assert.assertEquals(null, project2.getDescription());
    }

}
