/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

package org.codelibs.fess.servlet;

import java.lang.ref.Reference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;

import org.seasar.framework.util.DriverManagerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tomcat6ConfigServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(Tomcat6ConfigServlet.class);

    private static final long serialVersionUID = 1L;

    @Override
    public void destroy() {
        if (logger.isInfoEnabled()) {
            logger.info("Removing HTTP connection manager...");
        }
        shutdownCommonsHttpClient();

        if (logger.isInfoEnabled()) {
            logger.info("Removing all drivers...");
        }
        DriverManagerUtil.deregisterAllDrivers();

        cleanupAllThreads();
    }

    private void shutdownCommonsHttpClient() {
        try {
            final Class<?> clazz = Class.forName("org.apache.commons.httpclient.MultiThreadedHttpConnectionManager");
            final Method method = clazz.getMethod("shutdownAll", null);
            method.invoke(null, null);
        } catch (final ClassNotFoundException e) {
            // ignore
        } catch (final Exception e) {
            logger.warn("Could not shutdown Commons HttpClient.", e);
        }
    }

    @SuppressWarnings("deprecation")
    private void cleanupAllThreads() {
        final Thread[] threads = getThreads();
        final ClassLoader cl = this.getClass().getClassLoader();
        try {
            cl.getResource(null);
        } catch (final Exception e) {}

        final List<String> jvmThreadGroupList = new ArrayList<String>();
        jvmThreadGroupList.add("system");
        jvmThreadGroupList.add("RMI Runtime");

        // Iterate over the set of threads
        for (final Thread thread : threads) {
            if (thread != null) {
                final ClassLoader ccl = thread.getContextClassLoader();
                if (ccl != null && ccl.equals(cl)) {
                    // Don't warn about this thread
                    if (thread == Thread.currentThread()) {
                        continue;
                    }

                    // Don't warn about JVM controlled threads
                    final ThreadGroup tg = thread.getThreadGroup();
                    if (tg != null && jvmThreadGroupList.contains(tg.getName())) {
                        continue;
                    }

                    waitThread(thread);
                    // Skip threads that have already died
                    if (!thread.isAlive()) {
                        continue;
                    }

                    if (logger.isInfoEnabled()) {
                        logger.info("Interrupting a thread [" + thread.getName() + "]...");
                    }
                    thread.interrupt();

                    waitThread(thread);
                    // Skip threads that have already died
                    if (!thread.isAlive()) {
                        continue;
                    }

                    if (logger.isInfoEnabled()) {
                        logger.info("Stopping a thread [" + thread.getName() + "]...");
                    }
                    thread.stop();
                }
            }
        }

        Field threadLocalsField = null;
        Field inheritableThreadLocalsField = null;
        Field tableField = null;
        try {
            threadLocalsField = Thread.class.getDeclaredField("threadLocals");
            threadLocalsField.setAccessible(true);
            inheritableThreadLocalsField = Thread.class.getDeclaredField("inheritableThreadLocals");
            inheritableThreadLocalsField.setAccessible(true);
            // Make the underlying array of ThreadLoad.ThreadLocalMap.Entry objects
            // accessible
            final Class<?> tlmClass = Class.forName("java.lang.ThreadLocal$ThreadLocalMap");
            tableField = tlmClass.getDeclaredField("table");
            tableField.setAccessible(true);
        } catch (final Exception e) {
            // ignore
        }
        for (final Thread thread : threads) {
            if (thread != null) {

                Object threadLocalMap;
                try {
                    // Clear the first map
                    threadLocalMap = threadLocalsField.get(thread);
                    clearThreadLocalMap(cl, threadLocalMap, tableField);
                } catch (final Exception e) {
                    // ignore
                }
                try { // Clear the second map
                    threadLocalMap = inheritableThreadLocalsField.get(thread);
                    clearThreadLocalMap(cl, threadLocalMap, tableField);
                } catch (final Exception e) {
                    // ignore
                }
            }
        }
    }

    private void waitThread(final Thread thread) {
        int count = 0;
        while (thread.isAlive() && count < 5) {
            try {
                Thread.sleep(100);
            } catch (final InterruptedException e) {}
            count++;
        }
    }

    /*
     * Get the set of current threads as an array.
     */
    private Thread[] getThreads() {
        // Get the current thread group
        ThreadGroup tg = Thread.currentThread().getThreadGroup();
        // Find the root thread group
        while (tg.getParent() != null) {
            tg = tg.getParent();
        }

        int threadCountGuess = tg.activeCount() + 50;
        Thread[] threads = new Thread[threadCountGuess];
        int threadCountActual = tg.enumerate(threads);
        // Make sure we don't miss any threads
        while (threadCountActual == threadCountGuess) {
            threadCountGuess *= 2;
            threads = new Thread[threadCountGuess];
            // Note tg.enumerate(Thread[]) silently ignores any threads that
            // can't fit into the array
            threadCountActual = tg.enumerate(threads);
        }

        return threads;
    }

    private void clearThreadLocalMap(final ClassLoader cl, final Object map, final Field internalTableField) throws NoSuchMethodException,
            IllegalAccessException, NoSuchFieldException, InvocationTargetException {
        if (map != null) {
            final Method mapRemove = map.getClass().getDeclaredMethod("remove", ThreadLocal.class);
            mapRemove.setAccessible(true);
            final Object[] table = (Object[]) internalTableField.get(map);
            if (table != null) {
                for (final Object element : table) {
                    if (element != null) {
                        boolean remove = false;
                        // Check the key
                        final Field keyField = Reference.class.getDeclaredField("referent");
                        keyField.setAccessible(true);
                        final Object key = keyField.get(element);
                        if (cl.equals(key) || key != null && cl == key.getClass().getClassLoader()) {
                            remove = true;
                        }
                        // Check the value
                        final Field valueField = element.getClass().getDeclaredField("value");
                        valueField.setAccessible(true);
                        final Object value = valueField.get(element);
                        if (cl.equals(value) || value != null && cl == value.getClass().getClassLoader()) {
                            remove = true;
                        }
                        if (remove) {
                            final Object entry = ((Reference<?>) element).get();
                            if (logger.isInfoEnabled()) {
                                logger.info("Removing " + key.toString() + " from a thread local...");
                            }
                            mapRemove.invoke(map, entry);
                        }
                    }
                }
            }
        }
    }

}
