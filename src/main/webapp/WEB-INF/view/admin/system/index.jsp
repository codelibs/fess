<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Fess | <la:message key="labels.system_title_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin2/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin2/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin2/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="system" />
			<jsp:param name="menuType" value="system" />
		</jsp:include>

		<div class="content-wrapper">
			<%-- Content Header --%>
			<%-- Message --%>
			<div>
				<la:info id="msg" message="true">
					<div class="alert-message info">${msg}</div>
				</la:info>
				<la:errors />
			</div>

			<section class="content">
				<div class="row">
					<div class="col-md-12">
						<div class="box">
							<%-- Box Header --%>
							<div class="box-header">
								<h3 class="box-title">
									<la:message key="labels.system_title_system_status" />
								</h3>
							</div>
							<%-- Box Body --%>
							<div class="box-body">
								<table class="table table-bordered table-hover table-striped">
									<tbody>
										<tr>
											<th class="col-xs-3"><la:message
													key="labels.es_cluster_name" /></th>
											<td>${f:h(clusterName)}
												(${f:h(clusterStatus)})</td>
										</tr>
									</tbody>
								</table>
							</div>
						</div>

						<la:form>
							<div class="box">
								<%-- Box Header --%>
								<div class="box-header">
									<h3 class="box-title">
										<la:message key="labels.crawler_status_title" />
									</h3>
								</div>
								<%-- Box Body --%>
								<div class="box-body">
									<%-- Message --%>
									<table class="table table-bordered table-hover table-striped">
										<tbody>
											<tr>
												<th class="col-xs-3"><la:message
														key="labels.crawler_process_running" /></th>
												<td><c:if test="${crawlerRunning}">
														<la:message key="labels.crawler_running" />
													</c:if>
													<c:if test="${!crawlerRunning}">
														<la:message key="labels.crawler_stopped" />
													</c:if></td>
											</tr>
											<tr>
												<th><la:message key="labels.crawler_process_action" />
												</th>
												<td><c:if test="${!crawlerRunning}">
														<input type="submit" class="btn" name="start"
															value="<la:message key="labels.crawler_button_start"/>" />
													</c:if> <c:if test="${crawlerRunning}">
														<div class="form-inline">
															<la:select property="sessionId" styleClass="form-control">
																<option value=""><la:message
																		key="labels.crawler_sessionid_all" /></option>
																<c:forEach var="runningSessionId"
																	items="${runningSessionIds}">
																	<option value="${f:h(runningSessionId)}">${f:h(runningSessionId)}</option>
																</c:forEach>
															</la:select>
															<input type="submit" class="btn" name="stop"
																value="<la:message key="labels.crawler_button_stop"/>" />
														</div>
													</c:if></td>
											</tr>
										</tbody>
									</table>
								</div>
							</div>
						</la:form>
					</div>
				</div>
			</section>
		</div>
		<jsp:include page="/WEB-INF/view/common/admin2/footer.jsp"></jsp:include>
	</div>
	<jsp:include page="/WEB-INF/view/common/admin2/foot.jsp"></jsp:include>
</body>
</html>
