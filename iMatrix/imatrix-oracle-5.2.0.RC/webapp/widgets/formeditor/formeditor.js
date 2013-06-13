//保存
KindEditor.plugin('save', function(K) {
    var editor = this, name = 'save';
    // 点击图标时执行
    editor.clickToolbar(name, function() {
    	editorSave(editor);
    });
});
KindEditor.lang({save:'保存'});


//返回
KindEditor.plugin('back', function(K) {
    var editor = this, name = 'back';
    editor.clickToolbar(name, function() {
    	goBackForm("backForm",webRoot+ "/form/list-data.htm","form_main","page");
		KindEditor.remove("content");
    });
});
KindEditor.lang({back:'返回'});

//预览
KindEditor.plugin('preview', function(K) {
    var editor = this, name = 'preview';
    editor.clickToolbar(name, function() {
    	toPreview();
    });
});
KindEditor.lang({preview:'预览'});

function getMySelectNode(self){
	 var range = self.edit.cmd.range;
	 var sc = range.startContainer, so = range.startOffset;
	 var text = KindEditor(sc.childNodes[so]);
	 return text;
}

//单行文本
KindEditor.lang({text:"文本框"});
KindEditor.plugin('text', function(K) {
	 var self = this, name = 'text';
	 function shouldAddMenu() {
		 var text =getMySelectNode(self);
		 if (!text || text.name != 'input'||text.length<=0) {
			 return false;
		 }
		 var html = text.html();
		 if(text.length>=1&&text[0].getAttribute('pluginType').toUpperCase()=="TEXT"){
			 return true;
		 }
		 return false;
		
	}
	 self.addContextmenu({
		 title:'属性',
		 click:function(){
		 		self.hideMenu();
		 		var text =getMySelectNode(self);
	 			var startNode  =text[0];
				var classStyle=startNode.getAttribute("class");
				var styleContent=startNode.getAttribute("style");
				var event=startNode.getAttribute("onclick");
				var dbName=startNode.getAttribute("dbName");
				if(typeof classStyle == "undefined"||classStyle==null){
					classStyle="";
				}
				if(typeof styleContent == "undefined"||styleContent==null){
					styleContent="";
				}
				if(typeof event == "undefined"||event==null){
					event="";
				}
				if(typeof dbName == "undefined"||dbName==null){
					dbName="";
				}
				var value = startNode.getAttribute("value");
				var type = startNode.getAttribute("type");
				var urlParam = "formControl.controlType="+(type==null?"TEXT":type.toUpperCase())
				+"&formControl.controlValue="+(value==null?"":value)
				+"&formControl.name="+startNode.getAttribute("name")
				+"&formControl.dbName="+dbName
				+"&formControl.title="+startNode.getAttribute("title")
				+"&formControl.controlId="+startNode.getAttribute("id")
				+"&formControl.format="+startNode.getAttribute("format")
				+"&formControl.formatType="+startNode.getAttribute("formatType")
				+"&formControl.formatTip="+startNode.getAttribute("formatTip")
				+"&formControl.request="+startNode.getAttribute("request")
				+"&formControl.styleContent="+styleContent 
				+"&formControl.classStyle="+classStyle  
				+"&formControl.clickEvent="+event
				+"&formControl.dataType="+startNode.getAttribute("dataType").toUpperCase()
				+"&formControl.readOlny="+startNode.getAttribute("readolny")
				+"&occasion="+"update"
				+"&formControl.signatureVisible="+startNode.getAttribute("signatureVisible");
				var tableColumnId=startNode.getAttribute("columnId");
				var maxlength; 
				if(startNode.getAttribute("maxlen")!=undefined){
					maxlength = startNode.getAttribute("maxlen");
				}else{
					maxlength=startNode.getAttribute("maxlength");
				}
				if(typeof maxlength == "undefined"||maxlength==null){
					maxlength="";
				}
				urlParam=urlParam
				+"&formControl.maxLength="+maxlength;
				controlProperty(urlParam);
	 	 },
	 	 cond: function(){return shouldAddMenu();},
	 	 width:150
	 });
	 self.addContextmenu({
		 title:'删除',
		 click:function(){
			 if(confirm("确认删除该文本框控件？")){
					self.hideMenu();
					var text =getMySelectNode(self);
					text.remove();
				}else{
					self.hideMenu();
				}
		 },
		 cond: function(){return shouldAddMenu();},
		 width:150
	 });
	 self.clickToolbar(name, function() {
			controlClick("TEXT");
	 });
	});


