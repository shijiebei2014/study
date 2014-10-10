var formLoad = function(){
	/*
	* struts命名表单字段的规范
	*/
	var getFiledNamesFromJSONObject = function getFiledNamesFromJSONObject (jsonObj, prefixFieldName,fieldNames, fieldValues) {
				for(var attr in jsonObj){
					var tmp = jsonObj[attr];
					if(typeof(tmp) == "object"){//如果属性的值是一个object类型的
						getFiledNamesFromJSONObject(tmp, attr + ".", fieldNames, fieldValues);
					} else{//如果属性的值不是一个object类型的
						fieldNames.push(prefixFieldName + attr);
						fieldValues.push(jsonObj[attr]);
					}
				}
	};
	
	var iterate = function iterate(arr, val){
				for(var j = 0; j < arr.length; j++){
					var temp = arr[j];
					if($(temp).val() == val){
						return temp;
					}	
				}
				return null;
	};
	
	return {
		load : function formLoad(formId, obj){
				if(obj){
					/*
					* input的类型:
					*/
					var fields = ["text", "hidden", "password", "radio", "checkbox"];
						var fieldNames = [];
						var fieldValues = [];
						getFiledNamesFromJSONObject(obj, "", fieldNames, fieldValues);	
						
						/*
						* struts进行表单字段命名的时候,形如user.name这样的命名
						*/
						for(var i = 0; i < fields.length; i++){
							for(var k = 0; k < fieldNames.length; k++){
								var tmpAttr = fieldNames[k];//字段名
								var attrValue = fieldValues[k];//字段值
								
								var tmp = $("#" + formId + " :" + fields[i] + "[name=" + tmpAttr + "]");
								if(tmp.length > 0){//如果是input型的表单字段
									switch (i) {
										case 3://如果是radio
											var temp = iterate(tmp, attrValue);//获得值为attrValue的radio对象
											if(temp){
												$(temp).attr("checked", "checked");
											}
										break;
										case 4://如果是checkbox
											var vals = [];
											if(attrValue && (attrValue + "").indexOf(",") != -1){
												vals = attrValue.split(",");	
											} else {//只有一个值
												vals[0] = attrValue
											}
											for(var j = 0; j < vals.length; j++){
												var temp = iterate(tmp, vals[j]);
												if(temp){
													$(temp).attr("checked", "checked");
												}
											}
										break;
										default://如果是text,hidden,password类型的,直接赋值
											tmp.val(attrValue);
									}
								}	else {//如果不是input型的表单字段,比如select
									$("#" + formId + " select[name=" + tmpAttr + "]").val(attrValue);
								}
							}
						}
				}
			}
	};
}