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
<liferay-ui:error key="admin-username-override-dne" message="admin-username-override-dne" />

<%
	Project[] projects = (Project[])request.getAttribute(ManageAllocationsPortlet.PROJECT_LIST);
	boolean isAdmin = (Boolean) request.getAttribute(ManageAllocationsPortlet.ADMIN);
	boolean foundActive = false;
%>
<h5 style="float: left;">Active Projects Summary</h5> &nbsp;(<a href="/group/xup/allocations/usage">See Full View</a>)<br />

<% for (int i = 0; i < projects.length; i++) {  
       if (projects[i].getActiveNonExpiredAllocations().length > 0) { 
           foundActive = true; 
       %>
	   <div class="active-project-area">	
		<% Allocation[] allocations = projects[i].getAllocations(); %>
		<% if (allocations.length > 0) { //print out allocations table header %>
			<table style="width: 95%;">
				<thead>
					<tr>
						<th colspan="4"><%=projects[i].getTitle()%></th>
					</tr>
					<tr>
						<th style="width:15%;">Resource</th>

						<th style="text-align: center; width: 40%;">% Left</th>

						<th style="text-align: center; width:30%;">End Date [Days Left]</th>

						<th style="width:15%;">Burn Rate</th>
					</tr>
				</thead>
				<tbody>
				<% for (int j = 0; j < allocations.length; j++) { 
					if(allocations[j].isExpired() || allocations[j].getState().equals("inactive")) { 
						continue; 
					}
					 String leftStyle = "";
					 int remain = allocations[j].getPercentRemainInt();
					 if(remain < 10) {
						 leftStyle = "color:red;";
					 }
					 String remainingSUs = allocations[j].getRemainingAllocationFormatted();
					 String pi = projects[i].getPiLastName() +"," + projects[i].getPiFirstName();
					 String chargeNum = projects[i].getChargeNumber();
				%>
					<% WeakHashMap hash = DBUtil.getShortNameMap(); %>
					<% //convert between db resource names to short human-readable name %>
					<% String resName = allocations[j].getResource(); %>
					<% String origResName = resName; %>
					<% if (hash.containsKey(resName)) { %>
						<% resName = (String)hash.get(resName); %>
					<% } %>
					<% String clazz = "";
					   clazz += " allocation-listing"; %>
					<% //gray out the inactive ones %>
					<tr class="<%=clazz %>" >
						<td style="text-align: left;" ><span class="tooltip" title="PI:<%=pi%><br/>ID:<%=chargeNum%>"><%=resName%></span></td>
						<td style="text-align:center; <%=leftStyle %>"><a class="load tooltip" title="Award: <%=allocations[j].getBaseAllocationFormatted() %> SUs <br/>My Usage: <%=allocations[j].getUsedAllocationFormatted() %> SUs"><span class="load-overlay"></span><span class="load-value" style="width:<%=remain %>%;" ></span></a><p><%=remain %>% | <%=remainingSUs %> SUs remaining</p></td>
						<td style="text-align: center;" ><span class="tooltip" title="Start Date: <%=allocations[j].getFormattedStartDate() %> <br/> End Date: <%=allocations[j].getFormattedEndDate() %> <br/> Days Left: <%=allocations[j].getDaysLeft() %>"><%=allocations[j].getFormattedEndDate() %> [<%=allocations[j].getDaysLeft() %>d]</span></td>
						<td class="tooltip" title="Burn rate: <%= (int)allocations[j].getBurnRate() %> SUs / week.<br/> Ideal Burn rate: <%= (int)allocations[j].getIdealBurnRate() %> SUs / week.*<br/>Burn rate is <%= (int)(allocations[j].getBurnRateRatio() * 100) %>% of ideal burn rate.<br/>*Based on even weekly usage."><div id="proj<%= i %>alloc<%= j %>" class="burnDiv" burnRate="<%=allocations[j].getBurnRateRatio() * 100 %>"><%=allocations[j].getBurnRateRatio() * 100 %>%</div></td>     
					</tr>
				<% }//end for allocations.length %>
				</tbody>
			</table>
		<% } //end if (allocations.length > 0)
		%>
		
	</div>
	<div class="divider"></div>
<% }} //end if/for projects
if(!foundActive) {
	%><div style="clear:both;">Could not find any non-expired, active allocations!</div><%
}

if (isAdmin) { %>
<fieldset class="admin <c:if test="${empty ADMIN_USERNAME_OVERRIDE}">collapsed</c:if>">
	<legend><a href="#">Admin</a></legend>
	<div class="fieldset-wrapper">
		<form action="<portlet:actionURL><portlet:param name="ACTION_METHOD" value="ADMIN_USERNAME_OVERRIDE" /></portlet:actionURL>" method="post">
			<label for="<portlet:namespace/>ADMIN_USERNAME_OVERRIDE">Username override</label>
			<input type="text" name="<portlet:namespace/>ADMIN_USERNAME_OVERRIDE" id="<portlet:namespace/>ADMIN_USERNAME_OVERRIDE" value="${ADMIN_USERNAME_OVERRIDE}" />
			<input type="submit" value="Submit" />
		</form>
	</div>
</fieldset>
<% } %>

<style>
	div.loading {
		background: url('<%=themeDisplay.getPathThemeImages()%>/progress_bar/loading_animation.gif') transparent no-repeat center center;
		margin: 1em;
		padding-bottom: 64px;
		text-align: center;
	}
</style>

