<%--

  Youtube component.

  

--%><%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" %><%
%>
<%!
    String randomID=null;
	String imagePath=null;
	String videoId=null;
%>
<%
randomID = Integer.toHexString((int)(Math.random() * Integer.MAX_VALUE));
try
{
imagePath = properties.get("./fileReference", String.class);
videoId=imagePath.substring(imagePath.lastIndexOf("/")+1);
}
catch(Exception e)
{
	
    %>
<img src="/content/dam/youtube-videos/YouTube.jpg" height=200 width=400 />
	<%
}

if(imagePath!=null)
{
    %>
<iframe id="ytplayer_<%=randomID%>" type="text/html" width="400" height="200"
src="//www.youtube.com/embed/<%=videoId%>"
frameborder="0" allowfullscreen></iframe>
<%
}
else
{


}
%>




