<%@include file="/libs/foundation/global.jsp" %>
<%--
    Author: dkhetawat 
        Weather Info Component 
        --%>
<%@page session="false" %>
<cq:includeClientLib categories="weather.info.component"/>
<script src="http://maps.google.com/maps/api/js?sensor=true"></script>

<script type="text/javascript"> 
    function cookieCreation(latitude,longitude)
    {
                var coordinates=latitude + "%" + longitude;
                $.cookie('locationcoordinates',coordinates, { expires: 365 });
                var cookie = $.cookie('locationcoordinates');
                var coordinate = cookie.split("%");
                return coordinate; 
    }

    function onDefaultPageLoad() {
        try{
            navigator.geolocation.getCurrentPosition(function(position) {
                var latitude=position.coords.latitude;
                var longitude=position.coords.longitude;
                var coordinate = cookieCreation(latitude,longitude);
                loadWeather(coordinate[0]+','+coordinate[1]); 
            });
            
        }
        catch(e){alert(e);}

    }


    function loadWeather(latitude, longitude) {
        $.simpleWeather({
            location: latitude,
            woeid: longitude,
            unit: 'f',
            success: function(weather) {
                html = '<h2><i class="icon-'+weather.code+'"></i> '+weather.temp+'&deg;'+weather.units.temp+'</h2>';
                html += '<ul><li>'+weather.city+', '+weather.region+'</li>';
                html += '<li class="currently">'+weather.currently+'</li>';
                html += '<li>'+weather.alt.temp+'&deg;C</li></ul>';  
                
                $("#weather").html(html);
            },
            error: function(error) {
                $("#weather").html('<p>'+error+'</p>');
            }
        });
    }
    
</script>


<script type="text/javascript">

    
    $(document).ready(function(){
        $("#btn").click(function(){
            var zip=document.getElementById('zip').value;
            var url = "https://maps.googleapis.com/maps/api/geocode/json?address="+zip+"&sensor=true&components=country:IN";
            $.getJSON(url, function(data){
                var result = data['results'];
                var latitude =result[0].geometry.location.lat;
                var longitude =result[0].geometry.location.lng;
                var coordinate = cookieCreation(latitude,longitude);
                loadWeather(coordinate[0]+','+coordinate[1]);
                /* $.ajax({
                    type: "GET",
                    url: "/bin/servlet/weather",
                 // dataType: "json",
                    success: function(data)
                    {
                        //alert(JSON.stringify(data));


                        var obj=JSON.parse(data);
                        console.log(obj);
                         alert(obj); 
                    }
                    });*/
                loadXMLDoc(latitude,longitude);

            });
        });

    });
</script>
<script>
    function loadXMLDoc(latitude,longitude)
    {

        var xmlhttp;
        if (window.XMLHttpRequest)
        {// code for IE7+, Firefox, Chrome, Opera, Safari
            xmlhttp=new XMLHttpRequest();
        }
        else
        {// code for IE6, IE5
            xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
        }
        xmlhttp.onreadystatechange=function()
        {
            if (xmlhttp.readyState==4 && xmlhttp.status==200)
            {
                var obj=JSON.parse(xmlhttp.responseText);
                console.log(obj.results[0].icon);
                var imagePath=obj.results[0].icon;
                document.getElementById("places").innerHTML = imagePath; 

            }
        }
        xmlhttp.open("GET","/bin/servlet/weather?latitude="+latitude+"&longitude="+longitude,true);
        xmlhttp.send();
    }
</script>


<form action="" method="GET">
    <input type="text" name="zipcode" id="zip" />
    <input type="button" id="btn" value="Zipcode" />
</form>



<div id="weather"></div>
<div id="places"></div>

<script>
    onDefaultPageLoad();
</script>
