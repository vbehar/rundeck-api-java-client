package org.rundeck.api.parser;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Node;
import org.rundeck.api.domain.RundeckProject;

/**
 * Parser for a single {@link RundeckProject}
 * 
 * @author Vincent Behar
 */
public class ProjectParser implements NodeParser<RundeckProject> {

    private String xpath;

    public ProjectParser() {
        super();
    }

    /**
     * @param xpath of the project element if it is not the root node
     */
    public ProjectParser(String xpath) {
        super();
        this.xpath = xpath;
    }

    @Override
    public RundeckProject parseNode(Node node) {
        Node projectNode = xpath != null ? node.selectSingleNode(xpath) : node;

        RundeckProject project = new RundeckProject();

        project.setName(StringUtils.trimToNull(projectNode.valueOf("name")));
        project.setDescription(StringUtils.trimToNull(projectNode.valueOf("description")));

        return project;
    }

}
