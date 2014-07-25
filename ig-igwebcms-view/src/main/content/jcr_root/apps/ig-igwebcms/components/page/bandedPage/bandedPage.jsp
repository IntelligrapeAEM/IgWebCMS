<%--

  Banded component.

  This is generic responsive template . Banded

--%><%
%><%@include file="/apps/ig-igwebcms/global.jsp"%><%
%><%@page session="false" %><%
%><%
	// TODO add you code here
%>
<!DOCTYPE html>
<!--[if IE 9]><html class="lt-ie10" lang="en" > <![endif]-->
<html>
<head>
    <cq:include script="/libs/wcm/core/components/init/init.jsp"/>
    <cq:includeClientLib categories="commons.util.bootstrap" />
    <title>Foundation Template | Banded</title>
</head>
  <body>
    
	<div class="container">

		<!-- Header and Nav -->

  <div class="row">
    <div class="col-sm-3">
        <cq:include path="logo" resourceType="foundation/components/logo" />
    </div>
    <div class="col-sm-9">
	<div class="btn-group btn-group-lg" style="float:right;">
	  <button type="button" class="btn btn-default">Left</button>
	  <button type="button" class="btn btn-default">Middle</button>
	  <button type="button" class="btn btn-default">Right</button>
	</div>
    </div>
  </div>

  <!-- End Header and Nav -->


  <!-- First Band (Image) -->

  <div class="row">
    <div class="col-sm-12">
      <cq:include path="image1" resourceType="foundation/components/image" />

      <hr>
    </div>
  </div>
  <!-- Second Band (Image Left with Text) -->

  <div class="row">
    <div class="col-sm-4">
      <cq:include path="image2" resourceType="foundation/components/image" />
    </div>
    <div class="col-sm-8">
      <h4>This is a content section.</h4>
      <div class="row">
        <div class="col-sm-6">
            <cq:include path="text1" resourceType="foundation/components/text" />
        </div>
        <div class="col-sm-6">
          	 <cq:include path="text2" resourceType="foundation/components/text" />
        </div>
      </div>
    </div>
  </div>


  <!-- Third Band (Image Right with Text) -->

  <div class="row">
    <div class="col-sm-8">
        <cq:include path="text3" resourceType="foundation/components/text" />

    </div>
    <div class="col-sm-4">
      <cq:include path="image3" resourceType="foundation/components/image" />
    </div>
  </div>


  <!-- Footer -->

  <footer class="row">
    <div class="col-sm-12">
      <hr>
      <div class="row">
        <div class="col-sm-8">
          <p>© Copyright no one at all. Go to town.</p>
        </div>
        <div class="col-sm-4">
	<p>
	    <a href="#" class="btn btn-link">Link 1</a>
            <a href="#" class="btn btn-link">Link 2</a>
            <a href="#" class="btn btn-link">Link 3</a>
            <a href="#" class="btn btn-link">Link 4</a>	
	</p>
        </div>
      </div>
    </div>
  </footer>  
	</div>
  
</body>
</html>