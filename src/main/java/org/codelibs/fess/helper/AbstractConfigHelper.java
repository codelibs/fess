/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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
package org.codelibs.fess.helper;

import org.codelibs.core.concurrent.CommonPoolUtil;
import org.codelibs.core.lang.ThreadUtil;

/**
 * The abstract helper for configuration.
 *
 * This class provides a basic framework for managing configurations that can be reloaded.
 * It includes functionality for updating configurations in a separate thread and controlling the reloading interval.
 */
public abstract class AbstractConfigHelper {

    /**
     * The interval for reloading.
     */
    protected long reloadInterval = 1000L;

    /**
     * Update the configuration.
     */
    public void update() {
        CommonPoolUtil.execute(this::load);
    }

    /**
     * Wait for the next reloading.
     */
    protected void waitForNext() {
        if (reloadInterval > 0) {
            ThreadUtil.sleep(reloadInterval);
        }
    }

    /**
     * Load the configuration.
     * @return The number of loaded configurations.
     */
    public abstract int load();

    /**
     * Set the interval for reloading.
     * @param reloadInterval The interval for reloading.
     */
    public void setReloadInterval(final long reloadInterval) {
        this.reloadInterval = reloadInterval;
    }
}
