<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Fess | <bean:message key="labels.suggest_bad_word_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin2/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin2/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin2/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="crawl" />
			<jsp:param name="menuType" value="suggestBadWord" />
		</jsp:include>

		<div class="content-wrapper">

			<%-- Content Header --%>
			<section class="content-header">
				<h1>
					<bean:message key="labels.suggest_bad_word_configuration" />
				</h1>
				<ol class="breadcrumb">
					<li class="active"><s:link href="index">
							<bean:message key="labels.suggest_bad_word_link_list" />
						</s:link></li>
					<li class="active"><a href="#"><bean:message key="labels.suggest_bad_word_link_upload" /></a></li>
				</ol>
			</section>

			<section class="content">

				<div class="row">
					<div class="col-md-12">
						<div class="box">
							<%-- Box Header --%>
							<div class="box-header with-border">
								<h3 class="box-title">
									<bean:message key="labels.suggest_bad_word_link_upload" />
								</h3>
								<div class="box-tools pull-right">
									<span class="label label-default"><s:link href="index">
										<bean:message key="labels.suggest_bad_word_link_list" />
										</s:link></span>
									<span class="label label-default"><s:link href="createpage">
										<bean:message key="labels.suggest_bad_word_link_create_new" />
										</s:link></span>
									<span class="label label-default"><s:link href="downloadpage">
										<bean:message key="labels.suggest_bad_word_link_download" />
										</s:link></span>
									<span class="label label-default"><s:link href="uploadpage">
										<bean:message key="labels.suggest_bad_word_link_upload" />
										</s:link></span>
								</div>
							</div>
							<%-- Box Body --%>
							<div class="box-body">
								<s:form action="upload" enctype="multipart/form-data">
									<table class="table table-bordered">
										<tbody>
											<tr>
												<th class="col-xs-2"><bean:message key="labels.suggest_bad_word_file" /></th>
												<td><input type="file" name="suggestBadWordFile" /></td>
											</tr>
										</tbody>
										<tfoot>
											<tr>
												<td colspan="2">
													<input type="submit" name="upload" value="<bean:message key="labels.suggest_bad_word_button_upload"/>" />
												</td>
											</tr>
										</tfoot>
									</table>
								</s:form>

							</div>
							<%-- Box Footer --%>
							<div class="box-footer">

							</div>
						</div>
					</div>
				</div>

			</section>
		</div>

		<jsp:include page="/WEB-INF/view/common/admin2/footer.jsp"></jsp:include>
	</div>
	<jsp:include page="/WEB-INF/view/common/admin2/foot.jsp"></jsp:include>
</body>
</html>

