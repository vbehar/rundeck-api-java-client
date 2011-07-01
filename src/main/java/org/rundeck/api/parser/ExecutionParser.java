package org.rundeck.api.parser;

import java.util.Date;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Node;
import org.rundeck.api.domain.RundeckExecution;
import org.rundeck.api.domain.RundeckJob;
import org.rundeck.api.domain.RundeckExecution.ExecutionStatus;

/**
 * Parser for a single {@link RundeckExecution}
 * 
 * @author Vincent Behar
 */
public class ExecutionParser implements NodeParser<RundeckExecution> {

    private String xpath;

    public ExecutionParser() {
        super();
    }

    /**
     * @param xpath of the execution element if it is not the root node
     */
    public ExecutionParser(String xpath) {
        super();
        this.xpath = xpath;
    }

    @Override
    public RundeckExecution parseNode(Node node) {
        Node execNode = xpath != null ? node.selectSingleNode(xpath) : node;

        RundeckExecution execution = new RundeckExecution();

        execution.setId(Long.valueOf(execNode.valueOf("@id")));
        execution.setUrl(StringUtils.trimToNull(execNode.valueOf("@href")));
        execution.setStatus(ExecutionStatus.valueOf(StringUtils.upperCase(execNode.valueOf("@status"))));
        execution.setDescription(StringUtils.trimToNull(execNode.valueOf("description")));
        execution.setStartedBy(StringUtils.trimToNull(execNode.valueOf("user")));
        execution.setStartedAt(new Date(Long.valueOf(execNode.valueOf("date-started/@unixtime"))));
        execution.setAbortedBy(StringUtils.trimToNull(execNode.valueOf("abortedby")));
        String endedAt = StringUtils.trimToNull(execNode.valueOf("date-ended/@unixtime"));
        if (endedAt != null) {
            execution.setEndedAt(new Date(Long.valueOf(endedAt)));
        }

        Node jobNode = execNode.selectSingleNode("job");
        if (jobNode != null) {
            RundeckJob job = new JobParser().parseNode(jobNode);
            execution.setJob(job);
        }

        return execution;
    }

}
