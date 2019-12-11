<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title" /> | <la:message
		key="labels.web_crawling_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="crawl" />
			<jsp:param name="menuType" value="webConfig" />
		</jsp:include>
		<div class="content-wrapper">
			<section class="content-header">
				<h1>
					<la:message key="labels.web_crawling_configuration" />
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
								<la:form action="/admin/webconfig/"
									styleClass="form-horizontal">
									<div class="form-group">
										<label for="name" class="col-sm-2 control-label"><la:message
												key="labels.web_config_search_name" /></label>
										<div class="col-sm-10">
											<la:text styleId="name" property="name" styleClass="form-control"></la:text>
										</div>
									</div>
									<div class="form-group">
										<label for="urls" class="col-sm-2 control-label"><la:message
												key="labels.web_config_search_url" /></label>
										<div class="col-sm-10">
											<la:text styleId="urls" property="urls" styleClass="form-control"></la:text>
										</div>
									</div>
									<div class="form-group">
										<label for="description" class="col-sm-2 control-label"><la:message
												key="labels.web_config_search_description" /></label>
										<div class="col-sm-10">
											<la:text styleId="description" property="description" styleClass="form-control"></la:text>
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
								<c:if test="${webConfigPager.allRecordCount == 0}">
									<div class="row top10">
										<div class="col-sm-12">
											<em class="fa fa-info-circle text-light-blue"></em>
											<la:message key="labels.list_could_not_find_crud_table" />
										</div>
									</div>
								</c:if>
								<c:if test="${webConfigPager.allRecordCount > 0}">
									<div class="row">
										<div class="col-sm-12">
											<table class="table table-bordered table-striped">
												<thead>
													<tr>
														<th><la:message key="labels.name" /></th>
														<th class="col-md-4 text-center"><la:message
																key="labels.available" /></th>
													</tr>
												</thead>
												<tbody>
													<c:forEach var="data" varStatus="s"
														items="${webConfigItems}">
														<tr
															data-href="${contextPath}/admin/webconfig/details/4/${f:u(data.id)}">
															<td>${f:h(data.name)}</td>
															<td class="text-center"><c:if
																	test="${data.available=='true'}">
																	<span class="label label-primary"><la:message
																			key="labels.enabled" /></span>
																</c:if> <c:if test="${data.available=='false'}">
																	<span class="label label-default"><la:message
																			key="labels.disabled" /></span>
																</c:if></td>
														</tr>
													</c:forEach>
												</tbody>
											</table>
										</div>
									</div>
									<c:set var="pager" value="${webConfigPager}" scope="request" />
									<c:import url="/WEB-INF/view/common/admin/crud/pagination.jsp" />
									<c:if test="${pager.currentPageNumber > pager.allPageCount}">
										<script>location.href = "${contextPath}/admin/webconfig/list/${pager.allPageCount}";</script>
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

