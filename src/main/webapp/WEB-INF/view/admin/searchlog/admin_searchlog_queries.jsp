<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div class="card">
    <div class="card-header">
        <h3 class="card-title"><la:message key="labels.searchlog_table_queries"/></h3>
    </div>
    <div class="card-body p-0 table-responsive">
        <table class="table table-sm table-hover mb-0">
            <thead>
            <tr>
                <th class="searchlog-rank">#</th>
                <th><la:message key="labels.searchlog_col_word"/></th>
                <th class="text-right"><la:message key="labels.searchlog_count"/></th>
                <th class="text-right"><la:message key="labels.searchlog_metric_users"/></th>
                <th class="text-right"><la:message key="labels.searchlog_metric_avgHits"/></th>
                <th class="text-right"><la:message key="labels.searchlog_metric_clicks"/></th>
                <th class="text-right"><la:message key="labels.searchlog_metric_ctr"/></th>
                <th class="text-right"><la:message key="labels.searchlog_metric_avgRank"/></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="row" items="${report.tables['queries']}" varStatus="st">
                <tr>
                    <td class="searchlog-rank">${st.count}</td>
                    <td class="text-break"><a href="${searchWordLink}${f:u(row.word)}">${f:h(row.word)}</a></td>
                    <td class="text-right"><fmt:formatNumber value="${row.count}"/></td>
                    <td class="text-right">${row.users == null ? '-' : ''}<fmt:formatNumber value="${row.users}"/></td>
                    <td class="text-right">${row.avgHits == null ? '-' : ''}<fmt:formatNumber value="${row.avgHits}"
                                                                                               maxFractionDigits="1"
                                                                                               minFractionDigits="1"/></td>
                    <td class="text-right">${row.clicks == null ? '-' : ''}<fmt:formatNumber value="${row.clicks}"/></td>
                    <td class="text-right">${row.ctr == null ? '-' : ''}<c:if test="${row.ctr != null}"><fmt:formatNumber
                            value="${row.ctr * 100}" maxFractionDigits="1" minFractionDigits="1"/>%</c:if></td>
                    <td class="text-right">${row.avgRank == null ? '-' : ''}<fmt:formatNumber value="${row.avgRank}"
                                                                                               maxFractionDigits="1"
                                                                                               minFractionDigits="1"/></td>
                </tr>
            </c:forEach>
            <c:if test="${empty report.tables['queries']}">
                <tr>
                    <td colspan="8" class="searchlog-no-data text-muted"><la:message key="labels.searchlog_no_data"/></td>
                </tr>
            </c:if>
            </tbody>
        </table>
    </div>
</div>
<div class="row">
    <div class="col-lg-6">
        <div class="card">
            <div class="card-header">
                <h3 class="card-title"><la:message key="labels.searchlog_table_zeroHitQueries"/></h3>
            </div>
            <div class="card-body p-0">
                <table class="table table-sm table-hover mb-0">
                    <thead>
                    <tr>
                        <th class="searchlog-rank">#</th>
                        <th><la:message key="labels.searchlog_col_word"/></th>
                        <th class="text-right"><la:message key="labels.searchlog_count"/></th>
                        <th class="text-right"><la:message key="labels.searchlog_metric_users"/></th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="row" items="${report.tables['zeroHitQueries']}" varStatus="st">
                        <tr>
                            <td class="searchlog-rank">${st.count}</td>
                            <td class="text-break"><a href="${searchWordLink}${f:u(row.word)}">${f:h(row.word)}</a></td>
                            <td class="text-right"><fmt:formatNumber value="${row.count}"/></td>
                            <td class="text-right">${row.users == null ? '-' : ''}<fmt:formatNumber value="${row.users}"/></td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty report.tables['zeroHitQueries']}">
                        <tr>
                            <td colspan="4" class="searchlog-no-data text-muted"><la:message
                                    key="labels.searchlog_no_data"/></td>
                        </tr>
                    </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    <div class="col-lg-6">
        <div class="card">
            <div class="card-header">
                <h3 class="card-title"><la:message key="labels.searchlog_table_zeroClickQueries"/></h3>
            </div>
            <div class="card-body p-0">
                <table class="table table-sm table-hover mb-0">
                    <thead>
                    <tr>
                        <th class="searchlog-rank">#</th>
                        <th><la:message key="labels.searchlog_col_word"/></th>
                        <th class="text-right"><la:message key="labels.searchlog_count"/></th>
                        <th class="text-right"><la:message key="labels.searchlog_metric_lastSearchedAt"/></th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="row" items="${report.tables['zeroClickQueries']}" varStatus="st">
                        <tr>
                            <td class="searchlog-rank">${st.count}</td>
                            <td class="text-break"><a href="${searchWordLink}${f:u(row.word)}">${f:h(row.word)}</a></td>
                            <td class="text-right"><fmt:formatNumber value="${row.count}"/></td>
                            <td class="text-right text-nowrap">${empty row.lastSearchedAt ? '-' : f:h(row.lastSearchedAt)}</td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty report.tables['zeroClickQueries']}">
                        <tr>
                            <td colspan="4" class="searchlog-no-data text-muted"><la:message
                                    key="labels.searchlog_no_data"/></td>
                        </tr>
                    </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
