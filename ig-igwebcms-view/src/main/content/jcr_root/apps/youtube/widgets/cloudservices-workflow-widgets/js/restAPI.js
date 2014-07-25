
CQ.Ext.namespace("CQ.salesforce");
CQ.Ext.namespace("CQ.salesforce.restAPI");

CQ.salesforce.restAPI = {

    CUSTOMER_SECRET_CHECK_SERVLET : "/libs/mcm/salesforce/customer.json",

    TOKEN_PARAM_VALUE : "code",

    STATE_PARAM_VALUE : "myState",

    loadContentHandler: function (dialog) {

        var hashParams = CQ.salesforce.restAPI.getHashParams();

        // If there is authorization code returned back
        if(hashParams.code!==undefined && !this.isaccessTokenCreated(dialog)){
            // POST to Authorization endpoint to get access tokens and refresh tokens
            var client_id = dialog.getField("./customerkey").getValue();
            var client_secret = dialog.getField("./customersecret").getValue();
            var redirect_uri = window.location.protocol + "//" + window.location.host + window.location.pathname;
            var authorizationUrl = dialog.getField("./authorizationUrl").defaultValue;   // ./authorizationUrl

            CQ.Ext.Ajax.request({
                "url": CQ.HTTP.externalize(CQ.salesforce.restAPI.CUSTOMER_SECRET_CHECK_SERVLET),
                "params": {
                    "customer_key": client_id,
                    "customer_secret": client_secret,
                    "redirect_uri": encodeURI(redirect_uri),
                    "authorization_url": encodeURI(authorizationUrl),
                    "code": hashParams.code,
                    "checkType": "authorize"
                },
                "method": "GET",
                "success": function(response, options) {

                    var json = CQ.Ext.util.JSON.decode(response.responseText);

                    if (json['error']) {
                        CQ.Ext.Msg.show({
                            title: CQ.I18n.getMessage("Error"),
                            buttons: CQ.Ext.Msg.OK,
                            msg: CQ.I18n.getMessage("Error in Getting Access Token: ") + json['error'],
                            icon: CQ.Ext.MessageBox.ERROR
                        });
                        return ;
                    }

                    if(json['refresh_token'] && json['access_token']){

                        var accessToken = json["access_token"];
                        var refreshToken = json["refresh_token"];
                        var instanceUrl = json["instance_url"];
                        var id = json["id"];
                        var issuedat = json["issued_at"];
                        var scope = json["scope"];
                        var signature = json["signature"];

                        dialog.getField("./accesstoken").setValue(accessToken);
                        dialog.getField("./refreshtoken").setValue(refreshToken);
                        dialog.getField("./instanceurl").setValue(instanceUrl);
                        dialog.getField("./id").setValue(id);
                        dialog.getField("./issuedat").setValue(issuedat);
                        dialog.getField("./scope").setValue(scope);
                        dialog.getField("./signature").setValue(signature);

                        CQ.Ext.Msg.show({
                            title: CQ.I18n.getMessage("Success"),
                            buttons: CQ.Ext.Msg.OK,
                            msg: CQ.I18n.getMessage("Salesforce connected successfully, please save the configuration"),
                            icon: CQ.Ext.MessageBox.INFO
                        });

                        CQ.Ext.Msg.show({
                            title: CQ.I18n.getMessage("Success"),
                            msg: CQ.I18n.getMessage("Salesforce connected successfully, please save the configuration"),
                            icon: CQ.Ext.MessageBox.INFO,
                            buttons: {
                                ok: CQ.I18n.getMessage("Ok")
                            },
                            fn: function(buttonId) {
                                if (buttonId === "ok") {
                                    // Enable the OK Button
                                    CQ.cloudservices.getEditOk().enable();

                                    // Remove the other request parameters
                                    //if(window.location.href.indexOf("?")!=-1)
                                    //   window.location.href = window.location.href.substring(0, window.location.href.indexOf("?"));
                                }
                            }
                        });
                    }
                },
                "failure": function(response, options) {

                    console.log(response.responseText);

                    /**
                     *
                     * Alert Error to User
                     * */

                    CQ.Ext.Msg.show({
                        title: CQ.I18n.getMessage("Error"),
                        buttons: CQ.Ext.Msg.OK,
                        msg: CQ.I18n.getMessage("Error in Getting Access Token"),
                        icon: CQ.Ext.MessageBox.ERROR
                    });

                 },
                "scope": this

            });
        }
    },

    isaccessTokenCreated: function(dialog){
        return dialog.getField("./accesstoken").getValue()!=null &&
            dialog.getField("./accesstoken").getValue()!="";
    },

    isSalesforceCallback: function (hashParams) {
        return  typeof hashParams === "object" &&
            typeof hashParams["access_token"] !== "undefined" &&
            hashParams["access_token"] != null
    },

    displayMessage: function (type, message) {
        alert(type + ': ' + message);
    },

    checkCustomerSecret: function (dialog) {
        var customerKey = dialog.getField("./customerkey").getValue();
        var customerSecret = dialog.getField("./customersecret").getValue();
        var refreshToken = dialog.getField("./refreshtoken").getValue();
        var instanceUrl = dialog.getField("./instanceurl").getValue();

        CQ.Ext.Ajax.request({
            "url": CQ.HTTP.externalize(CQ.salesforce.restAPI.CUSTOMER_SECRET_CHECK_SERVLET),
            "params": {
                "customer_key": customerKey,
                "customer_secret": customerSecret,
                "refresh_token": refreshToken,
                "instance_url": instanceUrl
            },
            "method": "GET",
            "success": function(response, options) {
                var json = CQ.Ext.util.JSON.decode(response.responseText);

                if (json['error']) {
                    CQ.Ext.Msg.show({
                        title: CQ.I18n.getMessage("Error in Salesforce Communication"),
                        buttons: CQ.Ext.Msg.OK,
                        msg: json['errorMessage'],
                        icon: CQ.Ext.MessageBox.ERROR
                    });
                    return ;
                }

                if (json['accessToken'].length == 0 ||
                    json['instanceUrl'].length == 0 ) {
                    CQ.Ext.Msg.show({
                        title: CQ.I18n.getMessage("Error in Salesforce Communication"),
                        buttons: CQ.Ext.Msg.OK,
                        msg: CQ.I18n.getMessage("Invalid Value for Access Token received. Please try again or contact an administrator"),
                        icon: CQ.Ext.MessageBox.ERROR
                    });
                    return ;
                }

                dialog.getField("./accesstoken").setValue(json["accessToken"]);
                dialog.getField("./instanceurl").setValue(json["instanceUrl"]);
                CQ.Ext.Msg.show({
                    title: CQ.I18n.getMessage("Success"),
                    buttons: CQ.Ext.Msg.OK,
                    msg: CQ.I18n.getMessage("Customer secret checked with success, please save the configuration."),
                    icon: CQ.Ext.MessageBox.INFO
                });
                CQ.cloudservices.getEditOk().enable();

            },
            "failure": function(response, options) {
                CQ.Ext.Msg.show({
                    title: CQ.I18n.getMessage("Error"),
                    buttons: CQ.Ext.Msg.OK,
                    msg: CQ.I18n.getMessage("Unknown error in Salesforce communication, please try again."),
                    icon: CQ.Ext.MessageBox.ERROR
                });
            },
            "scope": this
        });

    },

    connect: function (dialog) {


        // clear Access and Refresh Tokens before reconnecting
        dialog.getField("./accesstoken").setValue("");
        dialog.getField("./refreshtoken").setValue("");

        // ajax to save the customer key

        CQ.Ext.Ajax.request({
            url: dialog.path,
            form: dialog.form.id,
            success: function(response, opts) {
                var customerKey = dialog.getField("./customerkey").getValue();
                var salesforceLoginUrl = dialog.getField("./loginUrl").getValue();

                var salesforceUrl = salesforceLoginUrl + "?" +
                    "response_type=" + CQ.salesforce.restAPI.TOKEN_PARAM_VALUE +
                    "&client_id=" + encodeURI(customerKey) +
                    // can"t use window.location.href because of the query parameter cq_ck
                    // redirection uri has to be exactly the same as in the configuration on salesforce.com
                    "&redirect_uri=" + encodeURI(window.location.protocol + "//" + window.location.host + window.location.pathname) +
                    "&immediate=false";

                window.location = salesforceUrl;
            }
        });

    },

    getHashParams: function() {

        var hashParams = {};
        var e,
            a = /\+/g,  // Regex for replacing addition symbol with a space
            r = /([^&;=]+)=?([^&;]*)/g,
            d = function (s) { return decodeURIComponent(s.replace(a, " ")); },
            //q = window.location.hash.substring(1);
            q = window.location.href.substring( window.location.href.indexOf('?')+1 );

        while (e = r.exec(q))
            hashParams[d(e[1])] = d(e[2]);

        return hashParams;
    }

};

CQ.salesforce.searchOperatorOptions = function(){
    var options = [];
    options.push({
        text: "Equals to",
        value: "="
    });
    options.push({
        text: "Less Than",
        value: "<"
    });
    options.push({
        text: "Greater Than",
        value: ">"
    });
    return options;
};


