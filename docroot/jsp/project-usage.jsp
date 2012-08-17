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

<h2>Usage for ${project.chargeNumber}</h2>

<a href="<portlet:renderURL/>">&larr; Back to allocations</a>
<aui:form>
	<aui:select name="allocationDates" inlineLabel="left">
		<c:forEach var="d" items="${dates}">
			<fmt:formatDate var="d0" value="${d[0]}" pattern="yyyy-MM"/>
			<fmt:formatDate var="d1" value="${d[1]}" pattern="yyyy-MM"/>
			<aui:option value="${d0}#${d1}" label="${d0} - ${d1}" />
		</c:forEach>
	</aui:select>
</aui:form>

<div id="<portlet:namespace/>usage" class="usage-chart">Loading...</div>

<liferay-portlet:resourceURL var="getUsageUrl" copyCurrentRenderParameters="false">
	<portlet:param name="resourceRequest" value="project_usage" />
</liferay-portlet:resourceURL>

<liferay-portlet:resourceURL var="getResourceUsageUrl" copyCurrentRenderParameters="false">
	<portlet:param name="resourceRequest" value="usage" />
</liferay-portlet:resourceURL>

<script>

(function($) {

	$(document).ready(function() {
		var defaultPost = {
			project: "${projectIndex}",
			startDate: "<fmt:formatDate value="${dates[0][0]}" pattern="yyyy-MM"/>",
			endDate: "<fmt:formatDate value="${dates[0][1]}" pattern="yyyy-MM"/>"
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
		
		$('#<portlet:namespace/>allocationDates').bind('change', function() {
			var dates = $(this).val().split('#');
			var datePost = {},
					post = {};
			datePost.startDate = dates[0];
			datePost.endDate = dates[1];
			$.extend(post, defaultPost, datePost);
			$.ajax({
				url: "${getUsageUrl}",
				type: "post",
				dataType: "json",
				data: post,
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

	});
})(jQuery);
</script>