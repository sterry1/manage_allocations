<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ page import="java.util.List"%>
<%@ page import="java.util.WeakHashMap"%>
<%@ page import="edu.utexas.tacc.portlets.Allocation"%>
<%@ page import="edu.utexas.tacc.portlets.DBUtil"%>
<%@ page import="edu.utexas.tacc.portlets.Project"%>
<%@ page import="edu.utexas.tacc.portlets.ManageAllocationsPortlet"%>

<portlet:defineObjects/>
<liferay-theme:defineObjects/>

<liferay-ui:success key="success-make-allocation-mgr" message="success-make-allocation-mgr" />

<%
	Project[] projects = (Project[])request.getAttribute(ManageAllocationsPortlet.PROJECT_LIST);
	boolean isAdmin = (Boolean) request.getAttribute(ManageAllocationsPortlet.ADMIN);
%>
<h4 style="float: left;">Projects</h4>
<div style="float: right;">
	<ul class="links">
		<li><a href="#" id="toggle-projects">Show Inactive Projects</a></li>
		<li><a href="#" id="toggle-allocs">Show Expired/Inactive Allocations</a></li>
	</ul>
</div>
<div class="divider"></div>
<% for (int i = 0; i < projects.length; i++) { %>
	<% String projClass = "inactive-project-area"; %>
	<% if (projects[i].getActiveAllocations().length > 0) { %>
		<% projClass = "active-project-area"; %>
	<% } %>
	<div class="<%=projClass%>">
		<div class="project-header">
			<h6 class="project-header-text">
				<%=projects[i].getTitle()%>
				<% if (projects[i].getIsPI() || projects[i].isCoPI() || projects[i].isAllocationMgr() || isAdmin) { %>
					<portlet:renderURL var="projectUsageUrl">
						<portlet:param name="action" value="project_usage"/>
						<portlet:param name="project" value="<%=Integer.toString(i)%>"/>
					</portlet:renderURL>
					<a class="allocation-usage" href="${projectUsageUrl}">
						<img class="tooltipb" title="View usage for <%=projects[i].getChargeNumber() %>"
									alt="View usage for <%=projects[i].getChargeNumber() %>"
									src="<%=this.getServletContext().getContextPath()%>/images/usage_32.png" />
					</a>
				<% } %>
			</h6>
			<ul>
				<li><strong>Project PI:</strong> <span><%=projects[i].getPiLastName()%>, <%=projects[i].getPiFirstName()%></span></li>
				<li><strong>Charge No.:</strong> <span><%=projects[i].getChargeNumber()%></span></li>
			</ul>
		</div>
		
		<% Allocation[] allocations = projects[i].getAllocations(); %>
		<% if (allocations.length > 0) { //print out allocations table header %>
			<table>
				<thead>
					<tr>
						<th style="width:125px;">Resource</th>
						<th style="text-align: center;">Award</th>
						<th style="text-align: center;">My Usage<br/>(SU)</th>
						<th style="text-align: center;">View Usage</th>
						<th style="text-align: center;">Burn Rate</th>
						<th style="text-align: center;">End Date<br/>[Days Left]</th>
						<th>Type</th>
						<th>State</th>
						<% if (projects[i].getIsPI() || projects[i].isCoPI() || projects[i].isAllocationMgr() || isAdmin) { %>
							<th>Users</th>
						<% } %>
					</tr>
				</thead>
				<tbody>
				<% for (int j = 0; j < allocations.length; j++) { %>
					<% WeakHashMap hash = DBUtil.getShortNameMap(); %>
					<% //convert between db resource names to short human-readable name %>
					<% String resName = allocations[j].getResource(); %>
					<% String origResName = resName; %>
					<% if (hash.containsKey(resName)) { %>
						<% resName = (String)hash.get(resName); %>
					<% } %>
					<% 
					 String leftStyle = "";
					 int remain = allocations[j].getPercentRemainInt();
					 if(remain < 10) {
						 leftStyle = "color:red;";
					 }
					 String remainingSUs = allocations[j].getRemainingAllocationFormatted();
					 String pi = projects[i].getPiLastName() +"," + projects[i].getPiFirstName();
					 String chargeNum = projects[i].getChargeNumber();
					String clazz = "";%>
					<% if (allocations[j].isExpired()) { %>
						<% clazz += " expired-allocation-listing"; %>
					<% } else if (allocations[j].getState().equals("inactive")) { %>
						<% clazz += " inactive-allocation-listing"; %>
					<% } else { %>
						<% clazz += " allocation-listing"; %>
					<% } %>
					<% //gray out the inactive ones %>
					<tr class="<%=clazz %>">
						<td style="width:125px;"><%=resName%></td>
						<td style="text-align: center; <%=leftStyle %>"><div class="tooltipb" title="Award: <%=allocations[j].getBaseAllocationFormatted() %> SUs <br />Remaining: <%=remainingSUs %> SUs<br/>My Usage: <%=allocations[j].getUsedAllocationFormatted() %> SUs"><%=remain %>% | <%=remainingSUs %> SUs left<br/><a class="load" ><span class="load-overlay"></span><span class="load-value" style="width:<%=remain %>%;"></span></a>from <%=allocations[j].getBaseAllocationFormatted() %> SU award</div></td>
						<td style="text-align: center;"><%=allocations[j].getUsedAllocationFormatted() %></td>

						<% if (! (
							resName.equals("staff.teragrid") ||
							resName.equals("ecss.xsede") ||
							resName.equals("replication-service.teragrid")
						)) { %>
							<td style="width: 32px; text-align:center;">
								<portlet:renderURL var="usageUrl">
									<portlet:param name="action" value="usage"/>
									<portlet:param name="project" value="<%=Integer.toString(i)%>"/>
									<portlet:param name="allocation" value="<%=Integer.toString(j)%>"/>
								</portlet:renderURL>
								<a class="allocation-usage" href="${usageUrl}">
									<img class="tooltipb" title="View usage detail for <%=resName%>"
												alt="View usage detail for <%=resName%>"
												src="<%=this.getServletContext().getContextPath()%>/images/usage_32.png" />
								</a>
							</td>
						<% } else { %>
							<td style="text-align:center;">N/A</td>
						<% } %>
						<td style="text-align: center;" class="tooltipb" title="Burn rate: <%= allocations[j].getBurnRateFormatted() %> SUs / week.<br/> Ideal burn rate: <%= allocations[j].getIdealBurnRateFormatted() %> SUs / week.*<br/>Burn rate is <%= (int)(allocations[j].getBurnRateRatio() * 100) %>% of ideal burn rate.<br/>*Based on even weekly usage."><div id="proj<%= i %>alloc<%= j %>" class="burnDiv" burnRate="<%=allocations[j].getBurnRateRatio() * 100 %>"><%=allocations[j].getBurnRateRatio() * 100 %>%</div></td>
						<td style="text-align: center;" ><span class="tooltipb" title="Start Date: <%=allocations[j].getFormattedStartDate() %> <br/> End Date: <%=allocations[j].getFormattedEndDate() %> <br/> Days Left: <%=allocations[j].getDaysLeft() %>"> <%=allocations[j].getFormattedEndDate() %> [<%=allocations[j].getDaysLeft() %>d]</span></td>
						<td><%=allocations[j].getType()%></td>
						<td><%=allocations[j].getState()%></td>
						<% if (projects[i].getIsPI() || projects[i].isCoPI() || projects[i].isAllocationMgr() || isAdmin) { %>
							<td style="width: 32px;">
								<portlet:resourceURL var="showUserUrl">
									<portlet:param name="<%=ManageAllocationsPortlet.RESOURCE_REQUEST%>" value="<%=ManageAllocationsPortlet.PROJECT_USERS%>"/>
									<portlet:param name="<%=ManageAllocationsPortlet.PROJECT_IDX%>" value="<%=Integer.toString(i)%>"/>
									<portlet:param name="<%=ManageAllocationsPortlet.ALLOCATION_IDX%>" value="<%=Integer.toString(j)%>"/>
								</portlet:resourceURL>
								<a id="pi_show_users_<%=i%>_<%=j%>" class="pi-show-users" href='<%=showUserUrl%>'>
									<img class="tooltipb" title="Manage users on <%=resName%>"
												alt="Show users on <%=resName%>"
												src="<%=this.getServletContext().getContextPath()%>/images/user_32.png" />
								</a>
							</td>
						<% } %>
					</tr>
				<% }//end for allocations.length %>
				</tbody>
			</table>
		<% } //end if (allocations.length > 0) %>
		<div class="divider"></div>
	</div>
<% } //end for projects %>
<% if (isAdmin) { %>
<fieldset class="admin <c:if test="${empty ADMIN_USERNAME_OVERRIDE}">collapsed</c:if>">
	<legend><a href="#">Admin</a></legend>
	<div class="fieldset-wrapper">
		<liferay-ui:error key="admin-username-override-dne" message="admin-username-override-dne" />
		<portlet:actionURL var="overrideUrl"><portlet:param name="ACTION_METHOD" value="ADMIN_USERNAME_OVERRIDE" /></portlet:actionURL>
		<portlet:actionURL var="resetUrl"><portlet:param name="ACTION_METHOD" value="ADMIN_USERNAME_OVERRIDE_RESET" /></portlet:actionURL>
		<form action="${overrideUrl}" method="post">
			<label for="<portlet:namespace/>ADMIN_USERNAME_OVERRIDE">Username override</label>
			<input type="text" name="<portlet:namespace/>ADMIN_USERNAME_OVERRIDE" id="<portlet:namespace/>ADMIN_USERNAME_OVERRIDE" value="${ADMIN_USERNAME_OVERRIDE}" />
			<input type="submit" value="Submit" />
			<input type="button" value="Reset" onClick="window.location='${resetUrl}'" />
		</form>
	</div>
</fieldset>
<% } %>
<div>
	<p>
		Allocations for Supplements, Advances and Transfers appear in the
		portal before they appear in the accounting records at XSEDE sites.
		Please allow 24 hours after you receive an award notification for
		the allocation updates to appear in the accounting records of the XSEDE
		sites.
	</p>
</div>
<style>
	div.loading {
		background: url('<%=themeDisplay.getPathThemeImages()%>/progress_bar/loading_animation.gif') transparent no-repeat center center;
		margin: 1em;
		padding-bottom: 64px;
		text-align: center;
	}
</style>
