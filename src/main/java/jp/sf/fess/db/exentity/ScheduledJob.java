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

package jp.sf.fess.db.exentity;

import jp.sf.fess.Constants;
import jp.sf.fess.db.bsentity.BsScheduledJob;
import jp.sf.fess.helper.SystemHelper;

import org.seasar.framework.container.SingletonS2Container;

/**
 * The entity of SCHEDULED_JOB.
 * <p>
 * You can implement your original methods here.
 * This class remains when re-generating.
 * </p>
 * @author DBFlute(AutoGenerator)
 */
public class ScheduledJob extends BsScheduledJob {

    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    public boolean isLoggingEnabled() {
        return Constants.T.equals(getJobLogging());
    }

    public boolean isCrawlerJob() {
        return Constants.T.equals(getCrawler());
    }

    public boolean isEnabled() {
        return Constants.T.equals(getAvailable());
    }

    public boolean isRunning() {
        return SingletonS2Container.getComponent(SystemHelper.class)
                .getJobExecutoer(getId()) != null;
    }
}
