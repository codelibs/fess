<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title"/> | <la:message key="labels.suggest_elevate_word_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="suggest" />
			<jsp:param name="menuType" value="suggestElevateWord" />
		</jsp:include>

		<div class="content-wrapper">

			<%-- Content Header --%>
			<section class="content-header">
				<h1>
					<la:message key="labels.suggest_elevate_word_configuration" />
				</h1>
				<ol class="breadcrumb">
					<li class="active"><la:link href="/admin/suggestelevateword">
							<la:message key="labels.suggest_elevate_word_link_list" />
						</la:link></li>
					<li class="active"><a href="#"><la:message key="labels.suggest_elevate_word_link_download" /></a></li>
				</ol>
			</section>

			<section class="content">

				<div class="row">
					<div class="col-md-12">
						<div class="box">
							<%-- Box Header --%>
							<div class="box-header with-border">
								<h3 class="box-title">
									<la:message key="labels.suggest_elevate_word_link_download" />
								</h3>
								<div class="box-tools pull-right">
									<span class="label label-default"><la:link href="/admin/suggestelevateword">
										<la:message key="labels.suggest_elevate_word_link_list" />
										</la:link></span>
									<span class="label label-default"><la:link href="createpage">
										<la:message key="labels.suggest_elevate_word_link_create_new" />
										</la:link></span>
									<span class="label label-default"><la:link href="downloadpage">
										<la:message key="labels.suggest_elevate_word_link_download" />
										</la:link></span>
									<span class="label label-default"><la:link href="uploadpage">
										<la:message key="labels.suggest_elevate_word_link_upload" />
										</la:link></span>
								</div>
							</div>
							<%-- Box Body --%>
							<div class="box-body">
								<la:form>
									<table class="table table-bordered">
										<tbody>
											<tr>
												<th class="col-xs-2"><la:message key="labels.suggest_elevate_word_file" /></th>
												<td><input name="download" value="<la:message key="labels.suggest_elevate_word_button_download"/>" type="submit" /></td>
											</tr>
										</tbody>
									</table>
								</la:form>

							</div>
							<%-- Box Footer --%>
							<div class="box-footer">

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

