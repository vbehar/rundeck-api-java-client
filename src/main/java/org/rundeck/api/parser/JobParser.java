package org.rundeck.api.parser;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Node;
import org.rundeck.api.domain.RundeckJob;

/**
 * Parser for a single {@link RundeckJob}
 * 
 * @author Vincent Behar
 */
public class JobParser implements NodeParser<RundeckJob> {

    private String xpath;

    public JobParser() {
        super();
    }

    /**
     * @param xpath of the job element if it is not the root node
     */
    public JobParser(String xpath) {
        super();
        this.xpath = xpath;
    }

    @Override
    public RundeckJob parseNode(Node node) {
        Node jobNode = xpath != null ? node.selectSingleNode(xpath) : node;

        RundeckJob job = new RundeckJob();

        job.setName(StringUtils.trimToNull(jobNode.valueOf("name")));
        job.setDescription(StringUtils.trimToNull(jobNode.valueOf("description")));
        job.setGroup(StringUtils.trimToNull(jobNode.valueOf("group")));

        // ID is either an attribute or an child element...
        String jobId = null;
        jobId = jobNode.valueOf("id");
        if (StringUtils.isBlank(jobId)) {
            jobId = jobNode.valueOf("@id");
        }
        job.setId(jobId);

        // project is either a nested element of context, or just a child element
        Node contextNode = jobNode.selectSingleNode("context");
        if (contextNode != null) {
            job.setProject(StringUtils.trimToNull(contextNode.valueOf("project")));
        } else {
            job.setProject(StringUtils.trimToNull(jobNode.valueOf("project")));
        }

        return job;
    }

}
