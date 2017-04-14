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
package org.codelibs.fess.app.web.admin.backup;

import static org.codelibs.core.stream.StreamUtil.stream;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.codelibs.core.exception.IORuntimeException;
import org.codelibs.core.io.CopyUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.elasticsearch.runner.net.Curl;
import org.codelibs.elasticsearch.runner.net.CurlResponse;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.es.log.exbhv.SearchLogBhv;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.RenderDataUtil;
import org.codelibs.fess.util.ResourceUtil;
import org.lastaflute.core.magic.async.AsyncManager;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.UndefinedResponse;
import org.lastaflute.web.ruts.process.ActionRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.healthmarketscience.jackcess.RuntimeIOException;
import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.CsvWriter;

/**
 * @author shinsuke
 */
public class AdminBackupAction extends FessAdminAction {

    private static final Logger logger = LoggerFactory.getLogger(AdminBackupAction.class);

    private static final String CSV_EXTENTION = ".csv";

    private static final DateTimeFormatter ISO_8601_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    @Resource
    private AsyncManager asyncManager;

    @Resource
    private HttpServletResponse response;

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
            } else {
                try (CurlResponse response = Curl.post(ResourceUtil.getElasticsearchHttpUrl() + "/_bulk").onConnect((req, con) -> {
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
            } else if (id.endsWith(CSV_EXTENTION)) {
                String name = id.substring(0, id.length() - CSV_EXTENTION.length());
                if ("search_log".equals(name)) {
                    writeSearchLogCsvResponse(id);
                    return HtmlResponse.asEmptyBody();
                } else if ("user_info".equals(id)) {
                    // TODO user_info=createdAt,updatedAt
                }
                // TODO search_field_log=name,searchLogId,value
                // TODO favorite_log=queryId,userInfoId,docId,url,createdAt
                // TODO click_log=queryId,userSessionId,docId,url,order,queryRequestedAt,requestedAt
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
                                    Curl.get(ResourceUtil.getElasticsearchHttpUrl() + "/" + index + "/_data").param("format", "json")
                                            .execute()) {
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

    private void writeSearchLogCsvResponse(final String id) {
        writeCsvResponseHeader(id);
        final CsvConfig cfg = new CsvConfig(',', '"', '"');
        cfg.setEscapeDisabled(false);
        cfg.setQuoteDisabled(false);
        try (final CsvWriter writer =
                new CsvWriter(new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), fessConfig.getCsvFileEncoding())), cfg)) {
            SearchLogBhv searchLogBhv = ComponentUtil.getComponent(SearchLogBhv.class);
            searchLogBhv.selectCursor(cb -> {
                cb.query().matchAll();
                cb.query().addOrderBy_RequestedAt_Asc();
            }, entity -> {
                List<String> list = new ArrayList<>();
                addToList(entity.getQueryId(), list);
                addToList(entity.getUserInfoId(), list);
                addToList(entity.getUserSessionId(), list);
                addToList(entity.getUser(), list);
                addToList(entity.getSearchWord(), list);
                addToList(entity.getHitCount(), list);
                addToList(entity.getQueryPageSize(), list);
                addToList(entity.getQueryOffset(), list);
                addToList(entity.getReferer(), list);
                addToList(entity.getLanguages(), list);
                addToList(entity.getRoles(), list);
                addToList(entity.getUserAgent(), list);
                addToList(entity.getClientIp(), list);
                addToList(entity.getAccessType(), list);
                addToList(entity.getQueryTime(), list);
                addToList(entity.getResponseTime(), list);
                addToList(entity.getRequestedAt(), list);
                try {
                    writer.writeValues(list);
                } catch (IOException e) {
                    throw new RuntimeIOException(e);
                }
            });
            writer.flush();
        } catch (final Exception e) {
            logger.warn("Failed to write " + id + " to response.", e);
        }
    }

    private void addToList(final Object value, final List<String> list) {
        if (value == null) {
            list.add(StringUtil.EMPTY);
        } else if (value instanceof LocalDateTime) {
            list.add(((LocalDateTime) value).format(ISO_8601_FORMATTER));
        } else if (value instanceof String[]) {
            String.join(",", (String[]) value);
        } else {
            list.add(value.toString());
        }
    }

    private void writeCsvResponseHeader(final String id) {
        response.setContentType("application/octet-stream");
        response.addHeader("Content-Disposition", "attachment; filename=\"" + id + "\"");
        response.addHeader("Pragma", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
        response.addHeader("Expires", "Thu, 01 Dec 1994 16:00:00 GMT");
    }

    private List<Map<String, String>> getBackupItems() {
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
