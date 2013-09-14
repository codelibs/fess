<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title><tiles:getAsString name="title" /></title>
<link href="${f:url('/css/admin/style-base.css')}" rel="stylesheet"
	type="text/css" />
<link href="${f:url('/css/admin/style.css')}" rel="stylesheet"
	type="text/css" />
<script type="text/javascript" src="${f:url('/js/admin/jquery-1.8.3.min.js')}"></script>
<tiles:insert attribute="headerScript" />
</head>
<body>
	<tiles:insert attribute="header">
		<tiles:put name="title">
			<tiles:getAsString name="title" />
		</tiles:put>
	</tiles:insert>
	<div id="body" class="container">
		<%-- HEADER: BEGIN --%>

		<div class="content">
			<%-- HEADER: END --%>
			<div class="row">

				<%-- MENU: BEGIN --%>
				<div class="span4">
					<tiles:insert attribute="menu">
						<tiles:put name="menuType">
							<tiles:getAsString name="menuType" />
						</tiles:put>
					</tiles:insert>
				</div>
				<%-- MENU: END --%>

				<%-- BODY: BEGIN --%>
				<div class="span12">
					<tiles:insert attribute="body" />
				</div>
				<%-- BODY: END --%>

			</div>


		</div>
		<%-- FOOTER BEGIN --%>
		<tiles:insert attribute="footer" />
		<%-- FOOTER END --%>

	</div>
</body>
</html>
