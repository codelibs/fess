<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %><!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message key="labels.search_list_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="log"/>
        <jsp:param name="menuType" value="searchList"/>
    </jsp:include>
    <div class="content-wrapper">
        <div class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>
                            <la:message key="labels.search_list_configuration"/>
                        </h1>
                    </div>
                    <div class="col-sm-6">
                        <ol class="breadcrumb float-sm-right">
                            <li class="breadcrumb-item active"><la:link href="/admin/searchlist/search?q=${f:u(q)}">
                                <la:message key="labels.search_list_configuration"/>
                            </la:link></li>
                        </ol>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <la:form action="/admin/searchlist/">
                <la:hidden property="crudMode"/>
                <la:hidden property="q"/>
                <c:if test="${crudMode==2}">
                    <la:hidden property="id"/>
                    <la:hidden property="seqNo"/>
                    <la:hidden property="primaryTerm"/>
                </c:if>
                <div class="row">
                    <div class="col-md-12">
                        <div class="card card-outline <c:if test="${crudMode == 1 || crudMode == 2}">card-success</c:if>">
                            <div class="card-header">
                                <h3 class="card-title">
                                    <c:if test="${crudMode == null}">
                                        <la:message key="labels.crud_title_list"/>
                                    </c:if>
                                    <c:if test="${crudMode == 1}">
                                        <la:message key="labels.crud_title_create"/>
                                    </c:if>
                                    <c:if test="${crudMode == 2}">
                                        <la:message key="labels.crud_title_edit"/>
                                    </c:if>
                                    <c:if test="${crudMode == 3}">
                                        <la:message key="labels.crud_title_delete"/>
                                    </c:if>
                                    <c:if test="${crudMode == 4}">
                                        <la:message key="labels.crud_title_details"/>
                                    </c:if>
                                </h3>
                                <div class="card-tools">
                                    <div class="btn-group">
                                        <c:choose>
                                            <c:when test="${crudMode == null}">
                                                <la:link href="createnew" styleClass="btn btn-success btn-xs">
                                                    <em class="fa fa-plus"></em>
                                                    <la:message key="labels.crud_link_create"/>
                                                </la:link>
                                            </c:when>
                                            <c:otherwise>
                                                <la:link href="/admin/searchlist/search?q=${f:u(q)}"
                                                         styleClass="btn btn-primary btn-xs">
                                                    <em class="fa fa-th-list"></em>
                                                    <la:message key="labels.crud_link_list"/>
                                                </la:link>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </div>
                            <div class="card-body">
                                <div>
                                    <la:info id="msg" message="true">
                                        <div class="alert alert-info">${msg}</div>
                                    </la:info>
                                    <la:errors property="_global"/>
                                </div>
                                <c:if test="${crudMode==2}">
                                    <div class="form-group row">
                                        <label for="doc_id" class="col-sm-3 text-sm-right col-form-label">_id</label>
                                        <div class="col-sm-9">${f:h(id)}</div>
                                    </div>
                                    <div class="form-group row">
                                        <label for="doc_id" class="col-sm-3 text-sm-right col-form-label">doc_id</label>
                                        <div class="col-sm-9">
                                                ${f:h(doc.doc_id)}
                                            <la:hidden property="doc.doc_id"/>
                                        </div>
                                    </div>
                                </c:if>
                                <div class="form-group row">
                                    <label for="url" class="col-sm-3 text-sm-right col-form-label">url</label>
                                    <div class="col-sm-9">
                                        <la:errors property="doc.url"/>
                                        <la:text property="doc.url" styleClass="form-control"
                                                 required="required" data-validation="required"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="title" class="col-sm-3 text-sm-right col-form-label">title</label>
                                    <div class="col-sm-9">
                                        <la:errors property="doc.title"/>
                                        <la:text property="doc.title" styleClass="form-control"
                                                 required="required" data-validation="required"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="role" class="col-sm-3 text-sm-right col-form-label">role</label>
                                    <div class="col-sm-9">
                                        <la:errors property="doc.role"/>
                                        <la:textarea property="doc.role" styleClass="form-control"
                                                     data-validation-help="1(username) | 2(groupname) | R(rolename)  e.g. Rguest"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="boost" class="col-sm-3 text-sm-right col-form-label">boost</label>
                                    <div class="col-sm-9">
                                        <la:errors property="doc.boost"/>
                                        <la:text property="doc.boost" styleClass="form-control"
                                                 title="Floating point number" required="required"
                                                 data-validation="custom"
                                                 data-validation-regexp="(\+|\-)?\d+(\.\d+)?((e|E)(\+|\-)?\d+)?"
                                                 data-validation-help="number (Float)"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="label" class="col-sm-3 text-sm-right col-form-label">label</label>
                                    <div class="col-sm-9">
                                        <la:errors property="doc.label"/>
                                        <la:textarea property="doc.label" styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="lang" class="col-sm-3 text-sm-right col-form-label">lang</label>
                                    <div class="col-sm-9">
                                        <la:errors property="doc.lang"/>
                                        <la:textarea property="doc.lang" styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="mimetype" class="col-sm-3 text-sm-right col-form-label">mimetype</label>
                                    <div class="col-sm-9">
                                        <la:errors property="doc.mimetype"/>
                                        <la:text property="doc.mimetype" styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="filetype" class="col-sm-3 text-sm-right col-form-label">filetype</label>
                                    <div class="col-sm-9">
                                        <la:errors property="doc.filetype"/>
                                        <la:text property="doc.filetype" styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="filename" class="col-sm-3 text-sm-right col-form-label">filename</label>
                                    <div class="col-sm-9">
                                        <la:errors property="doc.filename"/>
                                        <la:text property="doc.filename" styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="content" class="col-sm-3 text-sm-right col-form-label">content</label>
                                    <div class="col-sm-9">
                                        <la:errors property="doc.content"/>
                                        <la:text property="doc.content" styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="has_cache" class="col-sm-3 text-sm-right col-form-label">has_cache</label>
                                    <div class="col-sm-9">
                                        <la:errors property="doc.has_cache"/>
                                        <la:text property="doc.has_cache" styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="cache" class="col-sm-3 text-sm-right col-form-label">cache</label>
                                    <div class="col-sm-9">
                                        <la:errors property="doc.cache"/>
                                        <la:text property="doc.cache" styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="digest" class="col-sm-3 text-sm-right col-form-label">digest</label>
                                    <div class="col-sm-9">
                                        <la:errors property="doc.digest"/>
                                        <la:text property="doc.digest" styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="host" class="col-sm-3 text-sm-right col-form-label">host</label>
                                    <div class="col-sm-9">
                                        <la:errors property="doc.host"/>
                                        <la:text property="doc.host" styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="site" class="col-sm-3 text-sm-right col-form-label">site</label>
                                    <div class="col-sm-9">
                                        <la:errors property="doc.site"/>
                                        <la:text property="doc.site" styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="segment" class="col-sm-3 text-sm-right col-form-label">segment</label>
                                    <div class="col-sm-9">
                                        <la:errors property="doc.segment"/>
                                        <la:text property="doc.segment" styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="config_id" class="col-sm-3 text-sm-right col-form-label">config_id</label>
                                    <div class="col-sm-9">
                                        <la:errors property="doc.config_id"/>
                                        <la:text property="doc.config_id" styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="parent_id" class="col-sm-3 text-sm-right col-form-label">parent_id</label>
                                    <div class="col-sm-9">
                                        <la:errors property="doc.parent_id"/>
                                        <la:text property="doc.parent_id" styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="content_length" class="col-sm-3 text-sm-right col-form-label">content_length</label>
                                    <div class="col-sm-9">
                                        <la:errors property="doc.content_length"/>
                                        <la:text property="doc.content_length"
                                                 styleClass="form-control" title="Integer"
                                                 data-validation="custom" data-validation-regexp="^(\d+)?$"
                                                 data-validation-help="number (Integer)"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="favorite_count" class="col-sm-3 text-sm-right col-form-label">favorite_count</label>
                                    <div class="col-sm-9">
                                        <la:errors property="doc.favorite_count"/>
                                        <la:text property="doc.favorite_count"
                                                 styleClass="form-control" title="Integer"
                                                 data-validation="custom" data-validation-regexp="^(\d+)?$"
                                                 data-validation-help="number (Integer)"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="click_count" class="col-sm-3 text-sm-right col-form-label">click_count</label>
                                    <div class="col-sm-9">
                                        <la:errors property="doc.click_count"/>
                                        <la:text property="doc.click_count" styleClass="form-control"
                                                 title="Integer" data-validation="custom"
                                                 data-validation-regexp="^(\d+)?$"
                                                 data-validation-help="number (Integer)"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="created" class="col-sm-3 text-sm-right col-form-label">created</label>
                                    <div class="col-sm-9">
                                        <la:errors property="doc.created"/>
                                        <la:text property="doc.created" styleClass="form-control"
                                                 title="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
                                                 data-validation="custom"
                                                 data-validation-regexp="(^$|^[1-9]\d{3}\-\d\d\-\d\dT\d\d\:\d\d\:\d\d\.\d{3}Z$)"
                                                 data-validation-help="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="timestamp" class="col-sm-3 text-sm-right col-form-label">timestamp</label>
                                    <div class="col-sm-9">
                                        <la:errors property="doc.timestamp"/>
                                        <la:text property="doc.timestamp" styleClass="form-control"
                                                 title="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
                                                 data-validation="custom"
                                                 data-validation-regexp="(^$|^[1-9]\d{3}\-\d\d\-\d\dT\d\d\:\d\d\:\d\d\.\d{3}Z$)"
                                                 data-validation-help="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="last_modified" class="col-sm-3 text-sm-right col-form-label">last_modified</label>
                                    <div class="col-sm-9">
                                        <la:errors property="doc.last_modified"/>
                                        <la:text property="doc.last_modified" styleClass="form-control"
                                                 title="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
                                                 data-validation="custom"
                                                 data-validation-regexp="(^$|^[1-9]\d{3}\-\d\d\-\d\dT\d\d\:\d\d\:\d\d\.\d{3}Z$)"
                                                 data-validation-help="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="expires" class="col-sm-3 text-sm-right col-form-label">expires</label>
                                    <div class="col-sm-9">
                                        <la:errors property="doc.expires"/>
                                        <la:text property="doc.expires" styleClass="form-control"
                                                 title="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
                                                 data-validation="custom"
                                                 data-validation-regexp="(^$|^[1-9]\d{3}\-\d\d\-\d\dT\d\d\:\d\d\:\d\d\.\d{3}Z$)"
                                                 data-validation-help="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="virtual_host" class="col-sm-3 text-sm-right col-form-label">virtual_host</label>
                                    <div class="col-sm-9">
                                        <la:errors property="doc.virtual_host"/>
                                        <la:textarea property="doc.virtual_host" styleClass="form-control"/>
                                    </div>
                                </div>
                            </div>
                            <div class="card-footer">
                                <c:if test="${crudMode == 1}">
                                    <la:link href="/admin/searchlist/search?q=${f:u(q)}" styleClass="btn btn-default">
                                        <em class="fa fa-arrow-circle-left"></em>
                                        <la:message key="labels.crud_button_back"/>
                                    </la:link>
                                    <button type="submit" class="btn btn-success" name="create"
                                            value="<la:message key="labels.crud_button_create" />">
                                        <em class="fa fa-plus"></em>
                                        <la:message key="labels.crud_button_create"/>
                                    </button>
                                </c:if>
                                <c:if test="${crudMode == 2}">
                                    <la:link href="/admin/searchlist/search?q=${f:u(q)}" styleClass="btn btn-default">
                                        <em class="fa fa-arrow-circle-left"></em>
                                        <la:message key="labels.crud_button_back"/>
                                    </la:link>
                                    <button type="submit" class="btn btn-success" name="update"
                                            value="<la:message key="labels.crud_button_update" />">
                                        <em class="fa fa-pencil-alt"></em>
                                        <la:message key="labels.crud_button_update"/>
                                    </button>
                                </c:if>
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
<script src="${fe:url('/js/admin/plugins/form-validator/jquery.form-validator.min.js')}"
        type="text/javascript"></script>
<script src="${fe:url('/js/admin/searchlist.js')}" type="text/javascript"></script>
</body>
</html>
