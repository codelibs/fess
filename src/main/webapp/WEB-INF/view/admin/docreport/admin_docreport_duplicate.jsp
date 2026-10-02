<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<form method="get" action="${fe:url('/admin/docreport/duplicate')}" class="form-inline mb-3">
    <label for="docreport-url" class="mr-2"><la:message key="labels.docreport_url"/></label>
    <input type="text" id="docreport-url" name="url" value="${f:h(url)}" maxlength="1000"
           class="form-control form-control-sm mr-2 docreport-url"
           placeholder="<la:message key="labels.docreport_url_placeholder"/>">
    <button type="submit" class="btn btn-primary btn-sm mr-2">
        <la:message key="labels.docreport_apply"/>
    </button>
    <c:if test="${not report.unavailable}">
        <a class="btn btn-default btn-sm"
           href="${fe:url('/admin/docreport/downloadduplicate')}${empty reportQuery ? '' : '?'}${f:h(reportQuery)}"><i
                class="fa fa-download" aria-hidden="true"></i> <la:message key="labels.docreport_download_csv"/></a>
    </c:if>
</form>
<c:choose>
    <c:when test="${report.unavailable}">
        <div class="alert alert-warning"><la:message key="labels.docreport_duplicate_unavailable"/></div>
    </c:when>
    <c:when test="${report.failed}">
        <div class="alert alert-danger"><la:message key="labels.docreport_failed"/></div>
    </c:when>
    <c:otherwise>
        <p class="text-muted"><la:message key="labels.docreport_duplicate_description" arg0="${f:h(groupSize)}"/></p>
        <c:if test="${empty report.groups}">
            <div class="alert alert-info"><la:message key="labels.docreport_no_data"/></div>
        </c:if>
        <c:forEach var="group" items="${report.groups}" varStatus="st">
            <div class="card docreport-group">
                <div class="card-header">
                    <h3 class="card-title"><fmt:formatNumber value="${group.count}" var="groupCount"/><la:message
                            key="labels.docreport_duplicate_group" arg0="${st.count}" arg1="${groupCount}"/></h3>
                </div>
                <div class="card-body p-0 table-responsive">
                    <table class="table table-sm table-hover mb-0">
                        <thead>
                        <tr>
                            <th><la:message key="labels.docreport_col_title"/></th>
                            <th class="text-right"><la:message key="labels.docreport_col_size"/></th>
                            <th><la:message key="labels.docreport_col_last_modified"/></th>
                            <th><la:message key="labels.docreport_col_owner"/></th>
                            <th class="text-right"><la:message key="labels.docreport_col_clicks"/></th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="doc" items="${group.docs}">
                            <tr>
                                <td class="text-break">
                                    <div>${f:h(empty doc.title ? doc.filename : doc.title)}</div>
                                    <div class="small text-muted">${f:h(doc.url)}</div>
                                </td>
                                <td class="text-right text-nowrap"><fmt:formatNumber value="${doc.contentLength}"/></td>
                                <td class="text-nowrap"><fmt:formatDate value="${doc.lastModified}" pattern="yyyy-MM-dd HH:mm"/></td>
                                <td class="text-break">${f:h(doc.owner)}</td>
                                <td class="text-right"><fmt:formatNumber value="${doc.clickCount}"/></td>
                            </tr>
                        </c:forEach>
                        <c:if test="${group.count > fn:length(group.docs)}">
                            <tr>
                                <td colspan="5" class="text-muted small"><fmt:formatNumber
                                        value="${group.count - fn:length(group.docs)}" var="moreCount"/><la:message
                                        key="labels.docreport_duplicate_more" arg0="${moreCount}"/></td>
                            </tr>
                        </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </c:forEach>
    </c:otherwise>
</c:choose>
