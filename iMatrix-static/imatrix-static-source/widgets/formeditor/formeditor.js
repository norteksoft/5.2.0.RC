//保存
KE.lang['save']="保存";
KE.plugin['save'] = {
		click : function(id) {
			editorSave(id);
		}
};
//返回
KE.lang['back']="返回";
KE.plugin['back'] = {
	click : function(id) {
		goBackForm("backForm",webRoot+ "/form/form.htm","form_main","page");
		KE.remove(id, 0);
	}
};
//预览
KE.lang['preview']="预览";
KE.plugin['preview'] = {
		
		click : function(id) {
			toPreview();
		}
	};
//单行文本
KE.lang['text']="文本框";
KE.plugin['text'] = {
		getSelectedNode : function(id) {
			var g = KE.g[id];  
			var startNode = g.keRange.startNode;  
			if (startNode.nodeType != 1) return;
			var pluginType=startNode.getAttribute("pluginType");
			if(typeof pluginType == "undefined")return;
			if(pluginType==null)return;
			if (pluginType.toLowerCase( )!="text"){
				return;
			}
			if (!startNode.className.match(/^ke-\w+/i)) return startNode;
		},
		init : function(id) {
			var self = this;
			var g = KE.g[id];
			g.contextmenuItems.push({
				text :"属性",
				click : function(id, menu) {
					menu.hide();
					var g = KE.g[id];  
		    		var startNode =g.keRange.startNode;
					KE.util.select(id);
					var classStyle=$(startNode).attr("class");
					var styleContent=$(startNode).attr("style");
					var event=$(startNode).attr("onclick");
					if(typeof classStyle == "undefined"||classStyle==null){
						classStyle="";
					}
					if(typeof styleContent == "undefined"||styleContent==null){
						styleContent="";
					}
					if(typeof event == "undefined"||event==null){
						event="";
					}
					var urlParam = "formControl.controlType="+$(startNode).attr("type").toUpperCase()
					+"&formControl.controlValue="+$(startNode).attr("value")
					+"&formControl.name="+$(startNode).attr("name")
					+"&formControl.title="+$(startNode).attr("title")
					+"&formControl.controlId="+$(startNode).attr("id")
					+"&formControl.format="+$(startNode).attr("format")
					+"&formControl.formatType="+$(startNode).attr("formatType")
					+"&formControl.formatTip="+$(startNode).attr("formatTip")
					+"&formControl.request="+$(startNode).attr("request")
					+"&formControl.styleContent="+styleContent 
					+"&formControl.classStyle="+classStyle  
					+"&formControl.clickEvent="+event
					+"&formControl.dataType="+$(startNode).attr("dataType").toUpperCase()
					+"&formControl.readOlny="+$(startNode).attr("readolny")
					+"&occasion="+"update";
					var tableColumnId=$(startNode).attr("columnId");
					var maxLen=$(startNode).attr("maxLen");
					if(typeof maxLen == "undefined"){
						maxLen="";
					}
					urlParam=urlParam
					+"&formControl.maxLength="+maxLen;
					if(typeof tableColumnId == "undefined" ||tableColumnId==""){
						urlParam = urlParam
						+"&standard=false"
						+"&TB_iframe=true&width=600&height=330&modal=true";
					}else{
						urlParam = urlParam
						+"&standard=true"
						+"&tableColumnId="+$(startNode).attr("columnId");
					}
					controlProperty(urlParam);
				},
				cond : function(id) {
					return self.getSelectedNode(id);
				}
			});
			g.contextmenuItems.push({
				text :"删除",
				click : function(id, menu) {
					if(confirm("确认删除该"+KE.lang['text']+"控件？")){
						KE.util.select(id);
						menu.hide();
						var img = self.getSelectedNode(id);
						img.parentNode.removeChild(img);
					}else{
						menu.hide();
					}
				},
				cond : function(id) {
					return self.getSelectedNode(id);
				}
			});
		},
		click : function(id) {
			controlClick(id,"TEXT");
		}
	};


