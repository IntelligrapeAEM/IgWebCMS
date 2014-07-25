<%@ page import="java.util.UUID" %>
<%@ include file="/apps/ig-igwebcms/components/global.jspx" %>

<!--
-- A marker div is used for the id instead of putting it on the script element
-- because when run in the megamenu, the script appears to run disembodied so that
-- jquery(#uuid) returns no elements.
-->
<c:set var="uuid" value='<%= UUID.randomUUID().toString() %>'/>
<div id="${uuid}"></div>
<script>
    //Instead of document ready, put it in a function and call it when required
    jQuery(document).ready(function () {
       jQuery('#${uuid}').closest('.iframe-modal').ytIframeModal({src: 'http://www.youtube.com'}).trigger("showModal");
    });
</script>



