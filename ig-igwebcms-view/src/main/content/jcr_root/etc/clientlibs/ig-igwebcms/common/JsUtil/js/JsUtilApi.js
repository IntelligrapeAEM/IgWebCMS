(function(){

	function jsUtil(){}
		
	this.jsUtil = jsUtil;

    jsUtil.ajaxRequest = function(url ,dataType ,methodType ,callback)
    {
        //Used to make an AJAX request with specified method TYPE and in response it returns STATUS.
		$.ajax(
		{
			url:url,
            type:methodType,
			dataType: dataType,
			success:function(result,status,xhr){
                callback(status ,result)
 			} ,
			error:function(xhr, ajaxOptions, thrownError){
				alert("Error"+ xhr.statusText + xhr.responseText);
			}
		});	
    }

    jsUtil.getValue =function(id)
    {
        //Fetches value of item with CLASS.
		return $('.'+id).val();
    }

    jsUtil.setValue = function(id ,value)
    {
        //Sets value of item with CLASS.
		$('.'+id).val(value);
    }

    jsUtil.getText = function(id)
    {
        //Fetches Text of item with CLASS.
		return $('.'+id).text();
    }

    jsUtil.setText = function(id ,value)
    {
        //Sets Text of item with CLASS.
		$('.'+id).text(value);
    }

    jsUtil.loadData = function(id ,url)
    {
        //Method sets data extracted from URL into object with given CLASS.
		$('.'+id).load(url);
    }

    jsUtil.addOptions = function(id , values)
    {
        //Sets new options to the drop down(select) in the html. Pass and JS map with key(option) and value as value.
        var selectObj = $('.'+id);
		for(key in values)
        {
            selectObj.append("<option value='"+values[key]+"'>"+key+"</option>");
        }
    }

}).call(this);