<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %><!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.maintenance_title_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="log"/>
        <jsp:param name="menuType" value="maintenance"/>
    </jsp:include>
    <div class="content-wrapper">
        <div class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>
                            <la:message key="labels.maintenance_title_configuration"/>
                        </h1>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <la:form action="/admin/maintenance/" styleClass="row">
                <%-- Message: BEGIN --%>
                <div class="col-md-12">
                    <la:info id="msg" message="true">
                        <div class="alert alert-info">${msg}</div>
                    </la:info>
                    <la:errors/>
                </div>
                <c:if test="${editable}">
                    <%-- Message: END --%>
                    <div class="col-md-12">
                        <div class="card card-outline card-primary">
                            <div class="card-header">
                                <h3 class="card-title">
                                    <la:message key="labels.upgrade_reindex"/>
                                </h3>
                            </div>
                            <div class="card-body">
                                <div class="form-group row">
                                    <span class="font-weight-bold col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.replace_aliases"/></span>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="replaceAliases"/>
                                        <div class="form-check">
                                            <la:checkbox styleId="replaceAliases" styleClass="form-check-input" property="replaceAliases"/>
                                            <label for="replaceAliases" class="form-check-label">
                                                <la:message key="labels.enabled"/>
                                            </label>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <span class="font-weight-bold col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.reset_dictionaries"/></span>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="resetDictionaries"/>
                                        <div class="form-check">
                                            <la:checkbox styleId="resetDictionaries" styleClass="form-check-input" property="resetDictionaries" disabled="${fesenType=='cloud' or fesenType=='aws'}"/>
                                            <label for="resetDictionaries" class="form-check-label">
                                                <la:message key="labels.enabled"/>
                                            </label>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="numberOfShardsForDoc" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.number_of_shards_for_doc"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="numberOfShardsForDoc"/>
                                        <la:text styleId="numberOfShardsForDoc" property="numberOfShardsForDoc"
                                                 styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="autoExpandReplicasForDoc"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.auto_expand_replicas_for_doc"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="autoExpandReplicasForDoc"/>
                                        <la:text styleId="autoExpandReplicasForDoc"
                                                 property="autoExpandReplicasForDoc" styleClass="form-control"/>
                                    </div>
                                </div>
                            </div>
                            <div class="card-footer">
                                <button type="submit" class="btn btn-primary ${f:h(editableClass)}"
                                        name="reindexOnly"
                                        value="<la:message key="labels.reindex_start_button"/>">
                                    <em class="fa fa-arrow-circle-right"></em>
                                    <la:message key="labels.reindex_start_button"/>
                                </button>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-12">
                        <div class="card card-outline card-primary">
                            <div class="card-header">
                                <h3 class="card-title">
                                    <la:message key="labels.reload_doc_index"/>
                                </h3>
                            </div>
                            <div class="card-footer">
                                <button type="submit" class="btn btn-primary"
                                        name="reloadDocIndex"
                                        value="<la:message key="labels.reload_doc_index_button"/>">
                                    <em class="fa fa-sync"></em>
                                    <la:message key="labels.reload_doc_index_button"/>
                                </button>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-12">
                        <div class="card card-outline card-primary">
                            <div class="card-header">
                                <h3 class="card-title">
                                    <la:message key="labels.clear_crawler_index"/>
                                </h3>
                            </div>
                            <div class="card-footer">
                                <button type="button" class="btn btn-danger" data-toggle="modal" data-target="#confirmToClearCrawlerIndex">
                                    <em class="fa fa-trash"></em>
                                    <la:message key="labels.clear_crawler_index_button"/>
                                </button>
                                <div class="modal fade" id="confirmToClearCrawlerIndex"
                                     tabindex="-1" role="dialog">
                                    <div class="modal-dialog">
                                        <div class="modal-content bg-danger">
                                            <div class="modal-header">
                                                <h4 class="modal-title">
                                                    <la:message key="labels.clear_crawler_index_button"/>
                                                </h4>
                                                <button type="button" class="close" data-dismiss="modal"
                                                        aria-label="Close">
                                                    <span aria-hidden="true">×</span>
                                                </button>
                                            </div>
                                            <div class="modal-body">
                                                <p>
                                                    <la:message key="labels.crud_delete_confirmation"/>
                                                </p>
                                            </div>
                                            <div class="modal-footer justify-content-between">
                                                <button type="button" class="btn btn-outline-light"
                                                        data-dismiss="modal">
                                                    <la:message key="labels.crud_button_cancel"/>
                                                </button>
                                                <button type="submit" class="btn btn-outline-light"
                                                        name="clearCrawlerIndex"
                                                        value="<la:message key="labels.crud_button_delete" />">
                                                    <em class="fa fa-trash"></em>
                                                    <la:message key="labels.crud_button_delete"/>
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:if>
                <div class="col-md-12">
                    <div class="card card-outline card-primary">
                        <div class="card-header">
                            <h3 class="card-title">
                                <la:message key="labels.diagnostic_logs"/>
                            </h3>
                        </div>
                        <div class="card-footer">
                            <button type="submit" class="btn btn-primary"
                                    name="downloadLogs"
                                    value="<la:message key="labels.download_diagnostic_logs_button"/>">
                                <em class="fa fa-download"></em>
                                <la:message key="labels.download_diagnostic_logs_button"/>
                            </button>
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
