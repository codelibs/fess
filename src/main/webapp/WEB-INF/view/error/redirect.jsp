<% 
Integer statusCode = (Integer)request.getAttribute("javax.servlet.error.status_code");
String servletName = (String)request.getAttribute("javax.servlet.error.servlet_name");
String requestUri = (String)request.getAttribute("javax.servlet.error.request_uri");
String type = request.getParameter("type");
StringBuilder redirectPage = new StringBuilder();
redirectPage.append(((javax.servlet.http.HttpServletRequest)request).getContextPath());
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
} else if(!"badAuth".equals(type)) {
	redirectPage.append("/error/notfound/?url=");
	redirectPage.append(java.net.URLEncoder.encode(requestUri , "UTF-8"));
	response.sendRedirect(redirectPage.toString());
}
 %>Bad Authentication.
