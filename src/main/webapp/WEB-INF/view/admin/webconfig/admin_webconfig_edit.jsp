<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %><!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.web_crawling_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="crawl"/>
        <jsp:param name="menuType" value="webConfig"/>
    </jsp:include>
    <div class="content-wrapper">
        <div class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>
                            <la:message key="labels.web_crawling_title_details"/>
                        </h1>
                    </div>
                    <div class="col-sm-6">
                        <jsp:include page="/WEB-INF/view/common/admin/crud/breadcrumb.jsp"></jsp:include>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <la:form action="/admin/webconfig/">
                <la:hidden property="crudMode"/>
                <c:if test="${crudMode==2}">
                    <la:hidden property="id"/>
                    <la:hidden property="versionNo"/>
                </c:if>
                <la:hidden property="createdBy"/>
                <la:hidden property="createdTime"/>
                <la:hidden property="sortOrder"/>
                <div class="row">
                    <div class="col-md-12">
                        <div
                                class="card card-outline <c:if test="${crudMode == 1 || crudMode == 2}">card-success</c:if>">
                            <div class="card-header">
                                <jsp:include page="/WEB-INF/view/common/admin/crud/header.jsp"></jsp:include>
                            </div>
                            <div class="card-body">
                                <div class="form-group row">
                                    <la:info id="msg" message="true">
                                        <div class="alert alert-info">${msg}</div>
                                    </la:info>
                                    <la:errors property="_global"/>
                                </div>
                                <div class="form-group row">
                                    <label for="name" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.name"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="name"/>
                                        <la:text styleId="name" property="name" styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="urls" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.urls"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="urls"/>
                                        <la:textarea styleId="urls" property="urls" styleClass="form-control"
                                                     rows="5"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="includedUrls" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.included_urls"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="includedUrls"/>
                                        <la:textarea styleId="includedUrls" property="includedUrls"
                                                     styleClass="form-control" rows="5"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="excludedUrls" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.excluded_urls"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="excludedUrls"/>
                                        <la:textarea styleId="excludedUrls" property="excludedUrls"
                                                     styleClass="form-control" rows="5"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="includedDocUrls" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.included_doc_urls"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="includedDocUrls"/>
                                        <la:textarea styleId="includedDocUrls" property="includedDocUrls"
                                                     styleClass="form-control" rows="5"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="excludedDocUrls" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.excluded_doc_urls"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="excludedDocUrls"/>
                                        <la:textarea styleId="excludedDocUrls" property="excludedDocUrls"
                                                     styleClass="form-control" rows="5"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="configParameter" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.config_parameter"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="configParameter"/>
                                        <la:textarea styleId="configParameter" property="configParameter"
                                                     styleClass="form-control" rows="5"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="depth" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.depth"/></label>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="depth"/>
                                        <input type="number" name="depth" id="depth"
                                               value="${f:h(depth)}" class="form-control"
                                               min="0" max="10000">
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="maxAccessCount" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.max_access_count"/></label>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="maxAccessCount"/>
                                        <input type="number" name="maxAccessCount" id="maxAccessCount"
                                               value="${f:h(maxAccessCount)}" class="form-control"
                                               min="0" max="1000000000">
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="userAgent" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.user_agent"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="userAgent"/>
                                        <la:text styleId="userAgent" property="userAgent" styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="numOfThread" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.number_of_thread"/></label>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="numOfThread"/>
                                        <input type="number" name="numOfThread" id="numOfThread"
                                               value="${f:h(numOfThread)}" class="form-control"
                                               min="1" max="1000">
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="intervalTime" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.interval_time"/></label>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="intervalTime"/>
                                        <input type="number" name="intervalTime" id="intervalTime"
                                               value="${f:h(intervalTime)}" class="form-control"
                                               min="0">
                                        <la:message key="labels.millisec"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="boost" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.boost"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="boost"/>
                                        <la:text styleId="boost" property="boost" styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="permissions" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.permissions"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="permissions"/>
                                        <la:textarea styleId="permissions" property="permissions"
                                                     styleClass="form-control"
                                                     rows="5"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="virtualHosts" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.virtual_hosts"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="virtualHosts"/>
                                        <la:textarea styleId="virtualHosts" property="virtualHosts"
                                                     styleClass="form-control"
                                                     rows="5"/>
                                    </div>
                                </div>
                                <div class="form-group row"<c:if
                                        test="${!labelSettingEnabled}"> style="display:none"</c:if>>
                                    <label for="labelTypeIds" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.label_type"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="labelTypeIds"/>
                                        <la:select styleId="labelTypeIds" property="labelTypeIds" multiple="true"
                                                   styleClass="form-control">
                                            <c:forEach var="l" varStatus="s" items="${labelTypeItems}">
                                                <la:option value="${f:u(l.id)}">${f:h(l.name)}</la:option>
                                            </c:forEach>
                                        </la:select>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="available" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.available"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="available"/>
                                        <la:select styleId="available" property="available" styleClass="form-control">
                                            <la:option value="true">
                                                <la:message key="labels.enabled"/>
                                            </la:option>
                                            <la:option value="false">
                                                <la:message key="labels.disabled"/>
                                            </la:option>
                                        </la:select>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="permissions" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.description"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="description"/>
                                        <la:textarea styleId="description" property="description"
                                                     styleClass="form-control"
                                                     rows="5"/>
                                    </div>
                                </div>
                            </div>
                            <div class="card-footer">
                                <jsp:include page="/WEB-INF/view/common/admin/crud/buttons.jsp"></jsp:include>
                            </div>
                        </div>
                    </div>
                </div>
            </la:form>
        </section>
    </div>
    <jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>
</div>
<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
</body>
</html>
