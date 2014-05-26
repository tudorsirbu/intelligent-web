function displayResultsOnMap(data) {
	google.maps.event.addDomListener(window, 'load', initMap(data));
}
