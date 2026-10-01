<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<c:set var="trend" value="${report.charts['trend']}"/>
<c:set var="trendNoData" value="${empty trend.x or trend['empty']}"/>
<div class="card">
    <div class="card-header">
        <h3 class="card-title"><la:message key="labels.searchlog_chart_trend"/></h3>
        <c:if test="${not trendNoData}">
            <div class="card-tools">
                <div class="btn-group btn-group-sm" role="group">
                    <c:forEach var="s" items="${trend.series}" varStatus="st">
                        <button type="button" class="btn btn-outline-secondary ${st.first ? 'active' : ''}"
                                data-chart-target="trend" data-series="${f:h(s.key)}"><la:message
                                key="labels.searchlog_metric_${s.key}"/></button>
                    </c:forEach>
                </div>
            </div>
        </c:if>
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
<div class="row">
    <c:forEach var="tableName" items="topQueries,zeroHitQueries">
        <div class="col-lg-6">
            <div class="card">
                <div class="card-header">
                    <h3 class="card-title"><la:message key="labels.searchlog_table_${tableName}"/></h3>
                </div>
                <div class="card-body p-0">
                    <table class="table table-sm table-hover mb-0">
                        <thead>
                        <tr>
                            <th class="searchlog-rank">#</th>
                            <th><la:message key="labels.searchlog_col_word"/></th>
                            <th><la:message key="labels.searchlog_count"/></th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="row" items="${report.tables[tableName]}" varStatus="st">
                            <tr>
                                <td class="searchlog-rank">${st.count}</td>
                                <td class="text-break"><a href="${searchWordLink}${f:u(row.word)}">${f:h(row.word)}</a></td>
                                <td class="searchlog-bar-cell">
                                    <div class="searchlog-bar" style="width:${row.bar}%"></div>
                                    <span><fmt:formatNumber value="${row.count}"/></span></td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty report.tables[tableName]}">
                            <tr>
                                <td colspan="3" class="searchlog-no-data text-muted"><la:message
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
