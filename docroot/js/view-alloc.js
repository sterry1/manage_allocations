(function($) {
	$(document).ready(
			function() {
								
				AUI().ready('aui-tooltip', 'aui-io-plugin', function(A){  
					var t1 = new A.Tooltip({trigger: '.tooltipb', align: { points: [ 'bc', 'tc' ] },title: true}).render();
				});
				

				google.load('visualization', '1', {
					packages : [ 'gauge' ],
					callback : loadChart
				});
				
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
				
				$("#toggle-projects").bind('click', function() {
					var tog = $(this);
					if (tog.data("shown")) {
						tog.data("shown", false);
						$("div.inactive-project-area").slideUp();
						tog.text("Show inactive projects");
					} else {
						tog.data("shown", true);
						$("div.inactive-project-area").slideDown();
						tog.text("Hide inactive projects");
					}
					return false;
				}).data("shown", false);
				if ($("div.inactive-project-area").length == 0) {
					$("#toggle-projects").parent().remove();
				}
				$("#toggle-allocs").bind('click', function() {
					var tog = $(this);
					if (tog.data("shown")) {
						tog.data("shown", false);
						$("tr.expired-allocation-listing,tr.inactive-allocation-listing").fadeOut();
						tog.text("Show expired/inactive allocations");
					} else {
						tog.data("shown", true);
						$("tr.expired-allocation-listing,tr.inactive-allocation-listing").fadeIn();
						tog.text("Hide expired/inactive allocations");
					}
					return false;
				}).data("shown", false);
				if ($("tr.expired-allocation-listing,tr.inactive-allocation-listing").length == 0) {
					$("#toggle-allocs").parent().remove();
				}
				$('fieldset.admin:not(.admin-processed)').each(
						function(i,o) {
							$fieldset = $(this);
							$fieldset.children('legend').bind('click', function() {
								if ($fieldset.hasClass('collapsed')) {
									$fieldset.removeClass('collapsed');
									$fieldset.children('.fieldset-wrapper').slideDown();
								} else {
									$fieldset.children('.fieldset-wrapper').slideUp(function() { $fieldset.addClass('collapsed'); });
								}
								return false;
							});
							if ($fieldset.hasClass('collapsed')) {
								$fieldset.children('.fieldset-wrapper').slideUp();
							}
							$fieldset.addClass('admin-processed');
						}
				);

				$('a.pi-show-users').bind('click',
						function() {
					var $this = $(this);
					var href = $this.attr('href');
					var id = $this.attr('id') + '_users';
					var div = $('#' + id);
					if (div.length == 0) {
						$.ajax({
							url: href,
							dataType: 'html',
							beforeSend: function() {
								div = $('<div>').attr('id', id+"_wait").addClass('users-div').appendTo('body').html(
										'<div class="loading"><div class="message">Looking up users...</div></div>'
								);
								div.dialog({
									title: 'Please wait...',
									resizable: false,
									width: '300px',
									height: 'auto',
									modal: true,
									open: function(event, ui) { $(".ui-dialog-titlebar-close").hide(); }
								});
							},
							success: function(resp) {
								div.dialog('close');
								div.remove();
								div = $('<div>').attr('id', id).addClass('users-div').appendTo('body').html(resp);
								div.dialog({
									title: div.find('.title').text(),
									resizable: false,
									width: '550px',
									height: 'auto',
									modal: true,
									open: function(event, ui) {
										$('.ui-dialog-overlay').bind('click', function() {
											div.dialog('close');
											div.remove();
										});
									}
								});
								$('.users-tabs').tabs({ fx: { opacity: 'toggle', duration: 'fast' } });
							},
							error: function() {
								div.dialog('close');
								div.remove();
								alert('There was an error loading users.  Please try again.');
							}
						});
					} else {
						div.dialog({
							title: div.find('.title').text(),
							width: '600px',
							height: 'auto',
							modal: true,
							open: function(event, ui) {
								$('.ui-dialog-overlay').bind('click', function() {
									div.dialog('close');
									div.remove();
								});
							}
						});
					}
					return false;
				});
			});
})(jQuery);