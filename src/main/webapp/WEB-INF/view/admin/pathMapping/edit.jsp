<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Fess | <bean:message key="labels.path_mapping_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin2/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin2/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin2/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="crawl" />
			<jsp:param name="menuType" value="pathMapping" />
		</jsp:include>
			
		<div class="content-wrapper">
			<%-- Content Header --%>
			<section class="content-header">
				<h1>
					<bean:message key="labels.path_mapping_title_details" />
				</h1>
				<ol class="breadcrumb">
			
					<li><s:link href="index">
							<bean:message key="labels.path_mapping_link_list" />
						</s:link></li>
					<c:if test="${crudMode == 1}">
						<li class="active"><a href="#"><bean:message key="labels.path_mapping_link_create" /></a></li>
					</c:if>
					<c:if test="${crudMode == 2}">
						<li class="active"><a href="#"><bean:message key="labels.path_mapping_link_update" /></a></li>
					</c:if>
					<c:if test="${crudMode == 3}">
						<li class="active"><a href="#"><bean:message key="labels.path_mapping_link_delete" /></a></li>
					</c:if>
					<c:if test="${crudMode == 4}">
						<li class="active"><a href="#"><bean:message key="labels.path_mapping_link_confirm" /></a></li>
					</c:if>
				</ol>
			</section>

			<section class="content">
				<%-- Form --%>
				<s:form>
					<html:hidden property="crudMode" />
					<c:if test="${crudMode==2}">
						<html:hidden property="id" />
						<html:hidden property="versionNo" />
					</c:if>
					<html:hidden property="createdBy" />
					<html:hidden property="createdTime" />
					<div class="row">
						<div class="col-md-12">
							<div class="box">
								<%-- Box Header --%>
								<div class="box-header with-border">
									<h3 class="box-title">
										<c:if test="${crudMode == 1}">
											<bean:message key="labels.path_mapping_link_create" />
										</c:if>
										<c:if test="${crudMode == 2}">
											<bean:message key="labels.path_mapping_link_update" />
										</c:if>
									</h3>
									<div class="box-tools pull-right">
										<span class="label label-default"><s:link href="index">
												<bean:message key="labels.path_mapping_link_list" />
											</s:link></span>
									</div>
								</div>
								<%-- Box Body --%>
								<div class="box-body">
									<%-- Message --%>
									<div>
										<html:messages id="msg" message="true">
											<div class="alert-message info">
												<bean:write name="msg" ignore="true" />
											</div>
										</html:messages>
										<html:errors />
									</div>
									<%-- Form Fields --%>
									<div class="form-group">
										<label for="regex"><bean:message key="labels.regex" /></label>
										<html:text property="regex" styleClass="form-control" />
									</div>
									<div class="form-group">
										<label for="replacement"><bean:message key="labels.replacement" /></label>
										<html:text property="replacement" styleClass="form-control" />
									</div>
									<div class="form-group">
 										<label for="processType"><bean:message key="labels.processType" /></label>
 										<html:select property="processType" styleClass="form-control">
 											<html:option value="C">
 												<bean:message key="labels.path_mapping_pt_crawling" />
 											</html:option>
 											<html:option value="D">
 												<bean:message key="labels.path_mapping_pt_displaying" />
 											</html:option>
 											<html:option value="B">
 												<bean:message key="labels.path_mapping_pt_both" />
 											</html:option>
 										</html:select>
 									</div>
									<div class="form-group">
										<label for="sortOrder"><bean:message key="labels.sortOrder" /></label>
										<html:text property="sortOrder" styleClass="form-control" />
									</div>
								</div>
								<%-- Box Footer --%>
								<div class="box-footer">
									<c:if test="${crudMode == 1}">
										<input type="submit" class="btn" name="back" value="<bean:message key="labels.path_mapping_button_back"/>" />
										<input type="submit" class="btn btn-primary" name="confirmfromcreate"
											value="<bean:message key="labels.path_mapping_button_create"/>"
										/>
									</c:if>
									<c:if test="${crudMode == 2}">
										<input type="submit" class="btn" name="back" value="<bean:message key="labels.path_mapping_button_back"/>" />
										<input type="submit" class="btn btn-primary" name="confirmfromupdate"
											value="<bean:message key="labels.path_mapping_button_confirm"/>"
										/>
									</c:if>
								</div>
							</div>
						</div>
					</div>
				</s:form>

			</section>
		</div>
		<jsp:include page="/WEB-INF/view/common/admin2/footer.jsp"></jsp:include>
	</div>
	<jsp:include page="/WEB-INF/view/common/admin2/foot.jsp"></jsp:include>
</body>
</html>
