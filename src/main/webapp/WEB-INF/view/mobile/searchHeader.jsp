<%@page pageEncoding="UTF-8" %>
<div id="header">
  <div>
    <s:form>
      <div>
<m:img src="logo-top.gif" magniWidth="0.3" />
<br/>
<html:text property="query" title="Search" size="16" maxlength="1000" />
<input type="submit" value="<bean:message key="labels.search"/>" name="search"/>
      </div>
    </s:form>
  </div>
</div>
