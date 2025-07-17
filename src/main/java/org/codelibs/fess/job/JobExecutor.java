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
package org.codelibs.fess.job;

/**
 * Abstract base class for job executors that handle script execution within the job system.
 * This class provides a framework for executing scripts and managing shutdown operations.
 */
public abstract class JobExecutor {
    /** Listener to handle shutdown events */
    protected ShutdownListener shutdownListener;

    /**
     * Default constructor.
     */
    public JobExecutor() {
        // Default constructor
    }

    /**
     * Executes a script with the specified script type and content.
     *
     * @param scriptType the type of script to execute
     * @param script the script content to execute
     * @return the result of script execution
     */
    public abstract Object execute(String scriptType, String script);

    /**
     * Initiates shutdown of the job executor.
     * This method notifies the shutdown listener to perform cleanup operations.
     */
    public void shutdown() {
        shutdownListener.onShutdown();
    }

    /**
     * Adds a shutdown listener to be notified when the executor is shutting down.
     *
     * @param listener the shutdown listener to add
     */
    public void addShutdownListener(final ShutdownListener listener) {
        shutdownListener = listener;
    }

    /**
     * Interface for listening to shutdown events.
     */
    public interface ShutdownListener {
        /**
         * Called when the job executor is shutting down.
         */
        void onShutdown();
    }
}
