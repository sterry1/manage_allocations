<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="java.util.Iterator"%>
<%@ page import="java.util.List"%>
<%@ page import="edu.utexas.tacc.portlets.AddRemoveBean"%>
<%@ page import="edu.utexas.tacc.portlets.ManageAllocationsPortlet"%>
<portlet:defineObjects/>
<%
	AddRemoveBean arb = (AddRemoveBean) request.getAttribute(ManageAllocationsPortlet.ADD_REM_BEAN);
	String removeAll = (String) request.getAttribute(ManageAllocationsPortlet.REMOVE_ALL); 
	String projNum = (String) request.getAttribute(ManageAllocationsPortlet.PROJECT_IDX);
%>
<portlet:actionURL var="actionUrlSubmit">
	<portlet:param name="<%=ManageAllocationsPortlet.ACTION_METHOD %>" value="<%=ManageAllocationsPortlet.PI_DELETE_COMPLETE %>"/>
	<portlet:param name="<%=ManageAllocationsPortlet.STATUS %>" value="<%=ManageAllocationsPortlet.COMPLETE %>"/>
</portlet:actionURL>
<p>
	Please confirm you are <b>removing</b> the following users from
	<strong>
	<% if (removeAll.equals(ManageAllocationsPortlet.REMOVE_ALL_YES)) { %>
		all active allocations 
	<% } else { %>
		<%=arb.getResource() %>
	<% } %>
	</strong>
	&#40;Grant # <%=arb.getChargeNumber() %>&#41;:
</p>
<form action="<%= actionUrlSubmit %>" method="post" >
	<ul class="myul">
		<%
			List userList = arb.getUserList();
			Iterator it = userList.iterator();
			while (it.hasNext()) {
				// Names are seperated by = sign so replace with space
				String item = (String)it.next();
				String[] fields = item.split("=");
				if (fields.length == 4) {
		%>
					<li><span><%= fields[2] %>, <%= fields[3] %></span></li>
		<%
				}
			}
		%>
	</ul>
	<p>
		<input type="hidden" name="<%=ManageAllocationsPortlet.REMOVE_ALL %>" value="<%= removeAll%>">
		<input type="hidden" name="<%=ManageAllocationsPortlet.PROJECT_IDX %>" value="<%= projNum%>">
		<input type="submit" name="submit" value="Confirm">
		<input type="button" value="Cancel &amp; Return to Allocations Listing" onclick="window.location='<portlet:actionURL></portlet:actionURL>'"/>
	</p>
</form>
<script>
	(function($) {
		$('ul.myul li:nth-child(even)').addClass('even');
	})(jQuery);
</script>
