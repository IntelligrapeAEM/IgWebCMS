function populateCustomSelection(field, rec, path, jsonResource, targetedField) {
    var destinationField = field.findParentByType('panel').getComponent(targetedField);
    destinationField.reset();
    var destinationFieldOpts = [];
    //  alert("field "+field.getValue());
    response = CQ.utils.HTTP.get(jsonResource);
    if (CQ.HTTP.isOk(response)) {
        var jsonObjects = CQ.Util.eval(response);
        for (var item in jsonObjects) {
            if (item.indexOf("jcr") == -1 && item.indexOf("sling") == -1 && targetedField === "city") {
                var temp = field.findParentByType('panel').getComponent("temp");

                temp.setValue(jsonObjects[item].value);
                destinationFieldOpts.push({value: jsonObjects[item].value, text: jsonObjects[item].text});
                //           alert("temp"+temp.getValue());
            }
            else if (item.indexOf("jcr") == -1 && item.indexOf("sling") == -1 && item != "remove") {
                //   alert("item is"+jsonObjects[item].value);
                destinationFieldOpts.push({value: jsonObjects[item].value, text: jsonObjects[item].text});
            }
        }
    }
    destinationField.setOptions(destinationFieldOpts);
}


