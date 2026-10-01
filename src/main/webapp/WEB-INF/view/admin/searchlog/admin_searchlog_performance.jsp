<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div class="row">
    <c:forEach var="chartName" items="responseTime,responseTimeDistribution,queryTime">
        <div class="${chartName == 'responseTime' ? 'col-12' : 'col-lg-6'}">
            <div class="card">
                <div class="card-header">
                    <h3 class="card-title"><la:message key="labels.searchlog_chart_${chartName}"/></h3>
                </div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${empty report.charts[chartName].x or report.charts[chartName]['empty']}">
                            <p class="searchlog-no-data text-muted"><la:message key="labels.searchlog_no_data"/></p>
                        </c:when>
                        <c:otherwise>
                            <div class="searchlog-chart" data-chart="${f:h(chartName)}"></div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </c:forEach>
</div>
<div class="card">
    <div class="card-header">
        <h3 class="card-title"><la:message key="labels.searchlog_table_slowQueries"/></h3>
    </div>
    <div class="card-body p-0">
        <table class="table table-sm table-hover mb-0">
            <thead>
            <tr>
                <th class="searchlog-rank">#</th>
                <th><la:message key="labels.searchlog_col_word"/></th>
                <th class="text-right"><la:message key="labels.searchlog_count"/></th>
                <th class="text-right"><la:message key="labels.searchlog_metric_avgResponseTime"/></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="row" items="${report.tables['slowQueries']}" varStatus="st">
                <tr>
                    <td class="searchlog-rank">${st.count}</td>
                    <td class="text-break"><a href="${searchWordLink}${f:u(row.word)}">${f:h(row.word)}</a></td>
                    <td class="text-right"><fmt:formatNumber value="${row.count}"/></td>
                    <td class="text-right text-nowrap">${row.avgResponseTime == null ? '-' : ''}<c:if
                            test="${row.avgResponseTime != null}"><fmt:formatNumber value="${row.avgResponseTime}"
                                                                                    maxFractionDigits="0"/> ms</c:if></td>
                </tr>
            </c:forEach>
            <c:if test="${empty report.tables['slowQueries']}">
                <tr>
                    <td colspan="4" class="searchlog-no-data text-muted"><la:message key="labels.searchlog_no_data"/></td>
                </tr>
            </c:if>
            </tbody>
        </table>
    </div>
</div>
