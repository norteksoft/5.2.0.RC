<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>选择办理人</title>
	<script type="text/javascript" src="${resourceCtx}/js/jquery-all-1.0.js"></script>
	<script type="text/javascript" src="${resourceCtx}/widgets/jstree/jquery.jstree.js"></script>
	<link   type="text/css" rel="stylesheet" href="${resourceCtx}/css/${theme}/jquery-ui-1.8.16.custom.css" id="_style"/>
	<script type="text/javascript" src="${resourceCtx}/js/aa.js"></script>
	<script type="text/javascript" src="${resourceCtx}/js/public.js"></script>
	<link   type="text/css" rel="stylesheet" href="${resourceCtx}/widgets/colorbox/colorbox.css" />

</head>
<script type="text/javascript">
$(document).ready(function (){ 
  //生成页面上的表格
  var removeJson = window.parent.$("#removeStaffJson").val();
  var jsonObj = eval(removeJson);
  var tr = "";
  for(var i=0;i<jsonObj.length;i++){
    var id = jsonObj[i].id;
    var name = jsonObj[i].name;
    var type = jsonObj[i].type;
    var loginName = jsonObj[i].loginName;
   
    tr = "<tr>";
    if(type=='user'){
	      tr = tr + "<td style='width: 5%'>"
				    +"<input type='checkbox' name='_option_check_box' value='"+id+"'></input>"
				    +"<input type='hidden' id='login_name_"+id+"' value='"+loginName+"'></input>"
				    +"</td>";
		  tr = tr + "<td style='width: 75%' id='"+id+"' >"+name+"</td> ";	
		  tr = tr + "<td style='width: 20%' id='type_"+id+"' >用户</td>";    
    }else if(type=='department'){
          tr = tr + "<td style='width: 5%'>"
				    +"<input type='checkbox' name='_option_check_box' value='"+id+"'></input>"
				    +"</td>";
		  tr = tr + "<td style='width: 75%' id='"+id+"' >"+name+"</td> ";	
		  tr = tr + "<td style='width: 20%' id='type_"+id+"' >部门</td>"; 
    }else{
          tr = tr + "<td style='width: 5%'>"
				    +"<input type='checkbox' name='_option_check_box' value='"+id+"'></input>"
				    +"</td>";
		  tr = tr + "<td style='width: 75%' id='"+id+"' >"+name+"</td> ";	
		  tr = tr + "<td style='width: 20%' id='type_"+id+"' >工作组</td>"; 
    }
    tr = tr+"</tr>";
    $("#_remove_tbody").append(tr);
  }
});
//选择某几项移除(user)
function _choose_remove(){
	var allObjs=$("input[name='_option_check_box']");
	var hiddenInputId = $("#hiddenInputId").val();
	var showInputId = $("#showInputId").val();
	var loginNameId = $("#loginNameId").val();

	var idsResult = "";
	var showResult = "";
	var loginNameResult = "";
	for(var i =0;i<allObjs.length;i++){
	   if(!$(allObjs[i]).attr("checked")){
	   var  id = $(allObjs[i]).val();
	   idsResult+=id+",";
	   showResult+=$("#"+id).html()+",";
	   loginNameResult+=$("#login_name_"+id).val()+",";
	   }
	}
  fulfillHiddenInput(idsResult.substring(0,idsResult.length-1));
  window.parent.$("#"+showInputId).attr("value",showResult.substring(0,showResult.length-1));
  if(loginNameId!=''){
    window.parent.$("#"+loginNameId).attr("value",loginNameResult.substring(0,loginNameResult.length-1));
  }
  window.parent.$.colorbox.close();
}
//全部移除	
function  _remove_all(){
	var hiddenInputId = $("#hiddenInputId").val();
	var showInputId = $("#showInputId").val();
	var loginNameId = $("#loginNameId").val();
	fulfillHiddenInput("");
	window.parent.$("#"+showInputId).attr("value","");
	if(loginNameId!=''){
	window.parent.$("#"+loginNameId).attr("value","");
	}
	window.parent.$.colorbox.close();
}
//选择某几项移除(department_group)
function _choose_department_group_remove(){
    var allObjs=$("input[name='_option_check_box']");
	var hiddenInputId = $("#hiddenInputId").val();
	var showInputId = $("#showInputId").val();
	var idsResult = "";
	var showResult = "";
	var departIds = "";
	var groupIds = "";
	for(var i =0;i<allObjs.length;i++){
	   if(!$(allObjs[i]).attr("checked")){
		   var  id = $(allObjs[i]).val();
		   var name = $("#"+id).html();
		   var type = $("#type_"+id).html();
		   if(type=='部门'){
		    departIds+=id+",";
		   }
		   if(type=='工作组'){
		    groupIds+=id+",";
		   }
		   showResult+=name+",";
	   }
	}
	idsResult="departmentIds=="+departIds.substring(0,departIds.length-1)+"+#groupIds=="+groupIds.substring(0,groupIds.length-1);
	showResult=showResult.substring(0,showResult.length-1);
	fulfillHiddenInput(idsResult);
    window.parent.$("#"+showInputId).attr("value",showResult);
    window.parent.$.colorbox.close();
}
//给隐藏域付值
function fulfillHiddenInput(value){
	var hiddenInputId = $("#hiddenInputId").val();
	if(hiddenInputId=='NOHiddenInputId'){
	   window.parent.$("#builtInInput").attr("value",value);
	}else{
	   window.parent.$("#"+hiddenInputId).attr("value",value);
	}
}
</script>
<body >
<div class="opt-body">
<div class="cbox-btn">
    <#if treeType=='DEPARTMENT_WORKGROUP_TREE'>
	<button class="btn" onclick="_choose_department_group_remove();" ><span><span>选中移除</span></span></button>
	<#else>
	<button class="btn" onclick="_choose_remove();" ><span><span>选中移除</span></span></button>
	</#if>
	<button class="btn" onclick="_remove_all();"><span><span>全部移除</span></span></button>
</div>
<div id="opt-content">
<input id="hiddenInputId" type="hidden" value="${hiddenInputId}" >
<input id="showInputId" type="hidden" value="${showInputId}" >
<input id="loginNameId" type="hidden" value="${loginNameId}" >
<input id="treeType" type="hidden" value="${treeType}" >
﻿<table class="leadTable" >
	<thead>
		<tr>
		    <th></th>
			<th>选项名称</th>
			<th>类别</th>
		</tr>
	</thead>
	<tbody id="_remove_tbody" >
	
	</tbody>
</table>
<div style="height:10px;"></div>
</div>
</div>
</body>
<script type="text/javascript" src="${resourceCtx}/widgets/colorbox/jquery.colorbox.js"></script>
</html>