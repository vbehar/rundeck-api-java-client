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

import java.util.concurrent.TimeUnit;
import org.rundeck.api.RundeckClient;
import org.rundeck.api.domain.RundeckExecution;
import org.rundeck.api.domain.RundeckExecution.ExecutionStatus;

/**
 * TODO
 * 
 * @author Vincent Behar
 */
public class JobRunRequest extends JobTriggerRequest<JobRunRequest> {

    /** Default value for the "pooling interval" used when running jobs/commands/scripts */
    public static final transient long DEFAULT_POOLING_INTERVAL = 5;

    /** Default unit of the "pooling interval" used when running jobs/commands/scripts */
    public static final transient TimeUnit DEFAULT_POOLING_UNIT = TimeUnit.SECONDS;

    private Long poolingInterval;

    private TimeUnit poolingUnit;

    public JobRunRequest(RundeckClient client, String project) {
        super(client, project);
    }

    public JobRunRequest poolingInterval(Long poolingInterval, TimeUnit poolingUnit) {
        this.poolingInterval = poolingInterval;
        this.poolingUnit = poolingUnit;
        return this;
    }

    @Override
    public RundeckExecution execute() {
        if (poolingInterval <= 0) {
            poolingInterval = DEFAULT_POOLING_INTERVAL;
            poolingUnit = DEFAULT_POOLING_UNIT;
        }
        if (poolingUnit == null) {
            poolingUnit = DEFAULT_POOLING_UNIT;
        }

        RundeckExecution execution = super.execute();
        while (ExecutionStatus.RUNNING.equals(execution.getStatus())) {
            try {
                Thread.sleep(poolingUnit.toMillis(poolingInterval));
            } catch (InterruptedException e) {
                break;
            }
            execution = client.getExecution(execution.getId());
        }
        return execution;
    }

}
