/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.exception.StorageException;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.RenderDataUtil;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.ruts.process.ActionRuntime;

import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;

/**
 * @author shinsuke
 */
public class AdminStorageAction extends FessAdminAction {

    private static final Logger logger = LogManager.getLogger(AdminStorageAction.class);

    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameStorage()));
    }

    @Execute
    public HtmlResponse index() {
        saveToken();
        return asListHtml(StringUtil.EMPTY);
    }

    // TODO
    //    @Execute
    //    public HtmlResponse create() {
    //    }

    @Execute
    public HtmlResponse upload(final ItemForm form) {
        validate(form, messages -> {}, () -> asListHtml(form.path));
        if (form.uploadFile == null) {
            throwValidationError(messages -> messages.addErrorsStorageNoUploadFile(GLOBAL), () -> asListHtml(form.path));
        }
        verifyToken(() -> asListHtml(form.path));
        final String fileName = form.uploadFile.getFileName();
        try (final InputStream in = form.uploadFile.getInputStream()) {
            final MinioClient minioClient = createClient(fessConfig);
            minioClient.putObject(fessConfig.getStorageBucket(), form.uploadFile.getFileName(), in, (long) form.uploadFile.getFileSize(),
                    null, null, "application/octet-stream");
        } catch (final Exception e) {
            throwValidationError(messages -> messages.addErrorsStorageFileUploadFailure(GLOBAL, fileName), () -> asListHtml(form.path));
        }
        saveInfo(messages -> messages.addSuccessUploadFileToStorage(GLOBAL, fileName));
        return redirect(getClass()); // no-op
    }

    @Execute
    public ActionResponse list(final String id) {
        saveToken();
        return asListHtml(decodePath(id));
    }

    @Execute
    public ActionResponse download(final String id) {
        final String[] values = decodeId(id);
        if (StringUtil.isEmpty(values[1])) {
            throwValidationError(messages -> messages.addErrorsStorageFileNotFound(GLOBAL), () -> asListHtml(values[0]));
        }
        return asStream(values[1]).contentTypeOctetStream().stream(
                out -> {
                    try (InputStream in = createClient(fessConfig).getObject(fessConfig.getStorageBucket(), values[0] + values[1])) {
                        out.write(in);
                    } catch (final Exception e) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Failed to access {}", fessConfig.getStorageEndpoint(), e);
                        }
                        throwValidationError(messages -> messages.addErrorsStorageAccessError(GLOBAL, e.getLocalizedMessage()),
                                () -> asListHtml(values[0]));
                    }
                });
    }

    public static List<Map<String, Object>> getFileItems(final String prefix) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final ArrayList<Map<String, Object>> list = new ArrayList<>();
        try {
            final MinioClient minioClient = createClient(fessConfig);
            for (final Result<Item> result : minioClient.listObjects(fessConfig.getStorageBucket(),
                    prefix != null && prefix.length() > 0 ? prefix + "/" : prefix, false)) {
                final Map<String, Object> map = new HashMap<>();
                final Item item = result.get();
                final String objectName = item.objectName();
                map.put("id", URLEncoder.encode(objectName, Constants.UTF_8_CHARSET));
                map.put("name", getName(objectName));
                map.put("size", item.size());
                map.put("directory", item.isDir());
                if (!item.isDir()) {
                    map.put("lastModified", item.lastModified());
                }
                list.add(map);
                if (list.size() > fessConfig.getStorageMaxItemsInPageAsInteger()) {
                    break;
                }
            }
        } catch (final Exception e) {
            throw new StorageException("Failed to access " + fessConfig.getStorageEndpoint(), e);
        }
        return list;
    }

    private static String getName(final String objectName) {
        final String[] values = objectName.split("/");
        if (values.length == 0) {
            return StringUtil.EMPTY;
        }
        return values[values.length - 1];
    }

    protected static MinioClient createClient(final FessConfig fessConfig) {
        try {
            return new MinioClient(fessConfig.getStorageEndpoint(), fessConfig.getStorageAccessKey(), fessConfig.getStorageSecretKey());
        } catch (final Exception e) {
            throw new StorageException("Failed to create MinioClient: " + fessConfig.getStorageEndpoint(), e);
        }

    }

    protected static String decodePath(final String id) {
        final String[] values = decodeId(id);
        if (StringUtil.isEmpty(values[0]) && StringUtil.isEmpty(values[1])) {
            return StringUtil.EMPTY;
        } else if (StringUtil.isEmpty(values[0])) {
            return values[1];
        } else {
            return values[0] + "/" + values[1];
        }
    }

    protected static String[] decodeId(final String id) {
        final String value = URLDecoder.decode(id, Constants.UTF_8_CHARSET);
        final String[] values = split(value, "/").get(stream -> stream.filter(StringUtil::isNotEmpty).toArray(n -> new String[n]));
        if (values.length == 0) {
            // invalid?
            return new String[] { StringUtil.EMPTY, StringUtil.EMPTY };
        } else if (values.length == 1) {
            return new String[] { StringUtil.EMPTY, values[0] };
        } else {
            final StringBuilder buf = new StringBuilder();
            for (int i = 0; i < values.length - 1; i++) {
                if (buf.length() > 0) {
                    buf.append('/');
                }
                buf.append(values[i]);
            }
            return new String[] { buf.toString(), values[values.length - 1] };
        }
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
            return URLEncoder.encode(buf.toString(), Constants.UTF_8_CHARSET);
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
            map.put("id", URLEncoder.encode(buf.toString(), Constants.UTF_8_CHARSET));
            map.put("name", s);
            list.add(map);
        }));
        return list;
    }

    private HtmlResponse asListHtml(final String prefix) {
        return asHtml(path_AdminStorage_AdminStorageJsp).useForm(ItemForm.class).renderWith(data -> {
            RenderDataUtil.register(data, "endpoint", fessConfig.getStorageEndpoint());
            RenderDataUtil.register(data, "bucket", fessConfig.getStorageBucket());
            RenderDataUtil.register(data, "path", prefix);
            RenderDataUtil.register(data, "pathItems", createPathItems(prefix));
            RenderDataUtil.register(data, "parentId", createParentId(prefix));
            RenderDataUtil.register(data, "fileItems", getFileItems(prefix));
        });
    }

}