//多行文本
KindEditor.lang({textarea:"文本域"});
KindEditor.plugin('textarea', function(K) {
	var self = this, name = 'textarea';
	function shouldAddMenu() {
		 var text =getMySelectNode(self);
		 if (!text || text.name != 'textarea'||text.length<=0) {
			 return false;
		 }
		 var html = text.html();
		 if(text.length>=1&&text[0].getAttribute('pluginType').toUpperCase( )=="TEXTAREA"){
			 return true;
		 }
		 return false;
		
	}
	self.addContextmenu({
		 title:'属性',
		 click:function(){
		 		self.hideMenu();
		 		var text =getMySelectNode(self);
	 			var startNode  =text[0];
	 			var classStyle=startNode.getAttribute("class");
				var styleContent=startNode.getAttribute("style");
				var dbName=startNode.getAttribute("dbName");
				if(typeof classStyle == "undefined"||classStyle==null){
					classStyle="";
				}
				if(typeof styleContent == "undefined"||styleContent==null){
					styleContent="";
				}
				if(typeof dbName == "undefined"||dbName==null){
					dbName="";
				}
				var value = startNode.getAttribute("defaultValue");
	    		var urlParam = "formControl.controlType=TEXTAREA"
				+"&formControl.controlValue="+(value==null?"":value)
				+"&formControl.name="+startNode.getAttribute("name")
				+"&formControl.dbName="+dbName
				+"&formControl.title="+startNode.getAttribute("title")
				+"&formControl.controlId="+startNode.getAttribute("id")
				+"&formControl.styleContent="+styleContent 
				+"&formControl.classStyle="+classStyle  
				+"&occasion="+"update";
	    		
	    		var tableColumnId=startNode.getAttribute("columnId");
	    		
				var maxlength;
				if(startNode.getAttribute("maxlen")!=undefined){
					maxlength=startNode.getAttribute("maxlen");
				}else{
					maxlength=startNode.getAttribute("maxlength");
				}
				if(typeof maxlength == "undefined"||maxlength==null){
					maxlength="";
				}
				urlParam=urlParam
				+"&formControl.maxLength="+maxlength;
				var dataType=startNode.getAttribute("dataType");
				if(typeof dataType == "undefined"){
					dataType="TEXT";
				}
				urlParam=urlParam
				+"&formControl.dataType="+dataType; 
				controlProperty(urlParam);
	 	 },
	 	 cond: function(){return shouldAddMenu();},
	 	 width:150
	 });
	 self.addContextmenu({
		 title:'删除',
		 click:function(){
			 if(confirm("确认删除该文本域控件？")){
					self.hideMenu();
					var text =getMySelectNode(self);
					text.remove();
				}else{
					self.hideMenu();
				}
		 },
		 cond: function(){return shouldAddMenu();},
		 width:150
	 });
	 self.clickToolbar(name, function() {
		 controlClick("TEXTAREA");
	 });
});

