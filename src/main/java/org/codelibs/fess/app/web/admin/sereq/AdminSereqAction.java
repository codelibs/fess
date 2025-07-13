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
package org.codelibs.fess.app.web.admin.sereq;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.io.CopyUtil;
import org.codelibs.core.io.ReaderUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.curl.CurlRequest;
import org.codelibs.curl.CurlResponse;
import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.helper.CurlHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.ruts.process.ActionRuntime;

/**
 * Admin action for Search Request.
 *
 */
public class AdminSereqAction extends FessAdminAction {

    /**
     * Default constructor.
     */
    public AdminSereqAction() {
        // Default constructor
    }

    /** Role name for admin search request operations */
    public static final String ROLE = "admin-sereq";

    private static final Logger logger = LogManager.getLogger(AdminSereqAction.class);

    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameSereq()));
    }

    @Override
    protected String getActionRole() {
        return ROLE;
    }

    /**
     * Displays the search request management index page.
     *
     * @return HTML response for the search request page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse index() {
        return asListHtml(this::saveToken);
    }

    /**
     * Processes uploaded search request files and executes them against the search engine.
     *
     * @param form the upload form containing the request file
     * @return action response with the search results or error page
     */
    @Execute
    @Secured({ ROLE })
    public ActionResponse upload(final UploadForm form) {
        validate(form, messages -> {}, () -> asListHtml(null));
        verifyTokenKeep(() -> asListHtml(this::saveToken));

        String header = null;
        final StringBuilder buf = new StringBuilder(1000);
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(form.requestFile.getInputStream(), Constants.UTF_8))) {
            header = ReaderUtil.readLine(reader);
            if (header == null) {
                throwValidationError(messages -> messages.addErrorsInvalidHeaderForRequestFile(GLOBAL, "no header"),
                        () -> asListHtml(this::saveToken));
                return redirect(getClass()); // no-op
            }
            String line;
            while ((line = ReaderUtil.readLine(reader)) != null) {
                buf.append(line);
            }
        } catch (final Exception e) {
            throwValidationError(messages -> messages.addErrorsFailedToReadRequestFile(GLOBAL, e.getMessage()),
                    () -> asListHtml(this::saveToken));
        }

        final CurlRequest curlRequest = getCurlRequest(header);
        if (curlRequest == null) {
            final String msg = header;
            throwValidationError(messages -> messages.addErrorsInvalidHeaderForRequestFile(GLOBAL, msg), () -> asListHtml(this::saveToken));
        } else {
            try (final CurlResponse response = curlRequest.body(buf.toString()).execute()) {
                final File tempFile = ComponentUtil.getSystemHelper().createTempFile("sereq_", ".json");
                try (final InputStream in = response.getContentAsStream()) {
                    CopyUtil.copy(in, tempFile);
                } catch (final Exception e1) {
                    if (tempFile != null && tempFile.exists() && !tempFile.delete()) {
                        logger.warn("Failed to delete {}", tempFile.getAbsolutePath());
                    }
                    throw e1;
                }
                return asStream("es_" + ComponentUtil.getSystemHelper().getCurrentTimeAsLong() + ".json").contentTypeOctetStream()
                        .stream(out -> {
                            try (final InputStream in = new FileInputStream(tempFile)) {
                                out.write(in);
                            } finally {
                                if (tempFile.exists() && !tempFile.delete()) {
                                    logger.warn("Failed to delete {}", tempFile.getAbsolutePath());
                                }
                            }
                        });
            } catch (final Exception e) {
                logger.warn("Failed to process request file: {}", form.requestFile.getFileName(), e);
                throwValidationError(messages -> messages.addErrorsInvalidHeaderForRequestFile(GLOBAL, e.getMessage()),
                        () -> asListHtml(this::saveToken));
            }
        }
        return redirect(getClass()); // no-op
    }

    /**
     * Creates a CURL request from the provided header string.
     *
     * @param header the header string containing HTTP method and path
     * @return CURL request object or null if header is invalid
     */
    private CurlRequest getCurlRequest(final String header) {
        if (StringUtil.isBlank(header)) {
            return null;
        }

        final String[] values = header.split(" ");
        if (values.length != 2) {
            return null;
        }

        final String path;
        if (values[1].startsWith("/")) {
            path = values[1];
        } else {
            path = "/" + values[1];
        }

        final CurlHelper curlHelper = ComponentUtil.getCurlHelper();
        switch (values[0].toUpperCase(Locale.ROOT)) {
        case "GET":
            return curlHelper.get(path);
        case "POST":
            return curlHelper.post(path);
        case "PUT":
            return curlHelper.put(path);
        case "DELETE":
            return curlHelper.delete(path);
        default:
            break;
        }
        return null;
    }

    /**
     * Creates an HTML response for the list page with optional pre-processing.
     *
     * @param runnable optional runnable to execute before rendering (can be null)
     * @return HTML response for the search request list page
     */
    private HtmlResponse asListHtml(final Runnable runnable) {
        if (runnable != null) {
            runnable.run();
        }
        return asHtml(path_AdminSereq_AdminSereqJsp).useForm(UploadForm.class);
    }

}
