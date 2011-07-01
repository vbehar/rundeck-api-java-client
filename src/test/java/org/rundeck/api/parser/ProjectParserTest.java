package org.rundeck.api.parser;

import java.io.InputStream;
import org.dom4j.Document;
import org.junit.Assert;
import org.junit.Test;
import org.rundeck.api.domain.RundeckProject;

/**
 * Test the {@link ProjectParser}
 * 
 * @author Vincent Behar
 */
public class ProjectParserTest {

    @Test
    public void parseNode() throws Exception {
        InputStream input = getClass().getResourceAsStream("project.xml");
        Document document = ParserHelper.loadDocument(input);

        RundeckProject project = new ProjectParser("result/projects/project").parseNode(document);

        Assert.assertEquals("test", project.getName());
        Assert.assertEquals("test project", project.getDescription());
    }

}
