<%--

  Header component.

  This is header component that have Seach box , top nav and toolbar component

--%><%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" %><%
%><%
	// TODO add you code here
%>
<nav class="navbar navbar-default" data-topbar="">

	<div class="applogo" >
        <cq:include path="logo" resourceType="foundation/components/logo"/>
    </div>
    <div class="navbar-header">
        <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
        </button>
    </div>

    <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
    <div class=" navbar-right">
        <cq:include path="toolbar" resourceType="ig-igwebcms/components/content/toolbar"/>
    </div>
        <div style="clear:both;"></div>
        <div class="navbar-form navbar-right">
            <form  role="search" action="<%=resourcePage.getAbsoluteParent(2).getPath()%>/searchResult.html">
        		<div class="form-group">
            	<input type="text" class="form-control" placeholder="Search">
        		</div>
        		<button type="submit" class="btn btn-default">Search</button>
    		</form>
        </div>
    <div style="clear:both;"/>
    <cq:include path="topnav" resourceType="ig-igwebcms/components/content/topNavigation"/>
    </div>

</nav>