<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<portlet:defineObjects/>

<div class="highlight-section algn-center">
	Assign Allocation Manager Role
</div>

<liferay-ui:error key="error-make-allocation-mgr" message="error-make-allocation-mgr" />

<p>
	You are about the grant Allocation manager privileges to
	<strong><c:out value="${PERSON.firstName} ${PERSON.lastName}" /></strong>
	for the project <strong><c:out value="${PROJECT.chargeNumber}" /></strong>.
	This will give this user extra privileges on this projects, such as the
	ability to add and remove users from the project.
</p>
<p>
	Please confirm that this is correct.
</p>
<form method="post" action="<portlet:actionURL><portlet:param name="ACTION_METHOD" value="DO_MAKE_ALLOCATION_MGR" /></portlet:actionURL>">
<input type="hidden" name="ACCOUNT_ID" value='<c:out value="${PROJECT.accountID}"/>'/>
<input type="hidden" name="PERSON_ID" value='<c:out value="${PERSON.personID}"/>'/>
<input type="hidden" name="PROJECT_IDX" value='<c:out value="${PROJECT_IDX}"/>'/> 
<input type="submit" value="Make allocation manager" />
<input type="button" value="Cancel" onclick="window.location='<portlet:renderURL/>'" />
</form>

