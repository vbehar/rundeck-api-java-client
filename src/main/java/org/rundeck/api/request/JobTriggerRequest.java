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

import java.util.Properties;
import org.rundeck.api.RundeckClient;
import org.rundeck.api.domain.RundeckExecution;
import org.rundeck.api.parser.ExecutionParser;
import org.rundeck.api.util.AssertUtil;
import org.rundeck.api.util.ParametersUtil;

/**
 * TODO
 * 
 * @author Vincent Behar
 */
public class JobTriggerRequest<T extends JobTriggerRequest<?>> extends ApiRequest<RundeckExecution> {

    private final String jobId;

    private Properties options;

    private Properties nodeFilters;

    public JobTriggerRequest(RundeckClient client, String jobId) {
        super(client);
        this.jobId = jobId;
        AssertUtil.notBlank(jobId, "jobId is mandatory !");
    }

    @SuppressWarnings("unchecked")
    public T addOptions(Properties options) {
        this.options = options;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T filterNodes(Properties nodeFilters) {
        this.nodeFilters = nodeFilters;
        return (T) this;
    }

    @Override
    public RundeckExecution execute() {
        return get(new ApiPathBuilder("/job/", jobId, "/run").param("argString",
                                                                    ParametersUtil.generateArgString(options))
                                                             .nodeFilters(nodeFilters),
                   new ExecutionParser("result/executions/execution"));
    }

}
