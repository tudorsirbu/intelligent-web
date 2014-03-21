$(document).ready(function() {
	$("#results").hide(0);
	$(".bubblingG").hide();
	$("#trackingForm").submit(function( event ) {

		var obj = {};
		obj.keywords = $("#keywords").val();
		obj.regionLat = $("#region_lat").val();
		obj.regionLong = $("#region_long").val();
		obj.radius = $("#radius").val();

		var data = JSON.stringify(obj);

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
		event.preventDefault();
	});


	$("#userVenuesForm").submit(function( event ) {
		$("#results").hide(0);
		$("#results").empty();
		$(".bubblingG").show();
		var obj = {};
		obj.userId = $("#user_id").val();
		obj.days = $("#days_last_visited").val();

		var data = JSON.stringify(obj);
		console.log(data);
		var connectionEstablished = false;
		if(obj.days==0){
			$.ajax({
				type: "post",
				dataType: "json",
				url: "UserVenuesServlet",
				data: data,
				success: function(data){

					console.log(data);
					connectionEstablished = true;
				}
			});

			setInterval(function() {	
				if(connectionEstablished == true) {
					$.ajax({
						type: "post",
						dataType: "json",
						url: "UserVenuesServlet",
						data: data,
						success: function(data){
							displayVenueStream(data);
							console.log(data);
						}
					});
				}
			}, 5000);

		}else{
			$.ajax({
				type: "post",
				dataType: "json",
				url: "UserVenuesServlet",
				data: data,
				success: function(data){
					console.log(data);
					displayVenues(data);
				}
			});

		}




		event.preventDefault();
	});

	$("#discussionsTrackerForm").submit(function( event ) {
		event.preventDefault();

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
		
		
	});


	//$("#results").hide(0);
	//$("#userVenuesForm").submit(function( event ) {

	//	var obj = {};
	//	obj.userID = $("#user_id").val();
	//	obj.days = $("#days").val();

	//	var data = JSON.stringify(obj);

	//	$.ajax({
	//		type: "post",
	//	dataType: "json",
	//	url: "UserVenuesServlet",
	//	data: data,
	//	success: function(tweets) {
	//	console.log(tweets);
	//	displayTweets(tweets);
	//	}
	//});
	//event.preventDefault();
	//	});



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
		div += "</div>";	
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
					div += "<strong class='title'>" + retweet.name + "</strong>";
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

function displayVenues(data){
	
	$(".bubblingG").hide();
	$("#results").show(0);
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

function displayVenueStream(data) {
	console.log(data);
	$.each(data, function( key, venue ) {
		if(data){
			$(".bubblingG").hide();
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
			if(photoGroups.length>0)
				if(photoGroups[1].length!=0)
					$.each(photoGroups[1].items,function(key,value){
						div += "<img src='"+ value.url +"' />";
					});
				else{
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
		header += "<th>" + key + "</th>";
	}); 
	header += "<th>Total</th>";
	header += "</tr>";

	// add table head
	$("#keywordsTable").prepend(header);
	$.each(data,function(key, value){
		if(data){
			console.log(value.username);
			var row = "<tr>";
			row += "<td><a href=\"UsersServlet?user_id=" + value.id + "\" >" + value.username + "</a></td>";
			var total = 0;
			$.each(value.keywords, function(key,value){
				total += value;
				row += "<td>" + value + "</td>";
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

//function display_retweets(retweets, afterDiv) {
//$.each(retweets, function(_, retweet) {
//var div = "<div class='retweet'>";
//div += "<img src='"+ retweet.profileImageUrl +"' />";
//div += "<strong class='title'>" + retweet.name + "</strong>";
//div += "<span class='screen_name'> @" + retweet.screenName + "</span>";
//div += "<p class='text'>" + retweet.text + "</p>";
//div += "</div>";	
//console.log(div);
//console.log(afterDiv);
//afterDiv.after(div);
//});
//}
//SA IAU TOATE POZELE?!
