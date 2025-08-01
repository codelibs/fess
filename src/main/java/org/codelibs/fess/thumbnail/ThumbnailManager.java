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
package org.codelibs.fess.thumbnail;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.lang.ThreadUtil;
import org.codelibs.core.misc.Tuple3;
import org.codelibs.fess.Constants;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.exception.JobProcessingException;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.opensearch.config.exbhv.ThumbnailQueueBhv;
import org.codelibs.fess.opensearch.config.exentity.ThumbnailQueue;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocumentUtil;
import org.codelibs.fess.util.ResourceUtil;
import org.opensearch.index.query.QueryBuilders;

import com.google.common.collect.Lists;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * Manager class for handling thumbnail generation and management.
 * Provides functionality to generate, cache, and serve thumbnail images for documents.
 */
public class ThumbnailManager {
    private static final String NOIMAGE_FILE_SUFFIX = ".txt";

    /**
     * Default directory name for thumbnails.
     */
    protected static final String THUMBNAILS_DIR_NAME = "thumbnails";

    private static final Logger logger = LogManager.getLogger(ThumbnailManager.class);

    /**
     * Base directory for storing thumbnail files.
     */
    protected File baseDir;

    private final List<ThumbnailGenerator> generatorList = new ArrayList<>();

    private BlockingQueue<Tuple3<String, String, String>> thumbnailTaskQueue;

    private volatile boolean generating;

    private Thread thumbnailQueueThread;

    /**
     * Size of the thumbnail path cache.
     */
    protected int thumbnailPathCacheSize = 10;

    /**
     * File extension for generated thumbnail images.
     */
    protected String imageExtention = "png";

    /**
     * Number of subdirectories for organizing thumbnails.
     */
    protected int splitSize = 10;

    /**
     * Maximum size of the thumbnail generation task queue.
     */
    protected int thumbnailTaskQueueSize = 10000;

    /**
     * Number of tasks to process in bulk operations.
     */
    protected int thumbnailTaskBulkSize = 100;

    /**
     * Timeout in milliseconds for thumbnail task queue operations.
     */
    protected long thumbnailTaskQueueTimeout = 10 * 1000L;

    /**
     * Expiration time in milliseconds for no-image placeholder files.
     */
    protected long noImageExpired = 24 * 60 * 60 * 1000L; // 24 hours

    /**
     * Hash size for splitting thumbnail storage directories.
     */
    protected int splitHashSize = 10;

    /**
     * Default constructor for ThumbnailManager.
     */
    public ThumbnailManager() {
        // Default constructor
    }

