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
	for (var j = 0; j<document.addform.user.length; j++) {
		if (addform.user[j].checked) {             
			isChecked = true;
		}
	}
	if (document.addform.user.checked == true) {
			isChecked = true;
	}    
	if (isChecked == false) {
		alert("Please select name(s) to add to your allocation and try again.");
	}
	return isChecked;
}
</script>

<%
String action = (String)request.getAttribute(ManageAllocationsPortlet.ACTION);
String status = (String)request.getAttribute(ManageAllocationsPortlet.STATUS);
Project project = (Project)request.getAttribute(ManageAllocationsPortlet.PROJECT);
Allocation allocation = (Allocation)request.getAttribute(ManageAllocationsPortlet.ALLOCATION);

//convert between db resource names to short human-readable name
String resName = allocation.getResource();
String origResName = resName;
WeakHashMap hash = DBUtil.getShortNameMap();
if (hash.containsKey(resName)) {
	resName = (String)hash.get(resName);
}
User[] users = allocation.getUsers();
User[] allUsers = allocation.getAllUsers();
HashMap userHash = allocation.getUserHash();
boolean noUsers = true;
%>
<p>
	This form allows you to add existing users on your allocation to <%=resName%>.
	&nbsp;To add a user not in this list you must have their portal username and fill out the
	<a href="/group/xup/add-remove-user">XSEDE Add User form</a>. 
</p>
<p>
	Please select users to <strong>add</strong> to <%=resName%> (Grant # <%=project.getChargeNumber()%>):
</p>
<portlet:actionURL var="actionUrlSubmit">
    <portlet:param name="<%=ManageAllocationsPortlet.ACTION_METHOD %>" value="<%=ManageAllocationsPortlet.PI_ADD_CONFIRM %>"/>
</portlet:actionURL>
<form name="addform" action="<%= actionUrlSubmit %>" method="post" onsubmit="return validate_checkbox();">
	<input type="hidden" name="<%=ManageAllocationsPortlet.RESOURCE %>" value="<%= resName %>">
	<input type="hidden" name="<%=ManageAllocationsPortlet.ORIGINAL_RESOURCE %>" value="<%= origResName %>">
	<input type="hidden" name="<%=ManageAllocationsPortlet.CHARGE_NUMBER %>" value="<%= project.getChargeNumber() %>">
	<input type="hidden" name="<%=ManageAllocationsPortlet.GRANT_NUMBER %>" value="<%=project.getChargeNumber()%>">
	<ul class="myul">
	<% for (int u=0; u < allUsers.length; u++) { %>
		<% if (! userHash.containsKey(allUsers[u].getPersonID())) { %>
			<% noUsers = false; %>
			<li>
			<label>
				<input type="checkbox" name="<%=ManageAllocationsPortlet.USER %>" value="<%=allUsers[u].getPopsPersonID()%>=<%=allUsers[u].getPersonID()%>=<%=allUsers[u].getLastName()%>=<%=allUsers[u].getFirstName()%>">
				<%=allUsers[u].getLastName()%>, <%=allUsers[u].getFirstName()%>
			</label>
			</li>
		<% } %>
	<% } %>
	</ul>
	<% if (noUsers) { %>
		<p>
			There are no additional users on other allocations to add. To add additional users please visit the
			<a href="/group/xup/add-remove-user">Add/Remove user form</a>.
		</p>
	<% } else { %>
		<p>
			<strong class="stress">IMPORTANT</strong>: To add a user not in this list
			please complete the <a href="/group/xup/add-remove-user">Add User form</a>.
		</p>
		<input type="submit" name="addSubmit" value="Submit">
	<% } %>
	<input type="button" onclick="window.location='<portlet:actionURL></portlet:actionURL>'" value="Cancel &amp; Return to Allocations Listing">
</form>
<script>
	(function($) {
		$('ul.myul li:nth-child(even)').addClass('even');
	})(jQuery);
</script>
