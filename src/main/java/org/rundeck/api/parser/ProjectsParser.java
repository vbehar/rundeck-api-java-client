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