//多行文本
KE.lang['textarea']="文本域";
KE.plugin['textarea'] = {
	
	getSelectedNode : function(id) {
		var g = KE.g[id];  
		var startNode = g.keRange.startNode;  
		if (startNode.nodeType != 1){
			startNode = startNode.parentNode;
		} 
		var pluginType=startNode.getAttribute("pluginType");
		if(typeof pluginType == "undefined")return;
		if(pluginType==null)return;
		if ( pluginType.toLowerCase( )!="textarea"){
			return;
		}
		
		if (!startNode.className.match(/^ke-\w+/i)) return startNode;
	},
	init : function(id) {
		var self = this;
		var g = KE.g[id];
		g.contextmenuItems.push({
			text :"属性",
			click : function(id, menu) {
				KE.util.select(id);
				menu.hide();
	    		var startNode = self.getSelectedNode(id);
	    		
	    		var classStyle=$(startNode).attr("class");
				var styleContent=$(startNode).attr("style");
				if(typeof classStyle == "undefined"||classStyle==null){
					classStyle="";
				}
				if(typeof styleContent == "undefined"||styleContent==null){
					styleContent="";
				}
	    		var urlParam = "formControl.controlType=TEXTAREA"
				+"&formControl.controlValue="+$(startNode).attr("value")
				+"&formControl.name="+$(startNode).attr("name")
				+"&formControl.title="+$(startNode).attr("title")
				+"&formControl.controlId="+$(startNode).attr("id")
				+"&formControl.styleContent="+styleContent 
				+"&formControl.classStyle="+classStyle  
				+"&occasion="+"update";
	    		
	    		var tableColumnId=$(startNode).attr("columnId");
				var maxLen=$(startNode).attr("maxLen");
				if(typeof maxLen == "undefined"){
					maxLen="";
				}
				urlParam=urlParam
				+"&formControl.maxLength="+maxLen;
				var dataType=$(startNode).attr("dataType");
				if(typeof dataType == "undefined"){
					dataType="TEXT";
				}
				urlParam=urlParam
				+"&formControl.dataType="+dataType;
				if(typeof tableColumnId == "undefined"){
					urlParam = urlParam
					+"&standard=false"
					+"&TB_iframe=true&width=600&height=300&modal=true";
				}else{
					urlParam = urlParam
					+"&standard=true"
					+"&tableColumnId="+$(startNode).attr("columnId");
				}
				controlProperty(urlParam);
			},
			cond : function(id) {
				return self.getSelectedNode(id);
			}
		});
		g.contextmenuItems.push({
			text : "删除",
			click : function(id, menu) {
				if(confirm("确认删除该"+KE.lang['textarea']+"控件？")){
					KE.util.select(id);
					menu.hide();
					var control = self.getSelectedNode(id);
					control.parentNode.removeChild(control);
				}else{
					menu.hide();
				}
			},
			cond : function(id) {
				return self.getSelectedNode(id);
			}
		});
	},
	click : function(id) {
		controlClick(id,"TEXTAREA");
	}
};

//日期控件
KE.lang['time']="日期时间控件";
KE.plugin['time'] = {
		
		getSelectedNode : function(id) {
			var g = KE.g[id];  
			var startNode = g.keRange.startNode;  
			if (startNode.nodeType != 1) return;
			var pluginType=startNode.getAttribute("pluginType");
			if(typeof pluginType == "undefined")return;
			if(pluginType==null)return;
			if (pluginType.toLowerCase( )!="time"){
				return;
			}
			if (!startNode.className.match(/^ke-\w+/i)) return startNode;
		},
		init : function(id) {
			var self = this;
			var g = KE.g[id];
			g.contextmenuItems.push({
				text :"属性",
				click : function(id, menu) {
					KE.util.select(id);
					menu.hide();
					var g = KE.g[id];  
					var startNode =g.keRange.startNode;
					
					var classStyle=$(startNode).attr("class");
					var styleContent=$(startNode).attr("style");
					if(typeof classStyle == "undefined"||classStyle==null){
						classStyle="";
					}
					if(typeof styleContent == "undefined"||styleContent==null){
						styleContent="";
					}
					var urlParam = "formControl.controlType=TIME"
						+"&formControl.controlValue="+$(startNode).attr("value")
						+"&formControl.name="+$(startNode).attr("name")
						+"&formControl.title="+$(startNode).attr("title")
						+"&formControl.controlId="+$(startNode).attr("id")
						+"&formControl.request="+$(startNode).attr("request")
						+"&formControl.styleContent="+styleContent 
						+"&formControl.classStyle="+classStyle  
						+"&formControl.dataType="+$(startNode).attr("dataType").toUpperCase()
						+"&occasion="+"update";
						
					var tableColumnId=$(startNode).attr("columnId");
					var maxLen=$(startNode).attr("maxLen");
					if(typeof maxLen == "undefined"){
						maxLen="";
					}
					urlParam=urlParam
					+"&formControl.maxLength="+maxLen;
					if(typeof tableColumnId == "undefined"){
						urlParam = urlParam
						+"&standard=false"
						+"&TB_iframe=true&width=600&height=300&modal=true";
					}else{
						urlParam = urlParam
						+"&standard=true"
						+"&tableColumnId="+$(startNode).attr("columnId");
					}
						controlProperty(urlParam);
				},
				cond : function(id) {
					return self.getSelectedNode(id);
				}
			});
			g.contextmenuItems.push({
				text :"删除",
				click : function(id, menu) {
					if(confirm("确认删除该"+KE.lang['time']+"控件？")){
						KE.util.select(id);
						menu.hide();
						var img = self.getSelectedNode(id);
						img.parentNode.removeChild(img);
					}else{
						menu.hide();
					}
				},
				cond : function(id) {
					return self.getSelectedNode(id);
				}
			});
		},
		click : function(id) {
			controlClick(id,"TIME");
		}
	};

