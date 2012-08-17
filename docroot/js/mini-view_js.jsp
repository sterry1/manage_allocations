
<script>
	(function($) {
		$(document).ready(
				function() {					
					AUI().ready('aui-tooltip', 'aui-io-plugin', function(A){  
						var t1 = new A.Tooltip({trigger: '.tooltip', align: { points: [ 'bc', 'tc' ] },title: true}).render();
					});
					

					google.load('visualization', '1', {
						packages : [ 'gauge' ],
						callback : loadChart
					});
					

					// Create and populate the charts
					function loadChart() {
						
						$(".burnDiv").each(function(index) {
							var data = new google.visualization.DataTable();
							data.addColumn('string', 'Label');
							data.addColumn('number', 'Value');
							data.addRows(1);
							data.setValue(0, 0, 'Burn Rate');
							data.setValue(0, 1, parseInt($(this).attr('burnRate')));

							var options = {
								width : 85,
								height : 85,
								redFrom : 150,
								redTo : 200,
								min: 0, max: 200,
								/* greenFrom:25, greenTo: 75, */minorTicks : 5
							};

							var chart = new google.visualization.Gauge(document.getElementById($(this).attr('id')));
							chart.draw(data, options);
						});
						

					}
				});
	})(jQuery);
</script>