//日期控件
KindEditor.lang({time:"日期时间控件"});
KindEditor.plugin('time', function(K) {
		var self = this, name = 'time';
		function shouldAddMenu() {
			 var text =getMySelectNode(self);
			 if (!text || text.name != 'input'||text.length<=0) {
				 return false;
			 }
			 var html = text.html();
			 if(text.length>=1&&text[0].getAttribute('pluginType').toUpperCase( )=="TIME"){
				 return true;
			 }
			 return false;
			
		}
		self.addContextmenu({
			 title:'属性',
			 click:function(){
			 		self.hideMenu();
			 		var text =getMySelectNode(self);
		 			var startNode  =text[0];
		 			
		 			var classStyle=startNode.getAttribute("class");
					var styleContent=startNode.getAttribute("style");
					var dbName=startNode.getAttribute("dbName");
					if(typeof classStyle == "undefined"||classStyle==null){
						classStyle="";
					}
					if(typeof styleContent == "undefined"||styleContent==null){
						styleContent="";
					}
					if(typeof dbName == "undefined"||dbName==null){
						dbName="";
					}
					var value = startNode.getAttribute("value");
					var urlParam = "formControl.controlType=TIME"
						+"&formControl.controlValue="+(value==null?"":value)
						+"&formControl.name="+startNode.getAttribute("name")
						+"&formControl.dbName="+dbName
						+"&formControl.title="+startNode.getAttribute("title")
						+"&formControl.controlId="+startNode.getAttribute("id")
						+"&formControl.request="+startNode.getAttribute("request")
						+"&formControl.styleContent="+styleContent 
						+"&formControl.classStyle="+classStyle  
						+"&formControl.dataType="+startNode.getAttribute("dataType").toUpperCase()
						+"&occasion="+"update";
						
					var tableColumnId=startNode.getAttribute("columnId");
					controlProperty(urlParam);
		 	 },
		 	 cond: function(){return shouldAddMenu();},
		 	 width:150
		 });
		 self.addContextmenu({
			 title:'删除',
			 click:function(){
				 if(confirm("确认删除该日期时间控件？")){
						self.hideMenu();
						var text =getMySelectNode(self);
						text.remove();
					}else{
						self.hideMenu();
					}
			 },
			 cond: function(){return shouldAddMenu();},
			 width:150
		 });
		 self.clickToolbar(name, function() {
			 controlClick("TIME");
		 });
		
	});

//部门人员控件
KindEditor.lang({SELECT_MAN_DEPT:"部门人员控件"});
KindEditor.plugin('SELECT_MAN_DEPT', function(K) {
		
		var self = this, name = 'SELECT_MAN_DEPT';
		function shouldAddMenu() {
			 var text =getMySelectNode(self);
			 if (!text || text.name != 'input'||text.length<=0) {
				 return false;
			 }
			 var html = text.html();
			 if(text.length>=1&&text[0].getAttribute('pluginType').toUpperCase( )=="SELECT_MAN_DEPT"){
				 return true;
			 }
			 return false;
			
		}
		self.addContextmenu({
			 title:'属性',
			 click:function(){
			 		self.hideMenu();
			 		var text =getMySelectNode(self);
		 			var startNode  =text[0];
		 			
		 			var classStyle=startNode.getAttribute("class");
					var styleContent=startNode.getAttribute("style");
					if(typeof classStyle == "undefined"||classStyle==null){
						classStyle="";
					}
					if(typeof styleContent == "undefined"||styleContent==null){
						styleContent="";
					}
					controlDeptProperty(startNode.getAttribute("value"),startNode.getAttribute("pluginType"),startNode.getAttribute("resultName"),startNode.getAttribute("resultId"),startNode.getAttribute("inputType"),startNode.getAttribute("hiddenResultName"),startNode.getAttribute("hiddenId"),startNode.getAttribute("treeType"),startNode.getAttribute("selecttype"),styleContent,classStyle,startNode.getAttribute("dbName"));
		 	 },
		 	 cond: function(){return shouldAddMenu();},
		 	 width:150
		 });
		 self.addContextmenu({
			 title:'删除',
			 click:function(){
				 if(confirm("确认删除该日期时间控件？")){
						self.hideMenu();
						var text =getMySelectNode(self);
						text.remove();
					}else{
						self.hideMenu();
					}
			 },
			 cond: function(){return shouldAddMenu();},
			 width:150
		 });
		 self.clickToolbar(name, function() {
			 controlClick("SELECT_MAN_DEPT");
		 });
	});

