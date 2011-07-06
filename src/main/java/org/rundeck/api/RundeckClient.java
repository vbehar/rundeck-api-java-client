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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang.StringUtils;
import org.rundeck.api.RundeckApiException.RundeckApiLoginException;
import org.rundeck.api.domain.RundeckExecution;
import org.rundeck.api.domain.RundeckJob;
import org.rundeck.api.domain.RundeckProject;
import org.rundeck.api.domain.RundeckExecution.ExecutionStatus;
import org.rundeck.api.parser.ExecutionParser;
import org.rundeck.api.parser.ExecutionsParser;
import org.rundeck.api.parser.JobParser;
import org.rundeck.api.parser.JobsParser;
import org.rundeck.api.parser.ProjectParser;
import org.rundeck.api.parser.ProjectsParser;
import org.rundeck.api.util.AssertUtil;
import org.rundeck.api.util.NodeFiltersBuilder;
import org.rundeck.api.util.OptionsBuilder;
import org.rundeck.api.util.ParametersUtil;

/**
 * Main entry point to talk to a RunDeck instance.<br>
 * Usage : <br>
 * <code>
 * <pre>
 * RundeckClient rundeck = new RundeckClient("http://localhost:4440", "admin", "admin");
 * List&lt;RundeckJob&gt; jobs = rundeck.getJobs();
 * 
 * RundeckJob job = rundeck.findJob("my-project", "main-group/sub-group", "job-name");
 * RundeckExecution execution = rundeck.triggerJob(job.getId(),
 *                                                 new OptionsBuilder().addOption("version", "1.2.0").toProperties());
 * 
 * List&lt;RundeckExecution&gt; runningExecutions = rundeck.getRunningExecutions("my-project");
 * </pre>
 * </code>
 * 
 * @author Vincent Behar
 */
