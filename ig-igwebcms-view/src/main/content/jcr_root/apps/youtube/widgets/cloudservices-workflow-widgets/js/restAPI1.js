CQ.Ext.namespace("CQ.salesforce");
CQ.Ext.namespace("CQ.salesforce.restAPI1");

CQ.salesforce.restAPI1 = {

    connect: function (dialog) {


		 var channelId = dialog.getField("./loginUrl").getValue();
         var googleKey = dialog.getField("./customerkey").getValue();
		 var url="https://www.googleapis.com/youtube/v3/search?key="+googleKey+"&part=snippet,id&order=date&maxResults=20&channelId="+channelId;
         $.getJSON(url,function(data) { 
			alert(data.pageInfo.totalResults);
             if(data.pageInfo.totalResults>1)
             {
                 alert("Success");
                 CQ.cloudservices.getEditOk().enable();
                /* CQ.HTTP.post("/etc/cloudservices/./"+channelId, null, {"jcr:primaryType" : "cq:Page"});
                 CQ.HTTP.post("/etc/cloudservices/YouTube/"+channelId+"/jcr:content", null, {"jcr:primaryType" : "cq:PageContent","channelId":""+channelId+"","googleKey":""+googleKey+""});
                 */
             }
		 }
);


    },

   

};