/* 
 * The function gets a URL to a photo (eg. instagram.com/p/ID)
 * and after getting the media ID it calls the getMedia function
 */
function getMediaId(url){
	$.ajax({
		url: "http://api.instagram.com/oembed?url=" + url,
		dataType: 'jsonp',
		crossDomain: true,
		success: function(media) {
			getMedia(media.media_id);
		}
	});
}

/*
 * The the function is getting the media object using the
 * provided ID and returns certain details about it.
 */
function getMedia(id){
	$.ajax({
		url: "https://api.instagram.com/v1/media/" + id +"?client_id=7c6bfcdf43c242eab9dfebf227dc86c9",
		dataType: 'jsonp',
		crossDomain: true,
		success: function(media) {
			var mediaObject = {};
			mediaObject.created_time = media.data.created_time;
			mediaObject.imgUrl = media.data.images.low_resolution.url;
			mediaObject.username = media.data.user.username;
			return mediaObject;
		}
	});
	
	
	
}