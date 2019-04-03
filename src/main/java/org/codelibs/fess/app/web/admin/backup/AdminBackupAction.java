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
package org.codelibs.fess.app.web.admin.backup;

import static org.codelibs.core.stream.StreamUtil.stream;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.text.StringEscapeUtils;
import org.codelibs.core.exception.IORuntimeException;
import org.codelibs.core.io.CopyUtil;
import org.codelibs.core.misc.Pair;
import org.codelibs.curl.CurlResponse;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.es.config.exbhv.FileConfigBhv;
import org.codelibs.fess.es.config.exbhv.LabelTypeBhv;
import org.codelibs.fess.es.config.exbhv.WebConfigBhv;
import org.codelibs.fess.es.log.exbhv.ClickLogBhv;
import org.codelibs.fess.es.log.exbhv.FavoriteLogBhv;
import org.codelibs.fess.es.log.exbhv.SearchLogBhv;
import org.codelibs.fess.es.log.exbhv.UserInfoBhv;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.GsaConfigParser;
import org.codelibs.fess.util.RenderDataUtil;
import org.lastaflute.core.magic.async.AsyncManager;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.StreamResponse;
import org.lastaflute.web.ruts.process.ActionRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

/**
 * @author shinsuke
 */
public class AdminBackupAction extends FessAdminAction {

    private static final Logger logger = LoggerFactory.getLogger(AdminBackupAction.class);

    public static final String NDJSON_EXTENTION = ".ndjson";

    private static final DateTimeFormatter ISO_8601_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    @Resource
    private AsyncManager asyncManager;

    @Resource
    private WebConfigBhv webConfigBhv;

    @Resource
    private FileConfigBhv fileConfigBhv;

