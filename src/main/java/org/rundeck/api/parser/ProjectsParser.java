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

import java.util.ArrayList;
import java.util.List;
import org.dom4j.Node;
import org.rundeck.api.domain.RundeckProject;

/**
 * Parser for a {@link List} of {@link RundeckProject}
 * 
 * @author Vincent Behar
 */
public class ProjectsParser implements NodeParser<List<RundeckProject>> {

    private final String xpath;

    /**
     * @param xpath of the projects elements
     */
    public ProjectsParser(String xpath) {
        super();
        this.xpath = xpath;
    }

    @Override
    public List<RundeckProject> parseNode(Node node) {
        List<RundeckProject> projects = new ArrayList<RundeckProject>();

        @SuppressWarnings("unchecked")
        List<Node> projectNodes = node.selectNodes(xpath);

        for (Node projectNode : projectNodes) {
            RundeckProject project = new ProjectParser().parseNode(projectNode);
            projects.add(project);
        }

        return projects;
    }

}
