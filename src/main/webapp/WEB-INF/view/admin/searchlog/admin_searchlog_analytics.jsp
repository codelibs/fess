<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
${fe:html(true)}
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.searchlog_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="log"/>
        <jsp:param name="menuType" value="searchLog"/>
    </jsp:include>
    <main id="mainContent" class="content-wrapper">
        <div class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>
                            <la:message key="labels.searchlog_configuration"/>
                        </h1>
                    </div>
                    <div class="col-sm-6">
                        <ol class="breadcrumb float-sm-right">
                            <li class="breadcrumb-item active"><la:link href="/admin/searchlog">
                                <la:message key="labels.searchlog_title"/>
                            </la:link></li>
                        </ol>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <jsp:include page="/WEB-INF/view/admin/searchlog/admin_searchlog_tabs.jsp">
                <jsp:param name="tab" value="${tab}"/>
            </jsp:include>
            <%-- Filter --%>
            <form method="get" action="${fe:url('/admin/searchlog/report/')}${f:u(tab)}/"
                  class="form-inline searchlog-filter mb-3">
                <label for="searchlog-range" class="mr-2"><la:message key="labels.searchlog_range"/></label>
                <select id="searchlog-range" name="range" class="form-control form-control-sm mr-2">
                    <c:forEach var="r" items="${rangeItems}">
                        <option value="${f:h(r)}" ${cond.range == r ? 'selected' : ''}><la:message
                                key="labels.searchlog_range_${r}"/></option>
                    </c:forEach>
                </select>
                <div id="searchlog-custom-range" class="form-inline mr-2 ${cond.range == 'custom' ? '' : 'd-none'}">
                    <label for="searchlog-from" class="sr-only"><la:message key="labels.searchlog_range_from"/></label>
                    <input type="date" id="searchlog-from" name="from" value="${f:h(cond.from)}"
                           class="form-control form-control-sm">
                    <span class="mx-1">-</span>
                    <label for="searchlog-to" class="sr-only"><la:message key="labels.searchlog_range_to"/></label>
                    <input type="date" id="searchlog-to" name="to" value="${f:h(cond.to)}"
                           class="form-control form-control-sm">
                </div>
                <c:if test="${tab == 'chat'}">
                    <%-- the access type filter does not apply to chat logs; keep it for the other tabs --%>
                    <c:if test="${not empty cond.accessType}"><input type="hidden" name="accessType" value="${f:h(cond.accessType)}"></c:if>
                </c:if>
                <c:if test="${tab != 'chat'}">
                    <label for="searchlog-accesstype" class="sr-only"><la:message key="labels.searchlog_accesstype"/></label>
                    <select id="searchlog-accesstype" name="accessType" class="form-control form-control-sm mr-2">
                        <option value=""><la:message key="labels.searchlog_accesstype_all"/></option>
                        <c:set var="accessTypeListed" value="${false}"/>
                        <c:forEach var="a" items="${accessTypeItems}">
                            <c:if test="${cond.accessType == a}"><c:set var="accessTypeListed" value="${true}"/></c:if>
                            <option value="${f:h(a)}" ${cond.accessType == a ? 'selected' : ''}>${f:h(a)}</option>
                        </c:forEach>
                        <c:if test="${not empty cond.accessType and not accessTypeListed}">
                            <option value="${f:h(cond.accessType)}" selected>${f:h(cond.accessType)}</option>
                        </c:if>
                    </select>
                </c:if>
                <label for="searchlog-size" class="mr-2"><la:message key="labels.searchlog_size"/></label>
                <select id="searchlog-size" name="size" class="form-control form-control-sm mr-2">
                    <option value="">-</option>
                    <c:forEach var="s" items="${sizeItems}">
                        <option value="${s}" ${not empty param.size and cond.size == s ? 'selected' : ''}>${s}</option>
                    </c:forEach>
                </select>
                <div class="form-check mr-3">
                    <input type="checkbox" id="searchlog-compare" name="compare" value="true"
                           class="form-check-input" ${cond.compare ? 'checked' : ''}>
                    <label for="searchlog-compare" class="form-check-label"><la:message
                            key="labels.searchlog_compare"/></label>
                </div>
                <button type="submit" class="btn btn-primary btn-sm">
                    <la:message key="labels.searchlog_apply"/>
                </button>
                <c:if test="${not empty report.kpis}">
                    <span class="ml-2"><jsp:include page="/WEB-INF/view/admin/searchlog/admin_searchlog_csvlink.jsp"><jsp:param name="item" value="kpis"/></jsp:include></span>
                </c:if>
                <span class="ml-auto text-muted small"><i class="far fa-calendar-alt" aria-hidden="true"></i>
                    ${f:h(cond.from)} - ${f:h(cond.to)}</span>
            </form>
            <%-- Notices --%>
            <c:forEach var="n" items="${report.notices}">
                <div class="alert alert-${f:h(n.level)}"><la:message key="${n.key}"
                                                                    arg0="${fn:length(n.args) > 0 ? f:h(n.args[0]) : ''}"/></div>
            </c:forEach>
            <%-- Link from a query word to the raw search logs of the period --%>
            <c:set var="searchLogLink"
                   value="${fe:url('/admin/searchlog/search')}?logType=search&amp;requestedTimeRange=${f:u(cond.from += ' 00:00 - ' += cond.to += ' 23:59')}${empty cond.accessType ? '' : '&amp;accessType='}${f:u(cond.accessType)}"/>
            <c:set var="searchWordLink" scope="request" value="${searchLogLink}&amp;searchWord="/>
            <c:set var="zeroHitWordLink" scope="request" value="${searchLogLink}&amp;hitCount=zero&amp;searchWord="/>
            <%-- KPI cards (overview, clicks and chat) --%>
            <c:if test="${not empty report.kpis}">
                <div class="row">
                    <c:forEach var="k" items="${report.kpis}">
                        <c:set var="riseIsBad"
                               value="${k.key == 'zeroHitRate' or k.key == 'avgResponseTime' or k.key == 'avgRank' or k.key == 'errorRate'}"/>
                        <div class="col-lg col-md-4 col-sm-6">
                            <div class="card searchlog-kpi">
                                <div class="card-body">
                                    <div class="text-muted small"><la:message key="labels.searchlog_metric_${k.key}"/></div>
                                    <div class="h3 mb-0"><c:choose>
                                        <c:when test="${k.value == null}">-</c:when>
                                        <c:when test="${k.unit == 'percent'}"><fmt:formatNumber value="${k.value * 100}"
                                                                                                maxFractionDigits="1"
                                                                                                minFractionDigits="1"/>%</c:when>
                                        <c:when test="${k.unit == 'ms'}"><fmt:formatNumber value="${k.value}"
                                                                                           maxFractionDigits="0"/> ms</c:when>
                                        <c:when test="${k.unit == 'rank'}"><fmt:formatNumber value="${k.value}"
                                                                                             maxFractionDigits="1"
                                                                                             minFractionDigits="1"/></c:when>
                                        <c:otherwise><fmt:formatNumber value="${k.value}"/></c:otherwise>
                                    </c:choose></div>
                                    <c:if test="${k.change == null and cond.compare}">
                                        <div class="small text-muted">&mdash;</div>
                                    </c:if>
                                    <c:if test="${k.change != null}">
                                        <div class="small ${k.change == 0 ? 'text-muted' : ((k.change > 0) != riseIsBad ? 'text-success' : 'text-danger')}">
                                            ${k.change > 0 ? '&#9650;' : (k.change < 0 ? '&#9660;' : '')}
                                            <fmt:formatNumber value="${k.change < 0 ? -k.change : k.change}" maxFractionDigits="1"
                                                              minFractionDigits="1"/>${k.pointChange ? ' pt' : '%'}
                                        </div>
                                    </c:if>
                                    <c:if test="${(tab == 'overview' or tab == 'chat') and not empty report.charts['trend'].x and not report.charts['trend']['empty']}">
                                        <div class="searchlog-sparkline" data-chart="trend" data-series="${f:h(k.key)}"></div>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:if>
            <c:choose>
                <c:when test="${tab == 'queries'}">
                    <jsp:include page="/WEB-INF/view/admin/searchlog/admin_searchlog_queries.jsp"/>
                </c:when>
                <c:when test="${tab == 'clicks'}">
                    <jsp:include page="/WEB-INF/view/admin/searchlog/admin_searchlog_clicks.jsp"/>
                </c:when>
                <c:when test="${tab == 'performance'}">
                    <jsp:include page="/WEB-INF/view/admin/searchlog/admin_searchlog_performance.jsp"/>
                </c:when>
                <c:when test="${tab == 'audience'}">
                    <jsp:include page="/WEB-INF/view/admin/searchlog/admin_searchlog_audience.jsp"/>
                </c:when>
                <c:when test="${tab == 'chat'}">
                    <jsp:include page="/WEB-INF/view/admin/searchlog/admin_searchlog_chat.jsp"/>
                </c:when>
                <c:otherwise>
                    <jsp:include page="/WEB-INF/view/admin/searchlog/admin_searchlog_overview.jsp"/>
                </c:otherwise>
            </c:choose>
            <script type="application/json" id="searchlog-analytics-data">${chartJson}</script>
        </section>
    </main>
    <jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>
</div>
<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
<script src="${fe:url('/js/admin/plugins/echarts/echarts.common.min.js')}" type="text/javascript"></script>
<script src="${fe:url('/js/admin/searchlog.js')}" type="text/javascript"></script>
</body>
${fe:html(false)}
