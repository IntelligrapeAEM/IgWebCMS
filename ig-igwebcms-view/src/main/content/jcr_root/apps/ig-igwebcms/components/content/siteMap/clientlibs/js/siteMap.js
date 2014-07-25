$(document).ready(function(){
	$("#siteMapTree").children().addClass("firstLabel");
    $("li.noleaf").find("li").addClass("noImageClass");
   /* $("#siteMap").find("ul li a").addClass("aClass");
    $("ul li a").hover(
        function(){
        	$(this).addClass('aHover');
    	},
		function(){
        	$(this).addClass('aClass');
    	}
    );*/
});