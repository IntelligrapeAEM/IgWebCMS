<%@include file="/apps/ig-igwebcms/global.jsp"%>
<div class="container1">

<div class="row">
  <div class="col-sm-12">
    <!-- Desktop Slider -->

    <div class="hide-for-small">
      <div id="featured">
		<cq:include path="mainImg" resourceType="/libs/foundation/components/image" />
      </div>
    </div><!-- End Desktop Slider -->
    <!-- Mobile Header -->

    
  </div>
</div><br>

<div class="row">
  <div class="col-sm-12">
    <div class="row">
      <!-- Thumbnails -->

      <div class="col-sm-3">
	<a class="thumbnail" href="#">
		<cq:include path="thumbnail1" resourceType="/libs/foundation/components/image" />
    </a>
	<div class="panel panel-default">
		<div class="panel-heading">
        	            <cq:include path="thumbnailtext1" resourceType="/libs/foundation/components/text" />
		</div>	
	</div>
      </div>

      <div class="col-sm-3">
	<a class="thumbnail" href="#">
		<cq:include path="thumbnail2" resourceType="/libs/foundation/components/image" />
    </a>
	<div class="panel panel-default">
		<div class="panel-heading">
                        <cq:include path="thumbnailtext2" resourceType="/libs/foundation/components/text" />
		</div>	
	</div>
      </div>

      <div class="col-sm-3">
	<a class="thumbnail" href="#">
		<cq:include path="thumbnail3" resourceType="/libs/foundation/components/image" />
    </a>
	<div class="panel panel-default">
		<div class="panel-heading">
            <cq:include path="thumbnailtext3" resourceType="/libs/foundation/components/text" />
		</div>	
	</div>
      </div>
      <div class="col-sm-3">
	<a class="thumbnail" href="#">
		<cq:include path="thumbnailtext4" resourceType="/libs/foundation/components/image" />
    </a>
	<div class="panel panel-default">
		<div class="panel-heading">
            	<cq:include path="text4" resourceType="/libs/foundation/components/text" />
		</div>	
	</div>
      </div><!-- End Thumbnails -->
    </div>
  </div>
</div>

<div class="row">
  <div class="col-sm-12">
    <div class="row">
      <!-- Content -->

      <div class="col-sm-8">
        <div class="panel panel-default well">
          <div class="row">
            <div class="col-sm-6">
                <cq:include path="header1" resourceType="/libs/foundation/components/text" />
              <hr>
                            <cq:include path="subtext1" resourceType="/libs/foundation/components/text" />

            </div>

            <div class="col-sm-6">
              <cq:include path="subtext2" resourceType="/libs/foundation/components/text" />
            </div>
          </div>
        </div>
      </div>

      <div class="col-sm-4">
        <h4>Get In Touch!</h4>
        <hr>
        <a href="#">
        <div class="label label-default" style="text-align: center">
          <strong>Call To Action!</strong>
        </div></a> <a href="#">
        <br><br><div class="label label-default" style="text-align: center">
          <strong>Call To Action!</strong>
        </div></a>
      </div><!-- End Content -->
    </div>
  </div>
</div>
</div>
<cq:include path="par" resourceType="foundation/components/parsys" />