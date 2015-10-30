<%@page pageEncoding="UTF-8" %>
    <div>
      <div style="text-align: center;">
        <m:img src="logo-top.png" magniWidth="0.8" style="vertical-align: middle;" />
        <br/>
        <s:form>
          <div>
          	<html:text property="query" title="Search" size="20" maxlength="1000" />
            <br/>
            <input type="submit" value="<bean:message key="labels.top.search"/>" name="search" />
          </div>
        </s:form>
      </div>
    </div>
