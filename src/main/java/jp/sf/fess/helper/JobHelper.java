package jp.sf.fess.helper;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import jp.sf.fess.FessSystemException;
import jp.sf.fess.job.JobExecutor;

import org.apache.commons.io.IOUtils;
import org.seasar.framework.container.annotation.tiger.DestroyMethod;

public class JobHelper {
    private final ConcurrentHashMap<String, Process> runningProcessMap = new ConcurrentHashMap<String, Process>();

    private final ConcurrentHashMap<Long, JobExecutor> runningJobExecutorMap = new ConcurrentHashMap<Long, JobExecutor>();

    @DestroyMethod
    public void destroy() {
        for (final String sessionId : runningProcessMap.keySet()) {
            destroyCrawlerProcess(sessionId);
        }
    }

    public Process startCrawlerProcess(final String sessionId,
            final ProcessBuilder processBuilder) {
        destroyCrawlerProcess(sessionId);
        Process currentProcess;
        try {
            currentProcess = processBuilder.start();
            destroyCrawlerProcess(runningProcessMap.putIfAbsent(sessionId,
                    currentProcess));
            return currentProcess;
        } catch (final IOException e) {
            throw new FessSystemException("Crawler Process terminated.", e);
        }
    }

    public void destroyCrawlerProcess(final String sessionId) {
        final Process process = runningProcessMap.remove(sessionId);
        destroyCrawlerProcess(process);
    }

    public boolean isCrawlProcessRunning() {
        return !runningProcessMap.isEmpty();
    }

    protected void destroyCrawlerProcess(final Process process) {
        if (process != null) {
            try {
                IOUtils.closeQuietly(process.getInputStream());
            } catch (final Exception e) {
            }
            try {
                IOUtils.closeQuietly(process.getErrorStream());
            } catch (final Exception e) {
            }
            try {
                IOUtils.closeQuietly(process.getOutputStream());
            } catch (final Exception e) {
            }
            try {
                process.destroy();
            } catch (final Exception e) {
            }
        }
    }

    public Set<String> getRunningSessionIdSet() {
        return runningProcessMap.keySet();
    }

    public JobExecutor startJobExecutoer(final Long id,
            final JobExecutor jobExecutor) {
        return runningJobExecutorMap.putIfAbsent(id, jobExecutor);
    }

    public void finishJobExecutoer(final Long id) {
        runningJobExecutorMap.remove(id);
    }

    public JobExecutor getJobExecutoer(final Long id) {
        return runningJobExecutorMap.get(id);
    }
}
