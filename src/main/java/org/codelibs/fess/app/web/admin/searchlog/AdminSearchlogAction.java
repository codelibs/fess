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
package org.codelibs.fess.app.web.admin.searchlog;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.app.pager.SearchLogPager;
import org.codelibs.fess.app.service.SearchLogAnalyticsService;
import org.codelibs.fess.app.service.SearchLogService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.entity.AnalyticsCondition;
import org.codelibs.fess.entity.AnalyticsReport;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.RenderDataUtil;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.StreamResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.ruts.process.ActionRuntime;

import jakarta.annotation.Resource;
import tools.jackson.databind.ObjectMapper;

/**
 * Admin action for Search Log.
 *
 */
public class AdminSearchlogAction extends FessAdminAction {

    private static final Logger logger = LogManager.getLogger(AdminSearchlogAction.class);

    /**
     * Default constructor.
     */
    public AdminSearchlogAction() {
    }

    /** Role name for admin search log operations */
    public static final String ROLE = "admin-searchlog";

    private static final DateTimeFormatter FILE_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private static final String[] CONDITION_FIELDS =
            { "logType", "queryId", "userSessionId", "accessType", "requestedTimeRange", "pageSize", "searchWord", "hitCount" };

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** Service for managing search log data */
    @Resource
    private SearchLogService searchLogService;
    /** Pager for paginating search log results */
    @Resource
    private SearchLogPager searchLogPager;
    /** Service for aggregating search log analytics */
    @Resource
    private SearchLogAnalyticsService searchLogAnalyticsService;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameSearchlog()));
    }

    @Override
    protected String getActionRole() {
        return ROLE;
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    /**
     * Displays the analytics overview tab.
     *
     * @param form the analytics filter form
     * @return HTML response for the analytics overview
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse index(final AnalyticsForm form) {
        validate(form, messages -> {}, () -> redirect(getClass()));
        return asAnalyticsHtml(SearchLogAnalyticsService.TAB_OVERVIEW, form);
    }

    /**
     * Displays one analytics tab.
     *
     * @param tab the tab name (overview, queries, clicks, performance or audience)
     * @param form the analytics filter form
     * @return HTML response for the tab, or a redirect to the overview when the tab is missing or unknown
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse report(final OptionalThing<String> tab, final AnalyticsForm form) {
        validate(form, messages -> {}, () -> redirect(getClass()));
        return tab.filter(AdminSearchlogAction::isValidTab).map(t -> asAnalyticsHtml(t, form)).orElseGet(() -> redirect(getClass()));
    }

    /**
     * Downloads one table, chart or KPI list of an analytics tab as CSV. The report is the one the tab
     * shows for the same filter.
     *
     * @param tab the tab name (overview, queries, clicks, performance or audience)
     * @param item the item of the tab, see {@link SearchLogAnalyticsService#isExportable(String, String)}
     * @param form the analytics filter form
     * @return the CSV file, a redirect to the overview when the tab or the item is unknown, or a redirect to the tab when the report failed
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public ActionResponse downloadreport(final String tab, final String item, final AnalyticsForm form) {
        validate(form, messages -> {}, () -> redirect(getClass()));
        if (!searchLogAnalyticsService.isExportable(tab, item)) {
            return redirect(getClass());
        }
        final AnalyticsCondition cond = createAnalyticsCondition(tab, form);
        final AnalyticsReport report = searchLogAnalyticsService.getReport(tab, cond);
        if (report.isFailed()) {
            return redirectWith(getClass(), moreUrl("report", tab).params(buildAnalyticsParams(cond, form).toArray()));
        }
        return asCsvStream("searchlog_" + tab + "_" + item + "_" + cond.getFrom() + "_" + cond.getTo() + ".csv",
                writer -> searchLogAnalyticsService.exportCsv(report, tab, item, writer));
    }

    /**
     * Displays the log list tab.
     *
     * @return HTML response for the search log list page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse logs() {
        saveToken();
        return asListHtml();
    }

    /**
     * Displays a paginated list of search log entries.
     *
     * @param pageNumber the page number to display
     * @param form the search form containing filter criteria
     * @return HTML response with the search log list
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse list(final Integer pageNumber, final SearchForm form) {
        validate(form, messages -> {}, this::asListHtml);
        saveToken();
        searchLogPager.setCurrentPageNumber(pageNumber);
        return asHtml(path_AdminSearchlog_AdminSearchlogJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Searches for search log entries based on the provided search criteria.
     *
     * @param form the search form containing search criteria
     * @return HTML response with filtered search log results
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse search(final SearchForm form) {
        validate(form, messages -> {}, this::asListHtml);
        saveToken();
        searchLogPager.clear();
        copyBeanToBean(form, searchLogPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        searchLogPager.logType = SearchLogPager.normalizeLogType(searchLogPager.logType);
        searchLogPager.setPageSize(form.getPageSize());
        return asHtml(path_AdminSearchlog_AdminSearchlogJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Downloads all search log entries matching the search criteria as CSV, newest first. The criteria
     * come from the submitted form, not from the pager of the session, and the paging is ignored.
     *
     * @param form the search form containing search criteria
     * @return the CSV file
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public ActionResponse download(final SearchForm form) {
        validate(form, messages -> {}, this::asListHtml);
        verifyTokenKeep(this::asListHtml);
        final SearchLogPager pager = new SearchLogPager();
        copyBeanToBean(form, pager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        pager.logType = SearchLogPager.normalizeLogType(pager.logType);
        return asCsvStream(
                "searchlog_" + pager.logType + "_" + systemHelper.getCurrentTimeAsLocalDateTime().format(FILE_TIMESTAMP) + ".csv",
                writer -> searchLogService.exportCsv(pager, writer));
    }

    /**
     * Resets the search criteria and displays all search log entries.
     *
     * @param form the search form to reset
     * @return HTML response with the reset search log list
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse reset(final SearchForm form) {
        validate(form, messages -> {}, this::asListHtml);
        saveToken();
        searchLogPager.clear();
        return asHtml(path_AdminSearchlog_AdminSearchlogJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Returns to the search log list from a detail view.
     *
     * @param form the search form containing current state
     * @return HTML response for the search log list page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse back(final SearchForm form) {
        validate(form, messages -> {}, this::asListHtml);
        saveToken();
        return asHtml(path_AdminSearchlog_AdminSearchlogJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Sets up search paging data for rendering the search log list.
     *
     * @param data the render data to populate
     * @param form the search form containing current search criteria
     */
    protected void searchPaging(final RenderData data, final SearchForm form) {
        RenderDataUtil.register(data, "searchLogItems", searchLogService.getSearchLogList(searchLogPager)); // page navi
        RenderDataUtil.register(data, "tabItems", SearchLogAnalyticsService.TABS);

        // restore from pager
        copyBeanToBean(searchLogPager, form, op -> op.include(CONDITION_FIELDS));
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============

    // -----------------------------------------------------
    //                                               Details
    //                                               -------
    /**
     * Displays the details of a specific search log entry.
     *
     * @param crudMode the CRUD mode for the operation
     * @param logType the type of log entry
     * @param id the ID of the search log entry to display
     * @return HTML response for the search log details page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse details(final int crudMode, final String logType, final String id) {
        verifyCrudMode(crudMode, CrudMode.DETAILS, this::asListHtml);
        saveToken();
        return asDetailsHtml().useForm(EditForm.class, op -> {
            op.setup(form -> {
                form.id = id;
                form.logType = logType;
                form.crudMode = crudMode;
            });
        }).renderWith(data -> {
            RenderDataUtil.register(data, "logParamItems", searchLogService.getSearchLogMap(logType, id));
        });
    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    /**
     * Deletes a specific search log entry.
     *
     * @param form the edit form containing the log entry information
     * @return HTML response redirecting to the list page after deletion
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse delete(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.DETAILS, this::asListHtml);
        validate(form, messages -> {}, this::asDetailsHtml);
        verifyToken(this::asDetailsHtml);
        searchLogService.getSearchLog(form.logType, form.id).alwaysPresent(e -> {
            searchLogService.deleteSearchLog(e);
            saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        });
        return redirectWith(getClass(), moreUrl("logs"));
    }

    /**
     * Deletes all search log entries.
     *
     * @return HTML response redirecting to the list page after deletion
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse deleteall() {
        verifyToken(this::asListHtml);
        searchLogPager.clear();
        // TODO delete logs
        saveInfo(messages -> messages.addSuccessCrawlingInfoDeleteAll(GLOBAL));
        return redirectWith(getClass(), moreUrl("logs"));
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    /**
     * Checks whether the name is one of the analytics tabs.
     *
     * @param tab the tab name
     * @return true if the tab exists
     */
    static boolean isValidTab(final String tab) {
        return tab != null && SearchLogAnalyticsService.TABS.contains(tab);
    }

    /**
     * Serializes a value to JSON that is safe to embed in an inline script block.
     *
     * @param value the value to serialize
     * @return the JSON text with HTML-significant characters escaped, or "{}" when serialization fails
     */
    static String toEmbeddedJson(final Object value) {
        try {
            return new ObjectMapper().writeValueAsString(value)
                    .replace("<", "\\u003c")
                    .replace(">", "\\u003e")
                    .replace("&", "\\u0026")
                    .replace("'", "\\u0027")
                    .replace("\u2028", "\\u2028")
                    .replace("\u2029", "\\u2029");
        } catch (final Exception e) {
            logger.warn("Failed to serialize the chart data.", e);
            return "{}";
        }
    }

    private AnalyticsCondition createAnalyticsCondition(final String tab, final AnalyticsForm form) {
        final int defaultSize = SearchLogAnalyticsService.TAB_OVERVIEW.equals(tab) ? 10 : 25;
        return AnalyticsCondition.create(form.range, form.from, form.to, form.compare, form.accessType, form.size, defaultSize,
                Clock.systemDefaultZone());
    }

    /**
     * Streams a CSV file in the CSV encoding of the configuration, with a byte order mark for UTF-8 so
     * that Excel reads it as UTF-8. The size is not known and not limited, so nothing is buffered
     * beyond the writer. A failure to read the logs is rethrown, so the browser gets an error instead
     * of a file that looks complete; a failure to write means the client is gone and is only logged.
     *
     * @param fileName the name of the downloaded file
     * @param writeCall writes the CSV
     * @return the stream response
     */
    private StreamResponse asCsvStream(final String fileName, final CsvWriteCall writeCall) {
        final String encoding = fessConfig.getCsvFileEncoding();
        return asStream(fileName).contentTypeOctetStream().stream(out -> {
            // not closed: the container owns the response stream
            final Writer writer = new BufferedWriter(new OutputStreamWriter(out.stream(), encoding));
            try {
                if (Constants.UTF_8.equalsIgnoreCase(encoding)) {
                    writer.write('\uFEFF');
                }
                writeCall.write(writer);
                writer.flush();
            } catch (final IOException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to write {} to the response.", fileName, e);
                }
            }
        });
    }

    @FunctionalInterface
    private interface CsvWriteCall {
        void write(Writer writer) throws IOException;
    }

    private HtmlResponse asAnalyticsHtml(final String tab, final AnalyticsForm form) {
        final AnalyticsCondition cond = createAnalyticsCondition(tab, form);
        final AnalyticsReport report = searchLogAnalyticsService.getReport(tab, cond);
        if (cond.isAdjusted()) {
            report.getNotices().add(0, new AnalyticsReport.Notice("warning", "labels.searchlog_notice_range_adjusted"));
        }
        final Locale locale = ComponentUtil.getRequestManager().getUserLocale();
        localizeSeriesNames(report, locale);
        final String chartJson = toEmbeddedJson(buildChartData(report, locale));
        return asHtml(path_AdminSearchlog_AdminSearchlogAnalyticsJsp).renderWith(data -> {
            RenderDataUtil.register(data, "tab", tab);
            RenderDataUtil.register(data, "cond", cond);
            RenderDataUtil.register(data, "report", report);
            RenderDataUtil.register(data, "accessTypeItems", searchLogAnalyticsService.getAccessTypes(cond));
            RenderDataUtil.register(data, "rangeItems", AnalyticsCondition.RANGES);
            RenderDataUtil.register(data, "sizeItems", AnalyticsCondition.SIZES);
            RenderDataUtil.register(data, "tabItems", SearchLogAnalyticsService.TABS);
            RenderDataUtil.register(data, "analyticsQuery", buildAnalyticsQuery(cond, form));
            RenderDataUtil.register(data, "chartJson", chartJson);
        });
    }

    private void localizeSeriesNames(final AnalyticsReport report, final Locale locale) {
        report.getCharts().values().forEach(chart -> chart.getSeries().forEach(series -> {
            final String key = "labels.searchlog_metric_" + series.getKey();
            series.setName(ComponentUtil.getMessageManager().findMessage(locale, key).orElse(key));
        }));
    }

    private Map<String, Object> buildChartData(final AnalyticsReport report, final Locale locale) {
        final Map<String, Object> labels = new LinkedHashMap<>();
        final String previousKey = "labels.searchlog_previous_period";
        labels.put("previous", ComponentUtil.getMessageManager().findMessage(locale, previousKey).orElse(previousKey));
        final Map<String, Object> chartData = new LinkedHashMap<>();
        chartData.put("labels", labels);
        chartData.put("charts", report.getCharts());
        return chartData;
    }

    private String buildAnalyticsQuery(final AnalyticsCondition cond, final AnalyticsForm form) {
        final List<String> params = buildAnalyticsParams(cond, form);
        final StringBuilder buf = new StringBuilder();
        for (int i = 0; i < params.size(); i += 2) {
            if (buf.length() > 0) {
                buf.append('&');
            }
            buf.append(params.get(i)).append('=').append(params.get(i + 1));
        }
        return buf.toString();
    }

    /**
     * Lists the filter parameters of the condition as names and URL-encoded values, alternately.
     */
    private List<String> buildAnalyticsParams(final AnalyticsCondition cond, final AnalyticsForm form) {
        final List<String> params = new ArrayList<>();
        addParam(params, "range", cond.getRange());
        if (AnalyticsCondition.RANGE_CUSTOM.equals(cond.getRange())) {
            addParam(params, "from", cond.getFrom());
            addParam(params, "to", cond.getTo());
        }
        if (cond.isCompare()) {
            addParam(params, "compare", "true");
        }
        if (cond.getAccessType() != null) {
            addParam(params, "accessType", cond.getAccessType());
        }
        if (StringUtil.isNotBlank(form.size)) {
            addParam(params, "size", String.valueOf(cond.getSize()));
        }
        return params;
    }

    private void addParam(final List<String> params, final String name, final String value) {
        params.add(name);
        params.add(URLEncoder.encode(value, StandardCharsets.UTF_8));
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============
    //                                                                              JSP
    //                                                                           =========

    private HtmlResponse asListHtml() {
        return asHtml(path_AdminSearchlog_AdminSearchlogJsp).renderWith(data -> {
            RenderDataUtil.register(data, "searchLogItems", searchLogService.getSearchLogList(searchLogPager)); // page navi
            RenderDataUtil.register(data, "tabItems", SearchLogAnalyticsService.TABS);
        }).useForm(SearchForm.class, setup -> {
            setup.setup(form -> {
                copyBeanToBean(searchLogPager, form, op -> op.include(CONDITION_FIELDS));
            });
        });
    }

    private HtmlResponse asDetailsHtml() {
        return asHtml(path_AdminSearchlog_AdminSearchlogDetailsJsp);
    }
}
