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
package org.codelibs.fess.app.web.admin.storage;

import static org.codelibs.core.stream.StreamUtil.split;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.exception.StorageException;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.RenderDataUtil;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.StreamResponse;
import org.lastaflute.web.ruts.multipart.MultipartFormFile;
import org.lastaflute.web.ruts.process.ActionRuntime;
import org.lastaflute.web.servlet.request.stream.WrittenStreamOut;

import io.minio.GetObjectArgs;
import io.minio.GetObjectTagsArgs;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.SetObjectTagsArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.messages.Item;

/**
 * Admin action for Storage management.
 *
 */
public class AdminStorageAction extends FessAdminAction {

    /**
     * Default constructor.
     */
    public AdminStorageAction() {
        super();
    }

    /** Role name for admin storage operations */
    public static final String ROLE = "admin-storage";

    private static final Logger logger = LogManager.getLogger(AdminStorageAction.class);

    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameStorage()));
    }

    @Override
    protected String getActionRole() {
        return ROLE;
    }

    /**
     * Displays the storage management index page.
     *
     * @return HTML response for the storage list page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse index() {
        saveToken();
        return asListHtml(StringUtil.EMPTY);
    }

    /**
     * Displays a list of files and directories in the specified path.
     *
     * @param id the encoded path ID to list (optional)
     * @return action response with the storage list or redirect
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public ActionResponse list(final OptionalThing<String> id) {
        saveToken();
        return id.filter(StringUtil::isNotBlank).map(s -> asListHtml(decodePath(s))).orElse(redirect(getClass()));
    }

    /**
     * Uploads a file to the storage system.
     *
     * @param form the item form containing file and path information
     * @return HTML response redirecting to the storage list after upload
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse upload(final ItemForm form) {
        validate(form, messages -> {}, () -> asListHtml(form.path));
        if (form.uploadFile == null) {
            throwValidationError(messages -> messages.addErrorsStorageNoUploadFile(GLOBAL), () -> asListHtml(form.path));
        }
        verifyToken(() -> asListHtml(form.path));
        try {
            uploadObject(getObjectName(form.path, form.uploadFile.getFileName()), form.uploadFile);
        } catch (final StorageException e) {
            logger.warn("Failed to upload {}", form.uploadFile.getFileName(), e);
            throwValidationError(messages -> messages.addErrorsStorageFileUploadFailure(GLOBAL, form.uploadFile.getFileName()),
                    () -> asListHtml(encodeId(form.path)));

        }
        saveInfo(messages -> messages.addSuccessUploadFileToStorage(GLOBAL, form.uploadFile.getFileName()));
        return redirectWith(getClass(), moreUrl("list/" + encodeId(form.path)));
    }

    /**
     * Downloads a file from the storage system.
     *
     * @param id the encoded ID of the file to download
     * @return action response with the file stream for download
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public ActionResponse download(final String id) {
        final PathInfo pi = convertToItem(id);
        if (StringUtil.isEmpty(pi.getName())) {
            throwValidationError(messages -> messages.addErrorsStorageFileNotFound(GLOBAL), () -> asListHtml(encodeId(pi.getPath())));
        }
        final StreamResponse response = new StreamResponse(StringUtil.EMPTY);
        final String name = pi.getName();
        final String encodedName = URLEncoder.encode(name, Constants.UTF_8_CHARSET).replace("+", "%20");
        response.header("Content-Disposition", "attachment; filename=\"" + name + "\"; filename*=utf-8''" + encodedName);
        response.header("Pragma", "no-cache");
        response.header("Cache-Control", "no-cache");
        response.header("Expires", "Thu, 01 Dec 1994 16:00:00 GMT");
        response.contentTypeOctetStream();
        return response.stream(out -> {
            try {
                downloadObject(getObjectName(pi.getPath(), pi.getName()), out);
            } catch (final StorageException e) {
                logger.warn("Failed to download {}", pi.getName(), e);
                throwValidationError(messages -> messages.addErrorsStorageFileDownloadFailure(GLOBAL, pi.getName()),
                        () -> asListHtml(encodeId(pi.getPath())));
            }
        });
    }

    /**
     * Deletes a file from the storage system.
     *
     * @param id the encoded ID of the file to delete
     * @return HTML response redirecting to the storage list after deletion
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse delete(final String id) {
        final PathInfo pi = convertToItem(id);
        if (StringUtil.isEmpty(pi.getName())) {
            throwValidationError(messages -> messages.addErrorsStorageFileNotFound(GLOBAL), () -> asListHtml(encodeId(pi.getPath())));
        }
        final String objectName = getObjectName(pi.getPath(), pi.getName());
        try {
            deleteObject(objectName);
        } catch (final StorageException e) {
            logger.warn("Failed to delete {}", pi.getName(), e);
            throwValidationError(messages -> messages.addErrorsFailedToDeleteFile(GLOBAL, pi.getName()),
                    () -> asListHtml(encodeId(pi.getPath())));
        }
        saveInfo(messages -> messages.addSuccessDeleteFile(GLOBAL, pi.getName()));
        return redirectWith(getClass(), moreUrl("list/" + encodeId(pi.getPath())));
    }

    /**
     * Creates a new directory in the storage system.
     *
     * @param form the item form containing directory information
     * @return HTML response redirecting to the new directory
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse createDir(final ItemForm form) {
        validate(form, messages -> {}, () -> asListHtml(form.path));
        if (StringUtil.isBlank(form.name)) {
            throwValidationError(messages -> messages.addErrorsStorageDirectoryNameIsInvalid(GLOBAL), () -> asListHtml(form.path));
        }
        return redirectWith(getClass(), moreUrl("list/" + encodeId(getObjectName(form.path, form.name))));
    }

    /**
     * Displays the form for editing object tags.
     *
     * @param form the tag form containing object information
     * @return HTML response for the tag editing form
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse editTags(final TagForm form) {
        validate(form, messages -> {}, () -> asEditTagsHtml(form.path, form.name));
        saveToken();
        return asEditTagsHtml(form.path, form.name);
    }

    /**
     * Updates the tags for a storage object.
     *
     * @param form the tag form containing updated tag information
     * @return HTML response redirecting to the storage list after update
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse updateTags(final TagForm form) {
        validate(form, messages -> {}, () -> asEditTagsHtml(form.path, form.name));
        final String objectName = getObjectName(form.path, form.name);
        try {
            updateObjectTags(objectName, form.tags);
        } catch (final StorageException e) {
            logger.warn("Failed to update tags in {}", form.path, e);
            throwValidationError(messages -> messages.addErrorsStorageTagsUpdateFailure(GLOBAL, objectName),
                    () -> asEditTagsHtml(form.path, form.name));
        }
        saveInfo(messages -> messages.addSuccessUpdateStorageTags(GLOBAL, objectName));
        return redirectWith(getClass(), moreUrl("list/" + encodeId(form.path)));
    }

    /**
     * Updates the tags for a storage object in the MinIO system.
     *
     * @param objectName the name of the object to update tags for
     * @param tagItems the map of tag items from the form
     * @throws StorageException if the tag update fails
     */
    public static void updateObjectTags(final String objectName, final Map<String, String> tagItems) {
        final Map<String, String> tags = new HashMap<>();
        tagItems.keySet().stream().filter(s -> s.startsWith("name")).forEach(nameKey -> {
            final String valueKey = nameKey.replace("name", "value");
            final String name = tagItems.get(nameKey);
            if (StringUtil.isNotBlank(name)) {
                tags.put(name, tagItems.get(valueKey));
            }
        });
        if (logger.isDebugEnabled()) {
            logger.debug("tags: {} -> {}", tagItems, tags);
        }
        try {
            final FessConfig fessConfig = ComponentUtil.getFessConfig();
            final SetObjectTagsArgs args =
                    SetObjectTagsArgs.builder().bucket(fessConfig.getStorageBucket()).object(objectName).tags(tags).build();
            createClient(fessConfig).setObjectTags(args);
        } catch (final Exception e) {
            throw new StorageException("Failed to update tags for " + objectName, e);
        }
    }

    /**
     * Retrieves the tags for a storage object from the MinIO system.
     *
     * @param objectName the name of the object to get tags for
     * @return map of tag key-value pairs
     * @throws StorageException if retrieving tags fails
     */
    public static Map<String, String> getObjectTags(final String objectName) {
        try {
            final FessConfig fessConfig = ComponentUtil.getFessConfig();
            final GetObjectTagsArgs args = GetObjectTagsArgs.builder().bucket(fessConfig.getStorageBucket()).object(objectName).build();
            return createClient(fessConfig).getObjectTags(args).get();
        } catch (final Exception e) {
            throw new StorageException("Failed to get tags from " + objectName, e);
        }
    }

    /**
     * Uploads a file to the MinIO storage system.
     *
     * @param objectName the name for the object in storage
     * @param uploadFile the multipart file to upload
     * @throws StorageException if the upload fails
     */
    public static void uploadObject(final String objectName, final MultipartFormFile uploadFile) {
        try (final InputStream in = uploadFile.getInputStream()) {
            final FessConfig fessConfig = ComponentUtil.getFessConfig();
            final MinioClient minioClient = createClient(fessConfig);
            final PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(fessConfig.getStorageBucket())
                    .object(objectName)
                    .stream(in, uploadFile.getFileSize(), -1)
                    .contentType("application/octet-stream")
                    .build();
            minioClient.putObject(args);
        } catch (final Exception e) {
            throw new StorageException("Failed to upload " + objectName, e);
        }
    }

    /**
     * Downloads an object from the MinIO storage system.
     *
     * @param objectName the name of the object to download
     * @param out the output stream to write the object data to
     * @throws StorageException if the download fails
     */
    public static void downloadObject(final String objectName, final WrittenStreamOut out) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final GetObjectArgs args = GetObjectArgs.builder().bucket(fessConfig.getStorageBucket()).object(objectName).build();
        try (InputStream in = createClient(fessConfig).getObject(args)) {
            out.write(in);
        } catch (final Exception e) {
            throw new StorageException("Failed to download " + objectName, e);
        }
    }

    /**
     * Deletes an object from the MinIO storage system.
     *
     * @param objectName the name of the object to delete
     * @throws StorageException if the deletion fails
     */
    public static void deleteObject(final String objectName) {
        try {
            final FessConfig fessConfig = ComponentUtil.getFessConfig();
            final MinioClient minioClient = createClient(fessConfig);
            final RemoveObjectArgs args = RemoveObjectArgs.builder().bucket(fessConfig.getStorageBucket()).object(objectName).build();
            minioClient.removeObject(args);
        } catch (final Exception e) {
            throw new StorageException("Failed to delete " + objectName, e);
        }
    }

    /**
     * Creates a MinIO client instance with the configured endpoint and credentials.
     *
     * @param fessConfig the Fess configuration containing storage settings
     * @return configured MinIO client
     * @throws StorageException if client creation fails
     */
    protected static MinioClient createClient(final FessConfig fessConfig) {
        try {
            return MinioClient.builder()
                    .endpoint(fessConfig.getStorageEndpoint())
                    .credentials(fessConfig.getStorageAccessKey(), fessConfig.getStorageSecretKey())
                    .build();
        } catch (final Exception e) {
            throw new StorageException("Failed to create MinioClient: " + fessConfig.getStorageEndpoint(), e);
        }
    }

    /**
     * Retrieves a list of files and directories from the storage system.
     *
     * @param prefix the path prefix to list objects under
     * @return list of file and directory information maps
     */
    public static List<Map<String, Object>> getFileItems(final String prefix) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final List<Map<String, Object>> list = new ArrayList<>();
        final List<Map<String, Object>> fileList = new ArrayList<>();
        try {
            final MinioClient minioClient = createClient(fessConfig);
            final ListObjectsArgs args = ListObjectsArgs.builder()
                    .bucket(fessConfig.getStorageBucket())
                    .prefix(prefix != null && prefix.length() > 0 ? prefix + "/" : prefix)
                    .recursive(false)
                    .includeUserMetadata(false)
                    .useApiVersion1(false)
                    .build();
            for (final Result<Item> result : minioClient.listObjects(args)) {
                final Map<String, Object> map = new HashMap<>();
                final Item item = result.get();
                final String objectName = item.objectName();
                map.put("id", encodeId(objectName));
                map.put("path", prefix);
                map.put("name", getName(objectName));
                map.put("hashCode", item.hashCode());
                map.put("size", item.size());
                map.put("directory", item.isDir());
                if (!item.isDir()) {
                    map.put("lastModified", item.lastModified());
                    fileList.add(map);
                } else {
                    list.add(map);
                }
                if (list.size() + fileList.size() > fessConfig.getStorageMaxItemsInPageAsInteger()) {
                    break;
                }
            }
        } catch (final ErrorResponseException e) {
            final String code = e.errorResponse().code();
            if ("NoSuchBucket".equals(code)) {
                final MinioClient minioClient = createClient(fessConfig);
                try {
                    final MakeBucketArgs args = MakeBucketArgs.builder().bucket(fessConfig.getStorageBucket()).build();
                    minioClient.makeBucket(args);
                    logger.info("Created bucket: {}", fessConfig.getStorageBucket());
                } catch (final Exception e1) {
                    logger.warn("Failed to create bucket: {}", fessConfig.getStorageBucket(), e1);
                }
            } else if (logger.isDebugEnabled()) {
                logger.debug("Failed to access {}", fessConfig.getStorageEndpoint(), e);
            }
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to access {}", fessConfig.getStorageEndpoint(), e);
            }
        }
        list.addAll(fileList);
        return list;
    }

    /**
     * Extracts the file name from a full object path.
     *
     * @param objectName the full object path
     * @return the file name portion of the path
     */
    private static String getName(final String objectName) {
        final String[] values = objectName.split("/");
        if (values.length == 0) {
            return StringUtil.EMPTY;
        }
        return values[values.length - 1];
    }

    /**
     * Decodes an encoded path ID back to the original path.
     *
     * @param id the encoded path ID
     * @return the decoded path string
     */
    public static String decodePath(final String id) {
        final PathInfo pi = convertToItem(id);
        if (StringUtil.isEmpty(pi.getPath()) && StringUtil.isEmpty(pi.getName())) {
            return StringUtil.EMPTY;
        }
        if (StringUtil.isEmpty(pi.getPath())) {
            return pi.getName();
        }
        return pi.getPath() + "/" + pi.getName();
    }

    /**
     * Converts an encoded ID to a PathInfo object containing path and name.
     *
     * @param id the encoded ID to convert
     * @return PathInfo object with separated path and name
     */
    public static PathInfo convertToItem(final String id) {
        final String value = decodeId(id);
        final String[] values = split(value, "/").get(stream -> stream.filter(StringUtil::isNotEmpty).toArray(n -> new String[n]));
        if (values.length == 0) {
            // invalid?
            return new PathInfo(StringUtil.EMPTY, StringUtil.EMPTY);
        }
        if (values.length == 1) {
            return new PathInfo(StringUtil.EMPTY, values[0]);
        }
        final StringBuilder buf = new StringBuilder();
        for (int i = 0; i < values.length - 1; i++) {
            if (buf.length() > 0) {
                buf.append('/');
            }
            buf.append(values[i]);
        }
        return new PathInfo(buf.toString(), values[values.length - 1]);
    }

    /**
     * Creates an encoded parent directory ID from a path prefix.
     *
     * @param prefix the current path prefix
     * @return encoded parent directory ID, or empty string if at root
     */
    protected static String createParentId(final String prefix) {
        if (prefix == null) {
            return StringUtil.EMPTY;
        }
        final String[] values = prefix.split("/");
        if (values.length > 1) {
            final StringBuilder buf = new StringBuilder();
            for (int i = 0; i < values.length - 1; i++) {
                if (buf.length() > 0) {
                    buf.append('/');
                }
                buf.append(values[i]);
            }
            return encodeId(buf.toString());
        }
        return StringUtil.EMPTY;
    }

    /**
     * Creates a list of path navigation items for breadcrumb display.
     *
     * @param prefix the current path prefix
     * @return list of path item maps for navigation
     */
    protected static List<Map<String, String>> createPathItems(final String prefix) {
        final List<Map<String, String>> list = new ArrayList<>();
        final StringBuilder buf = new StringBuilder();
        split(prefix, "/").of(stream -> stream.filter(StringUtil::isNotEmpty).forEach(s -> {
            if (buf.length() > 0) {
                buf.append('/');
            }
            buf.append(s);
            final Map<String, String> map = new HashMap<>();
            map.put("id", encodeId(buf.toString()));
            map.put("name", s);
            list.add(map);
        }));
        return list;
    }

    /**
     * Creates a path prefix with trailing slash if path is not empty.
     *
     * @param path the base path
     * @return path with trailing slash or empty string
     */
    protected static String getPathPrefix(final String path) {
        return StringUtil.isEmpty(path) ? StringUtil.EMPTY : path + "/";
    }

    /**
     * Combines path and name to create a full object name.
     *
     * @param path the directory path
     * @param name the file or directory name
     * @return the full object name
     */
    public static String getObjectName(final String path, final String name) {
        return getPathPrefix(path) + name;
    }

    /**
     * Encodes an object name to a URL-safe base64 string.
     *
     * @param objectName the object name to encode
     * @return base64 encoded string
     */
    protected static String encodeId(final String objectName) {
        if (objectName == null) {
            return StringUtil.EMPTY;
        }
        return new String(Base64.getUrlEncoder().encode(objectName.getBytes(Constants.UTF_8_CHARSET)), Constants.UTF_8_CHARSET);
    }

    /**
     * Decodes a base64 encoded ID back to the original object name.
     *
     * @param id the encoded ID to decode
     * @return the decoded object name
     */
    protected static String decodeId(final String id) {
        if (id == null) {
            return StringUtil.EMPTY;
        }
        return new String(Base64.getUrlDecoder().decode(id.getBytes(Constants.UTF_8_CHARSET)), Constants.UTF_8_CHARSET);
    }

    private HtmlResponse asListHtml(final String path) {
        return asHtml(path_AdminStorage_AdminStorageJsp).useForm(ItemForm.class).renderWith(data -> {
            RenderDataUtil.register(data, "endpoint", fessConfig.getStorageEndpoint());
            RenderDataUtil.register(data, "bucket", fessConfig.getStorageBucket());
            RenderDataUtil.register(data, "path", path);
            RenderDataUtil.register(data, "pathItems", createPathItems(path));
            RenderDataUtil.register(data, "parentId", createParentId(path));
            RenderDataUtil.register(data, "fileItems", getFileItems(path));
        });
    }

    private HtmlResponse asEditTagsHtml(final String path, final String name) {
        return asHtml(path_AdminStorage_AdminStorageTagEditJsp).renderWith(data -> {
            RenderDataUtil.register(data, "endpoint", fessConfig.getStorageEndpoint());
            RenderDataUtil.register(data, "bucket", fessConfig.getStorageBucket());
            RenderDataUtil.register(data, "pathItems", createPathItems(path));
            RenderDataUtil.register(data, "parentId", encodeId(path));
            RenderDataUtil.register(data, "path", path);
            RenderDataUtil.register(data, "name", name);
            final Map<String, String> tags = new HashMap<>();
            getObjectTags(getObjectName(path, name)).entrySet().forEach(e -> {
                final int index = tags.size() / 2 + 1;
                tags.put("name" + index, e.getKey());
                tags.put("value" + index, e.getValue());
            });
            RenderDataUtil.register(data, "savedTags", tags);
        });
    }

    /**
     * Container class for path information containing separate path and name components.
     */
    public static class PathInfo {
        private final String path;
        private final String name;

        /**
         * Creates a new PathInfo instance.
         *
         * @param path the directory path component
         * @param name the file or directory name component
         */
        public PathInfo(final String path, final String name) {
            this.path = path;
            this.name = name;
        }

        /**
         * Gets the directory path component.
         *
         * @return the path component
         */
        public String getPath() {
            return path;
        }

        /**
         * Gets the file or directory name component.
         *
         * @return the name component
         */
        public String getName() {
            return name;
        }
    }
}
