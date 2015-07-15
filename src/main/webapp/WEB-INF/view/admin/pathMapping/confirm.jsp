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
					<c:if test="${crudMode==2 || crudMode==3 || crudMode==4}">
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
										<c:if test="${crudMode == 3}">
											<bean:message key="labels.path_mapping_link_delete" />
										</c:if>
										<c:if test="${crudMode == 4}">
											<bean:message key="labels.path_mapping_link_confirm" />
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
									<table class="table table-bordered">
										<tbody>
											<tr>
												<th class="col-xs-2"><bean:message key="labels.regex" /></th>
												<td>${f:h(regex)}<html:hidden property="regex" /></td>
											</tr>
											<tr>
												<th><bean:message key="labels.replacement" /></th>
												<td>${f:h(replacement)}<html:hidden property="replacement" /></td>
											</tr>
											<tr>
												<th><bean:message key="labels.processType" /></th>
												<td>
													<c:if test="${processType=='C'}">
														<bean:message key="labels.path_mapping_pt_crawling" />
													</c:if> 
													<c:if test="${processType=='D'}">
														<bean:message key="labels.path_mapping_pt_displaying" />
													</c:if>
													<c:if test="${processType=='B'}">
														<bean:message key="labels.path_mapping_pt_both" />
													</c:if> 
													<html:hidden property="processType" />
												</td>
											</tr>
											<tr>
												<th><bean:message key="labels.sortOrder" /></th>
												<td>${f:h(sortOrder)}<html:hidden property="sortOrder" /></td>
											</tr>
										</tbody>
									</table>
								</div>
								<%-- Box Footer --%>
								<div class="box-footer">
									<c:if test="${crudMode == 1}">
										<input type="submit" class="btn" name="editagain" value="<bean:message key="labels.path_mapping_button_back"/>" />
										<input type="submit" class="btn btn-primary" name="create"
											value="<bean:message key="labels.path_mapping_button_create"/>"
										/>
									</c:if>
									<c:if test="${crudMode == 2}">
										<input type="submit" class="btn" name="editagain" value="<bean:message key="labels.path_mapping_button_back"/>" />
										<input type="submit" class="btn btn-primary" name="update"
											value="<bean:message key="labels.path_mapping_button_update"/>"
										/>
									</c:if>
									<c:if test="${crudMode == 3}">
										<input type="submit" class="btn" name="back" value="<bean:message key="labels.path_mapping_button_back"/>" />
										<input type="submit" class="btn btn-primary" name="delete"
											value="<bean:message key="labels.path_mapping_button_delete"/>"
										/>
									</c:if>
									<c:if test="${crudMode == 4}">
										<input type="submit" class="btn" name="back" value="<bean:message key="labels.path_mapping_button_back"/>" />
										<input type="submit" class="btn" name="editfromconfirm"
											value="<bean:message key="labels.path_mapping_button_edit"/>"
										/>
										<input type="submit" class="btn" name="deletefromconfirm"
											value="<bean:message key="labels.path_mapping_button_delete"/>"
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
