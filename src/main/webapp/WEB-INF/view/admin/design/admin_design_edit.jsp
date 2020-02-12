<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %><!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.design_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="system"/>
        <jsp:param name="menuType" value="design"/>
    </jsp:include>
    <div class="content-wrapper">
        <div class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>
                            <la:message key="labels.design_configuration"/>
                        </h1>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <div class="row">
                <div class="col-md-12">
                    <div>
                        <la:info id="msg" message="true">
                            <div class="alert alert-info">${msg}</div>
                        </la:info>
                        <la:errors property="_global"/>
                    </div>
                    <div class="card card-outline card-success">
                        <c:if test="${editable}">
                            <la:form action="/admin/design/">
                                <div class="card-header">
                                    <h3 class="card-title">
                                        <la:message key="labels.design_title_edit_content"/>
                                    </h3>
                                </div>
                                <div class="card-body">
                                    <h4>${f:h(displayFileName)}</h4>
                                    <div>
                                        <la:errors property="content"/>
                                        <la:textarea styleId="content" property="content" rows="20"
                                                     styleClass="form-control"></la:textarea>
                                    </div>
                                </div>
                                <div class="card-footer">
                                    <button type="submit" class="btn btn-default" name="back"
                                            value="<la:message key="labels.design_button_back" />">
                                        <em class="fa fa-arrow-circle-left"></em>
                                        <la:message key="labels.design_button_back"/>
                                    </button>
                                    <button type="submit" class="btn btn-success" name="update"
                                            value="<la:message key="labels.design_button_update" />">
                                        <em class="fa fa-pencil-alt"></em>
                                        <la:message key="labels.design_button_update"/>
                                    </button>
                                </div>
                                <la:hidden property="fileName"/>
                            </la:form>
                        </c:if>
                    </div>
                </div>
            </div>
        </section>
    </div>
    <jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>
</div>
<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
</body>
</html>
