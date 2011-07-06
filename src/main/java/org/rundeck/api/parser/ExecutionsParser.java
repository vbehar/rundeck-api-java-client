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
