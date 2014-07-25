<%@ page session="false"%>
<%@ page import="
                    com.day.cq.commons.Doctype, com.day.cq.i18n.I18n, com.day.text.Text,
                    org.apache.commons.lang3.StringEscapeUtils,
                    org.apache.commons.lang3.StringUtils,com.day.cq.commons.Doctype,
                    com.day.cq.wcm.api.PageFilter,
                    com.day.cq.wcm.foundation.Navigation,
                    com.day.text.Text                " %>

<%@include file="/apps/ig-igwebcms/global.jsp"%>

<%
    String resourcePath=currentStyle.get("parentUrl",Text.getAbsoluteParent(currentPage.getPath(), 2));
    Resource res = resourceResolver.getResource(resourcePath);
    Page homePage = res.adaptTo(Page.class);

    int label = currentStyle.get("subMenuLabel",3);
    label = (label<=0)?4:(label>4)?4:label;

    String displayType= currentStyle.get("displayType","H");

    PageFilter filter = new PageFilter(request);
    Navigation nav = new Navigation(homePage, homePage.getDepth(), filter, label);

    String linkCheckerHint = filter.isIncludeInvalid() ? "" : "x-cq-linkchecker=\"valid\"";

    if(displayType.equals("H")){
%>

<div >
    <ul id="topnav" class="nav navbar-nav">
        <li class="dropdown">
            <%
                for (Navigation.Element e: nav) {
                    switch (e.getType()) {
                        case NODE_OPEN:
            %><ul class="dropdown-menu"><%
                break;
            case ITEM_BEGIN:
        %><li <%= e.hasChildren() ? "class=\"dropdown\"" : "" %>><a href="<%= e.getPath() %>.html" <%= linkCheckerHint %>><%= e.getTitle() %></a><%
                break;
            case ITEM_END:
        %></li><%
                break;
            case NODE_CLOSE:
        %></ul><%
                        break;
                }
            }
        %>
        </li>
        </li>
    </ul>
</div>
<%}else{%>
<ul id="tabmenu">
    <%
        for (Navigation.Element e: nav) {
            switch (e.getType()) {
                case NODE_OPEN:
    %><ul><%
        break;
    case ITEM_BEGIN:
%><li><a href="<%= e.getPath() %>.html" <%= linkCheckerHint %>><%= e.getTitle() %></a><%
        break;
    case ITEM_END:
%></li><%
        break;
    case NODE_CLOSE:
%></ul><%
                break;
        }
    }
%>
</ul>
<%}%>