//树跳转
function treechange(currentId){
	ajaxSubmit("defaultForm",webRoot+"/module/module-page.htm", "viewList");
}


//页面验证
function  validate(){
	$("#viewSaveForm").validate({
			submitHandler: function() {
				ajaxSubmit("viewSaveForm",  webRoot+"/module/module-page!save.htm", "pageTable",savePageCallBack);
			},
			rules: {
				code: "required",
				name:"required"
				},
			messages: {
				code: "必填",
				name:"必填"
			}
		});
	validatePageCode();
}

function savePageCallBack(){
	showMsg();
	validate();
	$("#page_id1").attr("value",$("#pageId").val());//字段设置和按钮设置用到
}

function savePage(){
	$('#viewSaveForm').submit();
}

function createView(){
	$("#page_id").attr("value","");
	ajaxSubmit("defaultForm",webRoot+"/module/module-page!input.htm", "pageTable", validate);
}

function updateViewCallBack(){
	validate();
	$("#page_id1").attr("value",$("#pageId").val());//字段设置和按钮设置用到
}

function updateView(id){
	if(id!=""&&typeof(id)!='undefined'){
		$("#page_id").attr("value",id);
		ajaxSubmit("defaultForm",webRoot+"/module/module-page!input.htm", "pageTable", updateViewCallBack);
	}else{
		var ids = jQuery("#pageTableId").getGridParam('selarrrow');
		if(ids.length==0){
			alert("请选择数据");
		}else if(ids.length==1){
			$("#page_id").attr("value",ids[0]);
			ajaxSubmit("defaultForm",webRoot+"/module/module-page!input.htm", "pageTable", updateViewCallBack);
		}else{
			alert("只能选择一条数据");
		}
	}
}

function deleteViews(){
	var ids = jQuery("#pageTableId").getGridParam('selarrrow');
	if(ids==''){
		showMessage("message", "<font color=\"red\">请选择一条数据</font>");
	}else{
		$.each(ids, function(i){
			if($("#pageIds").attr("value")==""){
				$("#pageIds").attr("value",ids[i]);
			}else{
				$("#pageIds").attr("value",$("#pageIds").attr("value")+","+ids[i]);
			}
		});
		ajaxSubmit("defaultForm",webRoot+"/module/module-page-delete.htm", "pageTablelist",deleteCallBack);
	}
}

function deleteCallBack(){
	$("#pageIds").attr("value","");
	showMsg();
}

function defaultDisplaySet(type){
	var ids = jQuery("#pageTableId").getGridParam('selarrrow');
	if(ids==''){
		showMessage("message", "<font color=\"red\">请选择一条数据</font>");
	}else if(ids.length==1){
		$("#page_id").attr("value",ids[0]);
		ajaxSubmit("defaultForm",webRoot+"/module/module-page-defaultDisplaySet.htm", "pageTablelist",showMsg);
	}else{
		showMessage("message", "<font color=\"red\">只能选择一条数据</font>");
	}
}

function enabelSet(type){
	var ids = jQuery("#pageTableId").getGridParam('selarrrow');
	if(ids==''){
		showMessage("message", "<font color=\"red\">请选择一条数据</font>");
	}else{
		$.each(ids, function(i){
			if($("#pageIds").attr("value")==""){
				$("#pageIds").attr("value",ids[i]);
			}else{
				$("#pageIds").attr("value",$("#pageIds").attr("value")+","+ids[i]);
			}
		});
		ajaxSubmit("defaultForm",webRoot+"/module/module-page-enableSet.htm", "pageTablelist",enabelSetallBack);
	}
}

function enabelSetallBack(){
	$("#pageIds").attr("value","");
	showMsg();
}