//部门人员控件
KE.lang['SELECT_MAN_DEPT']="部门人员控件";
KE.plugin['SELECT_MAN_DEPT'] = {
		Ovalue :"",
		getSelectedNode : function(id) {
			var g = KE.g[id];  
			var startNode = g.keRange.startNode;  
			if (startNode.nodeType != 1) return;
			var pluginType=startNode.getAttribute("pluginType");
			if (typeof pluginType == "undefined"||pluginType!="SELECT_MAN_DEPT"){
				return;
			}
			if (!startNode.className.match(/^ke-\w+/i)) return startNode;
		},
		init : function(id) {
			var self = this;
			var g = KE.g[id];
			g.contextmenuItems.push({
				text :"属性",
				click : function(id, menu) {
					menu.hide();
					var g = KE.g[id];  
		    		var startNode =g.keRange.startNode;
					KE.util.select(id);
					var classStyle=$(startNode).attr("class");
					var styleContent=$(startNode).attr("style");
					if(typeof classStyle == "undefined"||classStyle==null){
						classStyle="";
					}
					if(typeof styleContent == "undefined"||styleContent==null){
						styleContent="";
					}
					controlDeptProperty($(startNode).attr("value"),$(startNode).attr("pluginType"),$(startNode).attr("resultName"),$(startNode).attr("resultId"),$(startNode).attr("inputType"),$(startNode).attr("hiddenResultName"),$(startNode).attr("hiddenId"),$(startNode).attr("treeType"),$(startNode).attr("selecttype"),styleContent,classStyle);
				},
				cond : function(id) {
					return self.getSelectedNode(id);
				}
			});
			g.contextmenuItems.push({
				text :"删除",
				click : function(id, menu) {
					if(confirm("确认删除该"+KE.lang['SELECT_MAN_DEPT']+"控件？")){
						KE.util.select(id);
						menu.hide();
						var img = self.getSelectedNode(id);
						img.parentNode.removeChild(img);
					}else{
						menu.hide();
					}
				},
				cond : function(id) {
					return self.getSelectedNode(id);
				}
			});
		},
		click : function(id) {
			controlClick(id,"SELECT_MAN_DEPT");
		}
	};

