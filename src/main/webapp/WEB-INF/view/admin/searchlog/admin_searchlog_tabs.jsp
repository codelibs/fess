<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<ul class="nav nav-tabs searchlog-tabs mb-3">
    <c:forEach var="t" items="${tabItems}">
        <li class="nav-item"><a class="nav-link ${param.tab == t ? 'active' : ''}"
                                href="${fe:url('/admin/searchlog/report/')}${f:u(t)}/<c:if test="${not empty analyticsQuery}">?${f:h(analyticsQuery)}</c:if>"><la:message
                key="labels.searchlog_tab_${t}"/></a></li>
    </c:forEach>
    <li class="nav-item"><a class="nav-link ${param.tab == 'logs' ? 'active' : ''}"
                            href="${fe:url('/admin/searchlog/logs/')}"><la:message key="labels.searchlog_tab_logs"/></a></li>
</ul>
