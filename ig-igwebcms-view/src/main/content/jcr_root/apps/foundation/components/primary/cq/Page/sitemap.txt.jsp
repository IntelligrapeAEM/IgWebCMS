<%@page session="false"%>
<%@page import="org.apache.sling.api.resource.Resource,
				javax.jcr.Node,
				javax.jcr.PathNotFoundException,
				javax.jcr.Session,
                javax.jcr.util.TraversingItemVisitor,
                com.day.cq.commons.Externalizer,
                org.apache.sling.jcr.api.SlingRepository,
				com.day.cq.wcm.api.Page,
				org.apache.sling.api.resource.Resource,
				org.apache.sling.api.resource.ResourceResolver,
				javax.jcr.Property,
                java.util.List,
                java.util.ArrayList,
				java.util.HashSet,
                java.util.Collections,
                java.util.HashMap,
                java.util.SortedMap,
                java.util.Date,
                com.day.cq.commons.jcr.JcrUtil,
                javax.jcr.NodeIterator" 
%>
<%@include file="/libs/foundation/global.jsp" %>
<%! 
    HashSet<String> pageList = new HashSet<String>();

    HashSet<String> getPagesSet(Node node,ResourceResolver resourceResolver) throws Exception{
        pageList.clear();
        populatePagesSet(node,resourceResolver);
        return pageList;

    }

	void populatePagesSet(Node node,ResourceResolver resourceResolver) throws Exception{

            NodeIterator iter = node.getNodes();
            while(iter.hasNext()) {
                Node child = iter.nextNode();
                if(child.isNodeType("cq:Page")) {
                    Resource res= resourceResolver.getResource(child.getPath());
                    Page page= res.adaptTo(Page.class);
                    if(!page.isHideInNav()){
                        String tempPath=child.getPath()+".html";
                    	pageList.add(tempPath);
                        if(child.hasNodes())
                            populatePagesSet(child,resourceResolver);
                    } 
                }
            }
    }

	String printPagesList(HashSet<String> pagesList,String serverUrl) {

        String list = "";
        for(String key: pagesList)
            list += serverUrl+key+"\n";

        return list;
    }
%>
<%
	HashSet<String> newList = pageList;

	String tempPath=request.getRequestURL()+"";
	String uri=request.getRequestURI();
	int index=tempPath.indexOf(uri);
	String serverUrl=tempPath.substring(0,index);

    Externalizer externalizer = resourceResolver.adaptTo(Externalizer.class);
    response.setContentType("text/plain");

    SlingRepository repo = null;
    Session session = null;

    try {
        newList=getPagesSet(currentNode,resourceResolver);
    	repo = sling.getService(SlingRepository.class);
    	session = repo.loginAdministrative(null);
        response.getWriter().println(printPagesList(newList,serverUrl).trim());
    } finally {
        if (session != null) {
            session.logout();
        }
    } 
%>