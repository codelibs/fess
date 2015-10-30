<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title" /> | <la:message
		key="labels.file_crawling_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="crawl" />
			<jsp:param name="menuType" value="fileConfig" />
		</jsp:include>

		<div class="content-wrapper">

			<%-- Content Header --%>
			<section class="content-header">
				<h1>
					<la:message key="labels.file_crawling_title_details" />
				</h1>
				<ol class="breadcrumb">
					<li><la:link href="/admin/fileconfig">
							<la:message key="labels.file_crawling_link_list" />
						</la:link></li>
					<c:if test="${crudMode == 1}">
						<li class="active"><a href="#"><la:message
									key="labels.file_crawling_link_create" /></a></li>
					</c:if>
					<c:if test="${crudMode == 2}">
						<li class="active"><a href="#"><la:message
									key="labels.file_crawling_link_update" /></a></li>
					</c:if>
					<c:if test="${crudMode == 3}">
						<li class="active"><a href="#"><la:message
									key="labels.file_crawling_link_delete" /></a></li>
					</c:if>
					<c:if test="${crudMode == 4}">
						<li class="active"><a href="#"><la:message
									key="labels.file_crawling_link_confirm" /></a></li>
					</c:if>
				</ol>
			</section>

			<section class="content">

				<%-- Form --%>
				<la:form styleClass="form-horizontal">
					<la:hidden property="crudMode" />
					<c:if test="${crudMode==2}">
						<la:hidden property="id" />
						<la:hidden property="versionNo" />
					</c:if>
					<la:hidden property="createdBy" />
					<la:hidden property="createdTime" />
					<la:hidden property="sortOrder" />
					<div class="row">
						<div class="col-md-12">
							<div
								class="box <c:if test="${crudMode == 1}">box-success</c:if><c:if test="${crudMode == 2}">box-warning</c:if>">
								<%-- Box Header --%>
								<div class="box-header with-border">
									<h3 class="box-title">
										<c:if test="${crudMode == 1}">
											<la:message key="labels.file_crawling_link_create" />
										</c:if>
										<c:if test="${crudMode == 2}">
											<la:message key="labels.file_crawling_link_update" />
										</c:if>
									</h3>
									<div class="btn-group pull-right">
										<la:link href="/admin/fileconfig"
											styleClass="btn btn-primary btn-xs">
											<la:message key="labels.file_crawling_link_list" />
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

									<%-- Form Fields --%>
									<div class="form-group">
										<label for="name" class="col-sm-3 control-label"><la:message
												key="labels.name" /></label>
										<div class="col-sm-9">
											<la:text property="name" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="paths" class="col-sm-3 control-label"><la:message
												key="labels.paths" /></label>
										<div class="col-sm-9">
											<la:textarea property="paths" styleClass="form-control"
												rows="5" />
										</div>
									</div>
									<div class="form-group">
										<label for="includedPaths" class="col-sm-3 control-label"><la:message
												key="labels.included_paths" /></label>
										<div class="col-sm-9">
											<la:textarea property="includedPaths"
												styleClass="form-control" rows="5" />
										</div>
									</div>
									<div class="form-group">
										<label for="excludedPaths" class="col-sm-3 control-label"><la:message
												key="labels.excluded_paths" /></label>
										<div class="col-sm-9">
											<la:textarea property="excludedPaths"
												styleClass="form-control" rows="5" />
										</div>
									</div>
									<div class="form-group">
										<label for="includedDocPaths" class="col-sm-3 control-label"><la:message
												key="labels.included_doc_paths" /></label>
										<div class="col-sm-9">
											<la:textarea property="includedDocPaths"
												styleClass="form-control" rows="5" />
										</div>
									</div>
									<div class="form-group">
										<label for="excludedDocPaths" class="col-sm-3 control-label"><la:message
												key="labels.excluded_doc_paths" /></label>
										<div class="col-sm-9">
											<la:textarea property="excludedDocPaths"
												styleClass="form-control" rows="5" />
										</div>
									</div>
									<div class="form-group">
										<label for="configParameter" class="col-sm-3 control-label"><la:message
												key="labels.config_parameter" /></label>
										<div class="col-sm-9">
											<la:text property="configParameter" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="depth" class="col-sm-3 control-label"><la:message
												key="labels.depth" /></label>
										<div class="col-sm-9">
											<la:text property="depth" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="maxAccessCount" class="col-sm-3 control-label"><la:message
												key="labels.max_access_count" /></label>
										<div class="col-sm-9">
											<la:text property="maxAccessCount" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="numOfThread" class="col-sm-3 control-label"><la:message
												key="labels.number_of_thread" /></label>
										<div class="col-sm-9">
											<la:text property="numOfThread" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="intervalTime" class="col-sm-3 control-label"><la:message
												key="labels.interval_time" /></label>
										<div class="form-inline col-sm-9">
											<la:text property="intervalTime" styleClass="form-control" />
											<la:message key="labels.millisec" />
										</div>
									</div>
									<div class="form-group">
										<label for="boost" class="col-sm-3 control-label"><la:message
												key="labels.boost" /></label>
										<div class="col-sm-9">
											<la:text property="boost" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="roleTypeIds" class="col-sm-3 control-label"><la:message
												key="labels.role_type" /></label>
										<div class="col-sm-9">
											<la:select property="roleTypeIds" multiple="true"
												styleClass="form-control">
												<c:forEach var="rt" varStatus="s" items="${roleTypeItems}">
													<la:option value="${f:u(rt.id)}">${f:h(rt.name)}</la:option>
												</c:forEach>
											</la:select>
										</div>
									</div>
									<div class="form-group">
										<label for="roleTypeIds" class="col-sm-3 control-label"><la:message
												key="labels.label_type" /></label>
										<div class="col-sm-9">
											<la:select property="labelTypeIds" multiple="true"
												styleClass="form-control">
												<c:forEach var="l" varStatus="s" items="${labelTypeItems}">
													<la:option value="${f:u(l.id)}">${f:h(l.name)}</la:option>
												</c:forEach>
											</la:select>
										</div>
									</div>
									<div class="form-group">
										<label for="available" class="col-sm-3 control-label"><la:message
												key="labels.available" /></label>
										<div class="col-sm-9">
											<la:select property="available" styleClass="form-control">
												<la:option value="true">
													<la:message key="labels.enabled" />
												</la:option>
												<la:option value="false">
													<la:message key="labels.disabled" />
												</la:option>
											</la:select>
										</div>
									</div>
								</div>

								<%-- Box Footer --%>
								<div class="box-footer">
									<c:if test="${crudMode == 1}">
										<button type="submit" class="btn" name="back"
											value="<la:message key="labels.file_crawling_button_back" />">
											<la:message key="labels.file_crawling_button_back" />
										</button>
										<button type="submit" class="btn btn-success"
											name="confirmfromcreate"
											value="<la:message key="labels.file_crawling_button_confirm" />">
											<la:message key="labels.file_crawling_button_confirm" />
										</button>
									</c:if>
									<c:if test="${crudMode == 2}">
										<button type="submit" class="btn" name="back"
											value="<la:message key="labels.file_crawling_button_back" />">
											<la:message key="labels.file_crawling_button_back" />
										</button>
										<button type="submit" class="btn btn-warning"
											name="confirmfromupdate"
											value="<la:message key="labels.file_crawling_button_confirm" />">
											<la:message key="labels.file_crawling_button_confirm" />
										</button>
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
