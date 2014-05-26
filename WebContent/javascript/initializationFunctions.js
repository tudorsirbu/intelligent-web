var map;
function initMapEmpty(){
	var myLatlng = new google.maps.LatLng(53.388960,-1.469930);
	  var mapOptions = {
	    zoom: 3,
	    center: myLatlng
	  };
	  
	  map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);
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

function displayUserRDFa(user) {
	var entry = "";
	entry +="<div xmlns:dc=\"http://www.smartweb.com/data/#\" xmlns:foaf=\"http://xmlns.com/foaf/0.1/\" xmls:xs=\"http://www.w3.org/2001/XMLSchema#\">";
	entry += "<div class='user' about=\"#"+ user.username +"\"" + "typeof=\"foaf:Agent\">";
	entry +="<a href=\"UsersServlet?user_id=\"" +user.id+"\" class =\"title\"  property=\"foaf:name\">" + user.name + "</a>";
	entry +="<h3 property=\"dc:id\" datatype=\"xs:integer\">" + user.id + "</h3>";
	entry +="<h3 property=\"dc:locationName\">"+ user.location + "</h3>";
	entry +="<img property=\"foaf:depiction\" src=\""+user.profilePicURL+"\"/>";
	entry +="<h3 property=\"dc:description\">" +user.description+"</h3>";
	$.each(user.visited, function(key,value){
		entry +="<h3 property=\"dc:visited\">" +value.name+"</h3>";
	});
	entry += "</div>";
	entry += "</div>";
	return entry;
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
				div += "<h class='title'>Url:  </h><a href='"+venue.shortUrl+"'>"+ venue.shortUrl + "</a><br/><br/>";
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

function displayTweets(data) {
	$("#results").show(0);
	$("#results").empty();
	$.each(data, function( key, tweet ) {
		var div = "<div class='tweet'>";
		div += "<img src='"+ tweet.profileImageUrl +"' />";
		div += "<a href='UsersServlet?user_id="+ tweet.user_id +"' class='title'>" + tweet.name + "</a>";
		div += "<span class='screen_name'> @" + tweet.screenName + "</span>";
		div += "<p class='text'>" + tweet.text + "</p>";
		div += "<p class='text'>" + tweet.createdAt + "</p>";
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
							if(media.url.indexOf("video") < 0 && media.url.indexOf(".mp4") < 0){
								var tweetId = "#" + tweet.id;  
								var $instagramPic = $(tweetId).parent().parent();
								$(tweetId).attr("src",media.url);
								$instagramPic.show();
								$instagramPic.find(".instagramUser").attr("href", "http://www.instagram.com/"+media.author_name);
								$instagramPic.find(".instagramUser").text(media.author_name);
								$(tweetId).parent().attr("href",url);
							} else {
								var tweetId = "#" + tweet.id;  
								var $instagramPic = $(tweetId).parent().parent();
								$(tweetId).remove();
								$instagramPic.show();
								$instagramPic.find(".instagramUser").attr("href", "http://www.instagram.com/"+media.author_name);
								$instagramPic.find(".instagramUser").text("Instagram profile: " + media.author_name);
								$(tweetId).parent().attr("href",url);
							}
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

function getUserVenuesFormErrors() {
	$('#userVenuesForm :input').change(function() {
	    getDiscussionTrackerFormErrors();
	});
	
	var anyErrors = false;

	$("#userVenuesForm :input").each(function(value) {
        var $this = $(this);

        switch($this.attr('name')) {
			case "user_id":
				validateEmpty($this, true);
				break;
			case "days_last_visited":
				validatePositiveInteger($this, true);
				break;
		}
    });
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

function userDoesNotExist(){
	$("#results").show(0);
	var div = "<div class='venues'>";
	div += "<h3 class='title'>The user ID that you entered does not exist! Please try again with another user ID!</h><br/>";
	div += "</div>";
	$("#results").append(div);
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
				validateEmpty($this, true);
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

function displayKeywords(data){
	$("#results").hide(0);
	// remove any previous results displayed
	$("#results").empty();
	// add a table to display results
	$("#results").prepend("<table id=\"keywordsTable\">");

	// create table header
	console.log(data);
	var header = "<tr class=\"header\">";
	header += "<th>Twitter username</th>";
	$.each(data[1], function(key,value){
		header += "<th>" + value + "</th>";
	}); 
	header += "<th>Total</th>";
	header += "</tr>";

	// add table head
	$("#keywordsTable").prepend(header);
	$.each(data[0],function(key, user){
		if(data[0]){
			var row = "<tr>";
			row += "<td><a href=\"UsersServlet?user_id=" + user.id + "\" >" + user.username + "</a></td>";
			var total = 0;
			$.each(data[1], function(k, word){
				var found = false;
				$.each(user.keywords, function(key,value){
					if(value.keyword === word){
						console.log("Am intrat aici pt " + value.keyword);
						found = true;
						total += value.count;
						row += "<td>" + value.count + "</td>";
					}
				});
				if(!found){
					row += "<td>0</td>";
				}
			});
			console.log(total);
			row += "<td>"+total+"</td>";
			row += "</tr>";
			$("#keywordsTable").append(row);
		};
	});
	$("#results").show(0);

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

function displayUsersStream(users) {
	$("#results").show(0);
	
	$.each(users, function(key, user) {
		$("#results").prepend(displayUser(user));
	});
}

function displayUsers(users) {
	// remove any previous results displayed
	$("#results").empty();
	$("#results").show(0);
	
	$.each(users, function(key, user) {
		$("#results").append(displayUser(user));
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

function hideExistingResults(){
	$("#map-canvas").hide(0);
	$("#map-canvas").empty();
	$("#results").hide(0);
	// remove any previous results displayed
	$("#results").empty();
}

function displayResultsOnMap(data) {
	google.maps.event.addDomListener(window, 'load', initMap(data));
}
