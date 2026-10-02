<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<c:set var="trend" value="${report.charts['trend']}"/>
<c:set var="trendNoData" value="${empty trend.x or trend['empty']}"/>
<div class="card">
    <div class="card-header">
        <h3 class="card-title"><la:message key="labels.searchlog_chart_trend"/></h3>
        <div class="card-tools">
            <c:if test="${not trendNoData}">
                <div class="btn-group btn-group-sm" role="group">
                    <c:forEach var="s" items="${trend.series}" varStatus="st">
                        <button type="button" class="btn btn-outline-secondary ${st.first ? 'active' : ''}"
                                data-chart-target="trend" data-series="${f:h(s.key)}"><la:message
                                key="labels.searchlog_metric_${s.key}"/></button>
                    </c:forEach>
                </div>
            </c:if>
            <jsp:include page="/WEB-INF/view/admin/searchlog/admin_searchlog_csvlink.jsp"><jsp:param name="item" value="trend"/></jsp:include>
        </div>
    </div>
    <div class="card-body">
        <c:choose>
            <c:when test="${trendNoData}">
                <p class="searchlog-no-data text-muted"><la:message key="labels.searchlog_no_data"/></p>
            </c:when>
            <c:otherwise>
                <div class="searchlog-chart" data-chart="trend"></div>
            </c:otherwise>
        </c:choose>
    </div>
</div>
<div class="card">
    <div class="card-header">
        <h3 class="card-title"><la:message key="labels.searchlog_table_chatUsers"/></h3>
        <div class="card-tools">
            <jsp:include page="/WEB-INF/view/admin/searchlog/admin_searchlog_csvlink.jsp"><jsp:param name="item" value="chatUsers"/></jsp:include>
        </div>
    </div>
    <div class="card-body p-0">
        <table class="table table-sm table-hover mb-0">
            <thead>
            <tr>
                <th class="searchlog-rank">#</th>
                <th><la:message key="labels.searchlog_col_user"/></th>
                <th><la:message key="labels.searchlog_metric_requests"/></th>
                <th class="text-right"><la:message key="labels.searchlog_metric_promptTokens"/></th>
                <th class="text-right"><la:message key="labels.searchlog_metric_completionTokens"/></th>
                <th class="text-right"><la:message key="labels.searchlog_metric_totalTokens"/></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="row" items="${report.tables['chatUsers']}" varStatus="st">
                <tr>
                    <td class="searchlog-rank">${row.guest ? '' : st.count}</td>
                    <td class="text-break"><c:choose>
                        <c:when test="${row.guest}"><span class="text-muted"><la:message
                                key="labels.searchlog_chat_guest"/></span></c:when>
                        <c:otherwise>${f:h(row.user)}</c:otherwise>
                    </c:choose></td>
                    <td class="searchlog-bar-cell">
                        <div class="searchlog-bar" style="width:${row.bar}%"></div>
                        <span><fmt:formatNumber value="${row.count}"/></span></td>
                    <td class="text-right">${row.promptTokens == null ? '-' : ''}<c:if
                            test="${row.promptTokens != null}"><fmt:formatNumber value="${row.promptTokens}"/></c:if></td>
                    <td class="text-right">${row.completionTokens == null ? '-' : ''}<c:if
                            test="${row.completionTokens != null}"><fmt:formatNumber value="${row.completionTokens}"/></c:if></td>
                    <td class="text-right">${row.totalTokens == null ? '-' : ''}<c:if
                            test="${row.totalTokens != null}"><fmt:formatNumber value="${row.totalTokens}"/></c:if></td>
                </tr>
            </c:forEach>
            <c:if test="${empty report.tables['chatUsers']}">
                <tr>
                    <td colspan="6" class="searchlog-no-data text-muted"><la:message key="labels.searchlog_no_data"/></td>
                </tr>
            </c:if>
            </tbody>
        </table>
    </div>
</div>
<div class="row">
    <c:forEach var="tableName" items="chatIntents,chatModels">
        <div class="col-lg-6">
            <div class="card">
                <div class="card-header">
                    <h3 class="card-title"><la:message key="labels.searchlog_table_${tableName}"/></h3>
                    <div class="card-tools">
                        <jsp:include page="/WEB-INF/view/admin/searchlog/admin_searchlog_csvlink.jsp"><jsp:param name="item" value="${tableName}"/></jsp:include>
                    </div>
                </div>
                <div class="card-body p-0">
                    <table class="table table-sm table-hover mb-0">
                        <thead>
                        <tr>
                            <th class="searchlog-rank">#</th>
                            <th><la:message key="labels.searchlog_col_${tableName == 'chatIntents' ? 'intent' : 'model'}"/></th>
                            <th><la:message key="labels.searchlog_metric_requests"/></th>
                            <c:if test="${tableName == 'chatModels'}">
                                <th class="text-right"><la:message key="labels.searchlog_metric_llmCalls"/></th>
                            </c:if>
                            <th class="text-right"><la:message key="labels.searchlog_metric_totalTokens"/></th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="row" items="${report.tables[tableName]}" varStatus="st">
                            <tr>
                                <td class="searchlog-rank">${st.count}</td>
                                <td class="text-break">${f:h(row.value)}</td>
                                <td class="searchlog-bar-cell">
                                    <div class="searchlog-bar" style="width:${row.bar}%"></div>
                                    <span><fmt:formatNumber value="${row.count}"/></span></td>
                                <c:if test="${tableName == 'chatModels'}">
                                    <td class="text-right">${row.llmCalls == null ? '-' : ''}<c:if
                                            test="${row.llmCalls != null}"><fmt:formatNumber value="${row.llmCalls}"/></c:if></td>
                                </c:if>
                                <td class="text-right">${row.totalTokens == null ? '-' : ''}<c:if
                                        test="${row.totalTokens != null}"><fmt:formatNumber value="${row.totalTokens}"/></c:if></td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty report.tables[tableName]}">
                            <tr>
                                <td colspan="${tableName == 'chatModels' ? 5 : 4}" class="searchlog-no-data text-muted"><la:message
                                        key="labels.searchlog_no_data"/></td>
                            </tr>
                        </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </c:forEach>
</div>
