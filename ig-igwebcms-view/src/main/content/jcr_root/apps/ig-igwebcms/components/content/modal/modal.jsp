<%@ page import="java.util.UUID" %>
<%@ include file="/apps/ig-igwebcms/components/global.jspx" %>

<c:set var="width" value='60em' />
<c:set var="height" value='auto' />

<div class="modal-box" style="width: ${width}; height:${height};">
    <div class="close"></div>
    <div class="modal_content" style="width: ${width}; height:${height};">
        <cq:include script="modal-content.jsp"/>
    </div>
</div>
<div class="background"></div>
<div style="clear: both;"></div>

<!--
-- A marker div is used for the id instead of putting it on the script element
-- because when run in the megamenu, the script appears to run disembodied so that
-- jquery(#uuid) returns no elements.
-->
<c:set var="uuid" value='<%= UUID.randomUUID().toString() %>' />
<div id="${uuid}"></div>
<script>
    jQuery(document).ready(function () {
        jQuery('#${uuid}').closest('.modal').modal();
    });
</script>
<cq:include script="custom-initializer.jsp"/>
