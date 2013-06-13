<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/acs-taglibs.jsp"%>
<%@ page import="org.ajaxanywhere.AAUtils"%> 
<%@ page import="com.norteksoft.product.util.WebContextUtils"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
	  <%@ include file="/common/acs-iframe-meta.jsp"%>
	  <title><s:text name="company.companyManager"/></title>
</head>
<body onload="getContentHeight();">
  <div class="ui-layout-center">
  	<div class="opt-body">
		<div class="opt-btn" style="margin-bottom: 6px;">
			  <button class='btn' onclick="selectDepartment('_department_tree');"><span><span>提交</span></span></button>
		</div>
		<div id="opt-content">
		<div id="_department_tree" class="demo" type="DEPARTMENT_TREE"></div>
			<script type="text/javascript">
			$(function () {
				$.ajaxSetup({cache:false});
				$("#_department_tree").jstree({ 
					"plugins" : [ "themes", "json_data" ,"checkbox","ui"],
					 "themes" : {  
						 "theme" : "default",  
						 "dots" : true,  
						 "icons" : true 
					}, 
					"json_data" : { 
						"ajax" : {
							"url" : "${acsCtx}/organization/load-tree!loadDepartment.action",
							"data" : function (n) {
								return { currentId : n.attr ? n.attr("id") :"INITIALIZED" };
							}  
						}
					}
				});
			});
			function selecChange(){
				var node = $(".jstree-clicked").parent().attr("id");
				var nodeClass = $("#"+node).attr("calss");
			}
			function allUsers(id){
		    	var lists = $("#"+id).find("li.jstree-checked");
				var v="" ;
				for(var i=0; i<lists.length; i++){
					v+=$(lists[i]).attr("id");
					if(i!=lists.length-1)
						v+=";";
				}
				if(v!=""){
					var arr=v.split(";");
					return arr;
				}else{
					return "";
				}
			}
			function selectDepartment(treeId){
				var arr = allUsers(treeId);
				var mainDepartmentName = window.parent.$("#mainDepartmentName").val();
				if(arr.length <= 0 || (arr.length==1&&arr[0].indexOf('USERSBYDEPARTMENT')<0) || (arr.length>1&&arr[1].indexOf('USERSBYDEPARTMENT')<0)){
					alert('请选择部门！');
					return;
				}
				var departId="";
				var departName="";
				for(var i=0; i<arr.length; i++){ //USERSBYDEPARTMENT-3978=火箭总师办
					var type = arr[i].split('-')[0]; 
					if(type=="USERSBYDEPARTMENT"){
						departId += arr[i].split('-')[1].split('=')[0]+"=";
						var name = arr[i].split('-')[1].split('=')[1];
						if(mainDepartmentName!=name){
						    departName+=name+",";
						}else{
	                        alert(mainDepartmentName+"已被选为正职部门!"); 
						}
					}
				}
				var departIds=departId.substring(0,departId.length-1);
				var departNames=departName.substring(0,departName.length-1);
				window.parent.$("#dids").attr("value",departIds);
				window.parent.$("#departmentName").attr("value",departNames);
				window.parent.$.colorbox.close();
			}
			</script>
			</div>
		</div>
	</div>
</body>
</html>
