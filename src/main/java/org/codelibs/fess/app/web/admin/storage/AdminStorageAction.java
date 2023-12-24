/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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
import java.net.URLDecoder;
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
 * @author shinsuke
 */
public class AdminStorageAction extends FessAdminAction {

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

    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse index() {
        saveToken();
        return asListHtml(StringUtil.EMPTY);
    }

    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public ActionResponse list(final OptionalThing<String> id) {
        saveToken();
        return id.filter(StringUtil::isNotBlank).map(s -> asListHtml(decodePath(s))).orElse(redirect(getClass()));
    }

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

    @Execute
    @Secured({ ROLE })
    public HtmlResponse createDir(final ItemForm form) {
        validate(form, messages -> {}, () -> asListHtml(form.path));
        if (StringUtil.isBlank(form.name)) {
            throwValidationError(messages -> messages.addErrorsStorageDirectoryNameIsInvalid(GLOBAL), () -> asListHtml(form.path));
        }
        return redirectWith(getClass(), moreUrl("list/" + encodeId(getObjectName(form.path, form.name))));
    }

    @Execute
    @Secured({ ROLE })
    public HtmlResponse editTags(final TagForm form) {
        validate(form, messages -> {}, () -> asEditTagsHtml(form.path, form.name));
        saveToken();
        return asEditTagsHtml(form.path, form.name);
    }

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

    public static Map<String, String> getObjectTags(final String objectName) {
        try {
            final FessConfig fessConfig = ComponentUtil.getFessConfig();
            final GetObjectTagsArgs args = GetObjectTagsArgs.builder().bucket(fessConfig.getStorageBucket()).object(objectName).build();
            return createClient(fessConfig).getObjectTags(args).get();
        } catch (final Exception e) {
            throw new StorageException("Failed to get tags from " + objectName, e);
        }
    }

    public static void uploadObject(final String objectName, final MultipartFormFile uploadFile) {
        try (final InputStream in = uploadFile.getInputStream()) {
            final FessConfig fessConfig = ComponentUtil.getFessConfig();
            final MinioClient minioClient = createClient(fessConfig);
            final PutObjectArgs args = PutObjectArgs.builder().bucket(fessConfig.getStorageBucket()).object(objectName)
                    .stream(in, uploadFile.getFileSize(), -1).contentType("application/octet-stream").build();
            minioClient.putObject(args);
        } catch (final Exception e) {
            throw new StorageException("Failed to upload " + objectName, e);
        }
    }

    public static void downloadObject(final String objectName, final WrittenStreamOut out) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final GetObjectArgs args = GetObjectArgs.builder().bucket(fessConfig.getStorageBucket()).object(objectName).build();
        try (InputStream in = createClient(fessConfig).getObject(args)) {
            out.write(in);
        } catch (final Exception e) {
            throw new StorageException("Failed to download " + objectName, e);
        }
    }

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

    protected static MinioClient createClient(final FessConfig fessConfig) {
        try {
            return MinioClient.builder().endpoint(fessConfig.getStorageEndpoint())
                    .credentials(fessConfig.getStorageAccessKey(), fessConfig.getStorageSecretKey()).build();
        } catch (final Exception e) {
            throw new StorageException("Failed to create MinioClient: " + fessConfig.getStorageEndpoint(), e);
        }
    }

    public static List<Map<String, Object>> getFileItems(final String prefix) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final List<Map<String, Object>> list = new ArrayList<>();
        final List<Map<String, Object>> fileList = new ArrayList<>();
        try {
            final MinioClient minioClient = createClient(fessConfig);
            final ListObjectsArgs args = ListObjectsArgs.builder().bucket(fessConfig.getStorageBucket())
                    .prefix(prefix != null && prefix.length() > 0 ? prefix + "/" : prefix).recursive(false).includeUserMetadata(false)
                    .useApiVersion1(false).build();
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

    private static String getName(final String objectName) {
        final String[] values = objectName.split("/");
        if (values.length == 0) {
            return StringUtil.EMPTY;
        }
        return values[values.length - 1];
    }

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

    protected static String getPathPrefix(final String path) {
        return StringUtil.isEmpty(path) ? StringUtil.EMPTY : path + "/";
    }

    public static String getObjectName(final String path, final String name) {
        return getPathPrefix(path) + name;
    }

    @Deprecated
    protected static String urlEncode(final String str) {
        if (str == null) {
            return StringUtil.EMPTY;
        }
        return URLEncoder.encode(str, Constants.UTF_8_CHARSET);
    }

    @Deprecated
    protected static String urlDecode(final String str) {
        if (str == null) {
            return StringUtil.EMPTY;
        }
        return URLDecoder.decode(str, Constants.UTF_8_CHARSET);
    }

    protected static String encodeId(final String objectName) {
        if (objectName == null) {
            return StringUtil.EMPTY;
        }
        return new String(Base64.getUrlEncoder().encode(objectName.getBytes(Constants.UTF_8_CHARSET)), Constants.UTF_8_CHARSET);
    }

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

    public static class PathInfo {
        private final String path;
        private final String name;

        public PathInfo(final String path, final String name) {
            this.path = path;
            this.name = name;
        }

        public String getPath() {
            return path;
        }

        public String getName() {
            return name;
        }
    }
}
