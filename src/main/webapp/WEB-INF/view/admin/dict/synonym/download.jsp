<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title" /> | <la:message
		key="labels.dict_synonym_configuration" /></title>
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

			<%-- Content Header --%>
			<section class="content-header">
				<h1>
					<la:message key="labels.dict_synonym_title" />
				</h1>
				<ol class="breadcrumb">
					<li class="active"><la:link href="list/0?dictId=${dictId}">
							<la:message key="labels.dict_synonym_link_download" />
						</la:link></li>
				</ol>
			</section>

			<section class="content">

				<div class="row">
					<div class="col-md-12">
						<div class="box box-primary">
							<%-- Box Header --%>
							<div class="box-header with-border">
								<h3 class="box-title">
									<la:message key="labels.dict_synonym_link_download" />
								</h3>
								<div class="box-tools pull-right">
									<span class="label label-default"> <la:link
											href="/admin/dict/">
											<la:message key="labels.dict_list_link" />
										</la:link>
									</span> <span class="label label-default"> <la:link
											href="list/0/?dictId=${f:u(dictId)}">
											<la:message key="labels.dict_synonym_list_link" />
										</la:link>
									</span> <span class="label label-default"> <la:link
											href="createpage/${f:u(dictId)}">
											<la:message key="labels.dict_synonym_link_create" />
										</la:link>
									</span> <span class="label label-default"> <a href="#"> <la:message
												key="labels.dict_synonym_link_download" />
									</a>
									</span> <span class="label label-default"> <la:link
											href="uploadpage/${f:u(dictId)}">
											<la:message key="labels.dict_synonym_link_upload" />
										</la:link>
									</span>
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

								<%-- Edit Form: BEGIN --%>
								<la:form target="_blank">
									<div>
										<la:hidden property="dictId" />
										<table class="table table-bordered table-striped">
											<tbody>
												<tr>
													<th style="">${f:h(path)}</th>
													<td style="">
														<button type="submit" class="btn btn-primary"
															name="download"
															value="<la:message key="labels.dict_synonym_button_download" />">
															<la:message key="labels.dict_synonym_button_download" />
														</button>
													</td>
												</tr>
											</tbody>
										</table>
									</div>
								</la:form>
								<%-- Edit Form: BEGIN --%>

							</div>
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
