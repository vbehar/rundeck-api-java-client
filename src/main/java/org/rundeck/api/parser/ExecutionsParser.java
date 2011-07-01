package org.rundeck.api.parser;

import java.util.ArrayList;
import java.util.List;
import org.dom4j.Node;
import org.rundeck.api.domain.RundeckExecution;

/**
 * Parser for a {@link List} of {@link RundeckExecution}
 * 
 * @author Vincent Behar
 */
public class ExecutionsParser implements NodeParser<List<RundeckExecution>> {

    private final String xpath;

    /**
     * @param xpath of the executions elements
     */
    public ExecutionsParser(String xpath) {
        super();
        this.xpath = xpath;
    }

    @Override
    public List<RundeckExecution> parseNode(Node node) {
        List<RundeckExecution> executions = new ArrayList<RundeckExecution>();

        @SuppressWarnings("unchecked")
        List<Node> execNodes = node.selectNodes(xpath);

        for (Node execNode : execNodes) {
            RundeckExecution execution = new ExecutionParser().parseNode(execNode);
            executions.add(execution);
        }

        return executions;
    }

}
