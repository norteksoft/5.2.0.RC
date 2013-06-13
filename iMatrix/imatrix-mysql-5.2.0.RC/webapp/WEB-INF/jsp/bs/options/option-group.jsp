<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/setting-iframe-meta.jsp"%>
	
	<script type="text/javascript">
	
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
		//通用消息提示
		function showMessage(id, msg){
			if(msg != ""){
				$("#"+id).html(msg);
			}
			$("#"+id).show("show");
			setTimeout('$("#'+id+'").hide("show");',3000);
		}
		//树加载执行的回调方法
		function treechange(id){
			ajaxSubmit('defaultForm',"${settingCtx}/options/option-group.htm", 'groups_main'); 
		}
		//增加
		function addGroup(){
			if($("#systemId").val()!=""&&typeof($("#systemId").val())!='undefined'){
				ajaxSubmit("defaultForm", "${settingCtx}/options/option-group-input.htm", "groups_main", addCallBack);
			}else{
				$("#_message").html("<font class=\"onError\"><nobr>请选择对应的系统</nobr></font>");
				$("#_message").css("display","block");
				setTimeout('$("#_message").css("display","none");',3000);
			}
		}
		//修改
		function updateGroup(){
			var check = $("input[name='groupBoxs']:checked");
			if(check.length<=0 || check.length>1){
				showMessage("message","<font class=\"onError\"><nobr>不能修改空的或多个选项组</nobr></font>");
			}else{
				ajaxSubmit("defaultForm", "${settingCtx}/options/option-group-input.htm?optionGroupId=" + $(check).val(), "groups_main", addCallBack);
			}
		}
		//删除
		function deleteGroup(){
			var ids = "";
			var check = $("input[name^='jqg__group_table']:checked");
			if(check.length <= 0){
				showMessage("message","<font class=\"onError\"><nobr>不能没有选中的选项组</nobr></font>");
			}else{
				if(confirm("确认删除?")){
					jgridDelRow('_group_table', '${settingCtx}/options/option-group-delete.htm');
				}
			}		
		}
		//分页排序
		function jmesaSubmit(){
			ajaxSubmit("groupsForm", "${settingCtx}/options/option-group.htm", "groups_List");
		}
		//增加回调方法
		function addCallBack(){
			addAttr();
			validate();
			initOptionRow(true);
			getContentHeight();
		}
		//属性设置
		function addAttr(){
			var idFlag = $("#_optionGroupId").val();
			if(idFlag == "" || typeof(idFlag) == "undefined"){
				$('#optionGroupNoTip').attr('class', 'onShow');
				$('#optionGroupNoTip').html('必填项');
			}else{
				$("#optionGroupNo").attr("readonly","readonly");
				$("#optionGroupNo").removeAttr("onfocus");;
				$("#optionGroupNo").removeAttr("onblur");;
			}
		}
		//标准表单页面验证
		function validate(){
			$("#optionGroupFrom").validate({
				submitHandler: function() {
					optionGroupFromSubmit();
				},
				rules: {
					name:"required",
					code: "required"
				},
				messages: {
					name:"必填",
					code: "必填"
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
		var noFlag;
		//验证选项组值
		function validateNO(){
			var groupNo = $('#optionGroupNo').val();
			if(groupNo.length<1 || groupNo.length>50){
				$('#optionGroupNoTip').attr('class', 'onError');
				$('#optionGroupNoTip').html('长度错误，必填且最长25个字');
				noFlag = false;	
			}else{
				if(groupNo.indexOf("\"")>=0 || groupNo.indexOf("\'")>=0 || groupNo.indexOf(" ")>=0){
					$('#optionGroupNoTip').attr('class', 'onError');
					$('#optionGroupNoTip').html('不能包含空格，双引号或单引');
					noFlag = false;
				}else{
					$.post(webRoot + "/options/option-group!checkGroupNo.htm", "groupNo=" + groupNo, function(data){
						if(data == "true"){
							$('#optionGroupNoTip').attr('class', 'onSuccess');//onSuccess
							$('#optionGroupNoTip').html('正确');
							noFlag = true;
						}else{
							$('#optionGroupNoTip').attr('class', 'onError');
							$('#optionGroupNoTip').html('此编号已存在');
							noFlag = false;
						}
					});
				}
			}
		}
		//初始化选项行
		var rows = 0 ;
		function initOptionRow(flag){
			rows = $("#optionTable tr").length-1;
			if(flag){
				for(var i=0;i<rows;i++){
					//addValidate(i);
				}
			}
			var style=$($("#optionTable").children("tbody")).children("tr");
			if(style.length==1){
			    addOptionRow('optionTable',flag);
			}
		}
		//增加选项行
		function addOptionRow(id1,flag){
			var tr = createOptionRow(rows);
			$(tr).appendTo("#"+id1);
			//if(flag){
			//	addValidate(rows);
			//}
			rows++;
		}
		//创建一个新的选项行
		function createOptionRow(rows){
			var str = "<tr><td>"
					  	+ "<input name='option[" + rows + "].name' type='text' id='name_" + rows + "'/>"
					  	+ "<span id='name_" + rows + "Tip'></span>"		
					+ "</td>"
					+ "<td>"
						+ "<input name='option[" + rows + "].value' type='text' id='value_" + rows +"'/>"
						+ "<span id='value_" + rows + "Tip'></span>"
					+ "</td>"
					+ "<td align='center'>"
						+ "<select name='option[" + rows + "].selected' id='isSelect_" + rows + "' onchange='validateSelected(this);'>"
							+ "<option value='false' selected='selected'>否</option>"
							+ "<option value='true'>是</option>"
						+ "</select>"
					+ "</td>"
					+ "<td align='center'>"
						+ "<select name='option[" + rows + "].optionIndex' id='optionIndex_" + rows + "'>";

					for(var i = 0; i<=100; i++){
						str = str + '<option value="' + i + '">' + i + '</option>';
					}
					str = str 
					    + "</select>"
					+ "</td>"	
					+ "<td>"
					    + "<a class='small-btn' name='" + rows + "' href='#" + rows + "' onclick='deleteOptionRow(this," + rows + ");'><span><span>删除</span></span></a>&nbsp;&nbsp;"
					    + "<a class='small-btn' href='#" + rows + "' onclick='addOptionRow(\"optionTable\",true);'><span><span>添加</span></span></a>"
					+ "</td></tr>";				
			return str;
		}
		var selectFlag;
		//检验默认选中的值
		function validateSelected(selectId){
			var select = $("select[name*='.selected']");
			var count = 0;
			for(var i = 0; i<select.length; i++){
				if($(select[i]).val() == 'true'){
					count++;
				}
			}
			if(count > 1){
				$("#"+selectId.id+" option[value='false']").attr("selected",true);   
				alert("只能有一个默认选项");
				//selectFlag = false;
			}else{
				selectFlag = true;
			}
		}
		//删除一个现有的行
		function deleteOptionRow(obj,row,optionId){
			if($("#optionTable tr").length<=2){
				alert("至少需要一个选项！");
				return;
			}
			if(confirm("确认删除行?")){
				//deleteValidate(row);
				$(obj).parent().parent().remove();
				if(typeof(optionId)!='undefinded'){
					$.post(webRoot + "/options/option-group!deleteOption.htm", "optionId=" + optionId);
				}
			}
		}
		//保存方法
		function save(){
			if(validateOptionNameAndValue()){
				$("#_systemId").attr("value",$("#systemId").val());
				$("#optionGroupFrom").submit();
			}
			
		}
		function validateOptionNameAndValue(){
			var inputs = $("input[name*='.name']");
			for(var i = 0; i<inputs.length; i++){
				for(var j = i+1; j<inputs.length; j++){
					if($(inputs[i]).val()==""||$(inputs[i]).val()==null){
						alert("选项名不能为空");
						return false;
					}
					if($(inputs[i]).val() == $(inputs[j]).val()){
						var optIndexI=$(inputs[i]).parent().parent().find("select[name*='optionIndex']").val();
						var optIndexJ=$(inputs[j]).parent().parent().find("select[name*='optionIndex']").val();
						alert("显示顺序为"+optIndexI+"和"+optIndexJ+"的选项组名重复");
						return false;
					}
				}
			}
			var inputsValue = $("input[name*='.value']");
			for(var i = 0; i<inputsValue.length; i++){
				for(var j = i+1; j<inputsValue.length; j++){
					if($(inputsValue[i]).val()==""||$(inputsValue[i]).val()==null){
						alert("选项值不能为空");
						return false;
					}
				}
			}
			var lastName = $(inputs[inputs.length-1]).val();
			var lastValue = $(inputsValue[inputsValue.length-1]).val();
			if((lastName==null||lastName=="")&&(lastValue!=null&&lastValue!="")){
				alert("选项名不能为空");
				return false;
			}
			if((lastName!=null&&lastName!="")&&(lastValue==null||lastValue=="")){
				alert("选项值不能为空");
				return false;
			}
			return true;
		}
		//formValidator提交,检验
		function optionGroupFromSubmit(){
			if(typeof(selectFlag) == "undefined" || selectFlag == true){
				var value = $("#optionGroupName").val();
				var optionGroupId = $("#_optionGroupId").val();
				$.post(webRoot + "/options/option-group!checkGroupName.htm", "groupName=" + value + "&optionGroupId=" + optionGroupId, function(data){
					if(data == "true"){
						ajaxSubmit("optionGroupFrom", "${settingCtx}/options/option-group-save.htm", "groups_input", addCallBack);
					}else{
						showMessage("message","<font class=\"onError\"><nobr>选项组名已存在</nobr></font>");
					}
				});
			}else{
				showMessage("message","<font class=\"onError\"><nobr>部分信息不符合</nobr></font>");
			}
		}
		//返回方法
		function goBack(){
			ajaxSubmit("defaultForm", "${settingCtx}/options/option-group.htm", "groups_main");
		}
		function viewGroup(ts1,cellval,opts,rwdat,_act){
			var value="<a  href=\"#\" hidefocus=\"true\" onclick=\"_updateGroup("+opts.id+");\">" + ts1 + "</a>";
			return value;
		}
		function _updateGroup(id){
			ajaxSubmit("defaultForm", "${settingCtx}/options/option-group-input.htm?optionGroupId=" + id, "groups_main", addCallBack);
		}
		</script>
	<title></title>
</head>
<body>
<div class="ui-layout-center">
	<div class="opt-body">
		<form name="defaultForm" id="defaultForm">
			<input name="systemId" id="systemId" value="${systemId }" type="hidden"></input>
		</form>
		
		<aa:zone name="groups_main">
				<div class="opt-btn">
					<button class="btn" onclick="addGroup();"><span><span>新建</span></span></button>
					<button class="btn" onclick="deleteGroup();"><span><span >删除</span></span></button>
				</div>
				<span id="_message" style="display: none;"></span>
			<div id="opt-content" >
				<view:jqGrid url="${settingCtx}/options/option-group.htm?systemId=${systemId }" code="BS_OPTION_GROUP" pageName="groups" gridId="_group_table"></view:jqGrid>
			</div>
		</aa:zone>
	</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>