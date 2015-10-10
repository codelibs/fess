<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Fess | <la:message key="labels.suggest_elevate_word_configuration" /></title>
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
					<la:message key="labels.suggest_elevate_word_title_details" />
				</h1>
				<ol class="breadcrumb">
					<li><la:link href="index">
							<la:message key="labels.suggest_elevate_word_link_list" />
						</la:link></li>
					<c:if test="${crudMode == 1}">
						<li class="active"><a href="#"><la:message key="labels.suggest_elevate_word_link_create" /></a></li>
					</c:if>
					<c:if test="${crudMode == 2}">
						<li class="active"><a href="#"><la:message key="labels.suggest_elevate_word_link_update" /></a></li>
					</c:if>
					<c:if test="${crudMode == 3}">
						<li class="active"><a href="#"><la:message key="labels.suggest_elevate_word_link_delete" /></a></li>
					</c:if>
					<c:if test="${crudMode == 4}">
						<li class="active"><a href="#"><la:message key="labels.suggest_elevate_word_link_confirm" /></a></li>
					</c:if>
				</ol>
			</section>

			<section class="content">

				<%-- Form --%>
				<la:form>
					<la:hidden property="crudMode" />
					<c:if test="${crudMode==2}">
						<la:hidden property="id" />
						<la:hidden property="versionNo" />
					</c:if>
					<la:hidden property="createdBy" />
					<la:hidden property="createdTime" />
					<div class="row">
						<div class="col-md-12">
							<div class="box">
								<%-- Box Header --%>
								<div class="box-header with-border">
									<h3 class="box-title">
										<c:if test="${crudMode == 1}">
											<la:message key="labels.suggest_elevate_word_link_create" />
										</c:if>
										<c:if test="${crudMode == 2}">
											<la:message key="labels.suggest_elevate_word_link_update" />
										</c:if>
									</h3>
									<div class="box-tools pull-right">
										<span class="label label-default"><la:link href="index">
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
									<%-- Message --%>
									<div>
										<la:info id="msg" message="true">
											<div class="alert-message info">
												${msg}
											</div>
										</la:info>
										<la:errors />
									</div>

									<%-- Form Fields --%>
									<div class="form-group">
										<label for="word"><la:message key="labels.suggest_elevate_word_suggest_word" /></label>
										<la:text property="suggestWord" styleClass="form-control" />
									</div>
									<div class="form-group">
										<label for="reading"><la:message key="labels.suggest_elevate_word_reading" /></label>
										<la:text property="reading" styleClass="form-control" />
									</div>
									<div class="form-group">
										<label for="targetRole"><la:message key="labels.suggest_elevate_word_target_role" /></label>
										<la:text property="targetRole" styleClass="form-control" />
									</div>
									<div class="form-group">
										<label for="targetLabel"><la:message key="labels.suggest_elevate_word_target_label" /></label>
										<la:text property="targetLabel" styleClass="form-control" />
									</div>
									<div class="form-group">
										<label for="boost"><la:message key="labels.suggest_elevate_word_boost" /></label>
										<la:text property="boost" styleClass="form-control" />
									</div>
								</div>
								<%-- Box Footer --%>
								<div class="box-footer">
									<c:if test="${crudMode == 1}">
										<input type="submit" class="btn" name="back" value="<la:message key="labels.suggest_elevate_word_button_back"/>" />
										<input type="submit" class="btn btn-primary" name="confirmfromcreate"
											value="<la:message key="labels.suggest_elevate_word_button_create"/>"
										/>
									</c:if>
									<c:if test="${crudMode == 2}">
										<input type="submit" class="btn" name="back" value="<la:message key="labels.suggest_elevate_word_button_back"/>" />
										<input type="submit" class="btn btn-primary" name="confirmfromupdate"
											value="<la:message key="labels.suggest_elevate_word_button_confirm"/>"
										/>
									</c:if>
								</div>
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

