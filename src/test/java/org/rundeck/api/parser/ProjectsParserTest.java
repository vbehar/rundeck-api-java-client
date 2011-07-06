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
