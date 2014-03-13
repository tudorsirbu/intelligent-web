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
	
$("#results").hide(0);
$("#userVenuesForm").submit(function( event ) {
		
		var obj = {};
		obj.userID = $("#user_id").val();
		obj.days = $("#days").val();
		
		var data = JSON.stringify(obj);
		
		$.ajax({
			type: "post",
			dataType: "json",
			url: "UserVenuesServlet",
			data: data,
			success: function(tweets) {
				console.log(tweets);
				displayTweets(tweets);
			}
		});
		event.preventDefault();
	});
	

});

function displayTweets(data) {
	$("#results").show(0);
	$("#results").empty();
	$.each(data, function( key, tweet ) {
		var div = "<div class='tweet'>";
		div += "<img src='"+ tweet.profileImageUrl +"' />";
		div += "<strong class='title'>" + tweet.name + "</strong>";
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