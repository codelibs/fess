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
package org.codelibs.fess.app.web.admin.docreport;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.app.service.DocumentReportService;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.entity.DocumentReport;
import org.codelibs.fess.util.RenderDataUtil;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.HtmlResponse;

import jakarta.annotation.Resource;

/**
 * Admin action for the document report: groups of documents with the same content and documents
 * that have not been modified for a long time, to help clean up the crawled file servers.
 */
public class AdminDocreportAction extends FessAdminAction {

    /** Role name for admin document report operations */
    public static final String ROLE = "admin-docreport";

    /** The name of the duplicate tab. */
    public static final String TAB_DUPLICATE = "duplicate";

    /** The name of the dormant tab. */
    public static final String TAB_DORMANT = "dormant";

    private static final DateTimeFormatter FILE_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /**
     * Default constructor.
     */
    public AdminDocreportAction() {
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** Service building the document report */
    @Resource
    private DocumentReportService documentReportService;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected String getActionRole() {
        return ROLE;
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    /**
     * Displays the duplicate tab.
     *
     * @param form the duplicate filter form
     * @return HTML response for the duplicate tab
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse index(final DuplicateForm form) {
        return duplicate(form);
    }

    /**
     * Displays the largest groups of documents with the same content.
     *
     * @param form the duplicate filter form
     * @return HTML response for the duplicate tab
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse duplicate(final DuplicateForm form) {
        validate(form, messages -> {}, () -> redirect(getClass()));
        final String url = normalizeUrl(form.url);
        final DocumentReport report = documentReportService.getDuplicateReport(url);
        return asHtml(path_AdminDocreport_AdminDocreportJsp).renderWith(data -> {
            RenderDataUtil.register(data, "tab", TAB_DUPLICATE);
            RenderDataUtil.register(data, "report", report);
            RenderDataUtil.register(data, "url", url);
            RenderDataUtil.register(data, "groupSize", fessConfig.getDocreportDuplicateGroupSizeAsInteger());
            RenderDataUtil.register(data, "reportQuery", buildQuery(url, null, false));
        });
    }

    /**
     * Downloads every group of documents with the same content as CSV.
     *
     * @param form the duplicate filter form
     * @return the CSV file
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public ActionResponse downloadduplicate(final DuplicateForm form) {
        validate(form, messages -> {}, () -> redirect(getClass()));
        final String url = normalizeUrl(form.url);
        return asCsvStream("docreport_duplicate_" + systemHelper.getCurrentTimeAsLocalDateTime().format(FILE_TIMESTAMP) + ".csv",
                writer -> documentReportService.exportDuplicateCsv(url, writer));
    }

    /**
     * Displays one page of the documents that have not been modified for the given number of days.
     *
     * @param form the dormant filter form
     * @return HTML response for the dormant tab
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse dormant(final DormantForm form) {
        validate(form, messages -> {}, () -> redirect(getClass()));
        final String url = normalizeUrl(form.url);
        final int days = parseDays(form.days, fessConfig.getDocreportDormantDaysAsInteger());
        final boolean unclicked = Boolean.parseBoolean(form.unclicked);
        final int pageSize = Math.max(1, fessConfig.getPagingPageSizeAsInteger());
        final int maxPage = Math.max(1, fessConfig.getIndexerMaxResultWindowSizeAsInteger() / pageSize);
        final int page = Math.min(parsePage(form.pn), maxPage);
        final DocumentReport report = documentReportService.getDormantReport(url, days, unclicked, (page - 1) * pageSize, pageSize);
        final long totalPages = (report.getTotal() + pageSize - 1) / pageSize;
        final long lastPage = Math.min(totalPages, maxPage);
        return asHtml(path_AdminDocreport_AdminDocreportJsp).renderWith(data -> {
            RenderDataUtil.register(data, "tab", TAB_DORMANT);
            RenderDataUtil.register(data, "report", report);
            RenderDataUtil.register(data, "url", url);
            RenderDataUtil.register(data, "days", days);
            RenderDataUtil.register(data, "unclicked", unclicked);
            RenderDataUtil.register(data, "currentPage", page);
            RenderDataUtil.register(data, "lastPage", lastPage);
            RenderDataUtil.register(data, "pageLimited", totalPages > maxPage);
            RenderDataUtil.register(data, "maxDocs", (long) maxPage * pageSize);
            RenderDataUtil.register(data, "reportQuery", buildQuery(url, days, unclicked));
        });
    }

    /**
     * Downloads every document that has not been modified for the given number of days as CSV.
     *
     * @param form the dormant filter form
     * @return the CSV file
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public ActionResponse downloaddormant(final DormantForm form) {
        validate(form, messages -> {}, () -> redirect(getClass()));
        final String url = normalizeUrl(form.url);
        final int days = parseDays(form.days, fessConfig.getDocreportDormantDaysAsInteger());
        final boolean unclicked = Boolean.parseBoolean(form.unclicked);
        return asCsvStream("docreport_dormant_" + systemHelper.getCurrentTimeAsLocalDateTime().format(FILE_TIMESTAMP) + ".csv",
                writer -> documentReportService.exportDormantCsv(url, days, unclicked, writer));
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============
    /**
     * Trims the URL prefix of the filter.
     *
     * @param url the URL prefix
     * @return the trimmed prefix, or null when blank
     */
    protected static String normalizeUrl(final String url) {
        return StringUtil.isBlank(url) ? null : url.trim();
    }

    /**
     * Parses the number of days of the dormant filter.
     *
     * @param value the requested number of days
     * @param defaultDays the number of days used when the value is blank or not between 1 and
     *            {@link DocumentReportService#MAX_DORMANT_DAYS}
     * @return the number of days
     */
    protected static int parseDays(final String value, final int defaultDays) {
        if (StringUtil.isNotBlank(value)) {
            try {
                final int days = Integer.parseInt(value.trim());
                if (days >= 1 && days <= DocumentReportService.MAX_DORMANT_DAYS) {
                    return days;
                }
            } catch (final NumberFormatException e) {
                // fall back to the default
            }
        }
        return defaultDays;
    }

    /**
     * Parses the page number of the dormant list.
     *
     * @param value the requested page number
     * @return the page number, 1 when blank or invalid
     */
    protected static int parsePage(final String value) {
        if (StringUtil.isNotBlank(value)) {
            try {
                return Math.max(1, Integer.parseInt(value.trim()));
            } catch (final NumberFormatException e) {
                // fall back to the first page
            }
        }
        return 1;
    }

    /**
     * Builds the URL-encoded query string of the filter, for the tab, paging and download links.
     *
     * @param url the URL prefix, or null
     * @param days the number of days, or null for the duplicate tab
     * @param unclicked true to add the never-clicked filter
     * @return the query string without the leading question mark
     */
    protected static String buildQuery(final String url, final Integer days, final boolean unclicked) {
        final StringBuilder buf = new StringBuilder();
        if (url != null) {
            buf.append("url=").append(URLEncoder.encode(url, StandardCharsets.UTF_8));
        }
        if (days != null) {
            if (buf.length() > 0) {
                buf.append('&');
            }
            buf.append("days=").append(days);
        }
        if (unclicked) {
            if (buf.length() > 0) {
                buf.append('&');
            }
            buf.append("unclicked=true");
        }
        return buf.toString();
    }
}
