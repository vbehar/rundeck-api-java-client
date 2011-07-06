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

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import org.dom4j.Document;
import org.junit.Assert;
import org.junit.Test;
import org.rundeck.api.domain.RundeckNode;

/**
 * Test the {@link NodesParser}
 * 
 * @author Vincent Behar
 */
public class NodesParserTest {

    @Test
    public void parseNodes() throws Exception {
        InputStream input = getClass().getResourceAsStream("resources.xml");
        Document document = ParserHelper.loadDocument(input);

        List<RundeckNode> nodes = new NodesParser("project/node").parseXmlNode(document);
        Assert.assertEquals(1, nodes.size());

        RundeckNode node1 = nodes.get(0);
        Assert.assertEquals("strongbad", node1.getName());
        Assert.assertEquals("Node", node1.getType());
        Assert.assertEquals("a development host", node1.getDescription());
        Assert.assertEquals(Arrays.asList("dev"), node1.getTags());
        Assert.assertEquals("strongbad.local", node1.getHostname());
        Assert.assertEquals("i386", node1.getOsArch());
        Assert.assertEquals("unix", node1.getOsFamily());
        Assert.assertEquals("Linux", node1.getOsName());
        Assert.assertEquals("2.6.35-30-generic-pae", node1.getOsVersion());
        Assert.assertEquals("rundeck", node1.getUsername());
        Assert.assertEquals(null, node1.getEditUrl());
        Assert.assertEquals(null, node1.getRemoteUrl());
    }

}