//计算控件
KindEditor.lang({CALCULATE_COMPONENT:"计算控件"});
KindEditor.plugin('CALCULATE_COMPONENT', function(K) {
	var self = this, name = 'CALCULATE_COMPONENT';
	function shouldAddMenu() {
		 var text =getMySelectNode(self);
		 if (!text || text.name != 'input'||text.length<=0) {
			 return false;
		 }
		 var html = text.html();
		 if(text.length>=1&&text[0].getAttribute('pluginType').toUpperCase( )=="CALCULATE_COMPONENT"){
			 return true;
		 }
		 return false;
		
	}
	self.addContextmenu({
		 title:'属性',
		 click:function(){
		 		self.hideMenu();
		 		var text =getMySelectNode(self);
	 			var startNode  =text[0];
	 			
	 			var classStyle=startNode.getAttribute("class");
	 			var dataType=startNode.getAttribute("dataType");
				var styleContent=startNode.getAttribute("style");
				if(typeof classStyle == "undefined"||classStyle==null){
					classStyle="";
				}
				if(typeof styleContent == "undefined"||styleContent==null){
					styleContent="";
				}
				//tableColumnId,controlType,name,title,controlId,computational,precision,fontSize,componentWidth,componentHeight,maxlen
				var maxlength;
				if(startNode.getAttribute("maxlen")!=undefined){
					maxlength=startNode.getAttribute("maxlen");
				}else{
					maxlength=startNode.getAttribute("maxlength");
				}
				if(typeof maxlength == "undefined"||maxlength==null){
					maxlength="";
				}
				controlCalculateProperty(startNode.getAttribute("columnId"),startNode.getAttribute("pluginType"),startNode.getAttribute("name"),startNode.getAttribute("title"),startNode.getAttribute("id"),startNode.getAttribute("computational"),startNode.getAttribute("prec"),maxlength,styleContent,classStyle,startNode.getAttribute("dbName"),dataType);
			},
	 	 cond: function(){return shouldAddMenu();},
	 	 width:150
	 });
	 self.addContextmenu({
		 title:'删除',
		 click:function(){
			 if(confirm("确认删除该计算控件？")){
					self.hideMenu();
					var text =getMySelectNode(self);
					text.remove();
				}else{
					self.hideMenu();
				}
		 },
		 cond: function(){return shouldAddMenu();},
		 width:150
	 });
	 self.clickToolbar(name, function() {
		 controlClick("CALCULATE_COMPONENT");
	 });
});

//下拉菜单
KindEditor.lang({PULLDOWNMENU:"下拉菜单"});
KindEditor.plugin('PULLDOWNMENU', function(K) {
	var self = this, name = 'PULLDOWNMENU';
	function shouldAddMenu() {
		 var text =getMySelectNode(self);
		 if (!text || text.name != 'select'||text.length<=0) {
			 return false;
		 }
		 var html = text.html();
		 if(text.length>=1&&text[0].getAttribute('pluginType').toUpperCase( )=="PULLDOWNMENU"){
			 return true;
		 }
		 return false;
	}
	self.addContextmenu({
		 title:'属性',
		 click:function(){
		 		self.hideMenu();
		 		var text =getMySelectNode(self);
	 			var startNode  =text[0];
	 			
	 			var selectValues="";
				for(var i=1;i<startNode.length;i++){
					if(selectValues==""){
						selectValues=startNode.options[i].text+";"+startNode.options[i].myvalue;
					}else{
						selectValues=selectValues+","+startNode.options[i].text+";"+startNode.options[i].myvalue;
					}
				}
				var classStyle=startNode.getAttribute("class");
				var styleContent=startNode.getAttribute("style");
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
				controlPullDownMenuProperty(startNode.getAttribute("columnId"),startNode.getAttribute("pluginType"),startNode.getAttribute("name"),startNode.getAttribute("title"),startNode.getAttribute("id"),child,startNode.getAttribute("sInit"),selectValues,startNode.getAttribute("dataType"),styleContent,classStyle,startNode.getAttribute("dbName"));
			},
	 	 cond: function(){return shouldAddMenu();},
	 	 width:150
	 });
	 self.addContextmenu({
		 title:'删除',
		 click:function(){
			 if(confirm("确认删除该下拉菜单控件？")){
					self.hideMenu();
					var text =getMySelectNode(self);
					text.remove();
				}else{
					self.hideMenu();
				}
		 },
		 cond: function(){return shouldAddMenu();},
		 width:150
	 });
	 self.clickToolbar(name, function() {
		 controlClick("PULLDOWNMENU");
	 });
});

