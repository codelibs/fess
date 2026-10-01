<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div class="row">
    <c:forEach var="chartName" items="rankDistribution,pagingRate">
        <div class="col-lg-6">
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
<div class="row">
    <c:forEach var="tableName" items="clickedUrls,favoriteUrls">
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
                            <th><la:message key="labels.searchlog_col_url"/></th>
                            <th><la:message key="labels.searchlog_count"/></th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="row" items="${report.tables[tableName]}" varStatus="st">
                            <tr>
                                <td class="searchlog-rank">${st.count}</td>
                                <td class="text-break">${f:h(row.url)}</td>
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
