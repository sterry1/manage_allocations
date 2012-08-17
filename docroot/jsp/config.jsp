<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>

<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="com.liferay.portal.kernel.util.Validator" %>
<%@ page import="com.liferay.portlet.PortletPreferencesFactoryUtil" %>
<%@ page import="com.liferay.portal.kernel.util.Constants" %>
<%@ page import="javax.portlet.PortletPreferences" %>
<%@ page import="javax.portlet.RenderRequest" %>

<portlet:defineObjects />
<%
	PortletPreferences preferences = renderRequest.getPreferences();
	
	String portletResource = ParamUtil.getString(request, "portletResource");
	
	if (Validator.isNotNull(portletResource)) {
		preferences = PortletPreferencesFactoryUtil.getPortletSetup(request, portletResource);
	}

	String param_name = preferences.getValue("mode","");
%>
<liferay-portlet:actionURL var="configUrl" portletConfiguration="true" />
<aui:form action="<%=configUrl %>" method="post" name="fm" >
	<input	name="<portlet:namespace /><%=Constants.CMD%>" type="hidden" value="<%=Constants.UPDATE%>"
	<aui:layout>
		<aui:column>
			<span>Portlet mode</span>
		</aui:column>
		<aui:column>
			<%
				if (preferences != null) {
			%>
				<aui:input name="mode" label="Full view" type="radio" value="full-view" checked="<%=param_name.equals(\"full-view\") %>" />
				<aui:input name="mode" label="Mini view" type="radio" value="mini-view" checked="<%=param_name.equals(\"mini-view\") %>" />
			<%
				} else {
			%>
				<aui:input name="mode" label="Full view" type="radio" value="full-view" checked="true" />
				<aui:input name="mode" label="Mini view" type="radio" value="mini-view" checked="false" />
			<%
				}
			%>
		</aui:column>
	</aui:layout>
	<aui:button type="submit" />
</aui:form>
