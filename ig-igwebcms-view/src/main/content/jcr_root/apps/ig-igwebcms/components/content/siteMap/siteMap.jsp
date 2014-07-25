<%@ page session="false"%>
<%@ page import=" com.day.cq.wcm.api.PageFilter,
                  com.day.cq.wcm.foundation.Navigation,
                  com.day.text.Text
                " %>

<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/nodeapi/components/init.jsp" %>

<cq:includeClientLib categories="site.map.lib"  />

<%
	String resourcePath=properties.get("parentUrl","/content/geometrixx/en").toString();
	int label = properties.get("subMenuLabel",10);
	String displayIn= properties.get("displayIn","N");
	String target=(displayIn.equals("N"))?"_blank":"_self";

    Resource res = resourceResolver.getResource(resourcePath);
    Page homePage = res.adaptTo(Page.class);
    PageFilter filter = new PageFilter(request);
	Navigation nav = new Navigation(homePage, 2, filter, label);
    String linkCheckerHint = filter.isIncludeInvalid() ? "" : "x-cq-linkchecker=\"valid\"";
%>

		<div id="siteMap">
            <span class="title">Site Map</span>
            <ul id="siteMapTree">
                <%
                    for (Navigation.Element e: nav) {
                        switch (e.getType()) {
                           case NODE_OPEN:
                                %><ul><%
                                break;
                            case ITEM_BEGIN:
                                %><li <%= e.hasChildren() ? "class=\"noleaf\"" : "" %>><a href="<%= e.getPath() %>.html" <%= linkCheckerHint %> target="<%=target %>" ><%= e.getTitle() %></a><%
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
		</div>