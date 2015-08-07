<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Fess | <bean:message key="labels.log_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin2/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin2/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin2/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="crawl" />
			<jsp:param name="menuType" value="log" />
		</jsp:include>

		<div class="content-wrapper">

			<%-- Content Header --%>
			<section class="content-header">
				<h1>
					<bean:message key="labels.log_configuration" />
				</h1>
				<ol class="breadcrumb">
					<li class="active"><s:link href="index">
							<bean:message key="labels.log_configuration" />
						</s:link></li>
				</ol>
			</section>

			<section class="content">

				<div class="row">
					<div class="col-md-12">
						<div class="box">
							<%-- Box Header --%>
							<div class="box-header with-border">
								<h3 class="box-title">
									<bean:message key="labels.log_configuration" />
								</h3>
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

								<%-- List --%>
								<div>
									<table class="bordered-table zebra-striped">
										<tbody>
											<tr>
												<th><bean:message key="labels.log_file_name" /></th>
												<th style="width: 200px;text-align: center;"><bean:message key="labels.log_file_date" /></th>
											</tr>
											<c:forEach var="logFile" varStatus="s" items="${logFileItems}">
												<tr class="${s.index % 2 == 0 ? 'row1' : 'row2'}">
													<td>
														<s:link href="download/${f:u(logFile.logFileName)}">${f:h(logFile.name)}</s:link>
													</td>
													<td style="overflow-x: auto;>
														<fmt:formatDate	value="${logFile.lastModified}" type="BOTH" dateStyle="MEDIUM" />
													</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>

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

