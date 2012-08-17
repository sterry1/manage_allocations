<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>

<div class="no-projects">
	<h4>No active projects were found for your account</h4>
	<div class="divider separator"></div>
	<aui:layout>
		<aui:column columnWidth="50" first="true">
			<section>
				<h5>Apply for a New Project</h5>
				<p>
					To apply for a new project allocation, please go to the POPS
					Allocation Request System.
				</p>
				<p class="algn-center">
					<aui:button onClick="https://portal.xsede.org/submit-request" target="_top" value="Submit allocation request" />
				</p>
			</section>
		</aui:column>
		<aui:column columnWidth="50">
			<section>
				<h5>I am on a project already</h5>
				<p>
					If you are already involved in a project, but do not yet have access,
					you will need to <strong>contact the PI or the Allocation Manager for
					the project</strong> and request that you be added to the project.
					You will need to provide the PI or Allocation Manager with your
					XSEDE User Portal username.
				</p>
			</section>
		</aui:column>
	</aui:layout>
	<div class="divider separator"></div>
	<p>
		If you believe you are seeing this message in error, please <a href="/group/xup/help-desk">contact the help desk</a>.
	</p>
</div>
<c:if test="${ADMIN}">
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
</c:if>
