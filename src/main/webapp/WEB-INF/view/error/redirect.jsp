<%
Integer statusCode = (Integer)request.getAttribute("jakarta.servlet.error.status_code");
String servletName = (String)request.getAttribute("jakarta.servlet.error.servlet_name");
String requestUri = (String)request.getAttribute("jakarta.servlet.error.request_uri");
String type = request.getParameter("type");
String contextPath = ((jakarta.servlet.http.HttpServletRequest)request).getContextPath();
StringBuilder redirectPage = new StringBuilder();
redirectPage.append(contextPath);
if("systemError".equals(type)) {
	// The system error page is itself an action, so it can fail for the same reason the
	// original request did. Redirecting to it again would loop until the browser gives up,
	// which is why the request that is already on that page falls through to the container.
	String errorPath = requestUri != null ? requestUri : "";
	if(!contextPath.isEmpty() && errorPath.startsWith(contextPath)) {
		errorPath = errorPath.substring(contextPath.length());
	}
	while(errorPath.endsWith("/")) {
		errorPath = errorPath.substring(0, errorPath.length() - 1);
	}
	if(requestUri != null && !errorPath.toLowerCase(java.util.Locale.ROOT).endsWith("/error/systemerror")) {
		redirectPage.append("/error/systemerror/");
		response.sendRedirect(redirectPage.toString());
	} else {
		// The system error page itself failed, so there is no page left to send to. Answering
		// from here ends the request; going to that page again is what loops. The status is the
		// one the container is rendering this page for, read from a request attribute it is not
		// obliged to set, hence the fallback. The cause belongs in the log, not in the response.
		response.setStatus(statusCode != null ? statusCode.intValue() : jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		response.setContentType("text/plain; charset=UTF-8");
%>System Error.<%
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
	if(org.codelibs.fess.util.WebApiUtil.isApiRequestUri(requestUri, contextPath)) {
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
