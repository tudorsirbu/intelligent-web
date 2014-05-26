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
	
	$("#searchVisitedVenue").submit(function(event){
		var obj = {};
		obj.venue_name = $("#visited_venue").val();

		var data = JSON.stringify(obj);

		$.ajax({
			type: "post",
			dataType: "json",
			url: "UserVenueServlet",
			data: data,
			success: function(venues) {
				var div = "";
				
				if(venues != null){
					if(venues.visitedBy.length !=0){
						$.each(venues.visitedBy, function(key, v){
							console.log(v);
							div += displayUserRDFa(v);
						});
					} else {
						div += "<div class='user'>The venue was not visited by any users.</div>";
					}
				} else {
					div += "<div class='user'>The venue could not be found.</div>";
				}
				
				$("#results").empty();
				$("#results").append(div);
				$("#results").show(0);
				
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
				console.log(venues);
				
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
					}
				});

			}
		}
			
		event.preventDefault();
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
				displayTweets(tweets);
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

function displayTweets(data) {
	$("#results").show(0);
	$("#results").empty();
	$.each(data, function( key, tweet ) {
		var div = "<div class='tweet'>";
		div += "<img src='"+ tweet.profileImageUrl +"' />";
		div += "<a href='UsersServlet?user_id="+ tweet.user_id +"' class='title'>" + tweet.name + "</a>";
		div += "<span class='screen_name'> @" + tweet.screenName + "</span>";
		div += "<p class='text'>" + tweet.text + "</p>";
		div += "<a href='" + tweet.id + "' class='get_retweets'>" + tweet.retweetCount + " retweets</a>";
		div += "<div class='instagramPic' style='display:none;'><a class='instagramUser' target='_blank' style='display:block;'></a>";
		div	+= "<a href='#' class='imgURL' target='_blank'><img id='"+tweet.id +"' /></a></div>";
		div += "<div style='clear:both;'></div>";
		div += "</div>";
		
		
		$.each(tweet.extendedUrls, function( index, url ) {
			  if(url.indexOf('instagram.com/p') != -1) {
				  $.ajax({
						url: "http://api.instagram.com/oembed?url=" + url,
						dataType: 'jsonp',
						success: function(media) {
							console.log(media);
							var tweetId = "#" + tweet.id;  
							var $instagramPic = $(tweetId).parent().parent();
							$(tweetId).attr("src",media.url);
							$instagramPic.show();
							$instagramPic.find(".instagramUser").attr("href", "http://www.instagram.com/"+media.author_name);
							$instagramPic.find(".instagramUser").text(media.author_name);
							$(tweetId).parent().attr("href",url);
						}
					});
			  }
		});
		
		$("#results").append(div);
	});
	
	$(".get_retweets").click(function(event) {
		event.preventDefault();

		var data = JSON.stringify($(this).attr("href"));
		console.log(data);

		var $this = $(this).parent();

		$this.css('background-color', '#ffffff');

		$.ajax({
			type: "post",
			dataType: "json",
			url: "RetweetsServlet",
			data: data,
			success: function(data) {
				console.log(data);
				$.each(data, function(_, retweet) {
					var div = "<div class='retweet'>";
					div += "<img src='"+ retweet.profileImageUrl +"' />";
					div += "<a href='UsersServlet?user_id="+ retweet.user_id +"' class='title'>" + retweet.name + "</a>";
					div += "<span class='screen_name'> @" + retweet.screenName + "</span>";
					div += "<p class='text'>" + retweet.text + "</p>";
					div += "</div>";	
					console.log(div);
					$this.append(div);
				});
			}
		});

	});
}
var markersArray = [];
function clearOverlays() {
	  for (var i = 0; i < markersArray.length; i++ ) {
	    markersArray[i].setMap(null);
	  }
	  markersArray.length = 0;
	}

var map;
function initMapEmpty(){
	var myLatlng = new google.maps.LatLng(53.388960,-1.469930);
	  var mapOptions = {
	    zoom: 3,
	    center: myLatlng
	  };
	  
	  map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);
}
var region_lat;
var region_long;
var location_lat;
var location_long;

var mapDiscussionsTracker;
function displayMapDiscussionsTracker() {
    document.getElementById('mapDiscussionsTracker').style.display="block";
    document.getElementById('hideMapDiscussionsTracker').style.display="block";
    initializeMapDiscussionsTracker();
    
}