    @Resource
    private LabelTypeBhv labelTypeBhv;

    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameBackup()));
    }

    @Execute
    public HtmlResponse index() {
        saveToken();
        return asListHtml();
    }

    @Execute
    public HtmlResponse upload(final UploadForm form) {
        validate(form, messages -> {}, () -> asListHtml());
        verifyToken(() -> asListHtml());
        asyncManager.async(() -> {
            final String fileName = form.bulkFile.getFileName();
            if (fileName.startsWith("system") && fileName.endsWith(".properties")) {
                try (final InputStream in = form.bulkFile.getInputStream()) {
                    ComponentUtil.getSystemProperties().load(in);
                } catch (final IOException e) {
                    logger.warn("Failed to process system.properties file: " + form.bulkFile.getFileName(), e);
                }
            } else if (fileName.startsWith("gsa") && fileName.endsWith(".xml")) {
                final GsaConfigParser configParser = ComponentUtil.getComponent(GsaConfigParser.class);
                try (final InputStream in = form.bulkFile.getInputStream()) {
                    configParser.parse(new InputSource(in));
                } catch (final IOException e) {
                    logger.warn("Failed to process gsa.xml file: " + form.bulkFile.getFileName(), e);
                }
                configParser.getWebConfig().ifPresent(c -> webConfigBhv.insert(c));
                configParser.getFileConfig().ifPresent(c -> fileConfigBhv.insert(c));
                labelTypeBhv.batchInsert(Arrays.stream(configParser.getLabelTypes()).collect(Collectors.toList()));
            } else {
                try (CurlResponse response = ComponentUtil.getCurlHelper().post("/_bulk").onConnect((req, con) -> {
                    con.setDoOutput(true);
                    try (InputStream in = form.bulkFile.getInputStream(); OutputStream out = con.getOutputStream()) {
                        CopyUtil.copy(in, out);
                    } catch (IOException e) {
                        throw new IORuntimeException(e);
                    }
                }).execute()) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Bulk Response:\n" + response.getContentAsString());
                    }
                    systemHelper.reloadConfiguration();
                } catch (final Exception e) {
                    logger.warn("Failed to process bulk file: " + form.bulkFile.getFileName(), e);
                }
            }
        });
        saveInfo(messages -> messages.addSuccessBulkProcessStarted(GLOBAL));
        return redirect(getClass()); // no-op
    }

    @Execute
    public ActionResponse download(final String id) {
        if (stream(fessConfig.getIndexBackupAllTargets()).get(stream -> stream.anyMatch(s -> s.equals(id)))) {
            if (id.equals("system.properties")) {
                return asStream(id).contentTypeOctetStream().stream(out -> {
                    try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                        ComponentUtil.getSystemProperties().store(baos, id);
                        try (final InputStream in = new ByteArrayInputStream(baos.toByteArray())) {
                            out.write(in);
                        }
                    }
                });
            } else if (id.endsWith(NDJSON_EXTENTION)) {
                final String name = id.substring(0, id.length() - NDJSON_EXTENTION.length());
                if ("search_log".equals(name)) {
                    return writeNdjsonResponse(id, getSearchLogNdjsonWriteCall());
                } else if ("user_info".equals(name)) {
                    return writeNdjsonResponse(id, getUserInfoNdjsonWriteCall());
                } else if ("click_log".equals(name)) {
                    return writeNdjsonResponse(id, getClickLogNdjsonWriteCall());
                } else if ("favorite_log".equals(name)) {
                    return writeNdjsonResponse(id, getFavoriteLogNdjsonWriteCall());
                }
            } else {
                final String index;
                final String filename;
                if (id.endsWith(".bulk")) {
                    index = id.substring(0, id.length() - 5);
                    filename = id;
                } else {
                    index = id;
                    filename = id + ".bulk";
                }
                return asStream(filename).contentTypeOctetStream().stream(
                        out -> {
                            try (CurlResponse response =
                                    ComponentUtil.getCurlHelper().get("/" + index + "/_data").param("format", "json")
                                            .param("scroll", fessConfig.getIndexScrollSearchTimeout()).execute()) {
                                out.write(response.getContentAsStream());
                            }
                        });
            }
        }
        throwValidationError(messages -> messages.addErrorsCouldNotFindBackupIndex(GLOBAL), () -> {
            return asListHtml();
        });
        return redirect(getClass()); // no-op
    }

    private StreamResponse writeNdjsonResponse(final String id, final Consumer<Writer> writeCall) {
        return asStream(id)//
                .header("Pragma", "no-cache")//
                .header("Cache-Control", "no-cache")//
                .header("Expires", "Thu, 01 Dec 1994 16:00:00 GMT")//
                .header("Content-Type", "application/x-ndjson")//
                .stream(out -> {
                    try (final Writer writer = new BufferedWriter(new OutputStreamWriter(out.stream(), Constants.CHARSET_UTF_8))) {
                        writeCall.accept(writer);
                        writer.flush();
                    } catch (final Exception e) {
                        logger.warn("Failed to write " + id + " to response.", e);
                    }
                });
    }

    private static StringBuilder appendJson(final String field, final Object value, final StringBuilder buf) {
        buf.append('"').append(StringEscapeUtils.escapeJson(field)).append('"').append(':');
        if (value == null) {
            buf.append("null");
        } else if (value instanceof LocalDateTime) {
            final String format =
                    ((LocalDateTime) value).atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC")).format(ISO_8601_FORMATTER);
            buf.append('"').append(StringEscapeUtils.escapeJson(format)).append('"');
        } else if (value instanceof String[]) {
            final String json =
                    Arrays.stream((String[]) value).map(s -> "\"" + StringEscapeUtils.escapeJson(s) + "\"")
                            .collect(Collectors.joining(","));
            buf.append('[').append(json).append(']');
        } else if (value instanceof List) {
            final String json =
                    ((List<?>) value).stream().map(s -> "\"" + StringEscapeUtils.escapeJson(s.toString()) + "\"")
                            .collect(Collectors.joining(","));
            buf.append('[').append(json).append(']');
        } else if (value instanceof Map) {
            buf.append('{');
            final String json = ((Map<?, ?>) value).entrySet().stream().map(e -> {
                final StringBuilder tempBuf = new StringBuilder();
                appendJson(e.getKey().toString(), e.getValue(), tempBuf);
                return tempBuf.toString();
            }).collect(Collectors.joining(","));
            buf.append(json);
            buf.append('}');
        } else if (value instanceof Long || value instanceof Integer) {
            buf.append(((Number) value).longValue());
        } else if (value instanceof Number) {
            buf.append(((Number) value).doubleValue());
        } else {
            buf.append('"').append(StringEscapeUtils.escapeJson(value.toString())).append('"');
        }
        return buf;
    }

    public static Consumer<Writer> getSearchLogNdjsonWriteCall() {
        return writer -> {
            final SearchLogBhv bhv = ComponentUtil.getComponent(SearchLogBhv.class);
            bhv.selectCursor(
                    cb -> {
                        cb.query().matchAll();
                        cb.query().addOrderBy_RequestedAt_Asc();
                    },
                    entity -> {
                        final StringBuilder buf = new StringBuilder();
                        buf.append('{');
                        appendJson("id", entity.getId(), buf).append(',');
                        appendJson("query-id", entity.getQueryId(), buf).append(',');
                        appendJson("user-info-id", entity.getUserInfoId(), buf).append(',');
                        appendJson("user-session-id", entity.getUserSessionId(), buf).append(',');
                        appendJson("user", entity.getUser(), buf).append(',');
                        appendJson("search-word", entity.getSearchWord(), buf).append(',');
                        appendJson("hit-count", entity.getHitCount(), buf).append(',');
                        appendJson("query-page-size", entity.getQueryPageSize(), buf).append(',');
                        appendJson("query-offset", entity.getQueryOffset(), buf).append(',');
                        appendJson("referer", entity.getReferer(), buf).append(',');
                        appendJson("languages", entity.getLanguages(), buf).append(',');
                        appendJson("roles", entity.getRoles(), buf).append(',');
                        appendJson("user-agent", entity.getUserAgent(), buf).append(',');
                        appendJson("client-ip", entity.getClientIp(), buf).append(',');
                        appendJson("access-type", entity.getAccessType(), buf).append(',');
                        appendJson("query-time", entity.getQueryTime(), buf).append(',');
                        appendJson("response-time", entity.getResponseTime(), buf).append(',');
                        appendJson("requested-at", entity.getRequestedAt(), buf).append(',');
                        final Map<String, List<String>> searchFieldMap =
                                entity.getSearchFieldLogList()
                                        .stream()
                                        .collect(
                                                Collectors.groupingBy(Pair::getFirst,
                                                        Collectors.mapping(Pair::getSecond, Collectors.toList())));
                        appendJson("search-field", searchFieldMap, buf);
                        buf.append('}');
                        buf.append('\n');
                        try {
                            writer.write(buf.toString());
                        } catch (final IOException e) {
                            throw new IORuntimeException(e);
                        }
                    });
        };
    }

    public static Consumer<Writer> getUserInfoNdjsonWriteCall() {
        return writer -> {
            final UserInfoBhv bhv = ComponentUtil.getComponent(UserInfoBhv.class);
            bhv.selectCursor(cb -> {
                cb.query().matchAll();
                cb.query().addOrderBy_CreatedAt_Asc();
            }, entity -> {
                final StringBuilder buf = new StringBuilder();
                buf.append('{');
                appendJson("id", entity.getId(), buf).append(',');
                appendJson("created-at", entity.getCreatedAt(), buf).append(',');
                appendJson("updated-at", entity.getUpdatedAt(), buf);
                buf.append('}');
                buf.append('\n');
                try {
                    writer.write(buf.toString());
                } catch (final IOException e) {
                    throw new IORuntimeException(e);
                }
            });
        };
    }

    public static Consumer<Writer> getFavoriteLogNdjsonWriteCall() {
        return writer -> {
            final FavoriteLogBhv bhv = ComponentUtil.getComponent(FavoriteLogBhv.class);
            bhv.selectCursor(cb -> {
                cb.query().matchAll();
                cb.query().addOrderBy_CreatedAt_Asc();
            }, entity -> {
                final StringBuilder buf = new StringBuilder();
                buf.append('{');
                appendJson("id", entity.getId(), buf).append(',');
                appendJson("created-at", entity.getCreatedAt(), buf).append(',');
                appendJson("query-id", entity.getQueryId(), buf).append(',');
                appendJson("user-info-id", entity.getUserInfoId(), buf).append(',');
                appendJson("doc-id", entity.getDocId(), buf).append(',');
                appendJson("url", entity.getUrl(), buf);
                buf.append('}');
                buf.append('\n');
                try {
                    writer.write(buf.toString());
                } catch (final IOException e) {
                    throw new IORuntimeException(e);
                }
            });
        };
    }

    public static Consumer<Writer> getClickLogNdjsonWriteCall() {
        return writer -> {
            final ClickLogBhv bhv = ComponentUtil.getComponent(ClickLogBhv.class);
            bhv.selectCursor(cb -> {
                cb.query().matchAll();
                cb.query().addOrderBy_RequestedAt_Asc();
            }, entity -> {
                final StringBuilder buf = new StringBuilder();
                buf.append('{');
                appendJson("id", entity.getId(), buf).append(',');
                appendJson("query-id", entity.getQueryId(), buf).append(',');
                appendJson("user-session-id", entity.getUserSessionId(), buf).append(',');
                appendJson("doc-id", entity.getDocId(), buf).append(',');
                appendJson("url", entity.getUrl(), buf).append(',');
                appendJson("order", entity.getOrder(), buf).append(',');
                appendJson("query-requested-at", entity.getQueryRequestedAt(), buf).append(',');
                appendJson("requested-at", entity.getRequestedAt(), buf);
                buf.append('}');
                buf.append('\n');
                try {
                    writer.write(buf.toString());
                } catch (final IOException e) {
                    throw new IORuntimeException(e);
                }
            });
        };
    }

    public static List<Map<String, String>> getBackupItems() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return stream(fessConfig.getIndexBackupAllTargets()).get(stream -> stream.map(name -> {
            final Map<String, String> map = new HashMap<>();
            map.put("id", name);
            map.put("name", name);
            return map;
        }).collect(Collectors.toList()));
    }

    private HtmlResponse asListHtml() {
        return asHtml(path_AdminBackup_AdminBackupJsp).useForm(UploadForm.class).renderWith(
                data -> RenderDataUtil.register(data, "backupItems", getBackupItems()));
    }

}
