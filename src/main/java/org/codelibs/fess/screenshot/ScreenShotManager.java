/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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
package org.codelibs.fess.screenshot;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.codelibs.core.collection.LruHashMap;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocumentUtil;
import org.lastaflute.web.util.LaRequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScreenShotManager {
    private static final String DEFAULT_SCREENSHOT_DIR = "/WEB-INF/screenshots";

    private static final Logger logger = LoggerFactory.getLogger(ScreenShotManager.class);

    @Resource
    protected ServletContext application;

    protected File baseDir;

    public long shutdownTimeout = 5 * 60 * 1000; // 5min

    public int screenShotPathCacheSize = 10;

    private final List<ScreenShotGenerator> generatorList = new ArrayList<ScreenShotGenerator>();

    public String imageExtention = "png";

    public int splitSize = 5;

    private final BlockingQueue<ScreenShotTask> screenShotTaskQueue = new LinkedBlockingQueue<ScreenShotTask>();

    private boolean generating;

    private Thread screenshotGeneratorThread;

    @PostConstruct
    public void init() {
        final String varPath = System.getProperty("fess.var.path");
        if (varPath != null) {
            baseDir = new File(varPath, "screenshots");
        } else {
            final String path = application.getRealPath(DEFAULT_SCREENSHOT_DIR);
            if (StringUtil.isNotBlank(path)) {
                baseDir = new File(path);
            } else {
                baseDir = new File("." + DEFAULT_SCREENSHOT_DIR);
            }
        }
        if (baseDir.mkdirs()) {
            logger.info("Created: " + baseDir.getAbsolutePath());
        }
        if (!baseDir.isDirectory()) {
            throw new FessSystemException("Not found: " + baseDir.getAbsolutePath());
        }

        if (logger.isDebugEnabled()) {
            logger.debug("ScreenShot Directory: " + baseDir.getAbsolutePath());
        }

        generating = true;
        screenshotGeneratorThread = new Thread((Runnable) () -> {
            while (generating) {
                try {
                    screenShotTaskQueue.take().generate();
                } catch (final InterruptedException e1) {
                    logger.debug("Interupted task.", e1);
                } catch (final Exception e2) {
                    logger.warn("Failed to generage a screenshot.", e2);
                }
            }
        }, "ScreenShotGenerator");
        screenshotGeneratorThread.start();
    }

    @PreDestroy
    public void destroy() {
        generating = false;
        screenshotGeneratorThread.interrupt();
    }

    public void generate(final Map<String, Object> docMap) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        for (final ScreenShotGenerator generator : generatorList) {
            if (generator.isTarget(docMap)) {
                final String url = DocumentUtil.getValue(docMap, fessConfig.getIndexFieldUrl(), String.class);
                final String path = getImageFilename(docMap);
                if (!screenShotTaskQueue.offer(new ScreenShotTask(url, new File(baseDir, path), generator))) {
                    logger.warn("Failed to offer a screenshot task: " + url + " -> " + path);
                }
                break;
            }
        }
    }

    protected String getImageFilename(final Map<String, Object> docMap) {
        final StringBuilder buf = new StringBuilder(50);
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String docid = DocumentUtil.getValue(docMap, fessConfig.getIndexFieldDocId(), String.class);
        for (int i = 0; i < docid.length(); i++) {
            if (i > 0 && i % splitSize == 0) {
                buf.append('/');
            }
            buf.append(docid.charAt(i));
        }
        buf.append('.').append(imageExtention);
        return buf.toString();
    }

    public void storeRequest(final String queryId, final List<Map<String, Object>> documentItems) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final Map<String, String> dataMap = new HashMap<String, String>(documentItems.size());
        for (final Map<String, Object> docMap : documentItems) {
            final String docid = (String) docMap.get(fessConfig.getIndexFieldDocId());
            final String screenShotPath = getImageFilename(docMap);
            if (StringUtil.isNotBlank(docid) && StringUtil.isNotBlank(screenShotPath)) {
                dataMap.put(docid, screenShotPath);
            }
        }
        final Map<String, Map<String, String>> screenShotPathCache = getScreenShotPathCache(LaRequestUtil.getRequest().getSession());
        screenShotPathCache.put(queryId, dataMap);
    }

    public File getScreenShotFile(final String queryId, final String docId) {
        final HttpSession session = LaRequestUtil.getRequest().getSession(false);
        if (session != null) {
            final Map<String, Map<String, String>> screenShotPathCache = getScreenShotPathCache(session);
            final Map<String, String> dataMap = screenShotPathCache.get(queryId);
            if (dataMap != null) {
                final String path = dataMap.get(docId);
                final File file = new File(baseDir, path);
                if (file.isFile()) {
                    return file;
                }
            }
        }
        return null;
    }

    private Map<String, Map<String, String>> getScreenShotPathCache(final HttpSession session) {
        @SuppressWarnings("unchecked")
        Map<String, Map<String, String>> screenShotPathCache =
                (Map<String, Map<String, String>>) session.getAttribute(Constants.SCREEN_SHOT_PATH_CACHE);
        if (screenShotPathCache == null) {
            screenShotPathCache = new LruHashMap<String, Map<String, String>>(screenShotPathCacheSize);
            session.setAttribute(Constants.SCREEN_SHOT_PATH_CACHE, screenShotPathCache);
        }
        return screenShotPathCache;
    }

    public void add(final ScreenShotGenerator generator) {
        generatorList.add(generator);
    }

    protected static class ScreenShotTask {
        String url;

        File outputFile;

        ScreenShotGenerator generator;

        protected ScreenShotTask(final String url, final File outputFile, final ScreenShotGenerator generator) {
            this.url = url;
            this.outputFile = outputFile;
            this.generator = generator;
        }

        public void generate() {
            generator.generate(url, outputFile);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (outputFile == null ? 0 : outputFile.hashCode());
            result = prime * result + (url == null ? 0 : url.hashCode());
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ScreenShotTask other = (ScreenShotTask) obj;
            if (outputFile == null) {
                if (other.outputFile != null) {
                    return false;
                }
            } else if (!outputFile.equals(other.outputFile)) {
                return false;
            }
            if (url == null) {
                if (other.url != null) {
                    return false;
                }
            } else if (!url.equals(other.url)) {
                return false;
            }
            return true;
        }

    }

}