function hideMapDiscussionsTracker() {
    document.getElementById('mapDiscussionsTracker').style.display="none";
    document.getElementById('hideMapDiscussionsTracker').style.display="none";
}
function initializeMapDiscussionsTracker() {
    // create the map
	var myLatlng = new google.maps.LatLng(53.388960,-1.469930);
	  var mapOptions = {
	    zoom: 3,
	    center: myLatlng,
	    mapTypeId: google.maps.MapTypeId.ROADMAP
	  };
	  
	  mapDiscussionsTracker = new google.maps.Map(document.getElementById('mapDiscussionsTracker'), mapOptions);
	  markerMapDiscussionsTracker = new google.maps.Marker({
		    map:mapDiscussionsTracker,
		    draggable:true,
		    animation: google.maps.Animation.DROP,
		    position: myLatlng
		  });
		  google.maps.event.addListener(markerMapDiscussionsTracker, 'click', toggleBounceMapDiscussionsTracker);
		       
	      google.maps.event.addListener(markerMapDiscussionsTracker, "dragend", function(event) {
	            var lat = event.latLng.lat();
	            var lng = event.latLng.lng();
	        document.getElementById('region_lat').value=lat;
	        document.getElementById('region_long').value=lng;
	        });   
   }
function toggleBounceMapDiscussionsTracker() {

	  if (markerMapDiscussionsTracker.getAnimation() != null) {
	    markerMapDiscussionsTracker.setAnimation(null);
	  } else {
	    markerMapDiscussionsTracker.setAnimation(google.maps.Animation.BOUNCE);
	  }
	}
var mapGetUsersByVenue;
function displayMapGetUsersByVenue() {
    document.getElementById('mapGetUsersByVenue').style.display="block";
    document.getElementById('hideMapGetUsersByVenue').style.display="block";
    initializeMapGetUsersByVenue();
    
}

function hideMapGetUsersByVenue() {
    document.getElementById('mapGetUsersByVenue').style.display="none";
    document.getElementById('hideMapGetUsersByVenue').style.display="none";
}
function initializeMapGetUsersByVenue(){
	// create the map
	var myLatlng = new google.maps.LatLng(53.388960,-1.469930);
	  var mapOptions = {
	    zoom: 3,
	    center: myLatlng,
	    mapTypeId: google.maps.MapTypeId.ROADMAP
	  };
	  
	  mapGetUsersByVenue = new google.maps.Map(document.getElementById('mapGetUsersByVenue'), mapOptions);
	  markerMapGetUsersByVenue = new google.maps.Marker({
		    map:mapGetUsersByVenue,
		    draggable:true,
		    animation: google.maps.Animation.DROP,
		    position: myLatlng
		  });
		  google.maps.event.addListener(markerMapGetUsersByVenue, 'click', toggleBounceMapGetUsersByVenue);
		       
	      google.maps.event.addListener(markerMapGetUsersByVenue, "dragend", function(event) {
	            var lat = event.latLng.lat();
	            var lng = event.latLng.lng();
	        document.getElementById('location_lat').value=lat;
	        document.getElementById('location_long').value=lng;
	        });
}
function toggleBounceMapGetUsersByVenue() {

	  if (markerMapGetUsersByVenue.getAnimation() != null) {
	    markerMapGetUsersByVenue.setAnimation(null);
	    markerMapGetUsersByVenue.setAnimation(google.maps.Animation.BOUNCE);
	  }
	}

var activeWindow = new google.maps.InfoWindow();
function initMap(data){

	  $.each(data, function(key,venue){
		  var contentString = "No address";
		  var myLatlng = new google.maps.LatLng(venue.location.lat,venue.location.lng);
		  if(venue.location.address!=null)
			  contentString = venue.location.address; 
		  var infowindow = new google.maps.InfoWindow({
		      content: contentString
		  });
	
		  var marker = new google.maps.Marker({
		      position: myLatlng,
		      map: map,
		      title: venue.name
		  });
		  markersArray.push(marker);
		  google.maps.event.addListener(marker, 'click', function() {
			  if(activeWindow!=null)	  
				  activeWindow.close();  
		    infowindow.open(map,marker);
		    activeWindow= infowindow;
		  });
		
	});

}

