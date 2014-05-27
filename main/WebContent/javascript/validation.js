
var markersArray = [];
function clearOverlays() {
	  for (var i = 0; i < markersArray.length; i++ ) {
	    markersArray[i].setMap(null);
	  }
	  markersArray.length = 0;
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