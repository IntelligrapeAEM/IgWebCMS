<%@ page import="java.util.UUID" %>
<%@ include file="/apps/ig-igwebcms/components/global.jspx" %>

<!--
-- A wrapping div is used for the id instead of putting it on the script element
-- because when run in the megamenu, the script appears to run disembodied so that
-- jquery(#uuid) returns no elements.
-->
<c:if test="${wcmMode != 'EDIT' && wcmMode != 'DESIGN'}">
    <c:set var="uuid" value='<%= UUID.randomUUID().toString() %>'/>
    <div id="${uuid}"></div>
    <script>
        jQuery(document).ready(function () {
            jQuery('#${uuid}').closest('.zipPromptModal').zippromptmodal();
        });
    </script>
</c:if>
