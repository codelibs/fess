/*
 * Copyright 2012-2021 CodeLibs Project and the Others.
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
package org.codelibs.fess.job;

import org.codelibs.fess.Constants;

public abstract class JobExecutor {
    protected ShutdownListener shutdownListener;

    @Deprecated
    public Object execute(String script) {
        return execute(Constants.DEFAULT_SCRIPT, script);
    }

    public abstract Object execute(String scriptType, String script);

    public void shutdown() {
        shutdownListener.onShutdown();
    }

    public void addShutdownListener(final ShutdownListener listener) {
        shutdownListener = listener;
    }

    public interface ShutdownListener {
        void onShutdown();
    }
}