function displayVenues(data){
	
	$("#results").show(0);
	if(data[0]!=""){
		google.maps.event.addDomListener(window, 'load', initMap(data));
		if(data.length!=0){
			$.each(data, function( key, venue ) {
				photoGroups = venue.photos.groups;
				var div = "<div class='venues'>";
				div += "<h class='title'>" +"Name:  </h><span class='venues_name'>"+ venue.name + "</span><br/>";
				div += "<h class='title'>Category:  </h><span class='venues_name'>"+ venue.categories[0].name + "</span><br/>";
				if(venue.location.address)
				div += "<h class='title'>Address:  </h><span class='venues_name'>"+ venue.location.address + "</span><br/>";
				if(venue.shortUrl)
				div += "<h class='title'>Url:  </h><a href='url'>"+ venue.shortUrl + "</a><br/><br/>";
				if(photoGroups.length>1)
					if(photoGroups[1].length!=0)
						$.each(photoGroups[1].items,function(key,value){
							div += "<img src='"+ value.url +"' />";
						});
					else{
						if(photoGroups[0].length!=0 )
							$.each(photoGroups[0].items,function(key,value){
								div += "<img src='"+ value.url +"' />";
							});
					}
				div += "</div>";	
				$("#results").append(div);

			});
		}
		else{
			var div = "<div class='venues'>";
			div += "<h3 class='title'>" +"No checkins found for the last specified number of days!</h><br/>";
			div += "</div>";
			$("#results").append(div);
		}
	}
	else{
		var div = "<div class='venues'>";
		div += "<h3 class='title'>Name:  </h3><span class='venues_name'>"+ venue.name + "</span><br/>";
		div += "<h3 class='title'>Category:  </h3><span class='venues_name'>"+ venue.categories[0].name + "</span><br/>";
		if(venue.location.address)
			div += "<h3 class='title'>Address:  </h3><span class='venues_name'>"+ venue.location.address + "</span><br/>";
		if(venue.shortUrl)
			div += "<h3 class='title'>URL:  </h3><a href='url'>"+ venue.shortUrl + "</a><br/><br/>";
		if(photoGroups.length>1)
			if(photoGroups[1].length!=0)
				$.each(photoGroups[1].items,function(key,value){
					div += "<img src='"+ value.url +"' />";
				});
			else{
				if(photoGroups[0].length!=0 )
					$.each(photoGroups[0].items,function(key,value){
						div += "<img src='"+ value.url +"' />";
					});
			}
		div += "</div>";	
		div += "<h3 class='title'>" +"The user ID that you entered does not exist! Please try again with another user ID!</h3><br/>";
		div += "</div>";
		$("#results").append(div);
	}
}

function userDoesNotExist(){
		$("#results").show(0);
		var div = "<div class='venues'>";
		div += "<h3 class='title'>The user ID that you entered does not exist! Please try again with another user ID!</h><br/>";
		div += "</div>";
		$("#results").append(div);
}

function displayVenueStream(data) {
		google.maps.event.addDomListener(window, 'load', initMap(data));
		
		$.each(data, function( key, venue ) {
			if(data){
				$("#map-canvas").show(0);
				$("#results").show(0);
				var photoGroups = venue.photos.groups;
				var div = "<div class='venues'>";
				div += "<h class='title'>" +"Name:  </h><span class='venues_name'>"+ venue.name + "</span><br/>";
				div += "<h class='title'>Category:  </h><span class='venues_name'>"+ venue.categories[0].name + "</span><br/>";
				if(venue.location.address)
			
					div += "<h class='title'>Address:  </h><span class='venues_name'>"+ venue.location.address + "</span><br/>";
				if(venue.shortUrl)
				div += "<h class='title'>Url:  </h><a target="+"'_blank'"+" href="+venue.shortUrl+">"+ venue.shortUrl + "</a><br/><br/>";
				console.log("hhghh"+photoGroups);
				if(photoGroups.length>1)
					if(photoGroups[1].length!=0)
						$.each(photoGroups[1].items,function(key,value){
							div += "<img src='"+ value.url +"' />";
						});
					else{
						if(photoGroups.length>0)
						if(photoGroups[0].length!=0)
							$.each(photoGroups[0].items,function(key,value){
								div += "<img src='"+ value.url +"' />";
							});
					}
				div += "</div>";	
				$("#results").prepend(div);

			}
		});
	}


