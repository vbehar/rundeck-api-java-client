package org.rundeck.api.parser;

import java.util.ArrayList;
import java.util.List;
import org.dom4j.Node;
import org.rundeck.api.domain.RundeckJob;

/**
 * Parser for a {@link List} of {@link RundeckJob}
 * 
 * @author Vincent Behar
 */
public class JobsParser implements NodeParser<List<RundeckJob>> {

    private final String xpath;

    /**
     * @param xpath of the jobs elements
     */
    public JobsParser(String xpath) {
        super();
        this.xpath = xpath;
    }

    @Override
    public List<RundeckJob> parseNode(Node node) {
        List<RundeckJob> jobs = new ArrayList<RundeckJob>();

        @SuppressWarnings("unchecked")
        List<Node> jobNodes = node.selectNodes(xpath);

        for (Node jobNode : jobNodes) {
            RundeckJob job = new JobParser().parseNode(jobNode);
            jobs.add(job);
        }

        return jobs;
    }

}
