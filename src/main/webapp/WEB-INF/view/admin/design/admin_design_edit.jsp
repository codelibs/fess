<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Fess | <la:message key="labels.design_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin2/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin2/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin2/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="crawl" />
			<jsp:param name="menuType" value="design" />
		</jsp:include>

		<div class="content-wrapper">
		
			<%-- Content Header --%>
			<section class="content-header">
				<h1>
					<la:message key="labels.design_configuration" />
				</h1>
			</section>
			
			<section class="content">
				
				<div class="row">
					<div class="col-md-12">
						<div>
                            <%--
							<la:messages id="msg" message="true">
								<div class="alert-message info"><la:write name="msg" ignore="true" /></div>
							</la:messages>
                            --%>
							<la:errors />
						</div>
						<div class="box">
							<c:if test="${editable}">
								<la:form>
									<%-- Box Header --%>
									<div class="box-header with-border">
										<h3>
											<la:message key="labels.design_title_edit_content" />
										</h3>
									</div>
									<%-- Box Body --%>
									<div class="box-body">
										<h4>${f:h(fileName)}</h4>
										<div>
											<la:textarea property="content" style="width:98%" rows="20"></la:textarea>
										</div>
									</div>
									<%-- Box Footer --%>
									<div class="box-footer">
										<input type="submit" class="btn small" name="update" value="<la:message key="labels.design_button_update"/>" />
										<input type="submit" class="btn small" name="back" value="<la:message key="labels.design_button_back"/>" />
									</div>
									<la:hidden property="fileName" />
								</la:form>
							</c:if>
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
