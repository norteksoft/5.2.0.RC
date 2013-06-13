<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>数据字典</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	<script src="${mmsCtx}/js/module-page.js" type="text/javascript" charset="UTF-8"></script>		
	<script>
		var id="";
		$(document).ready(function() {
			viewTree();
		});
		
		function viewTree(){   
			$.ajaxSetup({cache:false});
			$("#viewTree").bind("select_node.jstree",function(e){
				id = $(".jstree-clicked").parent().attr("id");
			}).jstree({
				"json_data":{
						"ajax" : { 
							"url" : "${mmsCtx}/module/button!pageTree.htm?menuId=${menuId}&pageId=${pageId}",
							"data" : function (n) {  
								return {currentId : n.attr ? n.attr("id") :"0"  };   
							}
						}
			   },
			   "themes" : {  
				  "theme" : "default",  
				  "dots" : true,  
				  "icons" : true 
				 },
				 "ui":{
					 "select_multiple_modifier" : "alt"
				},
				 "plugins" : [ "themes", "json_data" ,"ui"]
			});
		}
		
		function selectView(){
			if(id!=""){
				var arr=id.split(";");//0:id;1:code;2:type
				if(arr[2]=="view"){
					window.parent.addViewValues(arr[0],arr[1],'${rowid}');
					window.parent.$("input[id='${rowid}']").focus();
					window.parent.$.colorbox.close();
				}else{
					alert("不能选择菜单,请选择页面");
				}
			}else{
				alert("请选择页面");
			}
		}
	</script>
								
</head>
<body style="padding: 5px;">
<div class="ui-layout-center">
	<div class="opt-btn">
			<button class="btn" onclick="selectView();"><span><span >确定</span></span></button>
	</div>
	<div id="viewTree" class="demo"></div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
