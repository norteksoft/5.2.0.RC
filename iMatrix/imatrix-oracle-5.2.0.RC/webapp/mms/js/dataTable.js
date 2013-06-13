/***************************
		辅助的JS脚本
 ***************************/
//通用ajaxAnywhere提交
function ajaxSubmit(form, url, zoons, ajaxCallback){
	var formId = "#"+form;
	if(url != ""){
		$(formId).attr("action", url);
	}
	ajaxAnywhere.formName = form;
	ajaxAnywhere.getZonesToReload = function() {
		return zoons;
	};
	ajaxAnywhere.onAfterResponseProcessing = function () {
		if(typeof(ajaxCallback) == "function"){
			ajaxCallback();
		}
	};
	ajaxAnywhere.submitAJAX();
}


//创建一个隐藏域
function createHiddenInput(name, value){
	return "<input type=\"hidden\" name=\""+name+"\" value=\""+value+"\"/>";
}
/***************************
	   数据表页面JS脚本
 ***************************/
//新建一个数据表
function createNewTable(url, callBack){
	ajaxSubmit("contentFrom",url,"dataTableContent",callBack);
}

//新建回调
function createCallBack(){
	HideSearchBox();
	if($("#tabelId").val() == ''){
		validataTableName();
		validataEntityName();
	}else{
		$("#tableName").attr("readonly","readonly");
		$("#entityName").attr("readonly","readonly");
		var inputs = $("input[name='existedTable']");
		for(var i=0; i<inputs.length; i++){
			$(inputs[i]).removeAttr("onclick");
			$(inputs[i]).attr("disabled","disabled");
		}
	}
	formValidate();
}

//表单验证
function formValidate(){
	$("#inputForm").validate({
		submitHandler: function() {
			saveByAjax();
		},
		rules: {
			name:"required",
			alias: "required",
			entityName: "required",
			remark:{
				maxlength:500
			}
		},
		messages: {
			name:"必填",
			alias: "必填",
			entityName: "必填",
			remark:{
				maxlength:"最多输入500字"
			}
		}
	});
}

//验证调用的检验方法
function check(value){
	if(value.indexOf("\"")>=0 || value.indexOf("\'")>=0){
		return "不能包含双引号或单引号";
	}else{
		return true;
	}
}

//单独验证数据表名
function validataTableName(){
	$("#tableNameTip").attr('class', 'onShow');
	$("#tableNameTip").html("请输入");
	$("#tableName").click(function(){
		$("#tableNameTip").attr('class', 'onFocus');
		$("#tableNameTip").html("表名必须唯一");
	});
}

//单独验证实体名
function validataEntityName(){
	$("#entityNameTip").attr('class', 'onShow');
	$("#entityNameTip").html("请输入");
	$("#entityName").click(function(){
		$("#entityNameTip").attr('class', 'onFocus');
		$("#entityNameTip").html("请输入");
	});
}

//修改一个数据表信息
function changeTableInfo(url,callback){
	var ids = jQuery("#dataTables").getGridParam('selarrrow');
	if(ids==""){
		showMessage("message", "<font color=\"red\">请选择一条数据</font>");
	}else if(ids.length > 1){
		showMessage("message", "<font color=\"red\">只能选择一条数据</font>");
	}else if(ids.length == 1){
		$("#contentFrom").append(createHiddenInput("tableId", ids[0]));
		createNewTable(url, callback);
	}
}

function viewCustomDataTable(ts1,cellval,opts,rwdat,_act){
	var v="<a  href=\"#\" hidefocus=\"true\" onclick=\"viewDataTable("+opts.id+");\">" + ts1 + "</a>";
	return v;
}

function viewDataTable(id){
	$("#contentFrom").append(createHiddenInput("tableId", id));
	ajaxSubmit("contentFrom",webRoot+"/form/data-table-viewCustom.htm","dataTableContent",createCallBack);
}

//删除一个数据表信息
function deleteTableInfo(url){
	var ids = jQuery("#dataTables").getGridParam('selarrrow');
	if(ids==''){
		showMessage("message", "<font color=\"red\">请选择一条数据</font>");
	}else{
		var canPost = true;
		$.each(ids, function(i){
			var id = ids[i];
			var state=jQuery("#dataTables").jqGrid("getCell",id,"tableState");
			if(state!= "DRAFT"){
				showMessage("message", "<font color=\"red\">不能删已启用和禁用的数据表</font>");
				canPost = false;
			}
		});
		if(canPost){
			if(confirm("确定删除吗？")){
				$.each(ids, function(i){
					$("#contentFrom").append(createHiddenInput("tableIds", ids[i]));
				});
				setPageState();
				ajaxSubmit("contentFrom", url, "dataTablelist");
				$("#contentFrom").find("input[name='tableIds']").remove();
			}
		}
	}
}


//改变数据表状态
function changeTableStates(){
	var ids = jQuery("#dataTables").getGridParam('selarrrow');
	if(ids==''){
		showMessage("message", "<font color=\"red\">请选择一条数据</font>");
	}else {
		for(var i=0;i<ids.length;i++){
			$("#contentFrom").append(createHiddenInput("tableIds", ids[i]));
		}
		ajaxSubmit("contentFrom", webRoot+"/form/data-table!changeTableState.htm", "dataTableContent", changeCallBack);
	}
}

