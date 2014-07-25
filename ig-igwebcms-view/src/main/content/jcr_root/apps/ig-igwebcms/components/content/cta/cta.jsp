<%-- @ include is static (precompile) include, so needs full path to file (can't use sling relative pathing/searching/inheritance --%>
<%@ include file="/apps/ig-igwebcms/global-common.jsp" %>
<%@ page import="
                static org.apache.commons.lang3.StringUtils.*,
                com.ig.igwebcms.core.util.* ,
                com.day.cq.wcm.api.Page
                " %>
<%--
    BAW 4-18-12: I've added the CTA to the Common area, and created a Common component grouping.
    Future feature adds will include a side-by-side option, a "no chevron" option, and a "no background" option.
---%>
<%
String arrowClass = "arrow";
String textOverlay = properties.get("textOverlay", "");
String ctaType = properties.get("ctaType", "blue-button");
String ctaAlignment = properties.get("ctaAlignment", "");

String analyticsName = properties.get("analyticsNiceName", "");

String modalType = properties.get("modalType","");
String target = properties.get("newWindow", "").equals("true") ? "target=\"_blank\"" : "";
boolean allowEmpty = properties.get("allowEmpty", false);
boolean removeArrow = properties.get("removeArrow", false);

//option to pass params to the modal window
boolean passParams = properties.get("passParams", false);

//option to open as modal window
boolean modalOverlay = properties.get("modalOverlay", false);

//The remove arrow option will have no effect on CTA types that specifically contain Arrow (i.e. blue arrow).
if(removeArrow || ctaType.toLowerCase().indexOf("arrow") > -1){arrowClass = "no-arrow";}

/*

    need to add some functionality in here to that class, such as modalType stuff
    also, modalType appears to trump fragments, etc.  is this true?
*/
String url = properties.get("url", "#");
String url_selector = null;
String fragment = properties.get("fragment", "");
String anchor_selector = properties.get("anchor-selector", "");

//Preserve base URL path for use with selectors
url_selector = url;

//Need a special modal type for Site Tour
boolean isSiteTour = false;
boolean isUrlInternal= URLs.isInternalURL(url);

if (!modalOverlay) {
    //reset these var to unset the modal logic
    modalType = "";
    //Determine if this is an internal page resource. If so, we need to add the .html extension.
    if (resourceResolver.getResource(url)!=null) {
         //check to see if the resource is a page before append the .html
         Page checkPage = resourceResolver.getResource(url).adaptTo(Page.class);
         if (checkPage != null) {
           url = url +".html";
            // A CQ page might also want to link to an anchor using the fragment in the advanced tab.
            if (fragment.length()>0) {
               url+= "#"+fragment;
            }
         }
       }




} else { // modalType set; do different magic for modals.
    if(isUrlInternal){
        url = url + "/_jcr_content.content.html?wcmmode=disabled";
    }
  if (passParams) {
    url = url + "&refer="+currentPage.getPath()+"&analyVal="+analyticsName.replace(" ","%20");
  }
}

// TODO: need to remove modalClass
String modalClass = url.startsWith("#") && url.length()>1 ? "modal" : "";

if(isEmpty(textOverlay) && !allowEmpty){
    %><span class="NoContent">Set CTA</span>
<%
} else {
    String ctaClass = Strings.join(" ", ctaType, ctaAlignment, modalClass, modalType);
    if (isEmpty(textOverlay)) {
        arrowClass += " empty";
        ctaClass += " empty";
    }
    
    if(modalType.equalsIgnoreCase("Modal" + "modalSitetour")){
        isSiteTour = true;
        url = "#";
    }
    %>
        <a <%=target%> analyticsName="<%=analyticsName%>" href="<% if (!anchor_selector.equals("")) { %><%= url_selector %>.<%= anchor_selector %>.html<% } else { %><%= url %><% } %>"  class="<%= ctaClass %>  fancybox  <%=isUrlInternal ?  " fancybox.ajax" :  " fancybox.iframe"%>"><span class="<%= arrowClass %>"><%=textOverlay %></span><span class="twc-icon-after icon-angle-right"></span></a>
    <%
} 
%>
<div class="clear"></div>
<script>
jQuery(document).ready(function() {

    // Assign click event to the div surrounding the anchor
    // so clicking the enitre button will produce the same
    // result as clicking just the text.
    $regNow = $('.registerForIDCTA');
    $regNow.on("click", function(){
        window.location.href = $regNow.find('a').attr('href');
    });

    if(typeof modalInit !== "undefined" ){
        $('a.modal').modalInit();
    }
    if(typeof fancybox!=="undefined"){
        $('a.modalGeneric').fancybox({
            type: 'ajax',
            fitToView: true,
            autoSize: true,
            openEffect:'fade',
            openSpeed:'slow'
        });
    }

    var isSiteTour = <%=isSiteTour%>
    //only load this block if the CTA points to the Site Tour
    if(isSiteTour){
        var totalWidth = 860;
        
        $.extend( $.easing,
        {
            customEasing: function (x, t, b, c, d, s) {
                if (s == undefined) s = 1;
                return c*((t=t/d-1)*t*((s+1)*t + s) + 1) + b;
            }
        });

        function displayCaps()
        {
            $('.caption1, .caption2, .caption3, .popArr').hide();
            $('.slide:visible .caption1').delay(100).show('scale', { percent: 100, easing: 'customEasing' }, 500, function(){$(this).find('.popArr').fadeIn(200);});
            $('.slide:visible .caption2').delay(500).show('scale', { percent: 100, easing: 'customEasing' }, 500, function(){$(this).find('.popArr').fadeIn(200);});
            $('.slide:visible .caption3').delay(900).show('scale', { percent: 100, easing: 'customEasing' }, 500, function(){$(this).find('.popArr').fadeIn(200);});
        }

        function animateProgress(current)
        {
            if(!current)
                current = 2;
            else
                current++;
            
            var prevPer = 0;
            if(current == 20)
            {
                $('#Progress').width('100%');
            }
            else
            {   
                var curTot = 4;
                var curInd = $('.slideBullets .link' + current).index();
                var count = $('#SlideCats .link' + current).parent().index();
                
                
                while(count > 0){
                    prevPer += getfullwidth('#SlideCats .SlideCats' + count);
                    count--;
                }
                count = $('#SlideCats .link' + current).parent().index()+1;
                
                prevPer = prevPer*100/totalWidth;
                
                var curPerinCat = curInd*100/curTot;
                var curCatPer = getfullwidth('#SlideCats .SlideCats' + count)*100/totalWidth;
                var percent = 0 + prevPer + (curPerinCat*curCatPer)/100;
                
                //$('#Progress').width(Math.floor(percent*10)/10 + '%');
                
                var progressWidth = Math.floor(percent*10)/10 + '%';
                if(current == 2)
                    progressWidth = "17.7%";
                
                $('#Progress').width(progressWidth);
            }
        }

        function getfullwidth(div)
        {
            var totalWidth = 0;
            var theDiv = $(div);
            totalWidth += theDiv.width();
            totalWidth += parseInt(theDiv.css("padding-left"), 10) + parseInt(theDiv.css("padding-right"), 10); //Total Padding Width
            totalWidth += parseInt(theDiv.css("margin-left"), 10) + parseInt(theDiv.css("margin-right"), 10); //Total Margin Width
            //totalWidth += parseInt(theDiv.css("borderLeftWidth"), 10) + parseInt(theDiv.css("borderRightWidth"), 10); //Total Border Width
            return totalWidth;
        }

        //return site tour section based off of slide number
        function getTourSectionName(slideNum)
        {
            var slide = "slide" + slideNum;
            var sectionName = "";
            
            //map of each slide, it's title section and page number in the section
            var tourMap =
            {
                "slide1":"Gateway  > 1",
                "slide2":"Residential Homepage > 1",
                "slide3":"Residential Homepage > 2",
                "slide4":"Residential Homepage > 3",
                "slide5":"Residential Homepage > 4",
                "slide6":"Packages > 1",
                "slide7":"Packages > 2",
                "slide8":"Packages > 3",
                "slide9":"Packages > 4",
                "slide10":"Packages > 5",
                "slide11":"Packages > 6",
                "slide12":"Packages > 7",
                "slide13":"Products > 1",
                "slide14":"Products > 2",
                "slide15":"Products > 3",
                "slide16":"Products > 4",
                "slide17":"Products > 5",
                "slide18":"TWC TV > 1",
                "slide19":"TWC TV > 2"
            };
            
            sectionName = tourMap[slide];
            
            return sectionName;
        }

        var body = document.body, html = document.documentElement;
        var isRunning = false;   
        
        $('#infoVeil').css("height", (body.scrollHeight * 2) + "px");//
        //Resize the background veil to fit the window when resized
        $(window).resize(function () {
            $('#infoVeil').css("height", (body.scrollHeight * 2) +  "px");
        });
      
        $('a.modalSitetour').click(function () {
            if (typeof(s) !== 'undefined' && !isRunning) {
                
                isRunning = true;
                var sectionName = getTourSectionName(1);
                var oldPageName = s.pageName;  //underlying page
                
                s.eVar59 = oldPageName;
                s.eVar57 = "";  // clear this to prevent conflicts
                s.eVar63 = sectionName;
                s.prop63 = sectionName;
                s.events = "event92";
                s.pageName = "Site Tour";
                runOnce = true;
                s.t();
            }       
            
            $('#infoSlider').css("top", 15 + "px");
            
            setTimeout( displayCaps, 1000 );
            $('#infoVeil').fadeIn();
            $('#infoSlider').fadeIn();
        });

        //Popup Closed
        $('.closeSlides').click(function () {
            $('#infoVeil').fadeOut();
            $('#infoSlider').fadeOut();
        });
      
        $(document).keyup(function(e) {
            if (e.keyCode == 27 && $("#infoVeil").is(":visible")) { 
                $('#infoVeil').fadeOut();
                $('#infoSlider').fadeOut();
            }   // esc
        });

        //Slider Start
        $('#slides').slides({
            preload: true,
            preloadImage: 'img/loading.gif',
            play: 7000,
            pause: 0,
            hoverPause: false,
            currentClass: 'current',
            paginationClass: 'slidePaging',
            playStop: 'playPause',
            animationStart: function (current) {
                //Hide previous captions
                $('.caption1, .caption2, .caption3').hide();
            },
            animationComplete: function (current) {
                //set styling to currently displaying item (current is the index)
                $('.slideVis').hide();
                $('.link').removeClass("current");
                $('.link' + current).addClass("current").parent().show();
                
                
                //set percentage bar width
                animateProgress(current);
                
                //animate captions 
                setTimeout( displayCaps, 300 );
                
                //analytics - for each slide in the tour other then the first time it loads
                if (typeof(s) !== 'undefined') {
                    var sectionName = getTourSectionName(current);
                    
                    s.linkTrackVars="events,eVar63,prop63";
                    s.linkTrackEvents="event92";
                    s.eVar63 = sectionName;
                    s.prop63 = sectionName;
                    s.events = "event92";
                    s.tl (this, 'o', 'Site Tour');                      
                }
                
            },
            slidesLoaded: function() {
                //set styling to first displaying item
                $('.caption1, .caption2, .caption3').hide();
                $(".link1").addClass("current").parent().show();
                setTimeout( displayCaps, 300 );
                setTimeout( animateProgress, 1000);
            }
        });
    }
    //END Site Tour Conditional
});
</script>