function displayKeywords(data){
	$("#results").hide(0);
	// remove any previous results displayed
	$("#results").empty();
	// add a table to display results
	$("#results").prepend("<table id=\"keywordsTable\">");

	// create table header
	var keywords = data[0].keywords;
	var header = "<tr class=\"header\">";
	header += "<th>Twitter username</th>";
	$.each(keywords, function(key,value){
		header += "<th>" + value.keyword + "</th>";
	}); 
	header += "<th>Total</th>";
	header += "</tr>";

	// add table head
	$("#keywordsTable").prepend(header);
	$.each(data,function(key, value){
		if(data){
			var row = "<tr>";
			row += "<td><a href=\"UsersServlet?user_id=" + value.id + "\" >" + value.username + "</a></td>";
			var total = 0;
			$.each(value.keywords, function(key,value){
				total += value.count;
				row += "<td>" + value.count + "</td>";
			});
			row += "<td>" + total + "</td>";
			row += "</tr>";
			$("#keywordsTable").append(row);
		};
	});
	$("#results").show(0);

}

function displayUsers(users) {
	// remove any previous results displayed
	$("#results").empty();
	$("#results").show(0);
	
	$.each(users, function(key, user) {
		$("#results").append(displayUser(user));
	});
}

function displayUsersStream(users) {
	$("#results").show(0);
	
	$.each(users, function(key, user) {
		$("#results").prepend(displayUser(user));
	});
}

function displayUser(user) {
	var entry = "";
	entry += "<div class='user'>";
	entry += "<img src='"+ user.profilePicURL +"' />";
	entry += "<a href='UsersServlet?user_id="+ user.id +"' class='title'>" + user.name + "</a>";
	entry += "<span class='screen_name'> @" + user.username + "</span>";
	entry += "<h3>" + user.description + "</h3>";
	entry += "<h3>" + user.location + "</h3>";
	entry += "</div>";
	return entry;
}

function displayUserRDFa(user) {
	var entry = "";
	entry += "<div class='user'>";
		entry +="<div xmlns:dc=\"http://www.smartweb.com/data/#\" xmlns:foaf=\"http://xmlns.com/foaf/0.1/\" xmls:xs=\"http://www.w3.org/2001/XMLSchema#\">";
			entry += "<p about=\"#"+ user.username +"\"" + "typeof=\"foaf:Agent\">";
				entry +="<b property=\"foaf:name\">" +"Has name: "+ user.name + "</b>";
				entry +="<span property=\"dc:id\" datatype=\"xs:integer\">" +"Has id:"+ user.id + "</span>";
				entry +="<span property=\"dc:locationName\">"+"Live at (Only shown if available):"+ user.location + "</span>";
				entry +="<span property=\"foaf:depiction\">"+"Has profile picture:" +"<img src=\""+user.profilePicURL+"\"/></span>";
				entry +="<span property=\"dc:description\">" +"Has description:" +user.description+"</span>";
			entry +="</p>";	
		entry += "</div>";
	entry += "</div>";
	return entry;
}

function isFormErrorFree(element) {
	var foundErrors = false;

	element.find(":input").each(function(value) {
        if ($(this).attr('data-error') == 'true') {
        	foundErrors = true;
        }
    });

	if(foundErrors == true) 
		return false;
	else
		return true;
}

function getTrackingFormErrors() {
	$('#trackingForm :input').change(function() {
	    getTrackingFormErrors();
	});

	$("#trackingForm :input").each(function(value) {
        var $this = $(this);

        switch($this.attr('name')) {
			case "keywords":
				validateEmpty($this);
				break;
			case "region_lat":
			case "region_long":
				validateDouble($this);
				break;
			case "radius":
				validatePositiveInteger($this);
				break;
		}
    });
}

function getDiscussionTrackerFormErrors() {
	$('#discussionsTrackerForm :input').change(function() {
	    getDiscussionTrackerFormErrors();
	});
	
	var anyErrors = false;

	$("#discussionsTrackerForm :input").each(function(value) {
        var $this = $(this);

        switch($this.attr('name')) {
			case "user_ids":
				validatePositiveLong($this, true);
				break;
			case "no_keywords":
				validatePositiveInteger($this, true);
				break;
			case "days_since":
				validatePositiveInteger($this, true);
				break;
		}
    });
}