//数据选择控件
KindEditor.lang({DATA_SELECTION:"数据选择控件"});
KindEditor.plugin('DATA_SELECTION', function(K) {
	var self = this, name = 'DATA_SELECTION';
	function shouldAddMenu() {
		 var text =getMySelectNode(self);
		 if (!text || text.name != 'input'||text.length<=0) {
			 return false;
		 }
		 var html = text.html();
		 if(text.length>=1&&text[0].getAttribute('pluginType').toUpperCase( )=="DATA_SELECTION"){
			 return true;
		 }
		 return false;
	}
	self.addContextmenu({
		 title:'属性',
		 click:function(){
		 		self.hideMenu();
		 		var text =getMySelectNode(self);
	 			var startNode  =text[0];
	 			
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
				controlDataSelectionProperty(startNode.getAttribute("pluginType"),startNode.getAttribute("value"),startNode.getAttribute("id"),startNode.getAttribute("data_table"),startNode.getAttribute("data_table_name"),data_fld_name,data_field,data_control,data_query);
			},
	 	 cond: function(){return shouldAddMenu();},
	 	 width:150
	 });
	 self.addContextmenu({
		 title:'删除',
		 click:function(){
			 if(confirm("确认删除该数据选择控件？")){
					self.hideMenu();
					var text =getMySelectNode(self);
					text.remove();
				}else{
					self.hideMenu();
				}
		 },
		 cond: function(){return shouldAddMenu();},
		 width:150
	 });
	 self.clickToolbar(name, function() {
		 controlClick("DATA_SELECTION");
	 });
});

//数据获取控件
KindEditor.lang({DATA_ACQUISITION:"数据获取控件"});
KindEditor.plugin('DATA_ACQUISITION', function(K) {
	var self = this, name = 'DATA_ACQUISITION';
	function shouldAddMenu() {
		 var text =getMySelectNode(self);
		 if (!text || text.name != 'input'||text.length<=0) {
			 return false;
		 }
		 var html = text.html();
		 if(text.length>=1&&text[0].getAttribute('pluginType').toUpperCase( )=="DATA_ACQUISITION"){
			 return true;
		 }
		 return false;
	}
	self.addContextmenu({
		 title:'属性',
		 click:function(){
		 		self.hideMenu();
		 		var text =getMySelectNode(self);
	 			var startNode  =text[0];
	 			
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
				controlDataAcquisitionProperty(startNode.getAttribute("pluginType"),startNode.getAttribute("value"),startNode.getAttribute("id"),startNode.getAttribute("data_table"),startNode.getAttribute("data_table_name"),data_fld_name,data_field,data_control,startNode.getAttribute("data_ref_control"),startNode.getAttribute("query_property"));
			},
	 	 cond: function(){return shouldAddMenu();},
	 	 width:150
	 });
	 self.addContextmenu({
		 title:'删除',
		 click:function(){
			 if(confirm("确认删除该数据选择控件？")){
					self.hideMenu();
					var text =getMySelectNode(self);
					text.remove();
				}else{
					self.hideMenu();
				}
		 },
		 cond: function(){return shouldAddMenu();},
		 width:150
	 });
	 self.clickToolbar(name, function() {
		 controlClick("DATA_ACQUISITION");
	 });
});

