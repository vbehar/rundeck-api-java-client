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
import org.rundeck.api.domain.RundeckNode;

/**
 * Parser for a {@link List} of {@link RundeckNode}
 * 
 * @author Vincent Behar
 */
public class NodesParser implements XmlNodeParser<List<RundeckNode>> {

    private final String xpath;

    /**
     * @param xpath of the rundeck-nodes elements
     */
    public NodesParser(String xpath) {
        super();
        this.xpath = xpath;
    }

    @Override
    public List<RundeckNode> parseXmlNode(Node node) {
        List<RundeckNode> rundeckNodes = new ArrayList<RundeckNode>();

        @SuppressWarnings("unchecked")
        List<Node> rundeckNodeNodes = node.selectNodes(xpath);

        for (Node rundeckNodeNode : rundeckNodeNodes) {
            RundeckNode rundeckNode = new NodeParser().parseXmlNode(rundeckNodeNode);
            rundeckNodes.add(rundeckNode);
        }

        return rundeckNodes;
    }

}
