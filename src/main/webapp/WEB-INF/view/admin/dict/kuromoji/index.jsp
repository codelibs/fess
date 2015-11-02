<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title" /> | <la:message
		key="labels.dict_kuromoji_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="system" />
			<jsp:param name="menuType" value="dict" />
		</jsp:include>
		<div class="content-wrapper">
			<section class="content-header">
				<h1>
					<la:message key="labels.dict_kuromoji_title" />
				</h1>
				<jsp:include page="/WEB-INF/view/common/admin/crud/breadcrumb.jsp"></jsp:include>
			</section>
			<section class="content">
				<div class="row">
					<div class="col-md-12">
						<div class="box box-primary">
							<%-- Box Header --%>
							<div class="box-header with-border">
								<h3 class="box-title">
									<la:message key="labels.dict_kuromoji_list_link" />
								</h3>
								<div class="btn-group pull-right">
									<la:link href="/admin/dict" styleClass="btn btn-default btn-xs">
										<la:message key="labels.dict_list_link" />
									</la:link>
									<la:link href="list/1?dictId=${f:u(dictId)}"
										styleClass="btn btn-primary btn-xs">
										<la:message key="labels.dict_kuromoji_list_link" />
									</la:link>
									<la:link href="createpage/${f:u(dictId)}"
										styleClass="btn btn-success btn-xs">
										<la:message key="labels.dict_kuromoji_link_create" />
									</la:link>
									<la:link href="downloadpage/${f:u(dictId)}"
										styleClass="btn btn-primary btn-xs">
										<la:message key="labels.dict_kuromoji_link_download" />
									</la:link>
									<la:link href="uploadpage/${f:u(dictId)}"
										styleClass="btn btn-success btn-xs">
										<la:message key="labels.dict_kuromoji_link_upload" />
									</la:link>
								</div>
							</div>
							<%-- Box Body --%>
							<div class="box-body">
								<%-- Message --%>
								<div>
									<la:info id="msg" message="true">
										<div class="alert alert-info">${msg}</div>
									</la:info>
									<la:errors />
								</div>
								<%-- List --%>
								<c:if test="${kuromojiPager.allRecordCount == 0}">
									<p class="callout callout-info">
										<la:message key="labels.list_could_not_find_crud_table" />
									</p>
								</c:if>
								<c:if test="${kuromojiPager.allRecordCount > 0}">
									<div class="row">
										<div class="col-sm-12">
											<table class="table table-bordered table-striped">
												<thead>
													<tr>
														<th><la:message key="labels.dict_kuromoji_token" /></th>
														<th><la:message key="labels.dict_kuromoji_reading" /></th>
													</tr>
												</thead>
												<tbody>
													<c:forEach var="data" varStatus="s"
														items="${kuromojiItemItems}">
														<tr class="${s.index % 2 == 0 ? 'row1' : 'row2'}"
															data-href="${contextPath}/admin/dict/kuromoji/details/${f:u(dictId)}/4/${f:u(data.id)}">
															<td>${f:h(data.token)}</td>
															<td>${f:h(data.reading)}</td>
														</tr>
													</c:forEach>
												</tbody>
											</table>
										</div>
									</div>
									<c:set var="pager" value="${kuromojiPager}" scope="request" />
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
