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

package jp.sf.fess.screenshot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import jp.sf.fess.Constants;
import jp.sf.fess.FessSystemException;

import org.apache.commons.io.FileUtils;
import org.seasar.framework.container.annotation.tiger.InitMethod;
import org.seasar.framework.util.StringUtil;
import org.seasar.robot.util.LruHashMap;
import org.seasar.struts.util.RequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScreenShotManager {
    private static final String DEFAULT_SCREENSHOT_DIR = "/WEB-INF/screenshots";

    private static final Logger logger = LoggerFactory
            .getLogger(ScreenShotManager.class);

    @Resource
    protected ServletContext application;

    public File baseDir;

    public int threadNum = 10;

    public String solrFieldName = "screenshot_s_s";

    public long shutdownTimeout = 5 * 60 * 1000; // 5min

    public int screenShotPathCacheSize = 10;

    private final List<ScreenShotGenerator> generatorList = new ArrayList<ScreenShotGenerator>();

    private ExecutorService executorService;

    @InitMethod
    public void init() {
        executorService = Executors.newFixedThreadPool(threadNum);

        if (baseDir == null) {
            final String path = application.getRealPath(DEFAULT_SCREENSHOT_DIR);
            if (StringUtil.isNotBlank(path)) {
                baseDir = new File(path);
            } else {
                baseDir = new File("." + DEFAULT_SCREENSHOT_DIR);
            }
            if (baseDir.mkdirs()) {
                logger.info("Created: " + baseDir.getAbsolutePath());
            }
        }
        if (!baseDir.isDirectory()) {
            throw new FessSystemException("Not found: "
                    + baseDir.getAbsolutePath());
        }

        if (logger.isDebugEnabled()) {
            logger.debug("ScreenShot Directory: " + baseDir.getAbsolutePath());
        }
    }

    public void generate(final Map<String, Object> docMap) {
        for (final ScreenShotGenerator generator : generatorList) {
            if (generator.isTarget(docMap)) {
                final String segment = (String) docMap.get("segment");
                final String url = (String) docMap.get("url");
                final String path = segment + "/" + generator.getPath(docMap);
                docMap.put(solrFieldName, path);
                executorService.execute(new GenerateTask(url, new File(baseDir,
                        path), generator));
                break;
            }
        }
    }

    public void storeRequest(final String queryId,
            final List<Map<String, Object>> documentItems) {
        final Map<String, String> dataMap = new HashMap<String, String>(
                documentItems.size());
        for (final Map<String, Object> docMap : documentItems) {
            final String url = (String) docMap.get("url");
            final String screenShotPath = (String) docMap.get(solrFieldName);
            if (StringUtil.isNotBlank(url)
                    && StringUtil.isNotBlank(screenShotPath)) {
                dataMap.put(url, screenShotPath);
            }
        }
        final Map<String, Map<String, String>> screenShotPathCache = getScreenShotPathCache(RequestUtil
                .getRequest().getSession());
        screenShotPathCache.put(queryId, dataMap);
    }

    public File getScreenShotFile(final String queryId, final String url) {
        final HttpSession session = RequestUtil.getRequest().getSession(false);
        if (session != null) {
            final Map<String, Map<String, String>> screenShotPathCache = getScreenShotPathCache(session);
            final Map<String, String> dataMap = screenShotPathCache
                    .get(queryId);
            if (dataMap != null) {
                final String path = dataMap.get(url);
                final File file = new File(baseDir, path);
                if (file.isFile()) {
                    return file;
                }
            }
        }
        return null;
    }

    private Map<String, Map<String, String>> getScreenShotPathCache(
            final HttpSession session) {
        Map<String, Map<String, String>> screenShotPathCache = (Map<String, Map<String, String>>) session
                .getAttribute(Constants.SCREEN_SHOT_PATH_CACHE);
        if (screenShotPathCache == null) {
            screenShotPathCache = new LruHashMap<String, Map<String, String>>(
                    screenShotPathCacheSize);
            session.setAttribute(Constants.SCREEN_SHOT_PATH_CACHE,
                    screenShotPathCache);
        }
        return screenShotPathCache;
    }

    public void delete(final String expiredSessionId) {
        final File screenShotDir = new File(baseDir, expiredSessionId);
        if (screenShotDir.isDirectory()) {
            logger.info("Deleted: " + screenShotDir.getAbsolutePath());
            try {
                FileUtils.deleteDirectory(screenShotDir);
            } catch (final IOException e) {
                logger.warn(
                        "Failed to delete " + screenShotDir.getAbsolutePath(),
                        e);
            }
        }
    }

    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(shutdownTimeout,
                    TimeUnit.MILLISECONDS)) {
                logger.warn("Forced to  shutdown processes. Modify shutdownTimeout if needed.");
                executorService.shutdownNow();
            }
        } catch (final InterruptedException e) {
            logger.warn("Interrupted shutdown processes.", e);
            executorService.shutdownNow();
        }
    }

    public void add(final ScreenShotGenerator generator) {
        generatorList.add(generator);
    }

    protected static class GenerateTask implements Runnable {
        String url;

        File outputFile;

        ScreenShotGenerator generator;

        protected GenerateTask(final String url, final File outputFile,
                final ScreenShotGenerator generator) {
            this.url = url;
            this.outputFile = outputFile;
            this.generator = generator;
        }

        @Override
        public void run() {
            generator.generate(url, outputFile);
        }
    }

}
