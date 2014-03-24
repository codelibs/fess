<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="content-style-type" content="text/css" />
<meta http-equiv="content-script-type" content="text/javascript" />
<title>${f:h(displayQuery)} - <bean:message
		key="labels.search_title" /></title>
<link href="${f:url('/css/style-base.css')}" rel="stylesheet"
	type="text/css" />
<link href="${f:url('/css/style.css')}" rel="stylesheet"
	type="text/css" />
<script type="text/javascript" src="${f:url('/js/jquery-1.11.0.min.js')}"></script>
</head>
<body>
	<jsp:include page="/WEB-INF/view/header.jsp" />
	<div class="container">
		<div class="content">
			<div class="span8">
				<div>
					<bean:message key="labels.open_uri" arg0="${f:h(uri)}" />
				</div>
				<script src="${f:url('/js/applet/deploy.min.js')}"></script>
				<script>
var attributes = {
	code:'FileLauncher.class',
	archive:'${f:url(launcherJarFile)}',
	width:'100%',
	height:40};
var parameters = {
	jnlp_href:'${f:url(launcherJnlpFile)}',
	uri:'${path}'
	<c:if test="${referrer != ''}">,referrer: '${referrer}'</c:if>
	};
deployJava.runApplet(attributes, parameters, '1.6'); 
				</script>
			</div>
		</div>
		<jsp:include page="/WEB-INF/view/footer.jsp" />
	</div>
</body>
</html>
