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
    //initialize IFrame Modal
    jQuery(document).ready(function() {
        jQuery('#${uuid}').closest('.iframe-modal').iframeModal({src: '${properties["url"]}'});
    });
</script>