//换页签
function changePageLeaf(flag){
	var page_id1 = $("#page_id1").attr("value");
	if(flag=="basic"){
		$("#menu_id").attr("value",$("#menuId").attr("value"));
		ajaxSubmit("defaultForm1",webRoot+"/module/module-page!input.htm", "contentZone,btnZone",changeViewBasicCallBack);
	}else{
		if(page_id1!=""){
			ajaxSubmit("defaultForm1",webRoot+"/module/button.htm", "contentZone,btnZone",changeViewSetButtonCallBack);
		}else{
			showMessage("message", "<font color=\"red\">先保存页面基本信息后，才能设置按钮信息</font>");
		}
	}
}
//通用消息提示
function showMessage(id, msg){
	if(msg != ""){
		$("#"+id).html(msg);
	}
	$("#"+id).show("show");
	setTimeout('$("#'+id+'").hide("show");',3000);
}

function changeViewBasicCallBack(){
	$("#pageId").attr("value",$("#page_id1").attr("value"));
	validate();
}

function changeViewSetButtonCallBack(){
	$("#menu_Id").attr("value",$("#menuId").attr("value"));
	//validateBtns();
}

function changeViewType(obj){
	$.ajax({
		   type: "POST",
		   url: "module-page!changeViewType.htm",
		   data: "type="+$(obj).val()+"&menuId="+$("#menuId").attr("value"),
		   success: function(data){
				$("#viewId").html(data);
		   }
		}); 
}

function validatePageCode(){
	$("#code").blur(function(){
		$.ajax({
			   type: "POST",
			   url: "module-page!validateCode.htm",
			   data: "myCode="+$("#code").attr("value")+"&pageId="+$("#pageId").attr("value"),
			   success: function(data){
			   		if(data=="true"){
			   			alert('编号 '+$("#code").attr("value")+' 已存在');
		   				$("#code").attr("value","");
		   				$("#code").focus();
			   		}
			   }
			}); 
	});
}

function previewPage(){
	if($("#page_id1").attr("value")!=""){
		var win = window.open(webRoot+'/module/module-page!preview.htm?pageId='+$("#page_id1").attr("value"),'win',"top=0,left=0,toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=no,width="+screen.availWidth+",height="+screen.availHeight);
	}else{
		alert("请先保存,再预览");
	}
	
}
function previewButton(){
	if($("#page_id1").attr("value")!=""){
		var win = window.open(webRoot+'/module/module-page!preview.htm?pageId='+$("#page_id1").attr("value")+'&fromBottonView=fromBottonView','win',"top=0,left=0,toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=no,width="+screen.availWidth+",height="+screen.availHeight);
	}else{
		alert("请先保存,再预览");
	}
}

//返回到页面管理列表
function returnPageList(){
	ajaxSubmit("defaultForm",webRoot+"/module/module-page-list.htm","pageTable");
}

//button
//调用列表控件的行点击时间 命名规则:columnName+Click
function toPageCodeClick(obj){
	var rowid = obj.rowid;
	$.colorbox({href:webRoot+'/module/button!selectPage.htm?pageId='+$("#page_id1").attr("value")+'&menuId='+$("#menuId").attr("value")+'&rowid='+rowid,iframe:true, innerWidth:800, innerHeight:500,overlayClose:false,title:"选择页面"});
}

function addViewValues(pageId,viewCode,rowid){
	//rowid_columnName
	$("#"+rowid+"_toPageCode").attr("value",viewCode);
	//给引用类型传id
	$("#"+rowid+"_toPage").attr("value",pageId);
}

//调用列表控件的行点击时间 命名规则:columnName+Click
function eventClick(obj){
var rowid = obj.rowid;
$.colorbox({href:webRoot+'/module/button!showEvent.htm?rowid='+rowid,iframe:true, innerWidth:800, innerHeight:500,overlayClose:false,title:""});
}

function addEvent(event,rowid){
	$("#"+rowid+"_event").attr("value",event);
}

function successSave(){
//列表组建需调的方法
getSubTableDatas("buttonSaveForm");

ajaxSubmit("buttonSaveForm",  webRoot+"/module/button!saveButtons.htm", "contentZone", saveButtonCallBack);
}
function saveButtonCallBack(){
	showMsg();
}