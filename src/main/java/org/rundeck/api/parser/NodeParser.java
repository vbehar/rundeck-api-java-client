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

import java.util.Arrays;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Node;
import org.rundeck.api.domain.RundeckNode;

/**
 * Parser for a single {@link RundeckNode}
 * 
 * @author Vincent Behar
 */
public class NodeParser implements XmlNodeParser<RundeckNode> {

    private String xpath;

    public NodeParser() {
        super();
    }

    /**
     * @param xpath of the rundeck-node element if it is not the root xml-node
     */
    public NodeParser(String xpath) {
        super();
        this.xpath = xpath;
    }

    @Override
    public RundeckNode parseXmlNode(Node node) {
        Node rundeckNodeNode = xpath != null ? node.selectSingleNode(xpath) : node;

        RundeckNode rundeckNode = new RundeckNode();

        rundeckNode.setName(StringUtils.trimToNull(rundeckNodeNode.valueOf("@name")));
        rundeckNode.setType(StringUtils.trimToNull(rundeckNodeNode.valueOf("@type")));
        rundeckNode.setDescription(StringUtils.trimToNull(rundeckNodeNode.valueOf("@description")));
        rundeckNode.setHostname(StringUtils.trimToNull(rundeckNodeNode.valueOf("@hostname")));
        rundeckNode.setOsArch(StringUtils.trimToNull(rundeckNodeNode.valueOf("@osArch")));
        rundeckNode.setOsFamily(StringUtils.trimToNull(rundeckNodeNode.valueOf("@osFamily")));
        rundeckNode.setOsName(StringUtils.trimToNull(rundeckNodeNode.valueOf("@osName")));
        rundeckNode.setOsVersion(StringUtils.trimToNull(rundeckNodeNode.valueOf("@osVersion")));
        rundeckNode.setUsername(StringUtils.trimToNull(rundeckNodeNode.valueOf("@username")));
        rundeckNode.setEditUrl(StringUtils.trimToNull(rundeckNodeNode.valueOf("@editUrl")));
        rundeckNode.setRemoteUrl(StringUtils.trimToNull(rundeckNodeNode.valueOf("@remoteUrl")));

        String tags = StringUtils.trimToEmpty(rundeckNodeNode.valueOf("@tags"));
        rundeckNode.setTags(Arrays.asList(StringUtils.split(tags, ",")));

        return rundeckNode;
    }

}
