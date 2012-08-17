<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>

<style>
.usage-chart {
	text-align: center;
}
</style>

<h2>Usage for ${project.chargeNumber}: ${resourceName}</h2>

<a href="<portlet:renderURL/>">&larr; Back to allocations</a>

<c:if test="${not userOnly}">
	<aui:form>
		<aui:select name="user" inlineLabel="left">
			<aui:option value="" label="Summary" />
			<c:forEach var="u" items="${allocation.users}">
				<aui:option value="${u.username}" label="${u.firstName} ${u.lastName} (Usage: ${u.usedAllocation})" />
			</c:forEach>
		</aui:select>
	</aui:form>
</c:if>

<div id="<portlet:namespace/>usage" class="usage-chart">Loading...</div>

<fmt:formatDate var="startDate" value="${allocation.startDate}" pattern="yyyy-MM"/>
<fmt:formatDate var="endDate" value="${allocation.endDate}" pattern="yyyy-MM"/>
<liferay-portlet:resourceURL var="getUsageUrl" copyCurrentRenderParameters="false">
	<portlet:param name="resourceRequest" value="usage" />
</liferay-portlet:resourceURL>

<script>

(function($) {

	$(document).ready(function() {
		var defaultPost = {
			project: "${projectIndex}",
			allocation: "${allocationIndex}",
			startDate: "${startDate}",
			endDate: "${endDate}"
		};
		
		var chart;
		var chartOptions = {
			width: 920,
			height: 480,
			lineWidth: 3,
			pointSize: 6,
			hAxis: {slantedText:true},
			vAxes: [{title:"Number of jobs"},{title:"SUs used"}],
			series: [{color:"#035A8D", targetAxisIndex:0},{color:"#9c4308", targetAxisIndex:1}],
			animation: {
				duration: 500,
				easing: 'inAndOut'
			}
		};
		
		function init() {
			chart = new google.visualization.LineChart(document.getElementById('<portlet:namespace/>usage'));
			$.ajax({
				url: "${getUsageUrl}",
				type: "post",
				dataType: "json",
				data: defaultPost,
				success: function(resp) {
					var data = new google.visualization.DataTable();
					data.addColumn('string','Month');
					data.addColumn('number','Jobs');
					data.addColumn('number','SUs');
					for (var i = 0; i < resp.result.length; i++) {
						data.addRow([resp.result[i].month, resp.result[i].jobs, resp.result[i].sus]);
					}
					chart.draw(data, chartOptions);
				}
			});
		}
		
		google.load("visualization", "1", {packages:["corechart"], callback: init});
		
		<c:if test="${not userOnly}">
			$('#<portlet:namespace/>user').bind('change', function() {
				var user = $(this).val();
				var userPost = {};
				if (user != '') {
					userPost.user = user;
				}
				$.extend(userPost, defaultPost);
				$.ajax({
					url: "${getUsageUrl}",
					type: "post",
					dataType: "json",
					data: userPost,
					success: function(resp) {
						var data = new google.visualization.DataTable();
						data.addColumn('string','Month');
						data.addColumn('number','Jobs');
						data.addColumn('number','SUs');
						for (var i = 0; i < resp.result.length; i++) {
							data.addRow([resp.result[i].month, resp.result[i].jobs, resp.result[i].sus]);
						}
						chart.draw(data, chartOptions);
					}
				});
			});
		</c:if>
	});
})(jQuery);
</script>