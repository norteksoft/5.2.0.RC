<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
	<title>格式设置</title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
    <meta http-equiv="Cache-Control" content="no-store"/>
    <meta http-equiv="Pragma" content="no-cache"/>
    <meta http-equiv="Expires" content="0"/>
    
	<script type="text/javascript" src="${resourceCtx}/js/jquery-all-1.0.js"></script>
	
	<link   type="text/css" rel="stylesheet" href="${resourceCtx}/widgets/colorbox/colorbox.css" />
	
	<link   type="text/css" rel="stylesheet" href="${resourceCtx}/widgets/jqgrid/ui.jqgrid.css" />
	<link   type="text/css" rel="stylesheet" href="${resourceCtx}/css/black/jquery-ui-1.8.16.custom.css" id="_style"/>
	<script type="text/javascript">
		$(document).ready(function(){
				var currentId = $("#currentId").attr("value").substring(0,$("#currentId").attr("value").lastIndexOf("_"));
				var type=parent.$("#"+"${currentInputId}").attr("value");
				if(type!=''){
					var values = parent.$("#"+currentId+"_controlValue").attr("value").split(",");
					if(values[0]==''){
						values[0] = 'TEXT';
					}
					if(values[0]=='SELECT_TREE'){
						$(".treeAttr").show();
						$("#treeContentId").attr("value",values[1]);
						$("#deptTreeType").attr("value",values[2]);
						$("#deptMultiple").attr("value",values[3]);
					}
					if(values.length>0){
						$("#controlType").attr("value",values[0]);
					}
				}
		});
		
		function showDiv(){
			var type = $("#controlType").attr("value");
			if(type=='SELECT_TREE'){
				 $(".treeAttr").show();
			}else{
				$(".treeAttr").hide();
			}
		}
		
		function ok(){
			var currentId = $("#currentId").attr("value").substring(0,$("#currentId").attr("value").lastIndexOf("_"));
			var type = $("#controlType").attr("value");
			 if(type=='SELECT_TREE'){
				var deptTreeType = $("#deptTreeType").attr("value");
				var deptMultiple = $("#deptMultiple").attr("value");
				var treeContentId = $("#treeContentId").attr("value");
				var controlValue = type+","+treeContentId+","+deptTreeType+","+deptMultiple;
				 parent.$("#"+currentId+"_controlValue").attr("value",controlValue);
				 parent.$("#"+"${currentInputId}").attr("value","人员部门树");
			 }else if(type=='TEXT'){
				 parent.$("#"+"${currentInputId}").attr("value","文本框");
				 parent.$("#"+currentId+"_controlValue").attr("value","TEXT");
			 }else if(type=='CHECKBOX'){
			 	parent.$("#"+"${currentInputId}").attr("value","复选框");
			 	parent.$("#"+currentId+"_controlValue").attr("value","CHECKBOX");
			 }else if(type=='SELECT'){
				 parent.$("#"+"${currentInputId}").attr("value","下拉框");
				 parent.$("#"+currentId+"_controlValue").attr("value","SELECT");
			 }else if(type=='MULTISELECT'){
			 	 parent.$("#"+"${currentInputId}").attr("value","多选下拉框");
			 	 parent.$("#"+currentId+"_controlValue").attr("value","MULTISELECT");
			 }else if(type=='TEXTAREA'){
			 	 parent.$("#"+"${currentInputId}").attr("value","文 本域");
			 	 parent.$("#"+currentId+"_controlValue").attr("value","TEXTAREA");
			 }else if(type=='CUSTOM'){
			 	 parent.$("#"+"${currentInputId}").attr("value","自定义");
			 	 parent.$("#"+currentId+"_controlValue").attr("value","CUSTOM");
			 }
			 parent.$.colorbox.close();
		}
	</script>
</head>
<body>
<div class="opt-body">
	<div class="opt-btn">
		<button class="btn" onclick="ok();"><span><span>确定</span></span></button>
		<button class="btn" onclick='parent.$.colorbox.close();'><span><span >取消</span></span></button>
	</div>
	<div id="opt-content">
		<input id="currentId" type="hidden" value="${currentInputId}"  />
		<table class="form-table-border-left">
		    <tr >
	      		<td>控件类型：</td>
	      		<td><select id="controlType" onchange="showDiv();">
	      				<option value="TEXT">文本框</option>
	      				<option value="CHECKBOX">复选框</option>
	      				<option value="SELECT">下拉框</option>
	      				<option value="MULTISELECT">多选下拉框</option>
	      				<option value="TEXTAREA">文本域</option>
	      				<option value="CUSTOM">自定义</option>
	      				<option value="SELECT_TREE">人员部门树</option>
					</select>
				</td>
		   </tr>
		   <tr class="treeAttr" style="display:none;">
      		<td>控件隐藏域属性名：</td>
      		<td><input id="treeContentId"></input></td>
	      </tr>
	      <tr class="treeAttr" style="display:none;">
	     	<td>选择类型：</td>
	     	<td>
	   			<select id="deptTreeType" name="formControl.deptTreeType">
	   				<option value="COMPANY">公司树</option>
	   				<option value="MAN_DEPARTMENT_GROUP_TREE">人员部门和工作组树</option>
	   				<option value="MAN_DEPARTMENT_TREE">人员部门树</option>
	   				<option value="MAN_GROUP_TREE">人员工作组树</option>
	   				<option value="DEPARTMENT_TREE">部门树</option>
	   				<option value="DEPARTMENT_WORKGROUP_TREE">部门工作组树</option>
	   				<option value="GROUP_TREE">工作组树</option>
	   			</select>
	   		</td>
	     </tr>
	     <tr class="treeAttr" style="display:none;">
	     	<td>类型：</td>
	     	<td>
				<select id="deptMultiple">
					<option value="false">单选</option>
					<option value="true">多选</option>
				</select>
	   		</td>
	     </tr>
	  </table>
	</div>
</div>
</body>
<script type="text/javascript" src="${resourceCtx}/widgets/colorbox/jquery.colorbox.js"></script>
</html>
