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
package org.rundeck.api.request;

import java.util.List;
import org.rundeck.api.RundeckClient;
import org.rundeck.api.domain.RundeckJob;
import org.rundeck.api.parser.JobParser;
import org.rundeck.api.parser.ListParser;
import org.rundeck.api.util.AssertUtil;

/**
 * TODO
 * 
 * @author Vincent Behar
 */
public class JobsListingRequest extends ApiRequest<List<RundeckJob>> {

    private final String project;

    private String jobFilter;

    public JobsListingRequest(RundeckClient client, String project) {
        super(client);
        this.project = project;
        AssertUtil.notBlank(project, "project is mandatory to get all jobs !");
    }

    public JobsListingRequest jobFilter(String jobFilter) {
        this.jobFilter = jobFilter;
        return this;
    }

    @Override
    public List<RundeckJob> execute() {
        return get(new ApiPathBuilder("/jobs").param("project", project).param("jobFilter", jobFilter),
                   new ListParser<RundeckJob>(new JobParser(), "result/jobs/job"));
    }

}
