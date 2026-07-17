<%
Integer statusCode = (Integer)request.getAttribute("jakarta.servlet.error.status_code");
String servletName = (String)request.getAttribute("jakarta.servlet.error.servlet_name");
String requestUri = (String)request.getAttribute("jakarta.servlet.error.request_uri");
String type = request.getParameter("type");
StringBuilder redirectPage = new StringBuilder();
redirectPage.append(((jakarta.servlet.http.HttpServletRequest)request).getContextPath());
if("systemError".equals(type)) {
	if(requestUri != null && !requestUri.endsWith("systemError")) {
		redirectPage.append("/error/systemerror/");
		response.sendRedirect(redirectPage.toString());
	} else {
		response.sendError(statusCode);
	}
} else if("logOut".equals(type)) {
	redirectPage.append("/login/?type=logout&code=" + statusCode);
	response.sendRedirect(redirectPage.toString());
} else if("badRequest".equals(type)) {
	redirectPage.append("/error/badrequest/");
	response.sendRedirect(redirectPage.toString());
} else if("busy".equals(type)) {
	redirectPage.append("/error/busy/");
	response.sendRedirect(redirectPage.toString());
} else if("badAuth".equals(type)) {
	// This branch is reached only from the 401 error-page mapping, so the status the
	// container is rendering this page for is always 401.
	if(org.codelibs.fess.util.WebApiUtil.isApiRequestUri(requestUri, ((jakarta.servlet.http.HttpServletRequest)request).getContextPath())) {
		// An API client needs the status, not a page: issuing no redirect leaves the 401
		// the container already set intact, and the body below is returned as-is.
%>Bad Authentication.<%
	} else {
		// A browser gets the themed error page instead of the plain-text output above.
		// message_key lets a static theme surface a localised error detail; the JSP-mode
		// action simply ignores the unknown parameter.
		redirectPage.append("/error/systemerror/?message_key=errors.bad_authentication");
		response.sendRedirect(redirectPage.toString());
	}
} else {
	redirectPage.append("/error/notfound/?url=");
	redirectPage.append(java.net.URLEncoder.encode(requestUri , "UTF-8"));
	response.sendRedirect(redirectPage.toString());
}
 %>
