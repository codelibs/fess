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
package org.codelibs.fess.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Static singleton buffer for collecting log notification events.
 * Uses a lock-free concurrent queue for thread safety.
 */
public class LogNotificationBuffer {

    private static final LogNotificationBuffer INSTANCE = new LogNotificationBuffer();

    private static final int MAX_BUFFER_SIZE = 1000;

    private final ConcurrentLinkedQueue<LogNotificationEvent> queue = new ConcurrentLinkedQueue<>();

    private final AtomicInteger size = new AtomicInteger(0);

    private LogNotificationBuffer() {
    }

    /**
     * Returns the singleton instance of the buffer.
     *
     * @return the singleton instance
     */
    public static LogNotificationBuffer getInstance() {
        return INSTANCE;
    }

    /**
     * Offers an event to the buffer. If the buffer exceeds the maximum size, the oldest event is dropped.
     *
     * @param event the log notification event to add
     */
    public void offer(final LogNotificationEvent event) {
        queue.offer(event);
        if (size.incrementAndGet() > MAX_BUFFER_SIZE) {
            if (queue.poll() != null) {
                size.decrementAndGet();
            }
        }
    }

    /**
     * Drains all events from the buffer and returns them as a list.
     *
     * @return a list of all buffered events
     */
    public List<LogNotificationEvent> drainAll() {
        final List<LogNotificationEvent> events = new ArrayList<>();
        LogNotificationEvent event;
        while ((event = queue.poll()) != null) {
            events.add(event);
            size.decrementAndGet();
        }
        return events;
    }

    /**
     * Represents a captured log event for notification.
     */
    public static class LogNotificationEvent {

        private final long timestamp;

        private final String level;

        private final String loggerName;

        private final String message;

        private final String throwable;

        /**
         * Constructs a new LogNotificationEvent.
         *
         * @param timestamp the event timestamp in milliseconds
         * @param level the log level name
         * @param loggerName the logger name
         * @param message the log message
         * @param throwable the throwable string, or null
         */
        public LogNotificationEvent(final long timestamp, final String level, final String loggerName, final String message,
                final String throwable) {
            this.timestamp = timestamp;
            this.level = level;
            this.loggerName = loggerName;
            this.message = message;
            this.throwable = throwable;
        }

        /**
         * Returns the event timestamp in milliseconds.
         *
         * @return the event timestamp in milliseconds
         */
        public long getTimestamp() {
            return timestamp;
        }

        /**
         * Returns the log level name.
         *
         * @return the log level name
         */
        public String getLevel() {
            return level;
        }

        /**
         * Returns the logger name.
         *
         * @return the logger name
         */
        public String getLoggerName() {
            return loggerName;
        }

        /**
         * Returns the log message.
         *
         * @return the log message
         */
        public String getMessage() {
            return message;
        }

        /**
         * Returns the throwable string.
         *
         * @return the throwable string, or null
         */
        public String getThrowable() {
            return throwable;
        }
    }
}
