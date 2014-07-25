<!DOCTYPE html>
<%@include file="/apps/ig-igwebcms/global.jsp"%>
<%@page session="false"%>
<cq:includeClientLib categories="ext.widget.lib" />

<html>
    <head>
        <script>
            function getHttp() {
                var xmlhttp;
                if (window.XMLHttpRequest) {
                    xmlhttp = new XMLHttpRequest();
                } else {
                    xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
                }
                return xmlhttp;
            }

            function getData() {
                try {
                    var xmlhttp = getHttp();
                    xmlhttp.onreadystatechange = function () {
                        if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
                            data = xmlhttp.responseText;
                            var objList = JSON.parse(xmlhttp.responseText);//obj.contacts;
                            var data = "<table width='100%' cellspacing='0' style=\"border:0x solid red;\">" +
                                    "<tr style=\"background-color:#aaccaa;color:white;\">" +
                                    "<th width='30%' style=\"text-align:left;\">Keys</th>" +
                                    "<th width='30%' style=\"text-align:left;\">Values In % </th>" +
                                    "<th width='40%' style=\"text-align:left;\">Operations</th>" +
                                    "</tr>";
                            for (var i = 0; i < objList.length; ++i) {
                                data += "<tr onmouseOver>" +
                                        "<td><input type='text' class='editTextCss' name='key_" + i + "' id='key_" + i + "' value=\"" + objList[i].key + "\" disabled='true' /></td>" +
                                        "<td><input type='text' class='editTextCss' name='value_" + i + "' id='value_" + i + "' value=\"" + objList[i].value + "\" disabled='true' /><input type='hidden' name='loc_" + i + "' id='loc_" + i + "' value='" + objList[i].path + "' /></td>" +
                                        "<td>" + "<span id='td_" + i + "'><a href='javascript:editMe(\"" + i + "\");' >Edit</a></span>&nbsp;&nbsp;<a href='javascript:deleteMe(\"" + objList[i].path + "\");' >Delete</a>" + "</td>" +
                                        "</tr>";
                            }
                            data += "</table>"
                            document.getElementById("dataList").innerHTML = data;
                        }
                    }
                    xmlhttp.open("GET", f1.parentLocation.value + ".nodedata.json", true);
                    xmlhttp.send();

                } catch (e) {
                    alert(e);
                }
            }

            function deleteMe(location) {
                try {
                    var xmlhttp = getHttp();
                    xmlhttp.onreadystatechange = function () {
                        if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
                            getData();
                        }
                    }
                    xmlhttp.open("POST", location, true);
                    xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
                    xmlhttp.send(":operation=delete");
                } catch (e) {
                    alert(e);
                }

            }
            function editMe(location) {
                document.getElementById("key_" + location).disabled = "";
                document.getElementById("value_" + location).disabled = "";
                document.getElementById("key_" + location).focus();
                document.getElementById("key_" + location).className = "textCss";
                document.getElementById("value_" + location).className = "textCss";
                document.getElementById("td_" + location).innerHTML = "<span id='td_" + location + "'><a href='javascript:saveMe(\"" + location + "\");' >Save</a></span>";
            }
            function saveMe(location) {
                if (location == "") {
                    key = f1.key.value;
                    value = f1.value.value;
                    location = f1.location.value + "/" + key;
                    var queryString = "modified=Y&key=" + key + "&value=" + value + "&location=" + location;
                    try {
                        var xmlhttp = getHttp();
                        xmlhttp.onreadystatechange = function () {
                            if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
                                document.getElementById("dataList").innerHTML = xmlhttp.responseText;
                                getData();
                            }
                        }
                        xmlhttp.open("POST", location, true);
                        xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
                        xmlhttp.send(queryString);
                    } catch (e) {
                        alert(e);
                    }

                } else {
                    var key = document.getElementById("key_" + location).value;
                    var value = document.getElementById("value_" + location).value;
                    var location = document.getElementById("loc_" + location).value;
                    queryString = "modified=Y&key=" + key + "&value=" + value + "&location=" + location + "&operation=EDIT";
                    try {
                        var xmlhttp = getHttp();
                        xmlhttp.onreadystatechange = function () {

                            if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
                                getData();
                            }
                        }
                        xmlhttp.open("POST", location, true);
                        xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
                        xmlhttp.send(queryString);
                    } catch (e) {
                        alert(e);
                    }
                }
            }

        </script>
    </head>
    <body>
        <div id="headerPart">Node Information Data Values
            <form name="f1">
                <input type="hidden" name="parentLocation" value="<%=resource.getParent().getPath() %>"/>
                <input type="hidden" name="location" value="<%=resource.getPath() %>"/>
                Key <input type="text" name="key" value="" class="textCss"/>
                Value <input type="text" name="value" value="" class="textCss"/>
                <input type="button" name="submit" value="Save" class="buttonCss" onClick="saveMe('')"/>
            </form>
        </div>
        <div id="dataList">No Data found</div>
    </body>
    <script>
        getData();
    </script>
</html>
