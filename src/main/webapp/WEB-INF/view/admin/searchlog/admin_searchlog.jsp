<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title" /> | <la:message
		key="labels.searchlog_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="log" />
			<jsp:param name="menuType" value="searchLog" />
		</jsp:include>
		<div class="content-wrapper">
			<section class="content-header">
				<h1>
					<la:message key="labels.searchlog_configuration" />
				</h1>
				<ol class="breadcrumb">
					<li class="active"><la:link href="/admin/searchlog">
							<la:message key="labels.searchlog_title" />
						</la:link></li>
				</ol>
			</section>
			<section class="content">
				<div class="row">
					<div class="col-xs-12">
						<div class="box box-primary">
							<div class="box-header with-border">
								<h3 class="box-title">
									<la:message key="labels.searchlog_title" />
								</h3>
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
								<la:form action="/admin/searchlog/"
									styleClass="form-horizontal">
									<div class="form-group">
										<label for="logTypeSearch" class="col-sm-2 control-label"><la:message
												key="labels.searchlog_log_type" /></label>
										<div class="col-sm-4">
											<la:select styleId="logTypeSearch" property="logType"
												styleClass="form-control">
												<la:option value="search"><la:message key="labels.searchlog_log_type_search" /></la:option>
												<la:option value="click"><la:message key="labels.searchlog_log_type_click" /></la:option>
												<la:option value="favorite"><la:message key="labels.searchlog_log_type_favorite" /></la:option>
											</la:select>
										</div>
									</div>
									<div class="form-group">
										<div class="col-sm-offset-2 col-sm-10">
											<button type="submit" class="btn btn-primary" id="submit"
												name="search"
												value="<la:message key="labels.crud_button_search" />">
												<i class="fa fa-search"></i>
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
								<c:if test="${searchLogPager.allRecordCount == 0}">
									<div class="row top20">
										<div class="col-sm-12">
											<i class="fa fa-info-circle text-light-blue"></i>
											<la:message key="labels.list_could_not_find_crud_table" />
										</div>
									</div>
								</c:if>
								<c:if test="${searchLogPager.allRecordCount > 0}">
									<div class="row top10">
										<div class="col-sm-12">
											<table class="table table-bordered table-striped dataTable">
												<thead>
													<tr>
														<th class="col-sm-3"><la:message
																key="labels.searchlog_requested_time" /></th>
														<th><la:message
																key="labels.searchlog_log_message" /></th>
													</tr>
												</thead>
												<tbody>
													<c:forEach var="data" varStatus="s"
														items="${searchLogItems}">
														<tr
															data-href="${contextPath}/admin/searchlog/details/4/${f:u(logType)}/${f:u(data.id)}">
															<td>${f:h(data.requestedAt)}</td>
															<td>${f:h(data.logMessage)}</td>
														</tr>
													</c:forEach>
												</tbody>
											</table>
										</div>
									</div>
									<c:set var="pager" value="${searchLogPager}"
										scope="request" />
									<c:import url="/WEB-INF/view/common/admin/crud/pagination.jsp" />
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

