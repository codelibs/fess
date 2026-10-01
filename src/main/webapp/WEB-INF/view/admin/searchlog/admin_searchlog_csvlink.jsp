<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%-- CSV download link of one item of the current analytics tab; the item name comes in as the "item" parameter --%>
<a class="btn btn-tool" href="${fe:url('/admin/searchlog/downloadreport/')}${f:u(tab)}/${f:u(param.item)}/<c:if test="${not empty analyticsQuery}">?${f:h(analyticsQuery)}</c:if>"
   title="<la:message key="labels.searchlog_download_csv"/>"><i class="fa fa-download" aria-hidden="true"></i> CSV</a>
