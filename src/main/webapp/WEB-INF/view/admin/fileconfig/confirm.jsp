<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Fess | <la:message key="labels.file_crawling_configuration" /></title>
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
					<li><la:link href="index">
							<la:message key="labels.file_crawling_link_list" />
						</la:link></li>
					<c:if test="${crudMode == 1}">
						<li class="active"><a href="#"><la:message key="labels.file_crawling_link_create" /></a></li>
					</c:if>
					<c:if test="${crudMode == 2}">
						<li class="active"><a href="#"><la:message key="labels.file_crawling_link_update" /></a></li>
					</c:if>
					<c:if test="${crudMode == 3}">
						<li class="active"><a href="#"><la:message key="labels.file_crawling_link_delete" /></a></li>
					</c:if>
					<c:if test="${crudMode == 4}">
						<li class="active"><a href="#"><la:message key="labels.file_crawling_link_confirm" /></a></li>
					</c:if>
				</ol>
			</section>

			<section class="content">

				<%-- Form --%>
				<la:form>
					<la:hidden property="crudMode" />
					<c:if test="${crudMode==2 || crudMode==3 || crudMode==4}">
						<la:hidden property="id" />
						<la:hidden property="versionNo" />
					</c:if>
					<la:hidden property="createdBy" />
					<la:hidden property="createdTime" />
					<la:hidden property="sortOrder"/>
					<div class="row">
						<div class="col-md-12">
							<div class="box">
								<%-- Box Header --%>
								<div class="box-header with-border">
									<h3 class="box-title">
										<c:if test="${crudMode == 1}">
											<la:message key="labels.file_crawling_link_create" />
										</c:if>
										<c:if test="${crudMode == 2}">
											<la:message key="labels.file_crawling_link_update" />
										</c:if>
										<c:if test="${crudMode == 3}">
											<la:message key="labels.file_crawling_link_delete" />
										</c:if>
										<c:if test="${crudMode == 4}">
											<la:message key="labels.file_crawling_link_confirm" />
										</c:if>
									</h3>
									<div class="box-tools pull-right">
										<span class="label label-default"><la:link href="index">
												<la:message key="labels.file_crawling_link_list" />
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
									<table class="table table-bordered">
										<tbody>
											<c:if test="${id != null}"><tr>
												<th class="col-xs-3"><la:message key="labels.id" /></th>
												<td>${f:h(id)}</td>
											</tr></c:if>
											<tr>
												<th class="col-xs-3"><la:message key="labels.name" /></th>
												<td>${f:h(name)}<la:hidden property="name" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.paths" /></th>
												<td>${f:br(f:h(paths))}<la:hidden property="paths" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.included_paths" /></th>
												<td>${f:br(f:h(includedPaths))}<la:hidden property="includedPaths" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.excluded_paths" /></th>
												<td>${f:br(f:h(excludedPaths))}<la:hidden property="excludedPaths" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.included_doc_paths" /></th>
												<td>${f:br(f:h(includedDocPaths))}<la:hidden property="includedDocPaths" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.excluded_doc_paths" /></th>
												<td>${f:br(f:h(excludedDocPaths))}<la:hidden property="excludedDocPaths" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.config_parameter" /></th>
												<td>${f:br(f:h(configParameter))}<la:hidden property="configParameter" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.depth" /></th>
												<td>${f:h(depth)}<la:hidden property="depth" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.max_access_count" /></th>
												<td>${f:h(maxAccessCount)}<la:hidden property="maxAccessCount" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.number_of_thread" /></th>
												<td>${f:h(numOfThread)}<la:hidden property="numOfThread" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.interval_time" /></th>
												<td>${f:h(intervalTime)}<la:hidden property="intervalTime" /><la:message key="labels.millisec"/></td>
											</tr>
											<tr>
												<th><la:message key="labels.boost" /></th>
												<td>${f:h(boost)}<la:hidden property="boost" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.role_type" /></th>
													<td>
														<c:forEach var="rt" varStatus="s" items="${roleTypeItems}">
															<c:forEach var="rtid" varStatus="s" items="${roleTypeIds}">
																<c:if test="${rtid==rt.id}">
																	${f:h(rt.name)}<br/>
																</c:if>
															</c:forEach>
														</c:forEach>
														<la:select property="roleTypeIds" multiple="true" style="display:none;">
															<c:forEach var="rt" varStatus="s" items="${roleTypeItems}">
																<la:option value="${f:u(rt.id)}">${f:h(rt.name)}</la:option>
															</c:forEach>
														</la:select>
													</td>
											</tr>
											<tr>
												<th><la:message key="labels.label_type" /></th>
												<td>
													<c:forEach var="l" varStatus="s" items="${labelTypeItems}">
														<c:forEach var="ltid" varStatus="s" items="${labelTypeIds}">
															<c:if test="${ltid==l.id}">
																${f:h(l.name)}<br/>
															</c:if>
														</c:forEach>
													</c:forEach>
													<la:select property="labelTypeIds" multiple="true" style="display:none;">
														<c:forEach var="l" varStatus="s" items="${labelTypeItems}">
															<la:option value="${f:u(l.id)}">${f:h(l.name)}</la:option>
														</c:forEach>
													</la:select>
												</td>
											</tr>
											<tr>
												<th><la:message key="labels.available"/></th>
												<td><la:hidden property="available"/>
													<c:if test="${available=='true'}"><la:message key="labels.enabled"/></c:if>
													<c:if test="${available=='false'}"><la:message key="labels.disabled"/></c:if>
												</td>
											</tr>
										</tbody>
									</table>
								</div>

								<%-- Box Footer --%>
								<div class="box-footer">
									<c:if test="${crudMode == 1}">
										<input type="submit" class="btn" name="editagain" value="<la:message key="labels.file_crawling_button_back"/>" />
										<input type="submit" class="btn btn-primary" name="create"
											value="<la:message key="labels.file_crawling_button_create"/>"
										/>
									</c:if>
									<c:if test="${crudMode == 2}">
										<input type="submit" class="btn" name="editagain" value="<la:message key="labels.file_crawling_button_back"/>" />
										<input type="submit" class="btn btn-primary" name="update"
											value="<la:message key="labels.file_crawling_button_update"/>"
										/>
									</c:if>
									<c:if test="${crudMode == 3}">
										<input type="submit" class="btn" name="back" value="<la:message key="labels.file_crawling_button_back"/>" />
										<input type="submit" class="btn btn-primary" name="delete"
											value="<la:message key="labels.file_crawling_button_delete"/>"
										/>
									</c:if>
									<c:if test="${crudMode == 4}">
										<input type="submit" class="btn" name="back" value="<la:message key="labels.file_crawling_button_back"/>" />
										<input type="submit" class="btn" name="editfromconfirm"
											value="<la:message key="labels.file_crawling_button_edit"/>"
										/>
										<input type="submit" class="btn" name="deletefromconfirm"
											value="<la:message key="labels.file_crawling_button_delete"/>"
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
