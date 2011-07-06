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
import java.util.Date;
import java.util.List;
import org.dom4j.Document;
import org.junit.Assert;
import org.junit.Test;
import org.rundeck.api.domain.RundeckExecution;
import org.rundeck.api.domain.RundeckExecution.ExecutionStatus;
import org.rundeck.api.domain.RundeckJob;
import org.rundeck.api.domain.RundeckNode;
import org.rundeck.api.domain.RundeckProject;

/**
 * Test the {@link ListParser}
 * 
 * @author Vincent Behar
 */
public class ListParserTest {

    @Test
    public void parseExecutions() throws Exception {
        InputStream input = getClass().getResourceAsStream("executions.xml");
        Document document = ParserHelper.loadDocument(input);

        List<RundeckExecution> executions = new ListParser<RundeckExecution>(new ExecutionParser(),
                                                                             "result/executions/execution").parseXmlNode(document);
        Assert.assertEquals(2, executions.size());

        RundeckExecution exec1 = executions.get(0);
        Assert.assertEquals(new Long(1), exec1.getId());
        Assert.assertEquals("http://localhost:4440/execution/follow/1", exec1.getUrl());
        Assert.assertEquals(ExecutionStatus.SUCCEEDED, exec1.getStatus());
        Assert.assertEquals("admin", exec1.getStartedBy());
        Assert.assertEquals(new Date(1308322895104L), exec1.getStartedAt());
        Assert.assertEquals(new Date(1308322959420L), exec1.getEndedAt());
        Assert.assertEquals(null, exec1.getAbortedBy());
        Assert.assertEquals("ls ${option.dir}", exec1.getDescription());

        RundeckExecution exec2 = executions.get(1);
        Assert.assertEquals(new Long(2), exec2.getId());
        Assert.assertEquals("http://localhost:4440/execution/follow/2", exec2.getUrl());
        Assert.assertEquals(ExecutionStatus.SUCCEEDED, exec2.getStatus());
        Assert.assertEquals("admin", exec2.getStartedBy());
        Assert.assertEquals(new Date(1309524165388L), exec2.getStartedAt());
        Assert.assertEquals(new Date(1309524174635L), exec2.getEndedAt());
        Assert.assertEquals(null, exec2.getAbortedBy());
        Assert.assertEquals("ls ${option.dir}", exec2.getDescription());

        RundeckJob job1 = exec1.getJob();
        Assert.assertEquals("1", job1.getId());
        Assert.assertEquals("ls", job1.getName());
        Assert.assertEquals("system", job1.getGroup());
        Assert.assertEquals("test", job1.getProject());
        Assert.assertEquals("list files", job1.getDescription());

        RundeckJob job2 = exec2.getJob();
        Assert.assertEquals("1", job2.getId());
        Assert.assertEquals("ls", job2.getName());
        Assert.assertEquals("system", job2.getGroup());
        Assert.assertEquals("test", job2.getProject());
        Assert.assertEquals("list files", job2.getDescription());

        Assert.assertEquals(job1, job2);
    }

    @Test
    public void parseJobs() throws Exception {
        InputStream input = getClass().getResourceAsStream("jobs.xml");
        Document document = ParserHelper.loadDocument(input);

        List<RundeckJob> jobs = new ListParser<RundeckJob>(new JobParser(), "result/jobs/job").parseXmlNode(document);
        Assert.assertEquals(2, jobs.size());

        RundeckJob job1 = jobs.get(0);
        Assert.assertEquals("1", job1.getId());
        Assert.assertEquals("ls", job1.getName());
        Assert.assertEquals("list files", job1.getDescription());
        Assert.assertEquals("system", job1.getGroup());
        Assert.assertEquals("test", job1.getProject());

        RundeckJob job2 = jobs.get(1);
        Assert.assertEquals("2", job2.getId());
        Assert.assertEquals("ps", job2.getName());
        Assert.assertEquals("list processes", job2.getDescription());
        Assert.assertEquals("system", job2.getGroup());
        Assert.assertEquals("test", job2.getProject());
    }

    @Test
    public void parseNodes() throws Exception {
        InputStream input = getClass().getResourceAsStream("resources.xml");
        Document document = ParserHelper.loadDocument(input);

        List<RundeckNode> nodes = new ListParser<RundeckNode>(new NodeParser(), "project/node").parseXmlNode(document);
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

    @Test
    public void parseProjects() throws Exception {
        InputStream input = getClass().getResourceAsStream("projects.xml");
        Document document = ParserHelper.loadDocument(input);

        List<RundeckProject> projects = new ListParser<RundeckProject>(new ProjectParser(), "result/projects/project").parseXmlNode(document);
        Assert.assertEquals(2, projects.size());

        RundeckProject project1 = projects.get(0);
        Assert.assertEquals("test", project1.getName());
        Assert.assertEquals("test project", project1.getDescription());

        RundeckProject project2 = projects.get(1);
        Assert.assertEquals("other", project2.getName());
        Assert.assertEquals(null, project2.getDescription());
    }

}