//计算控件
KE.lang['CALCULATE_COMPONENT']="计算控件";
KE.plugin['CALCULATE_COMPONENT'] = {
		Ovalue :"",
		getSelectedNode : function(id) {
			var g = KE.g[id];  
			var startNode = g.keRange.startNode;  
			if (startNode.nodeType != 1) return;
			var pluginType=startNode.getAttribute("pluginType");
			if (typeof pluginType == "undefined"||pluginType!="CALCULATE_COMPONENT"){
				return;
			}
			if (!startNode.className.match(/^ke-\w+/i)) return startNode;
		},
		init : function(id) {
			var self = this;
			var g = KE.g[id];
			g.contextmenuItems.push({
				text :"属性",
				click : function(id, menu) {
					menu.hide();
					var g = KE.g[id];  
		    		var startNode =g.keRange.startNode;
					KE.util.select(id);
					var classStyle=$(startNode).attr("class");
					var styleContent=$(startNode).attr("style");
					if(typeof classStyle == "undefined"||classStyle==null){
						classStyle="";
					}
					if(typeof styleContent == "undefined"||styleContent==null){
						styleContent="";
					}
					//tableColumnId,controlType,name,title,controlId,computational,precision,fontSize,componentWidth,componentHeight,maxlen
					var maxlen=$(startNode).attr("maxLen");
					if(typeof maxlen == "undefined"){
						maxlen="";
					}
					controlCalculateProperty($(startNode).attr("columnId"),$(startNode).attr("pluginType"),$(startNode).attr("name"),$(startNode).attr("title"),$(startNode).attr("id"),$(startNode).attr("computational"),$(startNode).attr("prec"),maxlen,styleContent,classStyle);
				},
				cond : function(id) {
					return self.getSelectedNode(id);
				}
			});
			g.contextmenuItems.push({
				text :"删除",
				click : function(id, menu) {
					if(confirm("确认删除该"+KE.lang['CALCULATE_COMPONENT']+"控件？")){
						KE.util.select(id);
						menu.hide();
						var img = self.getSelectedNode(id);
						img.parentNode.removeChild(img);
					}else{
						menu.hide();
					}
				},
				cond : function(id) {
					return self.getSelectedNode(id);
				}
			});
		},
		click : function(id) {
			controlClick(id,"CALCULATE_COMPONENT");
		}
	};

//下拉菜单
KE.lang['PULLDOWNMENU']="下拉菜单";
KE.plugin['PULLDOWNMENU'] = {
		Ovalue :"",
		getSelectedNode : function(id) {
			var g = KE.g[id];  
			var startNode = g.keRange.startNode;
			if(startNode.nodeType == 1){
				var pluginType = startNode.getAttribute("pluginType");
				if (typeof pluginType != "undefined" && pluginType=="PULLDOWNMENU"){
					return startNode;
				}
			}
			var parentStartNode=startNode.parentNode.parentNode;
			if(parentStartNode.nodeType == 1){
				var pluginType = parentStartNode.getAttribute("pluginType");
				if (typeof pluginType != "undefined" && pluginType=="PULLDOWNMENU"){
					return parentStartNode;
				}
			}
			return;
		},
		init : function(id) {
			var self = this;
			var g = KE.g[id];
			g.contextmenuItems.push({
				text :"属性",
				click : function(id, menu) {
					menu.hide();
					var g = KE.g[id];  
		    		var startNode =g.keRange.startNode;
		    		if(startNode.nodeName!="SELECT"){
						startNode=startNode.parentNode.parentNode;
					}
					KE.util.select(id);
					var selectValues="";
					for(var i=1;i<startNode.length;i++){
						if(selectValues==""){
							selectValues=startNode.options[i].text+";"+startNode.options[i].myvalue;
						}else{
							selectValues=selectValues+","+startNode.options[i].text+";"+startNode.options[i].myvalue;
						}
					}
					var classStyle=$(startNode).attr("class");
					var styleContent=$(startNode).attr("style");
					if(typeof classStyle == "undefined"||classStyle==null){
						classStyle="";
					}
					if(typeof styleContent == "undefined"||styleContent==null){
						styleContent="";
					}
					var child=startNode.getAttribute("child");
					if(typeof child=="undefined" ||child==null){
						child="";
					}
					controlPullDownMenuProperty($(startNode).attr("columnId"),$(startNode).attr("pluginType"),$(startNode).attr("name"),$(startNode).attr("title"),$(startNode).attr("id"),child,$(startNode).attr("sInit"),selectValues,$(startNode).attr("dataType"),styleContent,classStyle);
				},
				cond : function(id) {
					return self.getSelectedNode(id);
				}
			});
			g.contextmenuItems.push({
				text :"删除",
				click : function(id, menu) {
					if(confirm("确认删除该"+KE.lang['PULLDOWNMENU']+"控件？")){
						KE.util.select(id);
						menu.hide();
						var img = self.getSelectedNode(id);
						img.parentNode.removeChild(img);
					}else{
						menu.hide();
					}
				},
				cond : function(id) {
					return self.getSelectedNode(id);
				}
			});
		},
		click : function(id) {
			controlClick(id,"PULLDOWNMENU");
		}
	};

