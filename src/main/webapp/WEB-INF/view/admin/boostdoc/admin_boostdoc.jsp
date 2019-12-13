<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title" /> | <la:message
		key="labels.boost_document_rule_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="crawl" />
			<jsp:param name="menuType" value="boostDocumentRule" />
		</jsp:include>
		<div class="content-wrapper">
			<section class="content-header">
				<h1>
					<la:message key="labels.boost_document_rule_configuration" />
				</h1>
				<jsp:include page="/WEB-INF/view/common/admin/crud/breadcrumb.jsp"></jsp:include>
			</section>
			<section class="content">
				<div class="row">
					<div class="col-md-12">
						<div class="box box-primary">
							<div class="box-header with-border">
								<jsp:include page="/WEB-INF/view/common/admin/crud/header.jsp"></jsp:include>
							</div>
							<!-- /.box-header -->
							<div class="box-body">
								<%-- Message --%>
								<div>
									<la:info id="msg" message="true">
										<div class="alert alert-info">${msg}</div>
									</la:info>
									<la:errors />
								</div>
								<la:form action="/admin/boostdoc/"
										 styleClass="form-horizontal">
									<div class="form-group">
										<label for="urlExpr" class="col-sm-2 control-label"><la:message
												key="labels.boost_document_rule_url_expr" /></label>
										<div class="col-sm-10">
											<la:text styleId="urlExpr" property="urlExpr" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="boostExpr" class="col-sm-2 control-label"><la:message
												key="labels.boost_document_rule_boost_expr" /></label>
										<div class="col-sm-10">
											<la:text styleId="boostExpr" property="boostExpr" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<div class="col-sm-offset-2 col-sm-10">
											<button type="submit" class="btn btn-primary" id="submit"
													name="search"
													value="<la:message key="labels.crud_button_search" />">
												<em class="fa fa-search"></em>
												<la:message key="labels.crud_button_search" />
											</button>
											<button type="submit" class="btn btn-default" name="reset"
													value="<la:message key="labels.crud_button_reset" />">
												<la:message key="labels.crud_button_reset" />
											</button>
										</div>
									</div>
								</la:form>
								<%-- List --%>
								<c:if test="${boostDocPager.allRecordCount == 0}">
									<div class="row top10">
										<div class="col-sm-12">
											<em class="fa fa-info-circle text-light-blue"></em>
											<la:message key="labels.list_could_not_find_crud_table" />
										</div>
									</div>
								</c:if>
								<c:if test="${boostDocPager.allRecordCount > 0}">
									<div class="row">
										<div class="col-sm-12">
											<table class="table table-bordered table-striped">
												<thead>
													<tr>
														<th><la:message
																key="labels.boost_document_rule_list_url_expr" /></th>
													</tr>
												</thead>
												<tbody>
													<c:forEach var="data" varStatus="s"
														items="${boostDocumentRuleItems}">
														<tr
															data-href="${contextPath}/admin/boostdoc/details/4/${f:u(data.id)}">
															<td>${f:h(data.urlExpr)}</td>
														</tr>
													</c:forEach>
												</tbody>
											</table>
										</div>
									</div>
									<c:set var="pager" value="${boostDocPager}"
										scope="request" />
									<c:import url="/WEB-INF/view/common/admin/crud/pagination.jsp" />
									<c:if test="${pager.currentPageNumber > pager.allPageCount}">
										<script>location.href = "${contextPath}/admin/boostdoc/list/${pager.allPageCount}";</script>
									</c:if>
								</c:if>
							</div>
							<!-- /.box-body -->
						</div>
						<!-- /.box -->
					</div>
				</div>
			</section>
		</div>
		<jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>
	</div>
	<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
</body>
</html>
