/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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
package org.codelibs.fess.thumbnail;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpSession;

import org.codelibs.core.collection.LruHashMap;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.Tuple3;
import org.codelibs.fess.Constants;
import org.codelibs.fess.es.config.exbhv.ThumbnailQueueBhv;
import org.codelibs.fess.es.config.exentity.ThumbnailQueue;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.exception.JobProcessingException;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocumentUtil;
import org.codelibs.fess.util.ResourceUtil;
import org.lastaflute.web.util.LaRequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class ThumbnailManager {
    private static final String FESS_THUMBNAIL_PATH = "fess.thumbnail.path";

    private static final String FESS_VAR_PATH = "fess.var.path";

    private static final String NOIMAGE_FILE_SUFFIX = ".txt";

    private static final Logger logger = LoggerFactory.getLogger(ThumbnailManager.class);

    protected File baseDir;

    private final List<ThumbnailGenerator> generatorList = new ArrayList<>();

    private BlockingQueue<Tuple3<String, String, String>> thumbnailTaskQueue;

    private volatile boolean generating;

    private Thread thumbnailQueueThread;

    protected int thumbnailPathCacheSize = 10;

    protected String imageExtention = "png";

    protected int splitSize = 5;

    protected int thumbnailTaskQueueSize = 10000;

    protected int thumbnailTaskBulkSize = 100;

    protected long thumbnailTaskQueueTimeout = 60 * 1000L;

    protected long noImageExpired = 24 * 60 * 60 * 1000L; // 24 hours

    @PostConstruct
    public void init() {
        final String thumbnailPath = System.getProperty(FESS_THUMBNAIL_PATH);
        if (thumbnailPath != null) {
            baseDir = new File(thumbnailPath);
        } else {
            final String varPath = System.getProperty(FESS_VAR_PATH);
            if (varPath != null) {
                baseDir = new File(varPath, "thumbnails");
            } else {
                baseDir = ResourceUtil.getThumbnailPath().toFile();
            }
        }
        if (baseDir.mkdirs()) {
            logger.info("Created: " + baseDir.getAbsolutePath());
        }
        if (!baseDir.isDirectory()) {
            throw new FessSystemException("Not found: " + baseDir.getAbsolutePath());
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Thumbnail Directory: " + baseDir.getAbsolutePath());
        }

        thumbnailTaskQueue = new LinkedBlockingQueue<>(thumbnailTaskQueueSize);
        generating = true;
        thumbnailQueueThread = new Thread((Runnable) () -> {
            final List<Tuple3<String, String, String>> taskList = new ArrayList<>();
            while (generating) {
                try {
                    final Tuple3<String, String, String> task = thumbnailTaskQueue.poll(thumbnailTaskQueueTimeout, TimeUnit.MILLISECONDS);
                    if (task == null) {
                        if (!taskList.isEmpty()) {
                            storeQueue(taskList);
                        }
                    } else if (!taskList.contains(task)) {
                        taskList.add(task);
                        if (taskList.size() > thumbnailTaskBulkSize) {
                            storeQueue(taskList);
                        }
                    }
                } catch (final InterruptedException e) {
                    logger.debug("Interupted task.", e);
                } catch (final Exception e) {
                    if (generating) {
                        logger.warn("Failed to generage a thumbnail.", e);
                    }
                }
            }
        }, "ThumbnailGenerator");
        thumbnailQueueThread.start();
    }

    @PreDestroy
    public void destroy() {
        generating = false;
        thumbnailQueueThread.interrupt();
        try {
            thumbnailQueueThread.join(10000);
        } catch (final InterruptedException e) {
            logger.warn("Thumbnail thread is timeouted.", e);
        }
        generatorList.forEach(g -> {
            try {
                g.destroy();
            } catch (final Exception e) {
                logger.warn("Failed to stop thumbnail generator.", e);
            }
        });
    }

    public String getThumbnailPathOption() {
        return "-D" + FESS_THUMBNAIL_PATH + "=" + baseDir.getAbsolutePath();
    }

    protected void storeQueue(final List<Tuple3<String, String, String>> taskList) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final String[] targets = fessConfig.getThumbnailGeneratorTargetsAsArray();
        final List<ThumbnailQueue> list = new ArrayList<>();
        taskList.stream().filter(entity -> entity != null).forEach(task -> {
            for (final String target : targets) {
                final ThumbnailQueue entity = new ThumbnailQueue();
                entity.setGenerator(task.getValue1());
                entity.setUrl(task.getValue2());
                entity.setPath(task.getValue3());
                entity.setTarget(target);
                entity.setCreatedBy(Constants.SYSTEM_USER);
                entity.setCreatedTime(systemHelper.getCurrentTimeAsLong());
                list.add(entity);
            }
        });
        taskList.clear();
        final ThumbnailQueueBhv thumbnailQueueBhv = ComponentUtil.getComponent(ThumbnailQueueBhv.class);
        thumbnailQueueBhv.batchInsert(list);
    }

    public int generate() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final List<String> idList = new ArrayList<>();
        final ThumbnailQueueBhv thumbnailQueueBhv = ComponentUtil.getComponent(ThumbnailQueueBhv.class);
        thumbnailQueueBhv.selectList(cb -> {
            if (StringUtil.isBlank(fessConfig.getSchedulerTargetName())) {
                cb.query().setTarget_Equal(Constants.DEFAULT_JOB_TARGET);
            } else {
                cb.query().setTarget_InScope(Lists.newArrayList(Constants.DEFAULT_JOB_TARGET, fessConfig.getSchedulerTargetName()));
            }
            cb.query().addOrderBy_CreatedTime_Asc();
            cb.fetchFirst(fessConfig.getPageThumbnailQueueMaxFetchSizeAsInteger());
        }).forEach(entity -> {
            idList.add(entity.getId());
            final String generatorName = entity.getGenerator();
            try {
                final ThumbnailGenerator generator = ComponentUtil.getComponent(generatorName);
                final File outputFile = new File(baseDir, entity.getPath());
                final File noImageFile = new File(outputFile.getAbsolutePath() + NOIMAGE_FILE_SUFFIX);
                if (!noImageFile.isFile() || System.currentTimeMillis() - noImageFile.lastModified() > noImageExpired) {
                    if (noImageFile.isFile() && !noImageFile.delete()) {
                        logger.warn("Failed to delete " + noImageFile.getAbsolutePath());
                    }
                    if (!generator.generate(entity.getUrl(), outputFile)) {
                        new File(outputFile.getAbsolutePath() + NOIMAGE_FILE_SUFFIX).setLastModified(System.currentTimeMillis());
                    }
                } else if (logger.isDebugEnabled()) {
                    logger.debug("No image file exists: " + noImageFile.getAbsolutePath());
                }
            } catch (final Exception e) {
                logger.warn("Failed to create thumbnail for " + entity, e);
            }
        });
        if (!idList.isEmpty()) {
            thumbnailQueueBhv.queryDelete(cb -> {
                cb.query().setId_InScope(idList);
            });
            thumbnailQueueBhv.refresh();
        }
        return idList.size();
    }

    public void offer(final Map<String, Object> docMap) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        for (final ThumbnailGenerator generator : generatorList) {
            if (generator.isTarget(docMap)) {
                final String url = DocumentUtil.getValue(docMap, fessConfig.getIndexFieldUrl(), String.class);
                final String path = getImageFilename(docMap);
                final Tuple3<String, String, String> task = new Tuple3<>(generator.getName(), url, path);
                thumbnailTaskQueue.offer(task);
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
        final Map<String, String> dataMap = new HashMap<>(documentItems.size());
        for (final Map<String, Object> docMap : documentItems) {
            final String docid = (String) docMap.get(fessConfig.getIndexFieldDocId());
            final String thumbnailPath = getImageFilename(docMap);
            if (StringUtil.isNotBlank(docid) && StringUtil.isNotBlank(thumbnailPath)) {
                dataMap.put(docid, thumbnailPath);
            }
        }
        final Map<String, Map<String, String>> thumbnailPathCache = getThumbnailPathCache(LaRequestUtil.getRequest().getSession());
        thumbnailPathCache.put(queryId, dataMap);
    }

    public File getThumbnailFile(final String queryId, final String docId) {
        final HttpSession session = LaRequestUtil.getRequest().getSession(false);
        if (session != null) {
            final Map<String, Map<String, String>> thumbnailPathCache = getThumbnailPathCache(session);
            final Map<String, String> dataMap = thumbnailPathCache.get(queryId);
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

    private Map<String, Map<String, String>> getThumbnailPathCache(final HttpSession session) {
        @SuppressWarnings("unchecked")
        Map<String, Map<String, String>> thumbnailPathCache =
                (Map<String, Map<String, String>>) session.getAttribute(Constants.SCREEN_SHOT_PATH_CACHE);
        if (thumbnailPathCache == null) {
            thumbnailPathCache = new LruHashMap<>(thumbnailPathCacheSize);
            session.setAttribute(Constants.SCREEN_SHOT_PATH_CACHE, thumbnailPathCache);
        }
        return thumbnailPathCache;
    }

    public void add(final ThumbnailGenerator generator) {
        if (generator.isAvailable()) {
            generatorList.add(generator);
        }
    }

    public long purge(final long expiry) {
        if (!baseDir.exists()) {
            return 0;
        }
        try {
            final FilePurgeVisitor visitor = new FilePurgeVisitor(expiry);
            Files.walkFileTree(baseDir.toPath(), visitor);
            return visitor.getCount();
        } catch (final Exception e) {
            throw new JobProcessingException(e);
        }
    }

    private static class FilePurgeVisitor implements FileVisitor<Path> {

        private final long expiry;

        private long count;

        FilePurgeVisitor(final long expiry) {
            this.expiry = expiry;
        }

        public long getCount() {
            return count;
        }

        @Override
        public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
            if (System.currentTimeMillis() - Files.getLastModifiedTime(file).toMillis() > expiry) {
                Files.delete(file);
                count++;
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(final Path file, final IOException e) throws IOException {
            if (e != null) {
                logger.warn("I/O exception on " + file, e);
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(final Path dir, final IOException e) throws IOException {
            if (e != null) {
                logger.warn("I/O exception on " + dir, e);
            }
            if (dir.toFile().list().length == 0) {
                Files.delete(dir);
            }
            return FileVisitResult.CONTINUE;
        }

    }

    public void setThumbnailPathCacheSize(final int thumbnailPathCacheSize) {
        this.thumbnailPathCacheSize = thumbnailPathCacheSize;
    }

    public void setImageExtention(final String imageExtention) {
        this.imageExtention = imageExtention;
    }

    public void setSplitSize(final int splitSize) {
        this.splitSize = splitSize;
    }

    public void setThumbnailTaskQueueSize(final int thumbnailTaskQueueSize) {
        this.thumbnailTaskQueueSize = thumbnailTaskQueueSize;
    }

    public void setNoImageExpired(final long noImageExpired) {
        this.noImageExpired = noImageExpired;
    }

}