//数据选择控件
KE.lang['DATA_SELECTION']="数据选择控件";
KE.plugin['DATA_SELECTION'] = {
		Ovalue :"",
		getSelectedNode : function(id) {
			var g = KE.g[id];  
			var startNode = g.keRange.startNode;  
			if (startNode.nodeType != 1) return;
			var pluginType=startNode.getAttribute("pluginType");
			if (typeof pluginType == "undefined"||pluginType!="DATA_SELECTION"){
				return;
			}
			if (!startNode.className.match(/^ke-\w+/i)) return startNode;
		},
		init : function(id) {
			var self = this;
			var g = KE.g[id];
			g.contextmenuItems.push({
				text :"属性",
				click : function(id, menu) {
					menu.hide();
					var g = KE.g[id];  
		    		var startNode =g.keRange.startNode;
					KE.util.select(id);
					var data_query="";var data_control="";var data_fld_name="";var data_field="";
					if(startNode.getAttribute("data_fld_name")!=null)data_fld_name=startNode.getAttribute("data_fld_name");
					if(startNode.getAttribute("data_field")!=null)data_field=startNode.getAttribute("data_field");
					if(startNode.getAttribute("data_control")!=null)data_control=startNode.getAttribute("data_control");
					if(startNode.getAttribute("data_query")!=null)data_query=startNode.getAttribute("data_query");
					var data_querys=data_query.split(",");var data_controls=data_control.split(",");var data_fld_names=data_fld_name.split(",");var data_fields=data_field.split(",");
					for(var i=0;i<data_fields.length;i++){
						if(data_fields[i]!=""){
							var queryFlagDec=data_querys[i]=="1"?"是":"否";
							var str = "<tr><td >"+data_fields[i]+"</td><td >"+data_fld_names[i]+"</td><td >"+data_controls[i]+"</td><td >"+queryFlagDec+"</td><td ><a href=\"#\" onclick=\"del(this)\">删除</a></td></tr>";
							  $("#map_tbl").append(str);
						}
					}
					controlDataSelectionProperty($(startNode).attr("pluginType"),$(startNode).attr("value"),$(startNode).attr("id"),$(startNode).attr("data_table"),$(startNode).attr("data_table_name"),data_fld_name,data_field,data_control,data_query);
				},
				cond : function(id) {
					return self.getSelectedNode(id);
				}
			});
			g.contextmenuItems.push({
				text :"删除",
				click : function(id, menu) {
					if(confirm("确认删除该"+KE.lang['DATA_SELECTION']+"控件？")){
						KE.util.select(id);
						menu.hide();
						var img = self.getSelectedNode(id);
						img.parentNode.removeChild(img);
					}else{
						menu.hide();
					}
				},
				cond : function(id) {
					return self.getSelectedNode(id);
				}
			});
		},
		click : function(id) {
			controlClick(id,"DATA_SELECTION");
		}
	};

//数据获取控件
KE.lang['DATA_ACQUISITION']="数据获取控件";
KE.plugin['DATA_ACQUISITION'] = {
		Ovalue :"",
		getSelectedNode : function(id) {
			var g = KE.g[id];  
			var startNode = g.keRange.startNode;  
			if (startNode.nodeType != 1) return;
			var pluginType=startNode.getAttribute("pluginType");
			if (typeof pluginType == "undefined"||pluginType!="DATA_ACQUISITION"){
				return;
			}
			if (!startNode.className.match(/^ke-\w+/i)) return startNode;
		},
		init : function(id) {
			var self = this;
			var g = KE.g[id];
			g.contextmenuItems.push({
				text :"属性",
				click : function(id, menu) {
					menu.hide();
					var g = KE.g[id];  
		    		var startNode =g.keRange.startNode;
					KE.util.select(id);
					var data_control="";var data_fld_name="";var data_field="";
					if(startNode.getAttribute("data_fld_name")!=null)data_fld_name=startNode.getAttribute("data_fld_name");
					if(startNode.getAttribute("data_field")!=null)data_field=startNode.getAttribute("data_field");
					if(startNode.getAttribute("data_control")!=null)data_control=startNode.getAttribute("data_control");
					var data_controls=data_control.split(",");var data_fld_names=data_fld_name.split(",");var data_fields=data_field.split(",");
					for(var i=0;i<data_fields.length;i++){
						if(data_fields[i]!=""){
							var str = "<tr><td >"+data_fields[i]+"</td><td >"+data_fld_names[i]+"</td><td >"+data_controls[i]+"</td><td ><a href=\"#\" onclick=\"del(this)\">删除</a></td></tr>";
							  $("#map_tbl").append(str);
						}
					}
					controlDataAcquisitionProperty($(startNode).attr("pluginType"),$(startNode).attr("value"),$(startNode).attr("id"),$(startNode).attr("data_table"),$(startNode).attr("data_table_name"),data_fld_name,data_field,data_control,$(startNode).attr("data_ref_control"),$(startNode).attr("query_property"));
				},
				cond : function(id) {
					return self.getSelectedNode(id);
				}
			});
			g.contextmenuItems.push({
				text :"删除",
				click : function(id, menu) {
					if(confirm("确认删除该"+KE.lang['DATA_ACQUISITION']+"控件？")){
						KE.util.select(id);
						menu.hide();
						var img = self.getSelectedNode(id);
						img.parentNode.removeChild(img);
					}else{
						menu.hide();
					}
				},
				cond : function(id) {
					return self.getSelectedNode(id);
				}
			});
		},
		click : function(id) {
			controlClick(id,"DATA_ACQUISITION");
		}
	};

