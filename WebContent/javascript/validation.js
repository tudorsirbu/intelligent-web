
var markersArray = [];
function clearOverlays() {
	  for (var i = 0; i < markersArray.length; i++ ) {
	    markersArray[i].setMap(null);
	  }
	  markersArray.length = 0;
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