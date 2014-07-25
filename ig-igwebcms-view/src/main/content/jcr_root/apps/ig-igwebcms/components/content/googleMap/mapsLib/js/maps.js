$( document ).ready(function() {

	var mapConfig=$('.mapConfig');

    function initialize() {
        var myLatlng = new google.maps.LatLng(28.58, 77.32);
        var mapCanvas = $('.map_canvas');
        for(i=0;i<mapCanvas.length;i++)
        {
        	var specificMapConfig = mapConfig[i];
            var mapOptions = {
            center: myLatlng,
            zoom: 10,
            mapTypeId: google.maps.MapTypeId.ROADMAP,
            panControl: specificMapConfig.getAttribute('pancontrol'),
            zoomControl: specificMapConfig.getAttribute('zoomcontrol'),
            mapTypeControl: specificMapConfig.getAttribute('maptypecontrol'),
            scaleControl: specificMapConfig.getAttribute('scalecontrol'),
            streetViewControl: specificMapConfig.getAttribute('streetview'),
            overviewMapControl: false
            }
            var map = new google.maps.Map(mapCanvas[i], mapOptions);
            var marker = new google.maps.Marker({
               	position: myLatlng,
               	title:specificMapConfig.getAttribute('markertext'),
               	icon:specificMapConfig.getAttribute('markerImage')
           	});
            marker.setMap(map);
        }
    }
    if(mapConfig)
    {
        initialize();
    }
    google.maps.event.addDomListener(window, 'load', initialize);
});
