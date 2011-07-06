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
import org.dom4j.Document;
import org.junit.Assert;
import org.junit.Test;
import org.rundeck.api.domain.RundeckNode;

/**
 * Test the {@link NodeParser}
 * 
 * @author Vincent Behar
 */
public class NodeParserTest {

    @Test
    public void parseNode() throws Exception {
        InputStream input = getClass().getResourceAsStream("resources.xml");
        Document document = ParserHelper.loadDocument(input);

        RundeckNode node = new NodeParser("project/node").parseXmlNode(document);

        Assert.assertEquals("strongbad", node.getName());
        Assert.assertEquals("Node", node.getType());
        Assert.assertEquals("a development host", node.getDescription());
        Assert.assertEquals(Arrays.asList("dev"), node.getTags());
        Assert.assertEquals("strongbad.local", node.getHostname());
        Assert.assertEquals("i386", node.getOsArch());
        Assert.assertEquals("unix", node.getOsFamily());
        Assert.assertEquals("Linux", node.getOsName());
        Assert.assertEquals("2.6.35-30-generic-pae", node.getOsVersion());
        Assert.assertEquals("rundeck", node.getUsername());
        Assert.assertEquals(null, node.getEditUrl());
        Assert.assertEquals(null, node.getRemoteUrl());
    }

}