function changeCallBack(){
	showMsg("message",5000);
}
/***************************
	数据表信息页面JS脚本
 ***************************/
//验证tableName
var tableNameIsOk = true;
function checkTableName(obj){
	tableNameIsOk = false;
	var tableName = $(obj).val();
	if(tableName.length>0 && tableName.length<=100){
		if(tableName.indexOf(" ")>=0 || tableName.indexOf("\'")>=0 || tableName.indexOf("\"")>=0){
			//$("#tableNameTip").attr('class', 'onError');
			//$("#tableNameTip").html("不能包含空格,双引号或单引号");
			showMessage("message", "<font color=\"red\">不能包含空格,双引号或单引号</font>");
		}else{
			var tableId = $("#tabelId").val();
			$.ajax({
				type : "post",
				dataType : "json",
				data : "tableName="+tableName+"&tableId="+tableId,
				url : webRoot + "/form/data-table!checkTableName.htm",
				success : function(data) {
					if(data == true){
						tableNameIsOk = true;
						$("#tableNameTip").attr('class', 'onSuccess');
						$("#tableNameTip").html("成功");
					}else{
						//$("#tableNameTip").attr('class', 'onError');
						//$("#tableNameTip").html("表名已存在");
						showMessage("message", "<font color=\"red\">表名已存在</font>");
					}
				},
				error : function() {
					alert("服务器繁忙，请稍后再操作...");
				}
			});
		}
	}else{
		//$("#tableNameTip").attr('class', 'onError');
		//$("#tableNameTip").html("长度错误，必填且最长100");
		showMessage("message", "<font color=\"red\">长度错误，必填且最长100</font>");
	}
}
function validateTableName(obj){
	var tableName = $(obj).val();
	var re = /[^A-Za-z0-9_\$#]*/g;  
	$(obj).attr("value",tableName.replace(re,""));  
	if(tableName.length>0){
		if(tableName.substring(0,1)=="_"||tableName.substring(0,1)=="$"||tableName.substring(0,1)=="#"){
			if(tableName.length==1){
				$(obj).attr("value","");
			}else{
				$(obj).attr("value",tableName.substring(1,tableName.length));
			}
		}
	}
}

//验证entityName
var entityNameIsOk = true;
function checkEntityName(obj){
	entityNameIsOk = false;
	var entityName = $(obj).val();
	if(entityName.length>0){
		if(entityName.indexOf(" ")>=0 || entityName.indexOf("\'")>=0 || entityName.indexOf("\"")>=0){
			$("#entityNameTip").attr('class', 'onError');
			$("#entityNameTip").html("不能包含空格,双引号或单引号");
		}else{
			entityNameIsOk = true;
			$("#entityNameTip").attr('class', 'onSuccess');
			$("#entityNameTip").html("成功");
		}
	}else{
		$("#entityNameTip").attr('class', 'onError');
	}
}

//保存触发事件
function saveDataTable(){
	$("#inputForm").submit();
}

//验证框架的提交和保存
function saveByAjax(){
	if($("#entityName").val() == ""){
		if(tableNameIsOk){
			ajaxSubmit("inputForm",webRoot+"/form/data-table-save.htm","dataTableContext", saveCallBack);
		}
	}else{
		if(tableNameIsOk && entityNameIsOk){
			ajaxSubmit("inputForm",webRoot+"/form/data-table-save.htm","dataTableContext", saveCallBack);
		}
	}
}

//保存回调
function saveCallBack(){
	showMsg();
	createCallBack();
}
/***************************
	  字段设置页面JS脚本
 ***************************/
//验证调用的检验方法
function checkColumn(value){
	var rge = /^[0-9]+\S*$/;
	if(value.indexOf("\"")>=0 || value.indexOf("\'")>=0){
		return "不能包含双引号或单引号";
	}else if(rge.test(value)){
		return "字段名与别名不能以数字开头";
	}else {
		return true;
	}
}

//检验默认值
function checkDefaultValue(obj){
	var defaultValue = $(obj).val();
	var b;
	if((b=defaultValue.indexOf("\'"))>=0 || (b=defaultValue.indexOf("\""))>=0){
		$(obj).attr("value", defaultValue.substring(0,b));
	}
}

//保存触发事件
function saveColumns(){
	var result=iMatrix.getFormGridDatas("columnForm","tableColumnId");
	if(result){
		$("#columnForm").append(createHiddenInput('canChange', $("#_canChange").val()));
		$("#columnForm").append(createHiddenInput('tableId', $("#tabelId").val()));
		ajaxSubmit("columnForm",webRoot+"/form/data-table!saveColumns.htm","columnList",saveColumnsBack);
	}
}

function saveColumnsBack(){
	tableColumnBack();
	showMsg();
}


function importInto(){
	$.colorbox({href:webRoot+'/form/data-table!showImport.htm?tableId='+$("#tabelId").val(),iframe:true, innerWidth:400, innerHeight:200,overlayClose:false,title:"导入字段"});
}
function exportTo(){
	iMatrix.getSubTableDatas("columnForm");
	$("#columnForm").append(createHiddenInput('tableId', $("#tabelId").val()));
	ajaxSubmit("columnForm",webRoot+"/form/data-table!exportToExcel.htm");
	$("#columnForm").find("input[name='tableId']").remove();
}

function changeDataType(obj){
	var val=$(obj).val();
	var tr=$(obj).parent().parent();
	var obj1=$(tr).find("input[name='maxLength']").parent().prev();
	if(val=="TEXT" || val=="CLOB"){
		$(obj1).html('<input name="defaultValue" size="10" onkeyup="checkDefaultValue(this);"/>');
	}else if(val=="DATE"){
		$(obj1).html('<input name="defaultValue" size="10" onclick="WdatePicker({dateFmt:\'yyyy-MM-dd\'});" readonly="readonly"/>');
	}else if(val=="TIME"){
		$(obj1).html('<input name="defaultValue" size="10" onclick="WdatePicker({dateFmt:\'yyyy-MM-dd HH:mm\'});" readonly="readonly"/>');
	}else if(val=="BOOLEAN"){
		$(obj1).html('<select name="defaultValue"><option value="1">true</option><option value="0">false</option></select>');
	}else if(val=="BLOB"){
		$(obj1).html('<input name="defaultValue" size="10" type="hidden"/>');
	}else{
		$(obj1).html('<input name="defaultValue" size="10" onkeyup="value=this.value.replace(/[^0-9]/,\'\');"/>');
	}
}
/***************************
 数据表信息与字段设置共用JS脚本
 ***************************/
//返回到数据表列表
function returnTableList(){
	ajaxSubmit("contentFrom",webRoot+"/form/data-table-list-data.htm","dataTableContent");
}

function returnCustomTableList(){
	ajaxSubmit("contentFrom",webRoot+"/form/data-table-defaultDataTableList.htm","dataTableContent");
}

//页签跳转设置
function pageUlChange(flag){
	var id = $("#tabelId").val();
	if(flag == 'b'){
		if(id == ""){
			showMessage("message", "<font color=\"red\">先保存数据表信息后，才能设置字段信息</font>");
		}else{
			$("#contentFrom").append(createHiddenInput('tableId', id));
			ajaxSubmit("contentFrom",webRoot+"/form/data-table-dealWithTableColumn.htm","btnZone,contentZone",tableColumnBack);
		}
	}else if(flag == 'a'){
		$("#contentFrom").append(createHiddenInput('tableId', id));
		ajaxSubmit("contentFrom",webRoot+"/form/data-table-input.htm","btnZone,contentZone", createCallBack);
	}else if(flag == 'c'){
		if(id == ""){
			showMessage("message", "<font color=\"red\">先保存数据表信息后，才能设置生成代码配置信息</font>");
		}else{
			$("#contentFrom").append(createHiddenInput('tableId', id));
			ajaxSubmit("contentFrom",webRoot+"/form/data-table-generateSetting.htm","btnZone,contentZone",validateSetting);
		}
	}
}

//自定义数据表查看页面页签跳转设置
function viewClickChange(flag){
	var id = $("#tabelId").val();
	if(flag == 'b'){//字段信息
		$("#contentFrom").append(createHiddenInput('tableId', id));
		ajaxSubmit("contentFrom",webRoot+"/form/data-table-viewCustomTableColumn.htm","btnZone,contentZone",tableColumnBack);
	}else if(flag == 'a'){//基本信息
		$("#contentFrom").append(createHiddenInput('tableId', id));
		ajaxSubmit("contentFrom",webRoot+"/form/data-table-viewCustom.htm","btnZone,contentZone", createCallBack);
	}
}

function tableColumnBack(){
	setFormgridHeight('tableColumnId',$(window).height()-140);
}
//数据表/生成代码配置/选择对应的菜单
function selectMenu(){
	custom_tree({url:webRoot+'/form/generate-code-menu-tree.htm',
		onsuccess:function(){closeFun();},
		width:300,
		height:400,
		title:'选择对应菜单',
		nodeInfo:['menuId'],
		multiple:false,
		webRoot:imatrixRoot
	});
}

function closeFun(){
	var menuId=getSelectValue("menuId");
	$("#myMenuId").attr("value",menuId);
	var menuName=getSelectNodeTitle();
	$("#menuName").attr("value",menuName);
}
//数据表/生成代码配置/清除对应的菜单
function clearMenu(){
	$("#myMenuId").attr("value","");
	$("#menuName").attr("value","");
}

//表单验证
function validateSetting(){
	$("#inputForm").validate({
		submitHandler: function() {
			if('true'==$("#flowable").val()&&''==$("#workflowCode").val()){
				alert("请填写流程编号！");
				return;
			}
			ajaxSubmit("inputForm",webRoot+"/form/data-table-saveSetting.htm","columnList",saveSettingCallback);
		}
	});
}

function saveSettingCallback(){
	showMsg();
	validateSetting();
}

function saveSetting(){
	$("#inputForm").submit();
}