//紧急程度设置控件
KindEditor.lang({URGENCY:"紧急程度设置控件"});
KindEditor.plugin('URGENCY', function(K) {
	var self = this, name = 'URGENCY';
	function shouldAddMenu() {
		 var text =getMySelectNode(self);
		 if (!text || text.name != 'select'||text.length<=0) {
			 return false;
		 }
		 var html = text.html();
		 if(text.length>=1&&text[0].getAttribute('pluginType').toUpperCase( )=="URGENCY"){
			 return true;
		 }
		 return false;
	}
	self.addContextmenu({
		 title:'属性',
		 click:function(){
		 		self.hideMenu();
		 		var text =getMySelectNode(self);
	 			var startNode  =text[0];
	 			
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
				controlUrgencyProperty(startNode.getAttribute("pluginType"),values,describes);
			},
	 	 cond: function(){return shouldAddMenu();},
	 	 width:150
	 });
	 self.addContextmenu({
		 title:'删除',
		 click:function(){
			 if(confirm("确认删除该紧急程度设置控件？")){
					self.hideMenu();
					var text =getMySelectNode(self);
					text.remove();
				}else{
					self.hideMenu();
				}
		 },
		 cond: function(){return shouldAddMenu();},
		 width:150
	 });
	 self.clickToolbar(name, function() {
		 var html = editor.html();
		 if(html.indexOf("plugintype=\"URGENCY\"")<=-1){//不包含URGENCY控件
			 controlClick("URGENCY");
		}else{
			alert("该控件只能添加一个");
		}
	 });	
});

//特事特办控件
KindEditor.lang({CREATE_SPECIAL_TASK:"特事特办控件"});
KindEditor.plugin('CREATE_SPECIAL_TASK', function(K) {
	var self = this, name = 'CREATE_SPECIAL_TASK';
	function shouldAddMenu() {
		 var text =getMySelectNode(self);
		 if (!text || text.name != 'input'||text.length<=0) {
			 return false;
		 }
		 var html = text.html();
		 if(text.length>=1&&text[0].getAttribute('pluginType').toUpperCase( )=="CREATE_SPECIAL_TASK"){
			 return true;
		 }
		 return false;
	}
	 self.addContextmenu({
		 title:'删除',
		 click:function(){
			 if(confirm("确认删除该特事特办控件？")){
					self.hideMenu();
					var text =getMySelectNode(self);
					text.remove();
				}else{
					self.hideMenu();
				}
		 },
		 cond: function(){return shouldAddMenu();},
		 width:150
	 });
	 self.clickToolbar(name, function() {
		 var html = editor.html();
		 if(html.indexOf("plugintype=\"CREATE_SPECIAL_TASK\"")<=-1){//不包含URGENCY控件
			 controlClick("CREATE_SPECIAL_TASK");
		 }else{
			alert("该控件只能添加一个");
		 }
	 });	
});

//特事特办人员选择控件
KindEditor.lang({SPECIAL_TASK_TRANSACTOR:"特事特办人员选择控件"});
KindEditor.plugin('SPECIAL_TASK_TRANSACTOR', function(K) {
	var self = this, name = 'SPECIAL_TASK_TRANSACTOR';
	function shouldAddMenu() {
		 var text =getMySelectNode(self);
		 if (!text || text.name != 'input'||text.length<=0) {
			 return false;
		 }
		 var html = text.html();
		 if(text.length>=1&&text[0].getAttribute('pluginType').toUpperCase( )=="SPECIAL_TASK_TRANSACTOR"){
			 return true;
		 }
		 return false;
	}
	 self.addContextmenu({
		 title:'删除',
		 click:function(){
			 if(confirm("确认删除该特事特办人员选择控件？")){
					self.hideMenu();
					var text =getMySelectNode(self);
					text.remove();
				}else{
					self.hideMenu();
				}
		 },
		 cond: function(){return shouldAddMenu();},
		 width:150
	 });
	 self.clickToolbar(name, function() {
		 controlClick("SPECIAL_TASK_TRANSACTOR");
	 });	
	
});

