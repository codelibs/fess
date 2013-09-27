/*
 * Copyright 2009-2013 the Fess Project and the Others.
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

package jp.sf.fess.job;

import java.text.SimpleDateFormat;
import java.util.Date;

import jp.sf.fess.Constants;
import jp.sf.fess.helper.CrawlingSessionHelper;
import jp.sf.fess.helper.SystemHelper;
import jp.sf.fess.job.JobExecutor.ShutdownListener;

import org.seasar.framework.container.SingletonS2Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrawlJob {

    private static final Logger logger = LoggerFactory
            .getLogger(CrawlJob.class);

    public String execute(final JobExecutor jobExecutor) {
        return execute(jobExecutor, null, null, null, Constants.COMMIT);
    }

    public String execute(final JobExecutor jobExecutor,
            final String[] webConfigIds, final String[] fileConfigIds,
            final String[] dataConfigIds, final String operation) {
        final StringBuilder resultBuf = new StringBuilder();

        // create session id
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        final String sessionId = sdf.format(new Date());

        if (jobExecutor != null) {
            jobExecutor.addShutdownListener(new ShutdownListener() {
                @Override
                public void onShutdown() {
                    SingletonS2Container.getComponent(SystemHelper.class)
                            .destroyCrawlerProcess(sessionId);
                }
            });
        }

        // store crawling session
        final CrawlingSessionHelper crawlingSessionHelper = SingletonS2Container
                .getComponent("crawlingSessionHelper");
        try {
            crawlingSessionHelper.store(sessionId);
            SingletonS2Container.getComponent(SystemHelper.class)
                    .executeCrawler(sessionId, webConfigIds, fileConfigIds,
                            dataConfigIds, operation);
        } catch (final Exception e) {
            logger.error("Failed to execute a crawl job.", e);
            resultBuf.append(e.getMessage());
        }

        return resultBuf.toString();
    }

}
