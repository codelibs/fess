<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><tiles:insert template="/WEB-INF/view/common/admin/layout.jsp" flush="true">
	<tiles:put name="title"><la:message key="labels.dict_userdict_configuration" /></tiles:put>
	<tiles:put name="header" value="/WEB-INF/view/common/admin/header.jsp" />
	<tiles:put name="footer" value="/WEB-INF/view/common/admin/footer.jsp" />
	<tiles:put name="menu" value="/WEB-INF/view/common/admin/menu.jsp" />
	<tiles:put name="menuType" value="dict" />
	<tiles:put name="headerScript" type="string"></tiles:put>
	<tiles:put name="body" type="string">

      <div id="main">
      
<html:errors/>
<br/>
<la:link href="../index"><la:message key="labels.dict_button_back"/></la:link>

      </div>

	</tiles:put>
</tiles:insert>
