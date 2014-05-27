$(document).ready(function() {
	
	google.maps.event.addDomListener(window, 'load', initMapEmpty);
	$("#results").hide();
	$('#loader').hide();
	$("#map-canvas").hide();
	
	$("#results").on("click", '.show', function(event){
		event.preventDefault();
		var $this = $(this); //.parent();
		console.log($this);
		$this.siblings('.instragramPic').removeAttr("style");
		
	});
	
	var $loading = $('#loader');
	$(document)
		.ajaxStart(function () {
			$loading.show();
		})
		.ajaxStop(function () {
			$loading.hide();
		});

	$("#searchNearby").hide(0);
	$("#searchVenue").submit(function(event){
		var obj = {};
		obj.venue_name = $("#venue_name").val();
		obj.venue_city = $("#venue_city").val();

		var data = JSON.stringify(obj);
		
		$.ajax({
			type: "post",
			dataType: "json",
			url: "SearchVenue",
			data: data,
			success: function(venues) {
				console.log(venues);
				populateSelectVenues(venues);
			}
		});
		event.preventDefault();
	});
	

	
	$("#searchNearby").submit(function(event){
		var obj = {};
		obj.venues_list = $("#venues_list").val();
		obj.nearby_radius = $("#nearby_radius").val();

		var data = JSON.stringify(obj);
		
		$.ajax({
			type: "post",
			dataType: "json",
			url: "SearchNearby",
			data: data,
			success: function(venues) {
				displayVenues(venues);
			}
		});
		event.preventDefault();
	});
	


	$("#trackingForm").submit(function( event ) {

		getTrackingFormErrors();

		if (isFormErrorFree($(this)) == true) {
			hideMapDiscussionsTracker();
			var obj = {};
			obj.keywords = $("#keywords").val();
			obj.regionLat = $("#region_lat").val();
			obj.regionLong = $("#region_long").val();
			obj.radius = $("#radius").val();

			var data = JSON.stringify(obj);
			console.log(data);
			$.ajax({
				type: "post",
				dataType: "json",
				url: "TrackingServlet",
				data: data,
				success: function(tweets) {
					console.log(tweets);
					displayTweets(tweets);
				}	
			});
		}
		event.preventDefault();
	});

	$("#userVenuesForm").submit(function( event ) {
		clearOverlays();
		google.maps.event.addDomListener(window, 'load', initMapEmpty);
		
		$("#results").hide(0);
		$("#results").empty();

		getUserVenuesFormErrors();	

		if (isFormErrorFree($(this)) == true) {
			
			$("#results").hide(0);
			$("#results").empty();
			var obj = {};
			obj.userId = $("#user_id").val();
			obj.days = $("#days_last_visited").val();

			var data = JSON.stringify(obj);
			console.log(data);
			var connectionEstablished = false;
			var isUser = true;
			if(obj.days==0){
				$.ajax({
					type: "post",
					dataType: "json",
					url: "UserVenuesServlet",
					data: data,
					async: false,
					success: function(data){
						if(data==""){
							console.log(data);
							isUser = false; 
						}else{
							console.log(data);
							connectionEstablished = true;
							
						}
						
					}
				});
				if(isUser==true){
					setInterval(function() {	
						if(connectionEstablished == true) {
							$.ajax({
								global: false,
								type: "post",
								dataType: "json",
								url: "UserVenuesServlet",
								data: data,
								success: function(data){
									if(data.length!=0){
										$("#map-canvas").show();
										google.maps.event.trigger(map,'resize');
										initMapEmpty();
										displayVenueStream(data);
									}else
										displayVenueStream(data);
										
									
								}
							});
						}
					}, 5000);
				}else{
					userDoesNotExist();
				}
					
				
				

			}else{
				$.ajax({
					type: "post",
					dataType: "json",
					url: "UserVenuesServlet",
					data: data,
					success: function(data){
						$("#map-canvas").show();
						google.maps.event.trigger(map,'resize');
						initMapEmpty();
						displayVenues(data);
						console.log(data)
					}
				});

			}
		}
			
		event.preventDefault();
	});
	
	$('#searchUser').submit(function(event){
		if(!$("#screen_name").val()){
			markInputAsError($("#screen_name"));
			event.preventDefault();
		}
		
		
	});
	$('#searchVisitedVenue').submit(function(event){
		if(!$("#screen_name").val()){
			markInputAsError($("#visited_venue"));
			event.preventDefault();
		}
	});

	$("#discussionsTrackerForm").submit(function( event ) {
		event.preventDefault();

		getDiscussionTrackerFormErrors();

		if (isFormErrorFree($(this)) == true) {
			var obj = {};
			obj.keywords = $("#no_keywords").val();
			obj.daysSince = $("#days_since").val();
			obj.userIds = $("#user_ids").val();

			var data = JSON.stringify(obj);
			console.log(data);
			$.ajax({
				type: "post",
				dataType: "json",
				url: "UserTrackerServlet",
				data: data,
				success: function(data) {
					console.log(data);
					displayKeywords(data);
				}
			});
		}
	});


	$("#tabs").tabs();

	$(".get_tweets").click(function(event) {
		event.preventDefault();

		var data = JSON.stringify($(this).attr("data-href"));
		console.log(data);

		$.ajax({
			type: "post",
			dataType: "json",
			url: "UsersServlet",
			data: data,
			success: function(tweets) {
				console.log(tweets);
				displayRDFaTweets(tweets);
			}
		});
	});

	$(".showMap").click(function(event) {
		event.preventDefault();

		var data = JSON.stringify($(this).attr("data-href"));
		console.log(data.replace("\"","").replace("\"",""));
		

		var obj = {};
		obj.userId = data;
		

		var data = JSON.stringify(obj);
		console.log(data);
		$.ajax({
			type: "post",
			dataType: "json",
			url: "VenuesForUserServlet",
			data: data,
			success: function(data) {
				initMapResEmpty();
				initMapRes(data);
				console.log(data);
			}
		});
	});
	$("#byVenueForm").submit(function( event ) {
		event.preventDefault();

		getByVenueFormErrors();

		if (isFormErrorFree($(this)) == true) {
			hideMapGetUsersByVenue();

			$("#results").empty();
			
			var obj = {};
			obj.locationName = $("#location_name").val();
			obj.locationLat = $("#location_lat").val();
			obj.locationLong = $("#location_long").val();
			obj.days = $("#venue_days_since").val();

			var data = JSON.stringify(obj);
			console.log(data);
			
			console.log(obj.days);
			if (obj.days == 0) {
				
				$.ajax({
					type: "post",
					dataType: "json",
					url: "ByVenueServlet",
					data: data,
					success: function(data){
						console.log(data);
						connectionEstablished = true;
					}
				});

				setInterval(function() {	
					if(connectionEstablished == true) {
						$.ajax({
							global: false,
							type: "post",
							dataType: "json",
							url: "ByVenueServlet",
							data: data,
							success: function(users){
								console.log(users);
								displayUsersStream(users);
							}
						});
					}
				}, 5000);
			}
			else {
				$.ajax({
					type: "post",
					dataType: "json",
					url: "ByVenueServlet",
					data: data,
					success: function(users) {
						console.log(users);
						displayUsers(users);
					}
				});
			};
		}	
	});
});

