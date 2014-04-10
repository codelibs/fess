/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package jp.sf.fess.helper;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import jp.sf.fess.FessSystemException;
import jp.sf.fess.job.JobExecutor;

import org.apache.commons.io.IOUtils;
import org.seasar.framework.container.annotation.tiger.DestroyMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobHelper {
    private static final Logger logger = LoggerFactory
            .getLogger(JobHelper.class);

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
                logger.warn("Could not close a process input stream.", e);
            }
            try {
                IOUtils.closeQuietly(process.getErrorStream());
            } catch (final Exception e) {
                logger.warn("Could not close a process error stream.", e);
            }
            try {
                IOUtils.closeQuietly(process.getOutputStream());
            } catch (final Exception e) {
                logger.warn("Could not close a process output stream.", e);
            }
            try {
                process.destroy();
            } catch (final Exception e) {
                logger.error("Could not destroy a process correctly.", e);
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