//紧急程度设置控件
KE.lang['URGENCY']="紧急程度设置控件";
KE.plugin['URGENCY'] = {
		
		getSelectedNode : function(id) {
			var g = KE.g[id];  
			var startNode = g.keRange.startNode;  
			if(startNode.nodeType == 1){
				var pluginType = startNode.getAttribute("pluginType");
				if (typeof pluginType != "undefined" && pluginType=="URGENCY"){
					return startNode;
				}
			}
			var parentStartNode=startNode.parentNode.parentNode;
			if(typeof parentStartNode != "undefined" && parentStartNode.nodeType == 1){
				var pluginType = parentStartNode.getAttribute("pluginType");
				if (typeof pluginType != "undefined" && pluginType=="URGENCY"){
					return parentStartNode;
				}
			}
			return;
		},
		init : function(id) {
			var self = this;
			var g = KE.g[id];
			g.contextmenuItems.push({
				text :"属性",
				click : function(id, menu) {
					menu.hide();
					var g = KE.g[id];  
		    		var startNode =g.keRange.startNode;
					KE.util.select(id);
					var g = KE.g[id];  
					var startNode =g.keRange.startNode;
					if(startNode.nodeName!="SELECT"){
						startNode=startNode.parentNode.parentNode;
					}
					var values="";
					var describes="";
					if(startNode.name!=undefined){
						for(var i=1;i<startNode.length;i++){
							if(values==""){
								values=startNode.options[i].value;
							}else{
								values=values+","+startNode.options[i].value;
							}
							if(describes==""){
								describes=startNode.options[i].text;
							}else{
								describes=describes+","+startNode.options[i].text;
							}
						}
					}
					controlUrgencyProperty($(startNode).attr("pluginType"),values,describes);
				},
				cond : function(id) {
					return self.getSelectedNode(id);
				}
			});
			g.contextmenuItems.push({
				text :"删除",
				click : function(id, menu) {
					if(confirm("确认删除该"+KE.lang['URGENCY']+"控件？")){
						KE.util.select(id);
						menu.hide();
						var img = self.getSelectedNode(id);
						img.parentNode.removeChild(img);
					}else{
						menu.hide();
					}
				},
				cond : function(id) {
					return self.getSelectedNode(id);
				}
			});
		},
		click : function(id) {
			KE.util.selection(id);
			var startNode =this.getSelectedNode(id);
			if($("#priority",KE.g[id].iframeDoc).length==0||($("#priority",KE.g[id].iframeDoc).length==1&&startNode&&startNode.name=='priority')&&((KE.g[id].range.htmlText!=''&&KE.g[id].range.text=='')||KE.g[id].range.length==1)){
				controlClick(id,"URGENCY");
			}else{
				alert("该控件只能添加一个");
			}
			
		}
	};

