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
package org.rundeck.api;

import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.rundeck.api.domain.RundeckProject;
import betamax.Betamax;
import betamax.Recorder;

/**
 * Test the {@link RundeckClient}. Uses betamax to unit-test HTTP requests without a live RunDeck instance.
 * 
 * @author Vincent Behar
 */
public class RundeckClientTest {

    @Rule
    public Recorder recorder = new Recorder();

    private RundeckClient client;

    @Test
    @Betamax(tape = "get_projects")
    public void getProjects() throws Exception {
        List<RundeckProject> projects = client.getProjects();
        Assert.assertEquals(1, projects.size());
        Assert.assertEquals("test", projects.get(0).getName());
        Assert.assertNull(projects.get(0).getDescription());
    }

    @Before
    public void setUp() throws Exception {
        // not that you can put whatever here, because we don't actually connect to the RunDeck instance
        // but instead use betamax as a proxy to serve the previously recorded tapes (in src/test/resources)
        client = new RundeckClient("http://rundeck.local:4440", "PVnN5K3OPc5vduS3uVuVnEsD57pDC5pd");
    }

}
