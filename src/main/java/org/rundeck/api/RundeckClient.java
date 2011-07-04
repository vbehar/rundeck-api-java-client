package org.rundeck.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.commons.lang.StringUtils;
import org.rundeck.api.RundeckApiException.RundeckApiLoginException;
import org.rundeck.api.domain.RundeckExecution;
import org.rundeck.api.domain.RundeckJob;
import org.rundeck.api.domain.RundeckProject;
import org.rundeck.api.parser.ExecutionParser;
import org.rundeck.api.parser.ExecutionsParser;
import org.rundeck.api.parser.JobParser;
import org.rundeck.api.parser.JobsParser;
import org.rundeck.api.parser.ProjectParser;
import org.rundeck.api.parser.ProjectsParser;
import org.rundeck.api.util.ArgsUtil;
import org.rundeck.api.util.AssertUtil;

/**
 * Main entry point to talk to a RunDeck instance
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
     */
    public RundeckClient(String url, String login, String password) {
        super();
        this.url = url;
        this.login = login;
        this.password = password;
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
     * @see #triggerJob(String, Properties)
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
     * @param options of the job - optional
     * @return a {@link RundeckExecution} instance for the newly created (and running) execution - won't be null
     * @throws RundeckApiException in case of error when calling the API (non-existent job with this ID)
     * @throws RundeckApiLoginException if the login failed
     * @throws IllegalArgumentException if the jobId is blank (null, empty or whitespace)
     * @see #triggerJob(String)
     */
    public RundeckExecution triggerJob(String jobId, Properties options) throws RundeckApiException,
            RundeckApiLoginException, IllegalArgumentException {
        AssertUtil.notBlank(jobId, "jobId is mandatory to trigger a job !");
        StringBuilder apiPath = new StringBuilder("/job/").append(jobId).append("/run");
        if (options != null) {
            apiPath.append("?argString=").append(ArgsUtil.generateUrlEncodedArgString(options));
        }
        return new ApiCall(this).get(apiPath.toString(), new ExecutionParser("result/executions/execution"));
    }

    /*
     * Executions
     */

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
        AssertUtil.notBlank(jobId, "jobId is mandatory to get the executions of a job !");
        return new ApiCall(this).get("/job/" + jobId + "/executions",
                                     new ExecutionsParser("result/executions/execution"));
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
