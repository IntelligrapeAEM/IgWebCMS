<%--

  Google Map  component.

  This is google map componwnt

--%><%
%><%@include file="/apps/ig-igwebcms/global.jsp"%><%
%><%@page session="false" %><%
%>
<div class="mapConfig" maptypecontrol="${properties.mapTypeControl}"
     pancontrol="${properties.panControl}" scalecontrol="${properties.scaleControl}"
     zoomcontrol="${properties.zoomControl}" streetview="${properties.streetView}"
     markertext="${properties.markerText}" markerImage="${properties['image/fileReference']}" ></div>
<div class="map_canvas" style="width: ${properties.width}px ; height: ${properties.height}px">
</div>