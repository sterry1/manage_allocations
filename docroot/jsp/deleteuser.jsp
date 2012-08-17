<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.WeakHashMap"%>
<%@ page import="edu.utexas.tacc.portlets.Allocation"%>
<%@ page import="edu.utexas.tacc.portlets.DBUtil"%>
<%@ page import="edu.utexas.tacc.portlets.Project"%>
<%@ page import="edu.utexas.tacc.portlets.ManageAllocationsPortlet"%>
<%@ page import="edu.utexas.tacc.portlets.User"%>
<portlet:defineObjects/>
<script>
function validate_checkbox() {
	var isChecked = false;    
	for (var j = 0; j<document.deleteform.user.length; j++) {
		if (deleteform.user[j].checked) {             
			isChecked = true;
		}
	}
	if (document.deleteform.user.checked == true) {
		isChecked = true;
	}    
	if (isChecked == false) {
		alert("Please select name(s) to remove to your allocation and try again.");
	}
	return isChecked;
}
</script>
<%
	String action = (String) request.getAttribute(ManageAllocationsPortlet.ACTION);
	String status = (String) request.getAttribute(ManageAllocationsPortlet.STATUS);
	Project project = (Project) request.getAttribute(ManageAllocationsPortlet.PROJECT);
	Allocation allocation = (Allocation) request.getAttribute(ManageAllocationsPortlet.ALLOCATION);
	
	//convert between db resource names to short human-readable name
	String resName = allocation.getResource();
	String origResName = resName;
	WeakHashMap hash = DBUtil.getShortNameMap();
	if (hash.containsKey(resName)) {
		resName = (String) hash.get(resName);
	}
	User[] users = allocation.getUsers();
	HashMap popsIdMap = allocation.getPopsPersonIds();
%>

<portlet:actionURL var="actionUrlSubmit">
	<portlet:param name="<%=ManageAllocationsPortlet.ACTION_METHOD %>" value="<%=ManageAllocationsPortlet.PI_DELETE_CONFIRM %>"/>
</portlet:actionURL>

<p>
	Please select users to <strong>remove</strong> from (Grant # <%=project.getChargeNumber() %>):
</p>
<form name="deleteform" action="<%= actionUrlSubmit %>" method="post" onsubmit="return validate_checkbox();">
	<input type="hidden" name="<%=ManageAllocationsPortlet.RESOURCE %>" value="<%=resName %>">
	<input type="hidden" name="<%=ManageAllocationsPortlet.ORIGINAL_RESOURCE %>" value="<%=origResName %>">
	<input type="hidden" name="<%=ManageAllocationsPortlet.CHARGE_NUMBER %>" value="<%=project.getChargeNumber() %>">
	<input type="hidden" name="<%=ManageAllocationsPortlet.GRANT_NUMBER %>" value="<%=project.getChargeNumber()%>">
	<input type="hidden" name="<%=ManageAllocationsPortlet.PROJECT_IDX %>" value="<%=request.getAttribute(ManageAllocationsPortlet.PROJECT_IDX).toString() %>">
	<ul class="myul">
	<% for (int u=0; u < users.length; u++) { %>
		<li>
		<label>
			<input type="checkbox" name="<%=ManageAllocationsPortlet.USER %>" value="<%=popsIdMap.get(new Integer(users[u].getPersonID()))%>=<%=users[u].getPersonID()%>=<%=users[u].getLastName()%>=<%=users[u].getFirstName()%>">
			<%=users[u].getLastName()%>,&nbsp;<%=users[u].getFirstName()%>
		</label>
		</li>
	<% } %>
	</ul>
	<p>
		<input type="radio" name="<%=ManageAllocationsPortlet.REMOVE_ALL %>" value="<%=ManageAllocationsPortlet.REMOVE_ALL_NO %>" checked > Remove user(s) from the <strong><%= resName %></strong> only <br>
		<input type="radio" name="<%=ManageAllocationsPortlet.REMOVE_ALL %>" value="<%=ManageAllocationsPortlet.REMOVE_ALL_YES %>"> Remove user(s) from <strong>all active allocations</strong> in the current project 
	</p><p>
		<input type="submit" name="deleteSubmit" value="Submit">
		<input type="button" value="Cancel &amp; Return to Allocations Listing" onclick="window.location='<portlet:actionURL></portlet:actionURL>'">
	</p>
</form>
<script>
	(function($) {
		$('ul.myul li:nth-child(even)').addClass('even');
	})(jQuery);
</script>
