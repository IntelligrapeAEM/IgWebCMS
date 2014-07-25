<%@include file="/apps/ig-igwebcms/global.jsp"%>
<%@page session="false" %>

<head>
    <cq:include script="/libs/wcm/core/components/init/init.jsp"/>
    <cq:include script="/libs/foundation/components/page/stats.jsp"/>
    <title><%= currentPage.getTitle() == null ? currentPage.getName() : currentPage.getTitle() %></title>
    <cq:includeClientLib categories="commons.igwebcms"/>
    <script src="https://maps.googleapis.com/maps/api/js?sensor=false"></script>
</head>