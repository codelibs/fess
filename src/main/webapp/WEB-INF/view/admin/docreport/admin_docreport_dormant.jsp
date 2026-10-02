<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<form method="get" action="${fe:url('/admin/docreport/dormant')}" class="form-inline mb-3">
    <label for="docreport-url" class="mr-2"><la:message key="labels.docreport_url"/></label>
    <input type="text" id="docreport-url" name="url" value="${f:h(url)}" maxlength="1000"
           class="form-control form-control-sm mr-2 docreport-url"
           placeholder="<la:message key="labels.docreport_url_placeholder"/>">
    <label for="docreport-days" class="mr-2"><la:message key="labels.docreport_days"/></label>
    <input type="number" id="docreport-days" name="days" value="${f:h(days)}" min="1" max="36500"
           class="form-control form-control-sm mr-2">
    <div class="form-check mr-3">
        <input type="checkbox" id="docreport-unclicked" name="unclicked" value="true"
               class="form-check-input" ${unclicked ? 'checked' : ''}>
        <label for="docreport-unclicked" class="form-check-label"><la:message key="labels.docreport_unclicked"/></label>
    </div>
    <button type="submit" class="btn btn-primary btn-sm mr-2">
        <la:message key="labels.docreport_apply"/>
    </button>
    <a class="btn btn-default btn-sm"
       href="${fe:url('/admin/docreport/downloaddormant')}?${f:h(reportQuery)}"><i
            class="fa fa-download" aria-hidden="true"></i> <la:message key="labels.docreport_download_csv"/></a>
</form>
<c:choose>
    <c:when test="${report.failed}">
        <div class="alert alert-danger"><la:message key="labels.docreport_failed"/></div>
    </c:when>
    <c:otherwise>
        <p class="text-muted"><la:message key="labels.docreport_dormant_description"/></p>
        <c:if test="${pageLimited}">
            <fmt:formatNumber value="${maxDocs}" var="maxDocsText"/>
            <div class="alert alert-info"><la:message key="labels.docreport_dormant_limited" arg0="${maxDocsText}"/></div>
        </c:if>
        <div class="card">
            <div class="card-header">
                <h3 class="card-title"><fmt:formatNumber value="${report.total}" var="totalText"/><fmt:formatNumber
                        value="${report.totalSize}" var="totalSizeText"/><la:message key="labels.docreport_dormant_summary"
                                                                                     arg0="${totalText}"
                                                                                     arg1="${totalSizeText}"/></h3>
            </div>
            <div class="card-body p-0 table-responsive">
                <table class="table table-sm table-hover mb-0">
                    <thead>
                    <tr>
                        <th><la:message key="labels.docreport_col_title"/></th>
                        <th class="text-right"><la:message key="labels.docreport_col_size"/></th>
                        <th><la:message key="labels.docreport_col_last_modified"/></th>
                        <th><la:message key="labels.docreport_col_owner"/></th>
                        <th><la:message key="labels.docreport_col_last_modifier"/></th>
                        <th class="text-right"><la:message key="labels.docreport_col_clicks"/></th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="doc" items="${report.docs}">
                        <tr>
                            <td class="text-break">
                                <div>${f:h(empty doc.title ? doc.filename : doc.title)}</div>
                                <div class="small text-muted">${f:h(doc.url)}</div>
                            </td>
                            <td class="text-right text-nowrap"><fmt:formatNumber value="${doc.contentLength}"/></td>
                            <td class="text-nowrap"><fmt:formatDate value="${doc.lastModified}" pattern="yyyy-MM-dd HH:mm"/></td>
                            <td class="text-break">${f:h(doc.owner)}</td>
                            <td class="text-break">${f:h(doc.lastModifier)}</td>
                            <td class="text-right"><fmt:formatNumber value="${doc.clickCount}"/></td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty report.docs}">
                        <tr>
                            <td colspan="6" class="text-muted"><la:message key="labels.docreport_no_data"/></td>
                        </tr>
                    </c:if>
                    </tbody>
                </table>
            </div>
            <c:if test="${lastPage > 1}">
                <div class="card-footer">
                    <ul class="pagination pagination-sm justify-content-center mb-0">
                        <li class="page-item ${currentPage > 1 ? '' : 'disabled'}"><a class="page-link"
                                                                                      href="${currentPage > 1 ? fe:url('/admin/docreport/dormant') += '?' += f:h(reportQuery) += '&amp;pn=' += (currentPage - 1) : '#'}"><la:message
                                key="labels.prev_page"/></a></li>
                        <li class="page-item active"><span class="page-link">${currentPage} / ${lastPage}</span></li>
                        <li class="page-item ${currentPage < lastPage ? '' : 'disabled'}"><a class="page-link"
                                                                                             href="${currentPage < lastPage ? fe:url('/admin/docreport/dormant') += '?' += f:h(reportQuery) += '&amp;pn=' += (currentPage + 1) : '#'}"><la:message
                                key="labels.next_page"/></a></li>
                    </ul>
                </div>
            </c:if>
        </div>
    </c:otherwise>
</c:choose>
