$(document).ready(function() {
	
	$("#results").hide(0);
	
	$("#trackingForm").submit(function( event ) {
		
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
			success: function(data) {
				console.log(data);
				displayTweets(data);
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
		div += "<img src='"+ tweet.user.profileImageUrl +"' />";
		div += "<strong class='title'>" + tweet.user.name + "</strong>";
		div += "<span class='screen_name'> @" + tweet.user.screenName + "</span>";
		div += "<p class='text'>" + tweet.text + "</p>";
		div += "</div>";	
		$("#results").append(div);
	});
}