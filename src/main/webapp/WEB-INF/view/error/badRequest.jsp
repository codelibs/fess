<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html>
<% try{ %>
<html>
<head profile="http://a9.com/-/spec/opensearch/1.1/">
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="x-ua-compatible" content="ie=edge">
<title><la:message key="labels.system_error_title" /></title>
<link href="${f:url('/css/style-base.css')}" rel="stylesheet"
	type="text/css" />
<link href="${f:url('/css/style.css')}" rel="stylesheet"
	type="text/css" />
</head>
<body>
	<jsp:include page="header.jsp" />
	<div class="container">
		<div class="content">
			<div class="center row">
				<div class="col-md-12">
					<h2>
						<la:message key="labels.request_error_title" />
					</h2>
					<div class="errormessage"><la:message key="labels.bad_request" /></div>
				</div>
			</div>
		</div>
		<jsp:include page="footer.jsp" />
	</div>
	<input type="hidden" id="contextPath" value="<%=request.getContextPath()%>" />
	<script type="text/javascript"
		src="${f:url('/js/jquery-2.1.4.min.js')}"></script>
	<script type="text/javascript" src="${f:url('/js/bootstrap.js')}"></script>
</body>
</html>
<% }catch(Exception e){ session.invalidate();}%>
