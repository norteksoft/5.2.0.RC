/************************************************************
 模块名称: 委托管理页面对应js
 创 建 者：吴荣
 说    明: 该js文件名和jsp主页文件名相同
 ************************************************************/
//提交FORM
	function submitForm(){
		$("#delegateSaveForm").submit();
	}
	
	//保存并启用
	function submitAndStartForm(){
		$("#needStart").attr("value",true);
		$("#delegateSaveForm").submit();
	}	
	
	//页面验证
	function  validate(){

//		$("input:checkbox[@name='RoleNames']").formValidator({tipid:"RoleNamesTip",onshow:"请选择",onfocus:"至少选1个",oncorrect:"正确"}).inputValidator({min:1,onerror:"至少选1个"});
		$.validator.addMethod("linkRequired", function(value, element) {
			var $element = $(element);
			if($element.val()=='请选择环节'||$element.val()==''||$element.val()=='0'){
				return false;
			}
			return true;
		}, "必填");
		$.validator.addMethod("flowRequired", function(value, element) {
			var $element = $(element);
			if($element.val()=='请选择流程'||$element.val()==''||$element.val()=='0'){
				return false;
			}
			return true;
		}, "必填");
		$.validator.addMethod("styleRequired", function(value, element) {
			var $element = $(element);
			if($element.val()=='请选择'||$element.val()==''||$element.val()=='0'){
				return false;
			}
			return true;
		}, "必填");
		unFormValidator1();
//		change($("#styleSelect").attr("value"));
		$("#delegateSaveForm").validate({
		submitHandler: function() {
			saveForm();
		},
		rules: {
			manName: "required",
			name:"required",
			beginTime:"required",
			endTime:"required",
			content:{
				maxlength:500
				}
		},
		messages: {
			manName: "必填",
			name:"必填",
			beginTime:"必填",
			endTime:"必填",
			content:{
				maxlength: "超出500字"
				}
		}
	});
	 }
	
	function unFormValidator1(){
		var vId=$("#id").attr("value");
			var id = $("#styleSelect").attr("value");
			if(id==1){
				enableFlowSelect(true);
				enableRoleSelect(false);
			}else if(id==2){
				enableFlowSelect(false);				
				enableRoleSelect(false);
			}else if(id==3){
				enableFlowSelect(false);
				enableRoleSelect(true);
				getRolesByUser();
			}else{	
				enableFlowSelect(false);				
				enableRoleSelect(false);
			}
	}
	
	//是否启用流程选择
	function enableFlowSelect(flag){
		var boolean = !flag;
		if(flag){
			$("#flowIdSelect").attr("disabled",boolean);
			$("#selectLink").attr("disabled",boolean);
			$("#chooseP").show();
			$("#chooseT").show();
		}else{
			$("#flowIdSelect").attr("disabled","disabled");
			$("#selectLink").attr("disabled","disabled");
			$("#chooseP").hide();
			$("#chooseT").hide();
		}
	}
	
	//是否启用角色选择
	function enableRoleSelect(flag){
		var boolean = !flag;
		if(flag){
			$("#rolesId").parent().show();
		}else{
			$("#rolesId").parent().hide();
		}
	}
	
	//获取某人角色
	function getRolesByUser(){
		$.ajax({
			data:{id:$("#id").attr("value")},
			type:"post",
			url:webRoot+'/engine/delegate-main!getRolesByUser.htm',
			success:function(data, textStatus){
				$("#rolesId").html(data);
			},
	        error:function(){alert("出错了");}
		});
	}
	//验证正确后，调用来真正提交表单
	function saveForm(){
		ajaxSubmit("delegateSaveForm", "", "delegatemainlist", saveFormCallBack);
	}
	function saveFormCallBack(){
		validate();
		showMsg("message");
	}
	//指定流程下拉
	function change(id){
		if(id==0){
		    $("#inputForValidate1").attr("value","");
		}else if(id==1){
			$("#inputForValidate1").attr("value","yes");
			
		}else if(id==2){
			$("#inputForValidate1").attr("value","yes");
			$("#inputForValidate2").attr("value","yes");
			$("#inputForValidate3").attr("value","yes");
			
		}else if(id==3){
			$("#inputForValidate1").attr("value","yes");
			$("#inputForValidate2").attr("value","yes");
			$("#inputForValidate3").attr("value","yes");
		}
		
		unFormValidator1();
	}

	//流程 
	function changeFlow(id){
		if(id==0){
			$("#inputForValidate2").attr("value","");
		}else{
			$("#inputForValidate2").attr("value",id);
		}
	
			$("#defaultForm").html("<input name='processDefinitionId' type='hidden' value='"+id+"'/> ");
			ajaxSubmit("defaultForm",webRoot+'/engine/delegate-main!getLink.htm', "taskNamesSelect");
	}
	function changeLinkName(id){
		if(id==0 || id=="请选择环节"){
		    $("#inputForValidate3").attr("value","");
		}else{
			$("#inputForValidate3").attr("value",id);
		}
	}
	
	function addForm(url){
		$("#delegatemain").attr("action",url);
		$("#addOrEdit").attr("value","add");
		ajaxAnywhere.formName="delegatemain";
		ajaxAnywhere.getZonesToReload = function(){
			return "delegatemainlist";
		};
		ajaxAnywhere.onAfterResponseProcessing = function () {
			validate();
		 };
		ajaxAnywhere.submitAJAX();
		}
	
	//查看
	function viewForm(ts1,cellval,opts,rwdat,_act){
		var v="<a  href=\"#\" hidefocus=\"true\" onclick=\"_click_fun("+opts.id+");\">" + ts1 + "</a>";
		return v;
	}
	
	
	function _click_fun(id){
		ajaxSubmit("delegatemain", webRoot+'/engine/delegate-main!view.htm?id='+id, "delegatemainlist");
	}
	
	//查看
	function viewReceive(ts1,cellval,opts,rwdat,_act){
		var v="<a  href=\"#\" hidefocus=\"true\" onclick=\"_click_funReceive("+opts.id+");\">" + ts1 + "</a>";
		return v;
	}
	
	function _click_funReceive(id){
		ajaxSubmit("delegatemain", webRoot+'/engine/delegate-main!viewReceive.htm?id='+id, "delegatemainlist");
	}
	
	//修改
	function updateForm(url,title){
		var ids = jQuery("#main_table").getGridParam('selarrrow');
		if(ids==''){
			alert("请先选择");
		}else if(ids.length > 1){
			 alert("只能选择一条！");
		}else{
				upadateForm(url+"?id="+ids);
		}
	}
	
	//修改 委托
	function upadateForm(url){
		$("#delegatemain").attr("action",url);
		$("#addOrEdit").attr("value","edit");
		ajaxAnywhere.formName="delegatemain";
		ajaxAnywhere.getZonesToReload = function(){
			return "delegatemainlist";
		};
		ajaxAnywhere.onAfterResponseProcessing = function () {
			validate();
			showMsg();
		 };
		ajaxAnywhere.submitAJAX();
	}

	//创建 委托
	function createForm(url){
		$("#delegatemain").attr("action",url);
		ajaxAnywhere.formName="delegatemain";
		ajaxAnywhere.getZonesToReload = function(){
			return ;
		};
		ajaxAnywhere.onAfterResponseProcessing = function () {
			validate();
			messageControl();
		 };
		ajaxAnywhere.submitAJAX();
	}
	

	//删除
	function deleteForm(url){
		var ids = jQuery("#main_table").getGridParam('selarrrow');
		if(ids==''){
			alert("请先选择");
			return;
		}else{
			if(confirm("确认删除吗？")){
				delRow('main_table', url);
				showMsg();
			}
		}
	}
	
	function enalbeDelegateMain(url){
		var ids = jQuery("#main_table").getGridParam('selarrrow');
		if(ids==''){
			alert("请先选择");
		}else if(ids.length > 1){
			 alert("只能选择一条！");
		}else{
			ajaxSubmit('delegatemainForm',url+"?ids="+ids,'delegatemainlist',showMsg);
		}
	}
	
	
	function getFormContent(){
		if($("#contextArea").html().length>500)$("#contextArea").html($("#contextArea").html().substring(0,500));
		$('#textareaTip').html("最大输入500字,你已经输入了"+$("#contextArea").html().length);
	}
	