//自定义列表控件
KindEditor.lang({LIST_CONTROL:"自定义列表控件"});
KindEditor.plugin('LIST_CONTROL', function(K) {
	var self = this, name = 'LIST_CONTROL';
	function shouldAddMenu() {
		 var text =getMySelectNode(self);
		 if (!text || text.name != 'input'||text.length<=0) {
			 return false;
		 }
		 var html = text.html();
		 if(text.length>=1&&text[0].getAttribute('pluginType').toUpperCase( )=="LIST_CONTROL"){
			 return true;
		 }
		 return false;
	}
	self.addContextmenu({
		 title:'属性',
		 click:function(){
		 		self.hideMenu();
		 		var text =getMySelectNode(self);
	 			var startNode  =text[0];
	 			
	 			var lv_title="";var lv_size="";var lv_sum="";var lv_cal="";var lv_field="";
				if(startNode.getAttribute("lv_title")!=null)lv_title=startNode.getAttribute("lv_title");
				if(startNode.getAttribute("lv_size")!=null)lv_size=startNode.getAttribute("lv_size");
				if(startNode.getAttribute("lv_sum")!=null)lv_sum=startNode.getAttribute("lv_sum");
				if(startNode.getAttribute("lv_cal")!=null)lv_cal=startNode.getAttribute("lv_cal");
				if(startNode.getAttribute("lv_field")!=null)lv_field=startNode.getAttribute("lv_field");
				controlListControlProperty(startNode.getAttribute("pluginType"),startNode.getAttribute("id"),startNode.getAttribute("data_source"),startNode.getAttribute("value"),
						lv_title,lv_sum,lv_size,lv_cal,lv_field);
			},
	 	 cond: function(){return shouldAddMenu();},
	 	 width:150
	 });
	 self.addContextmenu({
		 title:'删除',
		 click:function(){
			 if(confirm("确认删除该自定义列表控件？")){
					self.hideMenu();
					var text =getMySelectNode(self);
					text.remove();
				}else{
					self.hideMenu();
				}
		 },
		 cond: function(){return shouldAddMenu();},
		 width:150
	 });
	 self.clickToolbar(name, function() {
			controlClick("LIST_CONTROL");
	 });	
});

