$(document).ready(function() {
	
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
	$("#results").empty();
	$.each(data, function( key, tweet ) {
		$("#results").append("<div><img src='"+ tweet.user.profileImageUrl +"' /> @" + tweet.user.name + " - " + tweet.text + "</div>");
	});
}