public class RundeckClient implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final transient int API_VERSION = 1;

    public static final transient String API_ENDPOINT = "/api/" + API_VERSION;

    private final String url;

    private final String login;

    private final String password;

    /**
     * Instantiate a new {@link RundeckClient} for the RunDeck instance at the given url
     * 
     * @param url of the RunDeck instance ("http://localhost:4440", "http://rundeck.your-compagny.com/", etc)
     * @param login
     * @param password
     * @throws IllegalArgumentException if the url, login or password is blank (null, empty or whitespace)
     */
    public RundeckClient(String url, String login, String password) throws IllegalArgumentException {
        super();
        this.url = url;
        this.login = login;
        this.password = password;
        AssertUtil.notBlank(url, "The RunDeck URL is mandatory !");
        AssertUtil.notBlank(login, "The RunDeck login is mandatory !");
        AssertUtil.notBlank(password, "The RunDeck password is mandatory !");
    }

    /**
     * Try to "ping" the RunDeck instance to see if it is alive
     * 
     * @throws RundeckApiException if the ping fails
     */
    public void ping() throws RundeckApiException {
        new ApiCall(this).ping();
    }

    /**
     * Test your credentials (login/password) on the RunDeck instance
     * 
     * @throws RundeckApiLoginException if the login fails
     */
    public void testCredentials() throws RundeckApiLoginException {
        new ApiCall(this).testCredentials();
    }

    /*
     * Projects
     */

    /**
     * List all projects
     * 
     * @return a {@link List} of {@link RundeckProject} : might be empty, but won't be null
     * @throws RundeckApiException in case of error when calling the API
     * @throws RundeckApiLoginException if the login failed
     */
    public List<RundeckProject> getProjects() throws RundeckApiException, RundeckApiLoginException {
        return new ApiCall(this).get("/projects", new ProjectsParser("result/projects/project"));
    }

    /**
     * Get the definition of a single project, identified by the given name
     * 
     * @param projectName name of the project - mandatory
     * @return a {@link RundeckProject} instance - won't be null
     * @throws RundeckApiException in case of error when calling the API (non-existent project with this name)
     * @throws RundeckApiLoginException if the login failed
     * @throws IllegalArgumentException if the projectName is blank (null, empty or whitespace)
     */
    public RundeckProject getProject(String projectName) throws RundeckApiException, RundeckApiLoginException,
            IllegalArgumentException {
        AssertUtil.notBlank(projectName, "projectName is mandatory to get the details of a project !");
        return new ApiCall(this).get("/project/" + projectName, new ProjectParser("result/projects/project"));
    }

    /*
     * Jobs
     */

    /**
     * List all jobs (for all projects)
     * 
     * @return a {@link List} of {@link RundeckJob} : might be empty, but won't be null
     * @throws RundeckApiException in case of error when calling the API
     * @throws RundeckApiLoginException if the login failed
     */
    public List<RundeckJob> getJobs() throws RundeckApiException, RundeckApiLoginException {
        List<RundeckJob> jobs = new ArrayList<RundeckJob>();
        for (RundeckProject project : getProjects()) {
            jobs.addAll(getJobs(project.getName()));
        }
        return jobs;
    }

    /**
     * List all jobs that belongs to the given project
     * 
     * @param project name of the project - mandatory
     * @return a {@link List} of {@link RundeckJob} : might be empty, but won't be null
     * @throws RundeckApiException in case of error when calling the API (non-existent project with this name)
     * @throws RundeckApiLoginException if the login failed
     * @throws IllegalArgumentException if the project is blank (null, empty or whitespace)
     * @see #getJobs(String, String, String, String...)
     */
    public List<RundeckJob> getJobs(String project) throws RundeckApiException, RundeckApiLoginException,
            IllegalArgumentException {
        return getJobs(project, null, null, new String[0]);
    }

    /**
     * List the jobs that belongs to the given project, and matches the given criteria (jobFilter, groupPath and jobIds)
     * 
     * @param project name of the project - mandatory
     * @param jobFilter a filter for the job Name - optional
     * @param groupPath a group or partial group path to include all jobs within that group path - optional
     * @param jobIds a list of Job IDs to include - optional
     * @return a {@link List} of {@link RundeckJob} : might be empty, but won't be null
     * @throws RundeckApiException in case of error when calling the API (non-existent project with this name)
     * @throws RundeckApiLoginException if the login failed
     * @throws IllegalArgumentException if the project is blank (null, empty or whitespace)
     * @see #getJobs(String)
     */
    public List<RundeckJob> getJobs(String project, String jobFilter, String groupPath, String... jobIds)
            throws RundeckApiException, RundeckApiLoginException, IllegalArgumentException {
        AssertUtil.notBlank(project, "project is mandatory to get all jobs !");
        StringBuilder apiPath = new StringBuilder("/jobs");
        apiPath.append("?project=").append(project);
        if (StringUtils.isNotBlank(jobFilter)) {
            apiPath.append("&jobFilter=").append(jobFilter);
        }
        if (StringUtils.isNotBlank(groupPath)) {
            apiPath.append("&groupPath=").append(groupPath);
        }
        if (jobIds != null && jobIds.length > 0) {
            apiPath.append("&idlist=").append(StringUtils.join(jobIds, ","));
        }
        return new ApiCall(this).get(apiPath.toString(), new JobsParser("result/jobs/job"));
    }

    /**
     * Find a job, identified by its project, group and name. Note that the groupPath is optional, as a job does not
     * need to belong to a group (either pass null, or an empty string).
     * 
     * @param project name of the project - mandatory
     * @param groupPath group to which the job belongs (if it belongs to a group) - optional
     * @param name of the job to find - mandatory
     * @return a {@link RundeckJob} instance - null if not found
     * @throws RundeckApiException in case of error when calling the API (non-existent project with this name)
     * @throws RundeckApiLoginException if the login failed
     * @throws IllegalArgumentException if the project or the name is blank (null, empty or whitespace)
     */
    public RundeckJob findJob(String project, String groupPath, String name) throws RundeckApiException,
            RundeckApiLoginException, IllegalArgumentException {
        AssertUtil.notBlank(project, "project is mandatory to find a job !");
        AssertUtil.notBlank(name, "job name is mandatory to find a job !");
        List<RundeckJob> jobs = getJobs(project, name, groupPath, new String[0]);
        return jobs.isEmpty() ? null : jobs.get(0);
    }

    /**
     * Get the definition of a single job, identified by the given ID
     * 
     * @param jobId identifier of the job - mandatory
     * @return a {@link RundeckJob} instance - won't be null
     * @throws RundeckApiException in case of error when calling the API (non-existent job with this ID)
     * @throws RundeckApiLoginException if the login failed
     * @throws IllegalArgumentException if the jobId is blank (null, empty or whitespace)
     */
    public RundeckJob getJob(String jobId) throws RundeckApiException, RundeckApiLoginException,
            IllegalArgumentException {
        AssertUtil.notBlank(jobId, "jobId is mandatory to get the details of a job !");
        return new ApiCall(this).get("/job/" + jobId, new JobParser("joblist/job"));
    }

    /**
     * Trigger the execution of a RunDeck job (identified by the given ID), and return immediately (without waiting the
     * end of the job execution)
     * 
     * @param jobId identifier of the job - mandatory
     * @return a {@link RundeckExecution} instance for the newly created (and running) execution - won't be null
     * @throws RundeckApiException in case of error when calling the API (non-existent job with this ID)
     * @throws RundeckApiLoginException if the login failed
     * @throws IllegalArgumentException if the jobId is blank (null, empty or whitespace)
     * @see #triggerJob(String, Properties, Properties)
     * @see #runJob(String)
     */
    public RundeckExecution triggerJob(String jobId) throws RundeckApiException, RundeckApiLoginException,
            IllegalArgumentException {
        return triggerJob(jobId, null);
    }

    /**
     * Trigger the execution of a RunDeck job (identified by the given ID), and return immediately (without waiting the
     * end of the job execution)
     * 
     * @param jobId identifier of the job - mandatory
     * @param options of the job - optional. See {@link OptionsBuilder}.
     * @return a {@link RundeckExecution} instance for the newly created (and running) execution - won't be null
     * @throws RundeckApiException in case of error when calling the API (non-existent job with this ID)
     * @throws RundeckApiLoginException if the login failed
     * @throws IllegalArgumentException if the jobId is blank (null, empty or whitespace)
     * @see #triggerJob(String, Properties, Properties)
     * @see #runJob(String, Properties)
     */
    public RundeckExecution triggerJob(String jobId, Properties options) throws RundeckApiException,
            RundeckApiLoginException, IllegalArgumentException {
        return triggerJob(jobId, options, null);
    }

    /**
     * Trigger the execution of a RunDeck job (identified by the given ID), and return immediately (without waiting the
     * end of the job execution)
     * 
     * @param jobId identifier of the job - mandatory
     * @param options of the job - optional. See {@link OptionsBuilder}.
     * @param nodeFilters for overriding the nodes on which the job will be executed - optional. See
     *            {@link NodeFiltersBuilder}
     * @return a {@link RundeckExecution} instance for the newly created (and running) execution - won't be null
     * @throws RundeckApiException in case of error when calling the API (non-existent job with this ID)
     * @throws RundeckApiLoginException if the login failed
     * @throws IllegalArgumentException if the jobId is blank (null, empty or whitespace)
     * @see #triggerJob(String)
     * @see #runJob(String, Properties, Properties)
     */
    public RundeckExecution triggerJob(String jobId, Properties options, Properties nodeFilters)
            throws RundeckApiException, RundeckApiLoginException, IllegalArgumentException {
        AssertUtil.notBlank(jobId, "jobId is mandatory to trigger a job !");
        StringBuilder apiPath = new StringBuilder("/job/").append(jobId).append("/run?");
        String argString = ParametersUtil.generateArgString(options);
        if (StringUtils.isNotBlank(argString)) {
            apiPath.append("argString=").append(ParametersUtil.urlEncode(argString)).append("&");
        }
        String filters = ParametersUtil.generateNodeFiltersString(nodeFilters);
        if (StringUtils.isNotBlank(filters)) {
            apiPath.append(filters);
        }
        return new ApiCall(this).get(apiPath.toString(), new ExecutionParser("result/executions/execution"));
    }

    /**
     * Run a RunDeck job (identified by the given ID), and wait until its execution is finished (or aborted) to return.
     * We will poll the RunDeck server at regular interval (every 5 seconds) to know if the execution is finished (or
     * aborted) or is still running.
     * 
     * @param jobId identifier of the job - mandatory
     * @return a {@link RundeckExecution} instance for the (finished/aborted) execution - won't be null
     * @throws RundeckApiException in case of error when calling the API (non-existent job with this ID)
     * @throws RundeckApiLoginException if the login failed
     * @throws IllegalArgumentException if the jobId is blank (null, empty or whitespace)
     * @see #triggerJob(String)
     * @see #runJob(String, Properties, Properties, long, TimeUnit)
     */
    public RundeckExecution runJob(String jobId) throws RundeckApiException, RundeckApiLoginException,
            IllegalArgumentException {
        return runJob(jobId, null);
    }

    /**
     * Run a RunDeck job (identified by the given ID), and wait until its execution is finished (or aborted) to return.
     * We will poll the RunDeck server at regular interval (every 5 seconds) to know if the execution is finished (or
     * aborted) or is still running.
     * 
     * @param jobId identifier of the job - mandatory
     * @param options of the job - optional. See {@link OptionsBuilder}.
     * @return a {@link RundeckExecution} instance for the (finished/aborted) execution - won't be null
     * @throws RundeckApiException in case of error when calling the API (non-existent job with this ID)
     * @throws RundeckApiLoginException if the login failed
     * @throws IllegalArgumentException if the jobId is blank (null, empty or whitespace)
     * @see #triggerJob(String, Properties)
     * @see #runJob(String, Properties, Properties, long, TimeUnit)
     */
    public RundeckExecution runJob(String jobId, Properties options) throws RundeckApiException,
            RundeckApiLoginException, IllegalArgumentException {
        return runJob(jobId, options, null);
    }

    /**
     * Run a RunDeck job (identified by the given ID), and wait until its execution is finished (or aborted) to return.
     * We will poll the RunDeck server at regular interval (every 5 seconds) to know if the execution is finished (or
     * aborted) or is still running.
     * 
     * @param jobId identifier of the job - mandatory
     * @param options of the job - optional. See {@link OptionsBuilder}.
     * @param nodeFilters for overriding the nodes on which the job will be executed - optional. See
     *            {@link NodeFiltersBuilder}
     * @return a {@link RundeckExecution} instance for the (finished/aborted) execution - won't be null
     * @throws RundeckApiException in case of error when calling the API (non-existent job with this ID)
     * @throws RundeckApiLoginException if the login failed
     * @throws IllegalArgumentException if the jobId is blank (null, empty or whitespace)
     * @see #triggerJob(String, Properties, Properties)
     * @see #runJob(String, Properties, Properties, long, TimeUnit)
     */
    public RundeckExecution runJob(String jobId, Properties options, Properties nodeFilters)
            throws RundeckApiException, RundeckApiLoginException, IllegalArgumentException {
        return runJob(jobId, options, nodeFilters, 5, TimeUnit.SECONDS);
    }

    /**
     * Run a RunDeck job (identified by the given ID), and wait until its execution is finished (or aborted) to return.
     * We will poll the RunDeck server at regular interval (configured by the poolingInterval/poolingUnit couple) to
     * know if the execution is finished (or aborted) or is still running.
     * 
     * @param jobId identifier of the job - mandatory
     * @param options of the job - optional. See {@link OptionsBuilder}.
     * @param poolingInterval for checking the status of the execution. Must be > 0.
     * @param poolingUnit unit (seconds, milli-seconds, ...) of the interval. Default to seconds.
     * @return a {@link RundeckExecution} instance for the (finished/aborted) execution - won't be null
     * @throws RundeckApiException in case of error when calling the API (non-existent job with this ID)
     * @throws RundeckApiLoginException if the login failed
     * @throws IllegalArgumentException if the jobId is blank (null, empty or whitespace)
     * @see #triggerJob(String, Properties)
     * @see #runJob(String, Properties, Properties, long, TimeUnit)
     */
    public RundeckExecution runJob(String jobId, Properties options, long poolingInterval, TimeUnit poolingUnit)
            throws RundeckApiException, RundeckApiLoginException, IllegalArgumentException {
        return runJob(jobId, options, null, poolingInterval, poolingUnit);
    }

    /**
     * Run a RunDeck job (identified by the given ID), and wait until its execution is finished (or aborted) to return.
     * We will poll the RunDeck server at regular interval (configured by the poolingInterval/poolingUnit couple) to
     * know if the execution is finished (or aborted) or is still running.
     * 
     * @param jobId identifier of the job - mandatory
     * @param options of the job - optional. See {@link OptionsBuilder}.
     * @param nodeFilters for overriding the nodes on which the job will be executed - optional. See
     *            {@link NodeFiltersBuilder}
     * @param poolingInterval for checking the status of the execution. Must be > 0.
     * @param poolingUnit unit (seconds, milli-seconds, ...) of the interval. Default to seconds.
     * @return a {@link RundeckExecution} instance for the (finished/aborted) execution - won't be null
     * @throws RundeckApiException in case of error when calling the API (non-existent job with this ID)
     * @throws RundeckApiLoginException if the login failed
     * @throws IllegalArgumentException if the jobId is blank (null, empty or whitespace)
     * @see #triggerJob(String, Properties)
     * @see #runJob(String, Properties, Properties, long, TimeUnit)
     */
    public RundeckExecution runJob(String jobId, Properties options, Properties nodeFilters, long poolingInterval,
            TimeUnit poolingUnit) throws RundeckApiException, RundeckApiLoginException, IllegalArgumentException {
        if (poolingInterval <= 0) {
            poolingInterval = 5;
            poolingUnit = TimeUnit.SECONDS;
        }
        if (poolingUnit == null) {
            poolingUnit = TimeUnit.SECONDS;
        }

        RundeckExecution execution = triggerJob(jobId, options, nodeFilters);
        while (ExecutionStatus.RUNNING.equals(execution.getStatus())) {
            try {
                Thread.sleep(poolingUnit.toMillis(poolingInterval));
            } catch (InterruptedException e) {
                break;
            }
            execution = getExecution(execution.getId());
        }
        return execution;
    }

    /*
     * Ad-hoc commands
     */

    /**
     * Trigger the execution of an ad-hoc command, and return immediately (without waiting the end of the execution).
     * The command will not be dispatched to nodes, but be executed on the RunDeck server.
     * 
     * @param project name of the project - mandatory
     * @param command to be executed - mandatory
     * @return a {@link RundeckExecution} instance for the newly created (and running) execution - won't be null
     * @throws RundeckApiException in case of error when calling the API (non-existent project with this name)
     * @throws RundeckApiLoginException if the login failed
     * @throws IllegalArgumentException if the project or command is blank (null, empty or whitespace)
     * @see #triggerAdhocCommand(String, String, Properties)
     * @see #runAdhocCommand(String, String)
     */
    public RundeckExecution triggerAdhocCommand(String project, String command) throws RundeckApiException,
            RundeckApiLoginException, IllegalArgumentException {
        return triggerAdhocCommand(project, command, null);
    }

    /**
     * Trigger the execution of an ad-hoc command, and return immediately (without waiting the end of the execution).
     * The command will be dispatched to nodes, accordingly to the nodeFilters parameter.
     * 
     * @param project name of the project - mandatory
     * @param command to be executed - mandatory
     * @param nodeFilters for selecting nodes on which the command will be executed. See {@link NodeFiltersBuilder}
     * @return a {@link RundeckExecution} instance for the newly created (and running) execution - won't be null
     * @throws RundeckApiException in case of error when calling the API (non-existent project with this name)
     * @throws RundeckApiLoginException if the login failed
     * @throws IllegalArgumentException if the project or command is blank (null, empty or whitespace)
     * @see #triggerAdhocCommand(String, String)
     * @see #runAdhocCommand(String, String, Properties)
     */
    public RundeckExecution triggerAdhocCommand(String project, String command, Properties nodeFilters)
            throws RundeckApiException, RundeckApiLoginException, IllegalArgumentException {
        AssertUtil.notBlank(project, "project is mandatory to trigger an ad-hoc command !");
        AssertUtil.notBlank(command, "command is mandatory to trigger an ad-hoc command !");
        StringBuilder apiPath = new StringBuilder("/run/command");
        apiPath.append("?project=").append(project);
        apiPath.append("&exec=").append(ParametersUtil.urlEncode(command));
        String filters = ParametersUtil.generateNodeFiltersString(nodeFilters);
        if (StringUtils.isNotBlank(filters)) {
            apiPath.append("&").append(filters);
        }
        RundeckExecution execution = new ApiCall(this).get(apiPath.toString(), new ExecutionParser("result/execution"));
        // the first call just returns the ID of the execution, so we need another call to get a "real" execution
        return getExecution(execution.getId());
    }

    /**
     * Run an ad-hoc command, and wait until its execution is finished (or aborted) to return. We will poll the RunDeck
     * server at regular interval (every 5 seconds) to know if the execution is finished (or aborted) or is still
     * running. The command will not be dispatched to nodes, but be executed on the RunDeck server.
     * 
     * @param project name of the project - mandatory
     * @param command to be executed - mandatory
     * @return a {@link RundeckExecution} instance for the (finished/aborted) execution - won't be null
     * @throws RundeckApiException in case of error when calling the API (non-existent project with this name)
     * @throws RundeckApiLoginException if the login failed
     * @throws IllegalArgumentException if the project or command is blank (null, empty or whitespace)
     * @see #runAdhocCommand(String, String, Properties, long, TimeUnit)
     * @see #triggerAdhocCommand(String, String)
     */
    public RundeckExecution runAdhocCommand(String project, String command) throws RundeckApiException,
            RundeckApiLoginException, IllegalArgumentException {
        return runAdhocCommand(project, command, null);
    }

    /**
     * Run an ad-hoc command, and wait until its execution is finished (or aborted) to return. We will poll the RunDeck
     * server at regular interval (configured by the poolingInterval/poolingUnit couple) to know if the execution is
     * finished (or aborted) or is still running. The command will not be dispatched to nodes, but be executed on the
     * RunDeck server.
     * 
     * @param project name of the project - mandatory
     * @param command to be executed - mandatory
     * @param poolingInterval for checking the status of the execution. Must be > 0.
     * @param poolingUnit unit (seconds, milli-seconds, ...) of the interval. Default to seconds.
     * @return a {@link RundeckExecution} instance for the (finished/aborted) execution - won't be null
     * @throws RundeckApiException in case of error when calling the API (non-existent project with this name)
     * @throws RundeckApiLoginException if the login failed
     * @throws IllegalArgumentException if the project or command is blank (null, empty or whitespace)
     * @see #runAdhocCommand(String, String, Properties, long, TimeUnit)
     * @see #triggerAdhocCommand(String, String)
     */
    public RundeckExecution runAdhocCommand(String project, String command, long poolingInterval, TimeUnit poolingUnit)
            throws RundeckApiException, RundeckApiLoginException, IllegalArgumentException {
        return runAdhocCommand(project, command, null, poolingInterval, poolingUnit);
    }

    /**
     * Run an ad-hoc command, and wait until its execution is finished (or aborted) to return. We will poll the RunDeck
     * server at regular interval (every 5 seconds) to know if the execution is finished (or aborted) or is still
     * running. The command will be dispatched to nodes, accordingly to the nodeFilters parameter.
     * 
     * @param project name of the project - mandatory
     * @param command to be executed - mandatory
     * @param nodeFilters for selecting nodes on which the command will be executed. See {@link NodeFiltersBuilder}
     * @return a {@link RundeckExecution} instance for the (finished/aborted) execution - won't be null
     * @throws RundeckApiException in case of error when calling the API (non-existent project with this name)
     * @throws RundeckApiLoginException if the login failed
     * @throws IllegalArgumentException if the project or command is blank (null, empty or whitespace)
     * @see #runAdhocCommand(String, String, Properties, long, TimeUnit)
     * @see #triggerAdhocCommand(String, String, Properties)
     */
    public RundeckExecution runAdhocCommand(String project, String command, Properties nodeFilters)
            throws RundeckApiException, RundeckApiLoginException, IllegalArgumentException {
        return runAdhocCommand(project, command, nodeFilters, 5, TimeUnit.SECONDS);
    }

    /**
     * Run an ad-hoc command, and wait until its execution is finished (or aborted) to return. We will poll the RunDeck
     * server at regular interval (configured by the poolingInterval/poolingUnit couple) to know if the execution is
     * finished (or aborted) or is still running. The command will be dispatched to nodes, accordingly to the
     * nodeFilters parameter.
     * 
     * @param project name of the project - mandatory
     * @param command to be executed - mandatory
     * @param nodeFilters for selecting nodes on which the command will be executed. See {@link NodeFiltersBuilder}
     * @param poolingInterval for checking the status of the execution. Must be > 0.
     * @param poolingUnit unit (seconds, milli-seconds, ...) of the interval. Default to seconds.
     * @return a {@link RundeckExecution} instance for the (finished/aborted) execution - won't be null
     * @throws RundeckApiException in case of error when calling the API (non-existent project with this name)
     * @throws RundeckApiLoginException if the login failed
     * @throws IllegalArgumentException if the project or command is blank (null, empty or whitespace)
     * @see #triggerAdhocCommand(String, String, Properties)
     */
    public RundeckExecution runAdhocCommand(String project, String command, Properties nodeFilters,
            long poolingInterval, TimeUnit poolingUnit) throws RundeckApiException, RundeckApiLoginException,
            IllegalArgumentException {
        if (poolingInterval <= 0) {
            poolingInterval = 5;
            poolingUnit = TimeUnit.SECONDS;
        }
        if (poolingUnit == null) {
            poolingUnit = TimeUnit.SECONDS;
        }

        RundeckExecution execution = triggerAdhocCommand(project, command, nodeFilters);
        while (ExecutionStatus.RUNNING.equals(execution.getStatus())) {
            try {
                Thread.sleep(poolingUnit.toMillis(poolingInterval));
            } catch (InterruptedException e) {
                break;
            }
            execution = getExecution(execution.getId());
        }
        return execution;
    }

    /*
     * Executions
     */

    /**
     * Get all running executions (for all projects)
     * 
     * @return a {@link List} of {@link RundeckExecution} : might be empty, but won't be null
     * @throws RundeckApiException in case of error when calling the API
     * @throws RundeckApiLoginException if the login failed
     */
    public List<RundeckExecution> getRunningExecutions() throws RundeckApiException, RundeckApiLoginException {
        List<RundeckExecution> executions = new ArrayList<RundeckExecution>();
        for (RundeckProject project : getProjects()) {
            executions.addAll(getRunningExecutions(project.getName()));
        }
        return executions;
    }

    /**
     * Get the running executions for the given project
     * 
     * @param project name of the project - mandatory
     * @return a {@link List} of {@link RundeckExecution} : might be empty, but won't be null
     * @throws RundeckApiException in case of error when calling the API (non-existent project with this name)
     * @throws RundeckApiLoginException if the login failed
     * @throws IllegalArgumentException if the project is blank (null, empty or whitespace)
     */
    public List<RundeckExecution> getRunningExecutions(String project) throws RundeckApiException,
            RundeckApiLoginException, IllegalArgumentException {
        AssertUtil.notBlank(project, "project is mandatory to trigger an ad-hoc command !");
        return new ApiCall(this).get("/executions/running?project=" + project,
                                     new ExecutionsParser("result/executions/execution"));
    }

    /**
     * Get the executions of the given job
     * 
     * @param jobId identifier of the job - mandatory
     * @return a {@link List} of {@link RundeckExecution} : might be empty, but won't be null
     * @throws RundeckApiException in case of error when calling the API (non-existent job with this ID)
     * @throws RundeckApiLoginException if the login failed
     * @throws IllegalArgumentException if the jobId is blank (null, empty or whitespace)
     */
    public List<RundeckExecution> getJobExecutions(String jobId) throws RundeckApiException, RundeckApiLoginException,
            IllegalArgumentException {
        return getJobExecutions(jobId, null);
    }

    /**
     * Get the executions of the given job
     * 
     * @param jobId identifier of the job - mandatory
     * @param status of the executions - optional (null for all)
     * @return a {@link List} of {@link RundeckExecution} : might be empty, but won't be null
     * @throws RundeckApiException in case of error when calling the API (non-existent job with this ID)
     * @throws RundeckApiLoginException if the login failed
     * @throws IllegalArgumentException if the jobId is blank (null, empty or whitespace)
     */
    public List<RundeckExecution> getJobExecutions(String jobId, ExecutionStatus status) throws RundeckApiException,
            RundeckApiLoginException, IllegalArgumentException {
        return getJobExecutions(jobId, status, null, null);
    }

    /**
     * Get the executions of the given job
     * 
     * @param jobId identifier of the job - mandatory
     * @param status of the executions - optional (null for all)
     * @param max number of results to return - optional (null for all)
     * @param offset the 0-indexed offset for the first result to return - optional
     * @return a {@link List} of {@link RundeckExecution} : might be empty, but won't be null
     * @throws RundeckApiException in case of error when calling the API (non-existent job with this ID)
     * @throws RundeckApiLoginException if the login failed
     * @throws IllegalArgumentException if the jobId is blank (null, empty or whitespace)
     */
    public List<RundeckExecution> getJobExecutions(String jobId, ExecutionStatus status, Long max, Long offset)
            throws RundeckApiException, RundeckApiLoginException, IllegalArgumentException {
        AssertUtil.notBlank(jobId, "jobId is mandatory to get the executions of a job !");
        StringBuilder apiPath = new StringBuilder("/job/").append(jobId).append("/executions?");
        if (status != null) {
            apiPath.append("status=").append(StringUtils.lowerCase(status.toString())).append("&");
        }
        if (max != null && max >= 0) {
            apiPath.append("max=").append(max).append("&");
        }
        if (offset != null && offset >= 0) {
            apiPath.append("offset=").append(offset);
        }
        return new ApiCall(this).get(apiPath.toString(), new ExecutionsParser("result/executions/execution"));
    }

    /**
     * Get a single execution, identified by the given ID
     * 
     * @param executionId identifier of the execution - mandatory
     * @return a {@link RundeckExecution} instance - won't be null
     * @throws RundeckApiException in case of error when calling the API (non-existent execution with this ID)
     * @throws RundeckApiLoginException if the login failed
     * @throws IllegalArgumentException if the executionId is null
     */
    public RundeckExecution getExecution(Long executionId) throws RundeckApiException, RundeckApiLoginException,
            IllegalArgumentException {
        AssertUtil.notNull(executionId, "executionId is mandatory to get the details of an execution !");
        return new ApiCall(this).get("/execution/" + executionId, new ExecutionParser("result/executions/execution"));
    }

    public String getUrl() {
        return url;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "RundeckClient [url=" + url + ", login=" + login + ", password=" + password + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((login == null) ? 0 : login.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
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
        RundeckClient other = (RundeckClient) obj;
        if (login == null) {
            if (other.login != null)
                return false;
        } else if (!login.equals(other.login))
            return false;
        if (password == null) {
            if (other.password != null)
                return false;
        } else if (!password.equals(other.password))
            return false;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        return true;
    }

}