//标准列表控件
KindEditor.lang({STANDARD_LIST_CONTROL:"标准列表控件"});
KindEditor.plugin('STANDARD_LIST_CONTROL', function(K) {
	var self = this, name = 'STANDARD_LIST_CONTROL';
	function shouldAddMenu() {
		 var text =getMySelectNode(self);
		 if (!text || text.name != 'input'||text.length<=0) {
			 return false;
		 }
		 var html = text.html();
		 if(text.length>=1&&text[0].getAttribute('pluginType').toUpperCase( )=="STANDARD_LIST_CONTROL"){
			 return true;
		 }
		 return false;
	}
	self.addContextmenu({
		 title:'属性',
		 click:function(){
		 		self.hideMenu();
		 		var text =getMySelectNode(self);
	 			var startNode  =text[0];
	 			
	 			controlStandardListControlProperty(startNode.getAttribute("pluginType"),startNode.getAttribute("id"),startNode.getAttribute("title"),
						startNode.getAttribute("name"),startNode.getAttribute("dataType"),startNode.getAttribute("columnId"),startNode.getAttribute("listviewcode"),startNode.getAttribute("dbName"));
			},
	 	 cond: function(){return shouldAddMenu();},
	 	 width:150
	 });
	 self.addContextmenu({
		 title:'删除',
		 click:function(){
			 if(confirm("确认删除该标准列表控件？")){
					self.hideMenu();
					var text =getMySelectNode(self);
					text.remove();
				}else{
					self.hideMenu();
				}
		 },
		 cond: function(){return shouldAddMenu();},
		 width:150
	 });
	 self.clickToolbar(name, function() {
			controlClick("STANDARD_LIST_CONTROL");
	 });	
});
//按钮控件
KindEditor.lang({BUTTON:"按钮控件"});
KindEditor.plugin('BUTTON', function(K) {
	var self = this, name = 'BUTTON';
	function shouldAddMenu() {
		 var text =getMySelectNode(self);
		 if (!text || text.name != 'input'||text.length<=0) {
			 return false;
		 }
		 var html = text.html();
		 if(text.length>=1&&text[0].getAttribute('pluginType').toUpperCase( )=="BUTTON"){
			 return true;
		 }
		 return false;
	}
	self.addContextmenu({
		 title:'属性',
		 click:function(){
		 		self.hideMenu();
		 		var text =getMySelectNode(self);
	 			var startNode  =text[0];
	 			
	 			var classStyle=startNode.getAttribute("class");
				var styleContent=startNode.getAttribute("style");
				var event=startNode.getAttribute("fun");
				var resultid = startNode.getAttribute("hiddenid");
				if(typeof classStyle == "undefined"||classStyle==null){
					classStyle="";
				}
				if(typeof styleContent == "undefined"||styleContent==null){
					styleContent="";
				}
				if(typeof event == "undefined"||event==null){
					event="";
				}
				if(typeof resultid == "undefined"||resultid==null){
					resultid="";
				}
				
				controlButtonProperty(startNode.getAttribute("pluginType"),startNode.getAttribute("id"),classStyle,styleContent,
						startNode.getAttribute("value"),event,resultid);
			},
	 	 cond: function(){return shouldAddMenu();},
	 	 width:150
	 });
	 self.addContextmenu({
		 title:'删除',
		 click:function(){
			 if(confirm("确认删除该按钮控件？")){
					self.hideMenu();
					var text =getMySelectNode(self);
					text.remove();
				}else{
					self.hideMenu();
				}
		 },
		 cond: function(){return shouldAddMenu();},
		 width:150
	 });
	 self.clickToolbar(name, function() {
			controlClick("BUTTON");
	 });	
	
});
//标签控件
KindEditor.lang({LABEL:"标签控件"});
KindEditor.plugin('LABEL', function(K) {
	var self = this, name = 'LABEL';
	function shouldAddMenu() {
		 var text =getMySelectNode(self);
		 if (!text || text.name != 'input'||text.length<=0) {
			 return false;
		 }
		 var html = text.html();
		 if(text.length>=1&&text[0].getAttribute('pluginType').toUpperCase( )=="LABEL"){
			 return true;
		 }
		 return false;
	}
	self.addContextmenu({
		 title:'属性',
		 click:function(){
		 		self.hideMenu();
		 		var text =getMySelectNode(self);
	 			var startNode = text[0];
	 			
	 			var classStyle=startNode.getAttribute("class");
				var styleContent=startNode.getAttribute("style");
				if(typeof classStyle == "undefined"||classStyle==null){
					classStyle="";
				}
				if(typeof styleContent == "undefined"||styleContent==null){
					styleContent="";
				}
				controlLabelProperty(startNode.getAttribute("pluginType"),startNode.getAttribute("id"),classStyle,styleContent,
						startNode.getAttribute("value"),startNode.getAttribute("printable"));
			},
	 	 cond: function(){return shouldAddMenu();},
	 	 width:150
	 });
	 self.addContextmenu({
		 title:'删除',
		 click:function(){
			 if(confirm("确认删除该按钮控件？")){
					self.hideMenu();
					var text =getMySelectNode(self);
					text.remove();
				}else{
					self.hideMenu();
				}
		 },
		 cond: function(){return shouldAddMenu();},
		 width:150
	 });
	 self.clickToolbar(name, function() {
			controlClick("LABEL");
	 });	
	
});