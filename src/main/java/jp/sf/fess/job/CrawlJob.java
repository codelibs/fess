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
import jp.sf.fess.FessSystemException;
import jp.sf.fess.helper.SystemHelper;
import jp.sf.fess.job.JobExecutor.ShutdownListener;

import org.seasar.framework.container.SingletonS2Container;

public class CrawlJob {

    protected int documentExpires = -2;

    public String execute(final JobExecutor jobExecutor) {
        return execute(jobExecutor, null, null, null, Constants.COMMIT);
    }

    public String execute(final JobExecutor jobExecutor,
            final String[] webConfigIds, final String[] fileConfigIds,
            final String[] dataConfigIds, final String operation) {

        // create session id
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        final String sessionId = sdf.format(new Date());

        return execute(jobExecutor, sessionId, webConfigIds, fileConfigIds,
                dataConfigIds, operation);

    }

    public String execute(final JobExecutor jobExecutor,
            final String sessionId, final String[] webConfigIds,
            final String[] fileConfigIds, final String[] dataConfigIds,
            final String operation) {
        final StringBuilder resultBuf = new StringBuilder();

        resultBuf.append("Session Id: ").append(sessionId).append("\n");
        resultBuf.append("Web  Config Id:");
        if (webConfigIds == null) {
            resultBuf.append(" ALL\n");
        } else {
            for (final String id : webConfigIds) {
                resultBuf.append(' ').append(id);
            }
            resultBuf.append('\n');
        }
        resultBuf.append("File Config Id:");
        if (fileConfigIds == null) {
            resultBuf.append(" ALL\n");
        } else {
            for (final String id : fileConfigIds) {
                resultBuf.append(' ').append(id);
            }
            resultBuf.append('\n');
        }
        resultBuf.append("Data Config Id:");
        if (dataConfigIds == null) {
            resultBuf.append(" ALL\n");
        } else {
            for (final String id : dataConfigIds) {
                resultBuf.append(' ').append(id);
            }
            resultBuf.append('\n');
        }

        if (jobExecutor != null) {
            jobExecutor.addShutdownListener(new ShutdownListener() {
                @Override
                public void onShutdown() {
                    SingletonS2Container.getComponent(SystemHelper.class)
                            .destroyCrawlerProcess(sessionId);
                }
            });
        }

        try {
            SingletonS2Container.getComponent(SystemHelper.class)
                    .executeCrawler(sessionId, webConfigIds, fileConfigIds,
                            dataConfigIds, operation, documentExpires);
        } catch (final FessSystemException e) {
            throw e;
        } catch (final Exception e) {
            throw new FessSystemException("Failed to execute a crawl job.", e);
        }

        return resultBuf.toString();

    }

    public int getDocumentExpires() {
        return documentExpires;
    }

    public void setDocumentExpires(final int documentExpires) {
        this.documentExpires = documentExpires;
    }

}