//特事特办控件
KE.lang['CREATE_SPECIAL_TASK']="特事特办控件";
KE.plugin['CREATE_SPECIAL_TASK'] = {
		getSelectedNode : function(id) {
			var g = KE.g[id];  
			var startNode = g.keRange.startNode;  
			if (startNode.nodeType != 1) return;
			var pluginType=startNode.getAttribute("pluginType");
			if (typeof pluginType == "undefined"||pluginType!="CREATE_SPECIAL_TASK"){
				return;
			}
			if (!startNode.className.match(/^ke-\w+/i)) return startNode;
		},
		init : function(id) {
			var self = this;
			var g = KE.g[id];
			g.contextmenuItems.push({
				text :"删除",
				click : function(id, menu) {
					if(confirm("确认删除该"+KE.lang['CREATE_SPECIAL_TASK']+"控件？")){
						KE.util.select(id);
						menu.hide();
						var img = self.getSelectedNode(id);
						img.parentNode.removeChild(img);
					}else{
						menu.hide();
					}
				},
				cond : function(id) {
					return self.getSelectedNode(id);
				}
			});
		},
		click : function(id) {
			KE.util.selection(id);
			var startNode =this.getSelectedNode(id);
			if($("input[name='CREATE_SPECIAL_TASK']",KE.g[id].iframeDoc).length==0||($("input[name='CREATE_SPECIAL_TASK']",KE.g[id].iframeDoc).length==1&&startNode&&startNode.name=='CREATE_SPECIAL_TASK'&&((KE.g[id].range.htmlText!=''&&KE.g[id].range.text=='')||KE.g[id].range.length==1))){
				controlClick(id,"CREATE_SPECIAL_TASK");
			}else{
				alert("该控件只能添加一个");
			}
		}
	};

//特事特办人员选择控件
KE.lang['SPECIAL_TASK_TRANSACTOR']="特事特办人员选择控件";
KE.plugin['SPECIAL_TASK_TRANSACTOR'] = {
		
		getSelectedNode : function(id) {
	var g = KE.g[id];  
	var startNode = g.keRange.startNode;  
	if (startNode.nodeType != 1) return;
	var pluginType=startNode.getAttribute("pluginType");
	if (typeof pluginType == "undefined"||pluginType!="SPECIAL_TASK_TRANSACTOR"){
		return;
	}
	if (!startNode.className.match(/^ke-\w+/i)) return startNode;
},
init : function(id) {
	var self = this;
	var g = KE.g[id];
	g.contextmenuItems.push({
		text :"删除",
		click : function(id, menu) {
			if(confirm("确认删除该"+KE.lang['SPECIAL_TASK_TRANSACTOR']+"控件？")){
				KE.util.select(id);
				menu.hide();
				var img = self.getSelectedNode(id);
				img.parentNode.removeChild(img);
			}else{
				menu.hide();
			}
		},
		cond : function(id) {
			return self.getSelectedNode(id);
		}
	});
},
click : function(id) {
	controlClick(id,"SPECIAL_TASK_TRANSACTOR");
}
};

//自定义列表控件
KE.lang['LIST_CONTROL']="自定义列表控件";
KE.plugin['LIST_CONTROL'] = {
		Ovalue :"",
		getSelectedNode : function(id) {
			var g = KE.g[id];  
			var startNode = g.keRange.startNode;  
			if (startNode.nodeType != 1) return;
			var pluginType=startNode.getAttribute("pluginType");
			if (typeof pluginType == "undefined"||pluginType!="LIST_CONTROL"){
				return;
			}
			if (!startNode.className.match(/^ke-\w+/i)) return startNode;
		},
		init : function(id) {
			var self = this;
			var g = KE.g[id];
			g.contextmenuItems.push({
				text :"属性",
				click : function(id, menu) {
					menu.hide();
					var g = KE.g[id];  
		    		var startNode =g.keRange.startNode;
					KE.util.select(id);
//					controlType,controlId,dataSrc,title,lcTitles,lcSums,lcSizes,lcCals,dataFields
					var lv_title="";var lv_size="";var lv_sum="";var lv_cal="";var lv_field="";
					if(startNode.getAttribute("lv_title")!=null)lv_title=startNode.getAttribute("lv_title");
					if(startNode.getAttribute("lv_size")!=null)lv_size=startNode.getAttribute("lv_size");
					if(startNode.getAttribute("lv_sum")!=null)lv_sum=startNode.getAttribute("lv_sum");
					if(startNode.getAttribute("lv_cal")!=null)lv_cal=startNode.getAttribute("lv_cal");
					if(startNode.getAttribute("lv_field")!=null)lv_field=startNode.getAttribute("lv_field");
					controlListControlProperty($(startNode).attr("pluginType"),$(startNode).attr("id"),$(startNode).attr("data_source"),$(startNode).attr("value"),
							lv_title,lv_sum,lv_size,lv_cal,lv_field);
				},
				cond : function(id) {
					return self.getSelectedNode(id);
				}
			});
			g.contextmenuItems.push({
				text :"删除",
				click : function(id, menu) {
					if(confirm("确认删除该"+KE.lang['LIST_CONTROL']+"控件？")){
						KE.util.select(id);
						menu.hide();
						var img = self.getSelectedNode(id);
						img.parentNode.removeChild(img);
					}else{
						menu.hide();
					}
				},
				cond : function(id) {
					return self.getSelectedNode(id);
				}
			});
		},
		click : function(id) {
			controlClick(id,"LIST_CONTROL");
		}
	};