    /**
     * Initializes the thumbnail manager after construction.
     * Sets up base directory and starts background processing.
     */
    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initialize {}", this.getClass().getSimpleName());
        }
        final String thumbnailPath = System.getProperty(Constants.FESS_THUMBNAIL_PATH);
        if (thumbnailPath != null) {
            baseDir = new File(thumbnailPath);
        } else {
            final String varPath = System.getProperty(Constants.FESS_VAR_PATH);
            if (varPath != null) {
                baseDir = new File(varPath, THUMBNAILS_DIR_NAME);
            } else {
                baseDir = ResourceUtil.getThumbnailPath().toFile();
            }
        }
        if (baseDir.mkdirs()) {
            logger.info("Created: {}", baseDir.getAbsolutePath());
        }
        if (!baseDir.isDirectory()) {
            throw new FessSystemException("Not found: " + baseDir.getAbsolutePath());
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Thumbnail Directory: {}", baseDir.getAbsolutePath());
        }

        thumbnailTaskQueue = new LinkedBlockingQueue<>(thumbnailTaskQueueSize);
        generating = !Constants.TRUE.equalsIgnoreCase(System.getProperty("fess.thumbnail.process"));
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
                    if (generating && logger.isDebugEnabled()) {
                        logger.debug("Interupted task.", e);
                    }
                } catch (final Exception e) {
                    if (generating) {
                        logger.warn("Failed to generate thumbnail.", e);
                    }
                }
            }
            if (!taskList.isEmpty()) {
                storeQueue(taskList);
            }
        }, "ThumbnailGenerator");
        thumbnailQueueThread.start();
    }

    /**
     * Cleans up resources when the thumbnail manager is destroyed.
     * Stops background processing and waits for threads to complete.
     */
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

    /**
     * Gets the system property option string for thumbnail path configuration.
     *
     * @return the property option string for JVM arguments
     */
    public String getThumbnailPathOption() {
        return "-D" + Constants.FESS_THUMBNAIL_PATH + "=" + baseDir.getAbsolutePath();
    }

    /**
     * Stores thumbnail generation tasks to the queue for processing.
     *
     * @param taskList list of thumbnail tasks to store
     */
    protected void storeQueue(final List<Tuple3<String, String, String>> taskList) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final String[] targets = fessConfig.getThumbnailGeneratorTargetsAsArray();
        final List<ThumbnailQueue> list = new ArrayList<>();
        taskList.stream().filter(entity -> entity != null).forEach(task -> {
            for (final String target : targets) {
                final ThumbnailQueue entity = new ThumbnailQueue();
                entity.setGenerator(task.getValue1());
                entity.setThumbnailId(task.getValue2());
                entity.setPath(task.getValue3());
                entity.setTarget(target);
                entity.setCreatedBy(Constants.SYSTEM_USER);
                entity.setCreatedTime(systemHelper.getCurrentTimeAsLong());
                list.add(entity);
            }
        });
        taskList.clear();
        if (logger.isDebugEnabled()) {
            logger.debug("Storing {} thumbnail tasks.", list.size());
        }
        final ThumbnailQueueBhv thumbnailQueueBhv = ComponentUtil.getComponent(ThumbnailQueueBhv.class);
        thumbnailQueueBhv.batchInsert(list);
    }

    /**
     * Generates thumbnails using the provided executor service.
     *
     * @param executorService the executor service for parallel processing
     * @param cleanup whether to run in cleanup mode
     * @return the number of tasks processed
     */
    public int generate(final ExecutorService executorService, final boolean cleanup) {
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
        }).stream().map(entity -> {
            idList.add(entity.getId());
            if (!cleanup) {
                return executorService.submit(() -> process(fessConfig, entity));
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Removing thumbnail queue: {}", entity);
            }
            return null;
        }).filter(f -> f != null).forEach(f -> {
            try {
                f.get();
            } catch (final Exception e) {
                logger.warn("Failed to process a thumbnail generation.", e);
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

    /**
     * Processes a single thumbnail generation task.
     *
     * @param fessConfig the Fess configuration
     * @param entity the thumbnail queue entity to process
     */
    protected void process(final FessConfig fessConfig, final ThumbnailQueue entity) {
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        systemHelper.calibrateCpuLoad();

        if (logger.isDebugEnabled()) {
            logger.debug("Processing thumbnail: {}", entity);
        }
        final String generatorName = entity.getGenerator();
        try {
            final File outputFile = new File(baseDir, entity.getPath());
            final File noImageFile = new File(outputFile.getAbsolutePath() + NOIMAGE_FILE_SUFFIX);
            if (!noImageFile.isFile() || systemHelper.getCurrentTimeAsLong() - noImageFile.lastModified() > noImageExpired) {
                if (noImageFile.isFile() && !noImageFile.delete()) {
                    logger.warn("Failed to delete {}", noImageFile.getAbsolutePath());
                }
                final ThumbnailGenerator generator = ComponentUtil.getComponent(generatorName);
                if (generator.isAvailable()) {
                    if (!generator.generate(entity.getThumbnailId(), outputFile)) {
                        new File(outputFile.getAbsolutePath() + NOIMAGE_FILE_SUFFIX).setLastModified(systemHelper.getCurrentTimeAsLong());
                    } else {
                        final long interval = fessConfig.getThumbnailGeneratorIntervalAsInteger().longValue();
                        if (interval > 0) {
                            ThreadUtil.sleep(interval);
                        }
                    }
                } else {
                    logger.warn("{} is not available.", generatorName);
                }
            } else if (logger.isDebugEnabled()) {
                logger.debug("No image file exists: {}", noImageFile.getAbsolutePath());
            }
        } catch (final Exception e) {
            logger.warn("Failed to create thumbnail for {}", entity, e);
        }
    }

    /**
     * Offers a document for thumbnail generation.
     *
     * @param docMap the document data map
     * @return true if the task was successfully added to the queue
     */
    public boolean offer(final Map<String, Object> docMap) {
        for (final ThumbnailGenerator generator : generatorList) {
            if (generator.isTarget(docMap)) {
                final String path = getImageFilename(docMap);
                final Tuple3<String, String, String> task = generator.createTask(path, docMap);
                if (task != null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Add thumbnail task: {}", task);
                    }
                    if (!thumbnailTaskQueue.offer(task)) {
                        logger.warn("Failed to add thumbnail task: {}", task);
                    }
                    return true;
                }
                return false;
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Thumbnail generator is not found: {}", docMap != null ? docMap.get("url") : docMap);
        }
        return false;
    }

    /**
     * Gets the image filename for a document based on its document map.
     *
     * @param docMap the document data map
     * @return the generated image filename
     */
    protected String getImageFilename(final Map<String, Object> docMap) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String docid = DocumentUtil.getValue(docMap, fessConfig.getIndexFieldDocId(), String.class);
        return getImageFilename(docid);
    }

    /**
     * Gets the image filename for a document based on its document ID.
     *
     * @param docid the document ID
     * @return the generated image filename
     */
    protected String getImageFilename(final String docid) {
        final StringBuilder buf = new StringBuilder(50);
        for (int i = 0; i < docid.length(); i += splitSize) {
            int hash = docid.substring(i).hashCode() % splitHashSize;
            if (hash < 0) {
                hash *= -1;
            }
            buf.append('_').append(Integer.toString(hash)).append('/');
        }
        buf.append(docid).append('.').append(imageExtention);
        return buf.toString();
    }

    /**
     * Gets the thumbnail file for a document if it exists.
     *
     * @param docMap the document data map
     * @return the thumbnail file or null if not found
     */
    public File getThumbnailFile(final Map<String, Object> docMap) {
        final String thumbnailPath = getImageFilename(docMap);
        if (StringUtil.isNotBlank(thumbnailPath)) {
            final File file = new File(baseDir, thumbnailPath);
            if (file.isFile()) {
                return file;
            }
        }
        return null;
    }

    /**
     * Adds a thumbnail generator to the manager.
     *
     * @param generator the thumbnail generator to add
     */
    public void add(final ThumbnailGenerator generator) {
        if (generator.isAvailable()) {
            if (logger.isDebugEnabled()) {
                logger.debug("{} is available.", generator.getName());
            }
            generatorList.add(generator);
        } else if (logger.isDebugEnabled()) {
            logger.debug("{} is not available.", generator.getName());
        }
    }

    /**
     * Purges old thumbnail files based on the expiry time.
     *
     * @param expiry the expiry time threshold
     * @return the number of files purged
     */
    public long purge(final long expiry) {
        if (!baseDir.exists()) {
            return 0;
        }
        try {
            final FilePurgeVisitor visitor = new FilePurgeVisitor(baseDir.toPath(), imageExtention, expiry);
            Files.walkFileTree(baseDir.toPath(), visitor);
            return visitor.getCount();
        } catch (final Exception e) {
            throw new JobProcessingException(e);
        }
    }

    /**
     * File visitor for purging old thumbnail files.
     */
    protected static class FilePurgeVisitor implements FileVisitor<Path> {

        /**
         * Expiry time threshold for file deletion.
         */
        protected final long expiry;

        /**
         * Count of processed files.
         */
        protected long count;

        /**
         * Maximum number of files to purge in a single operation.
         */
        protected final int maxPurgeSize;

        /**
         * List of files marked for deletion.
         */
        protected final List<Path> deletedFileList = new ArrayList<>();

        /**
         * Base path for thumbnail storage.
         */
        protected final Path basePath;

        /**
         * Image file extension for filtering.
         */
        protected final String imageExtention;

        /**
         * OpenSearch client for document validation.
         */
        protected final SearchEngineClient searchEngineClient;

        /**
         * Fess configuration for settings.
         */
        protected final FessConfig fessConfig;

        FilePurgeVisitor(final Path basePath, final String imageExtention, final long expiry) {
            this.basePath = basePath;
            this.imageExtention = imageExtention;
            this.expiry = expiry;
            fessConfig = ComponentUtil.getFessConfig();
            maxPurgeSize = fessConfig.getPageThumbnailPurgeMaxFetchSizeAsInteger();
            searchEngineClient = ComponentUtil.getSearchEngineClient();
        }

        /**
         * Deletes files marked for deletion after checking if they exist in the search index.
         */
        protected void deleteFiles() {
            final Map<String, Path> deleteFileMap = new HashMap<>();
            for (final Path path : deletedFileList) {
                final String docId = getDocId(path);
                if (StringUtil.isBlank(docId) || deleteFileMap.containsKey(docId)) {
                    deleteFile(path);
                } else {
                    deleteFileMap.put(docId, path);
                }
            }
            deletedFileList.clear();

            if (!deleteFileMap.isEmpty()) {
                final String docIdField = fessConfig.getIndexFieldDocId();
                searchEngineClient.getDocumentList(fessConfig.getIndexDocumentSearchIndex(), searchRequestBuilder -> {
                    searchRequestBuilder.setQuery(
                            QueryBuilders.termsQuery(docIdField, deleteFileMap.keySet().toArray(new String[deleteFileMap.size()])));
                    searchRequestBuilder.setFetchSource(new String[] { docIdField }, StringUtil.EMPTY_STRINGS);
                    return true;
                }).forEach(m -> {
                    final Object docId = m.get(docIdField);
                    if (docId != null) {
                        deleteFileMap.remove(docId);
                        if (logger.isDebugEnabled()) {
                            logger.debug("Keep thumbnail: {}", docId);
                        }
                    }
                });

                deleteFileMap.values().forEach(this::deleteFile);
                count += deleteFileMap.size();
            }
        }

        /**
         * Deletes a single file and any empty parent directories.
         *
         * @param path the file path to delete
         */
        protected void deleteFile(final Path path) {
            try {
                Files.delete(path);
                if (logger.isDebugEnabled()) {
                    logger.debug("Delete {}", path);
                }

                Path parent = path.getParent();
                while (deleteEmptyDirectory(parent)) {
                    parent = parent.getParent();
                }
            } catch (final IOException e) {
                logger.warn("Failed to delete {}", path, e);
            }
        }

        /**
         * Extracts the document ID from a file path.
         *
         * @param file the file path
         * @return the extracted document ID
         */
        protected String getDocId(final Path file) {
            final String s = file.toUri().toString();
            final String b = basePath.toUri().toString();
            final String id = s.replace(b, StringUtil.EMPTY).replace("." + imageExtention, StringUtil.EMPTY).replace("/", StringUtil.EMPTY);
            if (logger.isDebugEnabled()) {
                logger.debug("Base: {} File: {} DocId: {}", b, s, id);
            }
            return id;
        }

        /**
         * Gets the count of processed files.
         *
         * @return the number of files processed
         */
        public long getCount() {
            if (!deletedFileList.isEmpty()) {
                deleteFiles();
            }
            return count;
        }

        @Override
        public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
            if (ComponentUtil.getSystemHelper().getCurrentTimeAsLong() - Files.getLastModifiedTime(file).toMillis() > expiry) {
                deletedFileList.add(file);
                if (deletedFileList.size() > maxPurgeSize) {
                    deleteFiles();
                }
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(final Path file, final IOException e) throws IOException {
            if (e != null) {
                logger.warn("I/O exception on {}", file, e);
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(final Path dir, final IOException e) throws IOException {
            if (e != null) {
                logger.warn("I/O exception on {}", dir, e);
            }
            deleteEmptyDirectory(dir);
            return FileVisitResult.CONTINUE;
        }

        private boolean deleteEmptyDirectory(final Path dir) throws IOException {
            if (dir == null) {
                return false;
            }
            final File directory = dir.toFile();
            if (directory.list() != null && directory.list().length == 0 && !THUMBNAILS_DIR_NAME.equals(directory.getName())) {
                Files.delete(dir);
                if (logger.isDebugEnabled()) {
                    logger.debug("Delete {}", dir);
                }
                return true;
            }
            return false;
        }

    }

    /**
     * Migrates existing thumbnail files to the new directory structure.
     */
    public void migrate() {
        new Thread(() -> {
            final Path basePath = baseDir.toPath();
            final String suffix = "." + imageExtention;
            try (Stream<Path> paths = Files.walk(basePath)) {
                paths.filter(path -> path.toFile().getName().endsWith(imageExtention)).forEach(path -> {
                    final Path subPath = basePath.relativize(path);
                    final String docId = subPath.toString().replace("/", StringUtil.EMPTY).replace(suffix, StringUtil.EMPTY);
                    if (!docId.startsWith("_")) {
                        final String filename = getImageFilename(docId);
                        final Path newPath = basePath.resolve(filename);
                        if (!path.equals(newPath)) {
                            try {
                                try {
                                    Files.createDirectories(newPath.getParent());
                                } catch (final FileAlreadyExistsException e) {
                                    // ignore
                                }
                                Files.move(path, newPath);
                                logger.info("Move {} to {}", path, newPath);
                            } catch (final IOException e) {
                                logger.warn("Failed to move {}", path, e);
                            }
                        }
                    }
                });
            } catch (final IOException e) {
                logger.warn("Failed to migrate thumbnail images.", e);
            }
        }, "ThumbnailMigrator").start();
    }

    /**
     * Sets the thumbnail path cache size.
     *
     * @param thumbnailPathCacheSize the cache size to set
     */
    public void setThumbnailPathCacheSize(final int thumbnailPathCacheSize) {
        this.thumbnailPathCacheSize = thumbnailPathCacheSize;
    }

    /**
     * Sets the image file extension for thumbnails.
     *
     * @param imageExtention the file extension to set
     */
    public void setImageExtention(final String imageExtention) {
        this.imageExtention = imageExtention;
    }

    /**
     * Sets the split size for directory organization.
     *
     * @param splitSize the split size to set
     */
    public void setSplitSize(final int splitSize) {
        this.splitSize = splitSize;
    }

    /**
     * Sets the maximum size of the thumbnail task queue.
     *
     * @param thumbnailTaskQueueSize the queue size to set
     */
    public void setThumbnailTaskQueueSize(final int thumbnailTaskQueueSize) {
        this.thumbnailTaskQueueSize = thumbnailTaskQueueSize;
    }

    /**
     * Sets the expiration time for no-image placeholder files.
     *
     * @param noImageExpired the expiration time in milliseconds
     */
    public void setNoImageExpired(final long noImageExpired) {
        this.noImageExpired = noImageExpired;
    }

    /**
     * Sets the hash size for splitting thumbnail storage directories.
     *
     * @param splitHashSize the hash size to set
     */
    public void setSplitHashSize(final int splitHashSize) {
        this.splitHashSize = splitHashSize;
    }

}
