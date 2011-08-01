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

import java.util.Date;
import org.rundeck.api.RundeckClient;
import org.rundeck.api.domain.RundeckHistory;
import org.rundeck.api.parser.HistoryParser;
import org.rundeck.api.util.AssertUtil;

/**
 * TODO
 * 
 * @author Vincent Behar
 */
public class HistoryRequest extends ApiRequest<RundeckHistory> {

    private final String project;

    private String jobId;

    private String reportId;

    private String user;

    private String recent;

    private Date beginAt;

    private Date end;

    private Long max;

    private Long offset;

    public HistoryRequest(RundeckClient client, String project) {
        super(client);
        this.project = project;
        AssertUtil.notBlank(project, "project is mandatory to get the history !");
    }

    public HistoryRequest beginAt(Date beginAt) {
        this.beginAt = beginAt;
        return this;
    }

    public HistoryRequest max(Long max) {
        this.max = max;
        return this;
    }

    public HistoryRequest offset(Long offset) {
        this.offset = offset;
        return this;
    }

    @Override
    public RundeckHistory execute() {
        return get(new ApiPathBuilder("/history").param("project", project)
                                                 .param("jobIdFilter", jobId)
                                                 .param("reportIdFilter", reportId)
                                                 .param("userFilter", user)
                                                 .param("recentFilter", recent)
                                                 .param("begin", beginAt)
                                                 .param("end", end)
                                                 .param("max", max)
                                                 .param("offset", offset), new HistoryParser("result/events"));
    }

}
