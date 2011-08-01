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
import org.rundeck.api.domain.RundeckProject;
import org.rundeck.api.parser.ListParser;
import org.rundeck.api.parser.ProjectParser;

/**
 * TODO
 * 
 * @author Vincent Behar
 */
public class ProjectsListingRequest extends ApiRequest<List<RundeckProject>> {

    public ProjectsListingRequest(RundeckClient client) {
        super(client);
    }

    @Override
    public List<RundeckProject> execute() {
        return get(new ApiPathBuilder("/projects"), new ListParser<RundeckProject>(new ProjectParser(),
                                                                                   "result/projects/project"));
    }

}
