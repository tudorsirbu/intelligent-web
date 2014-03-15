$(document).ready(function() {
	
	$("#results").hide(0);
	
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
		var obj = {};
		obj.userId = $("#user_id").val();
		obj.days = $("#days_since").val();
		
		var data = JSON.stringify(obj);
		console.log(data);
		var connectionEstablished = false;
	
		$.ajax({
			type: "post",
			dataType: "json",
			url: "UserVenuesServlet",
			data: data,
			success: function(data){
				
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
		
		event.preventDefault();
	});
	
	$("#discussionsTrackerForm").submit(function( event ) {
		event.preventDefault();
		
		var obj = {};
		obj.keywords = $("#keywords").val();
		obj.daysSince = $("#days_since").val();
		obj.userIds = $("#user_ids").val();
		
		var data = JSON.stringify(obj);
		
		$.ajax({
			type: "post",
			dataType: "json",
			url: "UserTrackerServlet",
			data: data,
			success: function(data) {
				console.log(data);
			}
		});
		
	});
	

	$("#tabs").tabs();

	$(".get_tweets").click(function(event) {
		event.preventDefault();
		
		var data = JSON.stringify($(this).attr("href"));
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

function displayVenueStream(data) {
	$("#results").show(0);
	$("#results").empty();
	$.each(data, function( key, venue ) {
		var div = "<div class='tweet'>";
		div += "<span class='screen_name'>" + venue.name + "</span>";
		div += "<p class='text'>" + venue.categories + "</p>";
		div += "<p class='text'>" + venue.url + "</p>";
		div += "<p class='text'>" + venue.url + "</p>";
		div += "</div>";	
		$("#results").append(div);
	});
}

//function display_retweets(retweets, afterDiv) {
//	$.each(retweets, function(_, retweet) {
//		var div = "<div class='retweet'>";
//		div += "<img src='"+ retweet.profileImageUrl +"' />";
//		div += "<strong class='title'>" + retweet.name + "</strong>";
//		div += "<span class='screen_name'> @" + retweet.screenName + "</span>";
//		div += "<p class='text'>" + retweet.text + "</p>";
//		div += "</div>";	
//		console.log(div);
//		console.log(afterDiv);
//		afterDiv.after(div);
//	});
//}
