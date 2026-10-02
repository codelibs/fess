<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<div class="row">
    <c:forEach var="chartName" items="users,accessTypes">
        <div class="${chartName == 'users' ? 'col-lg-8' : 'col-lg-4'}">
            <div class="card">
                <div class="card-header">
                    <h3 class="card-title"><la:message key="labels.searchlog_chart_${chartName}"/></h3>
                    <div class="card-tools">
                        <jsp:include page="/WEB-INF/view/admin/searchlog/admin_searchlog_csvlink.jsp"><jsp:param name="item" value="${chartName}"/></jsp:include>
                    </div>
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
        <h3 class="card-title"><la:message key="labels.searchlog_table_weekHour"/></h3>
        <div class="card-tools">
            <jsp:include page="/WEB-INF/view/admin/searchlog/admin_searchlog_csvlink.jsp"><jsp:param name="item" value="weekHour"/></jsp:include>
        </div>
    </div>
    <div class="card-body table-responsive">
        <c:choose>
            <c:when test="${empty report.tables['weekHour']}">
                <p class="searchlog-no-data text-muted"><la:message key="labels.searchlog_no_data"/></p>
            </c:when>
            <c:otherwise>
                <table class="table table-sm table-bordered searchlog-heatmap mb-0">
                    <thead>
                    <tr>
                        <th></th>
                        <c:forEach begin="0" end="23" var="h">
                            <th class="text-center small">${h}</th>
                        </c:forEach>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="r" items="${report.tables['weekHour']}">
                        <tr>
                            <th class="small"><la:message key="labels.searchlog_dow_${r.day}"/></th>
                            <c:forEach var="cell" items="${r.cells}">
                                <td class="searchlog-heat-${cell.level}" title="${cell.count}"></td>
                            </c:forEach>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>
</div>
<div class="row">
    <c:forEach var="tableName" items="userAgents,referers,languages,virtualHosts">
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
                            <th><la:message key="labels.searchlog_value"/></th>
                            <th><la:message key="labels.searchlog_count"/></th>
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
<div class="row">
    <div class="col-lg-6">
        <div class="card">
            <div class="card-header">
                <h3 class="card-title"><la:message key="labels.searchlog_table_roles"/></h3>
                <div class="card-tools">
                    <jsp:include page="/WEB-INF/view/admin/searchlog/admin_searchlog_csvlink.jsp"><jsp:param name="item" value="roles"/></jsp:include>
                </div>
            </div>
            <div class="card-body p-0">
                <table class="table table-sm table-hover mb-0">
                    <thead>
                    <tr>
                        <th class="searchlog-rank">#</th>
                        <th><la:message key="labels.searchlog_col_role"/></th>
                        <th><la:message key="labels.searchlog_metric_searches"/></th>
                        <th class="text-right"><la:message key="labels.searchlog_metric_users"/></th>
                        <th class="text-right"><la:message key="labels.searchlog_metric_zeroHitRate"/></th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="row" items="${report.tables['roles']}" varStatus="st">
                        <tr>
                            <td class="searchlog-rank">${st.count}</td>
                            <td class="text-break">${f:h(row.value)}</td>
                            <td class="searchlog-bar-cell">
                                <div class="searchlog-bar" style="width:${row.bar}%"></div>
                                <span><fmt:formatNumber value="${row.count}"/></span></td>
                            <td class="text-right">${row.users == null ? '-' : ''}<fmt:formatNumber value="${row.users}"/></td>
                            <td class="text-right">${row.zeroHitRate == null ? '-' : ''}<c:if test="${row.zeroHitRate != null}"><fmt:formatNumber
                                    value="${row.zeroHitRate * 100}" maxFractionDigits="1" minFractionDigits="1"/>%</c:if></td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty report.tables['roles']}">
                        <tr>
                            <td colspan="5" class="searchlog-no-data text-muted"><la:message
                                    key="labels.searchlog_no_data"/></td>
                        </tr>
                    </c:if>
                    </tbody>
                </table>
            </div>
            <div class="card-footer small text-muted"><la:message key="labels.searchlog_note_roles"/></div>
        </div>
    </div>
</div>