//标准列表控件
KE.lang['STANDARD_LIST_CONTROL']="标准列表控件";
KE.plugin['STANDARD_LIST_CONTROL'] = {
		Ovalue :"",
		getSelectedNode : function(id) {
			var g = KE.g[id];  
			var startNode = g.keRange.startNode;  
			if (startNode.nodeType != 1) return;
			var pluginType=startNode.getAttribute("pluginType");
			if (typeof pluginType == "undefined"||pluginType!="STANDARD_LIST_CONTROL"){
				return;
			}
			if (!startNode.className.match(/^ke-\w+/i)) return startNode;
		},
		init : function(id) {
			var self = this;
			var g = KE.g[id];
			g.contextmenuItems.push({
				text :"属性",
				click : function(id, menu) {
					menu.hide();
					var g = KE.g[id];  
		    		var startNode =g.keRange.startNode;
					KE.util.select(id);
					controlStandardListControlProperty($(startNode).attr("pluginType"),$(startNode).attr("id"),$(startNode).attr("title"),
							$(startNode).attr("name"),$(startNode).attr("dataType"),$(startNode).attr("columnId"),$(startNode).attr("listviewid"));
				},
				cond : function(id) {
					return self.getSelectedNode(id);
				}
			});
			g.contextmenuItems.push({
				text :"删除",
				click : function(id, menu) {
					if(confirm("确认删除该"+KE.lang['STANDARD_LIST_CONTROL']+"控件？")){
						KE.util.select(id);
						menu.hide();
						var img = self.getSelectedNode(id);
						img.parentNode.removeChild(img);
					}else{
						menu.hide();
					}
				},
				cond : function(id) {
					return self.getSelectedNode(id);
				}
			});
		},
		click : function(id) {
			controlClick(id,"STANDARD_LIST_CONTROL");
		}
	};
//按钮控件
KE.lang['BUTTON']="按钮控件";
KE.plugin['BUTTON'] = {
		Ovalue :"",
		getSelectedNode : function(id) {
			var g = KE.g[id];  
			var startNode = g.keRange.startNode;  
			if (startNode.nodeType != 1) return;
			var pluginType=startNode.getAttribute("pluginType");
			if (typeof pluginType == "undefined"||pluginType!="BUTTON"){
				return;
			}
			if (!startNode.className.match(/^ke-\w+/i)) return startNode;
		},
		init : function(id) {
			var self = this;
			var g = KE.g[id];
			g.contextmenuItems.push({
				text :"属性",
				click : function(id, menu) {
					menu.hide();
					var g = KE.g[id];  
		    		var startNode =g.keRange.startNode;
					KE.util.select(id);
					var classStyle=$(startNode).attr("class");
					var styleContent=$(startNode).attr("style");
					var event=$(startNode).attr("onclick");
					if(typeof classStyle == "undefined"||classStyle==null){
						classStyle="";
					}
					if(typeof styleContent == "undefined"||styleContent==null){
						styleContent="";
					}
					if(typeof event == "undefined"||event==null){
						event="";
					}
					controlButtonProperty($(startNode).attr("pluginType"),$(startNode).attr("id"),classStyle,styleContent,
							$(startNode).attr("value"),event);
				},
				cond : function(id) {
					return self.getSelectedNode(id);
				}
			});
			g.contextmenuItems.push({
				text :"删除",
				click : function(id, menu) {
					if(confirm("确认删除该"+KE.lang['BUTTON']+"控件？")){
						KE.util.select(id);
						menu.hide();
						var img = self.getSelectedNode(id);
						img.parentNode.removeChild(img);
					}else{
						menu.hide();
					}
				},
				cond : function(id) {
					return self.getSelectedNode(id);
				}
			});
		},
		click : function(id) {
			controlClick(id,"BUTTON");
		}
	};