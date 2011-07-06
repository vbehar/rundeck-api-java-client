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
package org.rundeck.api.domain;

import java.io.Serializable;

/**
 * Represents an abort of a {@link RundeckExecution}
 * 
 * @author Vincent Behar
 */
public class RundeckAbort implements Serializable {

    private static final long serialVersionUID = 1L;

    private AbortStatus status;

    private RundeckExecution execution;

    public AbortStatus getStatus() {
        return status;
    }

    public void setStatus(AbortStatus status) {
        this.status = status;
    }

    public RundeckExecution getExecution() {
        return execution;
    }

    public void setExecution(RundeckExecution execution) {
        this.execution = execution;
    }

    @Override
    public String toString() {
        return "RundeckAbort [status=" + status + ", execution=" + execution + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((execution == null) ? 0 : execution.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RundeckAbort other = (RundeckAbort) obj;
        if (execution == null) {
            if (other.execution != null)
                return false;
        } else if (!execution.equals(other.execution))
            return false;
        if (status == null) {
            if (other.status != null)
                return false;
        } else if (!status.equals(other.status))
            return false;
        return true;
    }

    /**
     * The status of an abort
     */
    public static enum AbortStatus {
        PENDING, FAILED, ABORTED;
    }

}
