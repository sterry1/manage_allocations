<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ page import="java.util.List"%>
<%@ page import="java.util.WeakHashMap"%>
<%@ page import="edu.utexas.tacc.portlets.Allocation"%>
<%@ page import="edu.utexas.tacc.portlets.DBUtil"%>
<%@ page import="edu.utexas.tacc.portlets.Project"%>
<%@ page import="edu.utexas.tacc.portlets.ManageAllocationsPortlet"%>
<%@ page import="edu.utexas.tacc.portlets.User"%>
<portlet:defineObjects/>
<liferay-theme:defineObjects/>
<%
boolean isAdmin = (Boolean) request.getAttribute(ManageAllocationsPortlet.ADMIN);
Project p = (Project) request.getAttribute(ManageAllocationsPortlet.PROJECT);
Allocation a = (Allocation) request.getAttribute(ManageAllocationsPortlet.ALLOCATION);
String projIdx = request.getAttribute(ManageAllocationsPortlet.PROJECT_IDX).toString();
String allocIdx = request.getAttribute(ManageAllocationsPortlet.ALLOCATION_IDX).toString();

//convert between db resource names to short human-readable name
WeakHashMap hash = DBUtil.getShortNameMap();
String resourceName = a.getResource();
String origResourceName = resourceName;
if (hash.containsKey(resourceName)) {
	resourceName = (String) hash.get(resourceName);
}
%>
<span class="title">Users: <%=p.getTitle()%> (<%=p.getChargeNumber()%>)</span>
<div class="users-tabs" >
	<ul>
		<li><a href="#users<%=projIdx %><%=allocIdx %>">Users on <%=resourceName %></a></li>
		<li><a href="#removed<%=projIdx %><%=allocIdx %>">Removed Users on <%=resourceName %></a></li>
	</ul>
	<div id="users<%=projIdx %><%=allocIdx %>">
		<aui:layout>
			<aui:column columnWidth="50">
				<strong><%=a.getUsers().length %> Users on <%=resourceName %></strong>
			</aui:column>
			<aui:column columnWidth="50">
				<c:choose>
				<c:when test="${resourceActive}">
				<ul class="liststylenone">
					<% if (a.getState().equals("active") || a.getState().equals("pending")) { %>
					<li>
						<portlet:actionURL var="actionUrlSubmit">
							<portlet:param name="<%=ManageAllocationsPortlet.ACTION_METHOD %>" value="<%=ManageAllocationsPortlet.PI_ADD %>"/>
							<portlet:param name="<%=ManageAllocationsPortlet.STATUS %>" value="<%=ManageAllocationsPortlet.LIST %>" />
							<portlet:param name="<%=ManageAllocationsPortlet.PROJECT_IDX %>" value="<%=projIdx %>"/>
							<portlet:param name="<%=ManageAllocationsPortlet.ALLOCATION_IDX %>" value="<%=allocIdx %>"/>
						</portlet:actionURL>
						<a href="<%= actionUrlSubmit %>">Add User(s) to <%=resourceName %></a>
					</li>
					<% } %>
					<li>
						<portlet:actionURL var="actionUrlSubmitRemove">
							<portlet:param name="<%=ManageAllocationsPortlet.ACTION_METHOD %>" value="<%=ManageAllocationsPortlet.PI_DELETE %>"/>
							<portlet:param name="<%=ManageAllocationsPortlet.STATUS %>" value="<%=ManageAllocationsPortlet.LIST %>" />
							<portlet:param name="<%=ManageAllocationsPortlet.PROJECT_IDX %>" value="<%=request.getAttribute(ManageAllocationsPortlet.PROJECT_IDX).toString() %>"/>
							<portlet:param name="<%=ManageAllocationsPortlet.ALLOCATION_IDX %>" value="<%=request.getAttribute(ManageAllocationsPortlet.ALLOCATION_IDX).toString() %>"/>
						</portlet:actionURL>
						<a href="<%= actionUrlSubmitRemove %>">Remove User(s) from <%=resourceName %></a>
					</li>
				</ul>
				</c:when>
				<c:otherwise>
					<em>This resource is no longer in service.  User management is disabled.</em>
				</c:otherwise>
				</c:choose>
			</aui:column>
		</aui:layout>
		<div class="tablediv">
			<table>
				<thead>
					<tr class="allocation-heading">
						<th class="left">Name</th>
						<th class="left">Local<br/>Username</th>
						<th class="right">Allocation<br/>Usage (SU)</th>
						<th class="left">Contact</th>
						<th>&nbsp;</th>
					</tr>
				</thead>
				<tbody>
					<% User[] users = a.getUsers(); %>
					<% String clazz = a.getState().equals("inactive") ? "inactive-allocation-listing" : "allocation-listing"; %>
					<% for (int i = 0; i < users.length; i++) { %>
						<tr class="<%=clazz %> <%=i % 2 == 0 ? "even" : "odd" %>">
							<td class="left">
								<%=users[i].getLastName() %>, <%=users[i].getFirstName() %>
								<% List<String> roles = users[i].getRoles(); %>
								<% boolean canPromote = false; %>
								<% if (roles.contains("pi") || roles.contains("co_pi")) { %>
									<img src="<%=themeDisplay.getPathThemeImage() %>/ratings/star_hover.png" alt="**" title="PI/Co-PI" />
								<% } else if (roles.contains("allocation_manager")) { %>
									<img src="<%=themeDisplay.getPathThemeImage() %>/ratings/star_on.png" alt="*" title="Allocation manager"/>	
								<% } else { canPromote = true; } %>
							</td>
							<td class="left"><%=users[i].getLocalUsername(a.getResource()) == null ? "" : users[i].getLocalUsername(a.getResource()) %></td>
							<td class="right"><%=users[i].getUsedAllocation() %></td>
							<td class="left"><a href="mailto:<%=users[i].getEmail() %>"><%=users[i].getEmail() %></a></td>
							<td class="left">
								<% if (canPromote && (p.getIsPI() || isAdmin)) { %>
									<portlet:actionURL var="marUrl">
										<portlet:param name="<%=ManageAllocationsPortlet.ACTION_METHOD %>" value="<%=ManageAllocationsPortlet.MAKE_ALLOCATION_MGR %>" />
										<portlet:param name="<%=ManageAllocationsPortlet.ACCOUNT_ID %>" value="<%=Integer.toString(p.getAccountID()) %>" />
										<portlet:param name="<%=ManageAllocationsPortlet.PROJECT_IDX %>" value="<%=projIdx %>" />
										<portlet:param name="<%=ManageAllocationsPortlet.PERSON_ID %>" value="<%=Integer.toString(users[i].getPersonID()) %>" />
									</portlet:actionURL>
									<a href="<%=marUrl%>">
										<img src="<%=themeDisplay.getPathThemeImage() %>/common/assign_user_roles.png" alt="*" title="Make Allocation manager"/>
									</a>
								<% } %>
							</td>
						</tr>
					<% } %>
				</tbody>
			</table>
		</div>
		<aui:layout>
			<aui:column columnWidth="50">
				<ul class="liststylenone">
					<li><img src="<%=themeDisplay.getPathThemeImage() %>/ratings/star_hover.png" alt="**" /> PI/Co-PI</li>
					<li><img src="<%=themeDisplay.getPathThemeImage() %>/ratings/star_on.png" alt="*"/> Allocation manager</li>
				</ul>
			</aui:column>
			<aui:column columnWidth="50">
				<portlet:resourceURL var="downloadAllocDataUrl">
					<portlet:param name="<%=ManageAllocationsPortlet.RESOURCE_REQUEST %>" value="downloadAllocData"/>
					<portlet:param name="<%=ManageAllocationsPortlet.PROJECT_IDX %>" value="<%=projIdx %>"/>
					<portlet:param name="<%=ManageAllocationsPortlet.ALLOCATION_IDX %>" value="<%=allocIdx %>"/>
				</portlet:resourceURL>
				<a href="<%=downloadAllocDataUrl %>">
					Download this as CSV
					<img src="<%=themeDisplay.getPathThemeImage() %>/common/download.png" alt="" title="Download"/>
				</a>
			</aui:column>
		</aui:layout>
	</div>
	<div id="removed<%=projIdx %><%=allocIdx %>">
		<div>
			Users Removed from <%=resourceName %>
		</div>
		<portlet:actionURL var="reAddUserUrl" >
			<portlet:param name="<%=ManageAllocationsPortlet.ACTION_METHOD %>" value="<%=ManageAllocationsPortlet.PI_ADD_CONFIRM %>"/>
			<portlet:param name="<%=ManageAllocationsPortlet.STATUS %>" value="<%=ManageAllocationsPortlet.ADD %>"/>
		</portlet:actionURL>
		<div class="tablediv">
			<table>
				<thead>
					<tr class="allocation-heading">
						<th class="left">Name</th>
						<th class="left">Local<br/>Username</th>
						<th class="left">Contact</th>
						<th class="center"></th>
					</tr>
				</thead>
				<tbody>
					<% users = a.getDeadUsers(); %>
					<% clazz = a.getState().equals("inactive") ? "inactive-allocation-listing" : "allocation-listing"; %>
					<% for (int i = 0; i < users.length; i++) { %>
						<tr class="<%=clazz %> <%=i % 2 == 0 ? "even" : "odd" %>">
							<td class="left"><%=users[i].getLastName() %>, <%=users[i].getFirstName() %></td>
							<td class="left"><%=users[i].getLocalUsername(a.getResource()) == null ? "" : users[i].getLocalUsername(a.getResource()) %></td>
							<td class="left"><a href="mailto:<%=users[i].getEmail() %>"><%=users[i].getEmail() %></a></td>
							<td class="center">
								<form action="<%=reAddUserUrl%>" method="POST">
									<input type="hidden" name="<%=ManageAllocationsPortlet.RESOURCE %>" value="<%=resourceName %>">
									<input type="hidden" name="<%=ManageAllocationsPortlet.ORIGINAL_RESOURCE %>" value="<%=origResourceName %>"/>
									<input type="hidden" name="<%=ManageAllocationsPortlet.CHARGE_NUMBER %>" value="<%=p.getChargeNumber() %>"/>
									<input type="hidden" name="<%=ManageAllocationsPortlet.GRANT_NUMBER %>" value="<%=p.getChargeNumber()%>"/>
									<input type="hidden" name="<%=ManageAllocationsPortlet.USER %>" value="<%=users[i].getPopsPersonID()%>=<%=users[i].getPersonID()%>=<%=users[i].getLastName()%>=<%=users[i].getFirstName()%>"/>
									<input type="submit" value="Re-Add"/>
								</form>
							</td>
						</tr>
					<% } %>
				</tbody>
			</table>
		</div>
	</div>
</div>