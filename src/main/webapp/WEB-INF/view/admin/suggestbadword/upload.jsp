<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title" /> | <la:message
		key="labels.suggest_bad_word_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="suggest" />
			<jsp:param name="menuType" value="suggestBadWord" />
		</jsp:include>

		<div class="content-wrapper">

			<%-- Content Header --%>
			<section class="content-header">
				<h1>
					<la:message key="labels.suggest_bad_word_configuration" />
				</h1>
				<ol class="breadcrumb">
					<li class="active"><la:link href="/admin/suggestbadword">
							<la:message key="labels.suggest_bad_word_link_list" />
						</la:link></li>
					<li class="active"><a href="#"><la:message
								key="labels.suggest_bad_word_link_upload" /></a></li>
				</ol>
			</section>

			<section class="content">

				<div class="row">
					<div class="col-md-12">
						<div class="box box-primary">
							<%-- Box Header --%>
							<div class="box-header with-border">
								<h3 class="box-title">
									<la:message key="labels.suggest_bad_word_link_upload" />
								</h3>
								<div class="btn-group pull-right">
									<la:link href="/admin/suggestbadword"
										styleClass="btn btn-default btn-xs">
										<la:message key="labels.suggest_bad_word_link_list" />
									</la:link>
									<la:link href="createpage" styleClass="btn btn-success btn-xs">
										<la:message key="labels.suggest_bad_word_link_create_new" />
									</la:link>
									<la:link href="downloadpage"
										styleClass="btn btn-primary btn-xs">
										<la:message key="labels.suggest_bad_word_link_download" />
									</la:link>
									<la:link href="uploadpage" styleClass="btn btn-success btn-xs">
										<la:message key="labels.suggest_bad_word_link_upload" />
									</la:link>
								</div>
							</div>
							<%-- Box Body --%>
							<div class="box-body">
								<la:form action="upload" enctype="multipart/form-data">
									<table class="table table-bordered">
										<tbody>
											<tr>
												<th class="col-xs-2"><la:message
														key="labels.suggest_bad_word_file" /></th>
												<td><input type="file" name="suggestBadWordFile" /></td>
											</tr>
										</tbody>
										<tfoot>
											<tr>
												<td colspan="2">
													<button type="submit" class="btn btn-primary" name="upload"
														value="<la:message key="labels.suggest_bad_word_button_upload" />">
														<la:message key="labels.suggest_bad_word_button_upload" />
													</button>
												</td>
											</tr>
										</tfoot>
									</table>
								</la:form>

							</div>
							<%-- Box Footer --%>
							<div class="box-footer"></div>
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

