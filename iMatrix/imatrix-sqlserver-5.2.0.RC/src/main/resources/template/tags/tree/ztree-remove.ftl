<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>选择办理人</title>
	<script type="text/javascript" src="${resourceCtx}/js/jquery-all-1.0.js"></script>
	<link   type="text/css" rel="stylesheet" href="${resourceCtx}/css/${theme}/jquery-ui-1.8.16.custom.css" id="_style"/>
	<script type="text/javascript" src="${resourceCtx}/js/aa.js"></script>
	<script type="text/javascript" src="${resourceCtx}/js/public.js"></script>

</head>
<script type="text/javascript">
$(document).ready(function (){ 
  //生成页面上的表格
  var removeJson = window.parent.$("#removeStaffJson").val();
  var jsonObj = eval(removeJson);
  var tr = "";
  for(var i=0;i<jsonObj.length;i++){
    var type = jsonObj[i].type;
    var showValue = jsonObj[i].showValue;
    var hiddenValue = jsonObj[i].hiddenValue;
   
    tr = "<tr>";
    if(type=='user'){
	      tr = tr + "<td style='width: 5%'>"
				    +"<input type='checkbox' name='_option_check_box' value='"+type+":"+showValue+":"+hiddenValue+"'></input>"
				    +"</td>";
		  tr = tr + "<td style='width: 75%'  >"+showValue+"</td> ";	
		  tr = tr + "<td style='width: 20%'  >用户</td>";    
    }else if(type=='department'){
          tr = tr + "<td style='width: 5%'>"
				    +"<input type='checkbox' name='_option_check_box' value='"+type+":"+showValue+":"+hiddenValue+"'></input>"
				    +"</td>";
		  tr = tr + "<td style='width: 75%'  >"+showValue+"</td> ";	
		  tr = tr + "<td style='width: 20%'  >部门</td>"; 
    }else{
          tr = tr + "<td style='width: 5%'>"
				    +"<input type='checkbox' name='_option_check_box' value='"+type+":"+showValue+":"+hiddenValue+"'></input>"
				    +"</td>";
		  tr = tr + "<td style='width: 75%'  >"+showValue+"</td> ";	
		  tr = tr + "<td style='width: 20%'  >工作组</td>"; 
    }
    tr = tr+"</tr>";
    $("#_remove_tbody").append(tr);
  }
});

function _choose_remove(){
 var allObjs=$("input[name='_option_check_box']");
  var showContent="";
  var showCach="";
  var hiddenContent="";
  var hiddenCach="";
  var usersShow="";
  var departmentsShow="";
  var workgroupsShow="";
  var usersHidden="";
  var departmentsHidden="";
  var workgroupsHidden="";
  for(var i =0;i<allObjs.length;i++){
	   var valueArr = $(allObjs[i]).val().split(":");
	   var type= valueArr[0];
	   var showValue = valueArr[1];
	   var hiddenValue = valueArr[2];
	   if(!$(allObjs[i]).attr("checked")){
	       if(type=='user'){
	         usersShow=usersShow+showValue+",";
	         usersHidden=usersHidden+hiddenValue+",";
	       }
	       
	       if(type=='department'){
	         departmentsShow=departmentsShow+showValue+",";
	         departmentsHidden=departmentsHidden+hiddenValue+",";
	       }
	       
	       if(type=='workgroup'){
	         workgroupsShow=workgroupsShow+showValue+",";
	         workgroupsHidden=workgroupsHidden+hiddenValue+",";
	       }
	   }
   } 
   showContent=getContent(usersShow,departmentsShow,workgroupsShow);
   hiddenContent=getContent(usersHidden,departmentsHidden,workgroupsHidden);
   //赋值
   window.parent.$("#${showInput}").attr("value",showContent);
   window.parent.$("#${hiddenInput}").attr("value",hiddenContent);
   
   //更新缓存
   showCach=getCachContent(usersShow,departmentsShow,workgroupsShow);
   hiddenCach=getCachContent(usersHidden,departmentsHidden,workgroupsHidden);
   
   
   document.cookie="showCachContent${showInput}=";
   document.cookie="hiddenCachContent${hiddenInput}=";
   document.cookie="showCachContent${showInput}="+escape(showCach)
	                   +";expires="+setExpiresTime(30);
   document.cookie="hiddenCachContent${hiddenInput}="+escape(hiddenCach)
	                   +";expires="+setExpiresTime(30);
	                   
  window.parent.$.colorbox.close();
}
function getContent(users,departments,workgroups){
 var result ="";
 if(users!=''){
     result=result+users;
 }
 
 if(departments!=''){
     result=result+departments;
 }
 
 if(workgroups!=''){
     result=result+workgroups;
 }
 
 return result.substring(0,result.length-1);
}

function getCachContent(users,departments,workgroups){
var result="";
 if(users!=''){
      result=result+"user:"+users.substring(0,users.length-1)+"+";
 }
 
 if(departments!=''){
      result=result+"department:"+departments.substring(0,departments.length-1)+"+";
 }
 
 if(workgroups!=''){
      result=result+"workgroup:"+workgroups.substring(0,workgroups.length-1)+"+";
 }
 return result.substring(0,result.length-1);
}
//设置cookie过期时间
function setExpiresTime(minutes){
   var date = new Date();
   date.setTime(date.getTime()+minutes*60*60);
   return date.toGMTString();
}
</script>
<body >
<div class="opt-body">
<div class="cbox-btn">
	<button class="btn" onclick="_choose_remove();" ><span><span>选中移除</span></span></button>
</div>
<div id="opt-content">
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
</html>