function getByVenueFormErrors() {
	$('#byVenueForm :input').change(function() {
	    getByVenueFormErrors();
	});
	
	var $form = $(this);
	var $locationName = $form.add("input[name=location_name]");
	var $locationLat = $form.add("input[name=location_lat]");
	var $locationLong = $form.add("input[name=location_long]");
	var $venueDaysSince = $form.add("input[name=venue_days_since]");
	
	if (!isEmpty($locationName)) {
		markInputAsCorrect($locationName);
    	markInputAsCorrect($locationLat);
    	markInputAsCorrect($locationLong);
	}
	else if (isEmpty($locationName) && !isEmpty($locationLat) && !isEmpty($locationLong)) {
    	markInputAsCorrect($locationName);
    	markInputAsCorrect($locationLat);
    	markInputAsCorrect($locationLong);
    }
	else {
		validateEmpty($locationName);
		validateEmpty($locationLong);
		validateEmpty($locationLat);
	}
	validateEmpty($venueDaysSince);
}

function getUserVenuesFormErrors() {
	$('#userVenuesForm :input').change(function() {
	    getDiscussionTrackerFormErrors();
	});
	
	var anyErrors = false;

	$("#userVenuesForm :input").each(function(value) {
        var $this = $(this);

        switch($this.attr('name')) {
			case "user_id":
				validatePositiveLong($this, true);
				break;
			case "days_last_visited":
				validatePositiveInteger($this, true);
				break;
		}
    });
}


function isEmpty(element) {
	if(!element.val()) {
		return true;
	} else {
		return false;
	}
}

function validateEmpty(element) {
	if(!element.val()) {
		markInputAsError(element);
	} else {
		markInputAsCorrect(element);
	}
}

function validateDouble(element, mandatory) {
	if (mandatory == true) {
		var n = element.val();
		if(n === +n && n !== (n|0)) {
			markInputAsCorrect(element);
		} else {
			markInputAsError(element);
		}
	} else {
		markInputAsCorrect(element);
	}
}

function validateInteger(element, mandatory) {
	if (mandatory == true) {
		if(element.val() % 1 != 0 || !element.val()) {
			markInputAsError(element);
		} else {
			markInputAsCorrect(element);
		}	
	} else {
		markInputAsCorrect(element);
	}
}

function validatePositiveInteger(element, mandatory) {
	if (mandatory == true) {
		if(element.val() % 1 != 0 || element.val() < 0 || !element.val()) {
			markInputAsError(element);
		} else {
			markInputAsCorrect(element);
		}	
	} else {
		markInputAsCorrect(element);
	}
}

function validatePositiveLong(element, mandatory) {
	if (mandatory == true) {
		if(element.val() % 1 != 0 || element.val() < 0 || element.val().length >= 19 || !element.val()) {
			markInputAsError(element);
		} else {
			markInputAsCorrect(element);
		}	
	} else {
		markInputAsCorrect(element);
	}
}

function markInputAsError(element) {
	element.css('border-color', '#c0392b');
	element.attr('data-error', 'true');
}

function markInputAsCorrect(element) {
	element.css('border-color', '#27ae60');
	element.attr('data-error', 'false');
}

function populateSelectVenues(data){
	$("#venues_list").empty();
	$.each(data, function(key,value){
		if(value.location.address != null)
			$("#venues_list").prepend("<option value="+ value.location.lat +"," + value.location.lng
					+">" + value.name + " - " + value.location.address + "</option>");
		else
			$("#venues_list").prepend("<option value="+ value.location.lat +"," + value.location.lng +">" + value.name + "</option>");
	});
	$("#searchNearby").show(0);
}


/* 
 * The function gets a URL to a photo (eg. instagram.com/p/ID)
 * and after getting the media ID it calls the getMedia function
 */
function getMedia(url){
	var result = null;
	$.ajax({
		url: "http://api.instagram.com/oembed?url=" + url,
		dataType: 'jsonp',
		crossDomain: true,
		success: function(media) {
			 result = media;
		}
	});
	console.log(result);
	return result;
}
