<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/acs-taglibs.jsp"%>
<%@ taglib uri="http://ajaxanywhere.sourceforge.net/" prefix="aa" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	  <title><s:text name="company.companyManager"/></title>
	   <%@ include file="/common/acs-iframe-meta.jsp"%>
</head>
<body onload="getContentHeight();" style="padding: 15px;">
<div class="ui-layout-center">
<div class="opt-body">
	<div class="opt-btn" style="margin-bottom: 5px;">
		<button id='submitBtn' class='btn' onclick="selectOk('_function_tree');"><span><span>提交</span></span></button>
	</div>
	<div id="opt-content">
	<form id="functionForm" name="functionForm" action="">
		<input type="hidden" id="_isAddOrRomove" name="isAddOrRomove" value="${isAddOrRomove}"/>
		<input type="hidden" id="_roleId" name="roleId" value="${roleId}"/>
		<div id="functionIds"></div>
	</form>
	<div id=_function_tree class="demo"></div>
		<script type="text/javascript">
		$().ready(function () {
			$.ajaxSetup({cache:false});
			$("#_function_tree").jstree({ 
				"plugins" : [ "themes", "json_data" ,"checkbox","ui"],
				 "themes" : {
					 "theme" : "default",  
					 "dots" : true,  
					 "icons" : true 
				}, 
				"json_data" : {
					"ajax" : {
						"url" : "${acsCtx}/authorization/function-group!loadFunctionTree.action",
						"data" : function (n) {
							return { nodeId : (n.attr ? n.attr("id"):"INIT"),
								roleId:$('#_roleId').attr('value'),
								isAddOrRomove:$('#_isAddOrRomove').attr('value') };
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
		function selectOk(treeId){
			var arr = allUsers(treeId);
			if(arr.length <= 0){
				alert('请选择资源！');
				return;
			}
			$('#submitBtn').attr('disabled','disabled');
			for(var i=0; i<arr.length; i++){
				var type2id = arr[i].split('_');
				if(type2id[0]=="FUN"){
					$('#functionIds').append('<input type="hidden" name="functionIds" value="'+type2id[1]+'"/>');
				}
				if(type2id[0]=="SYSTEM"){
					$('#functionIds').append('<input type="hidden" name="functionIds" value="0"/>');
					break;
				}
			}
			ajax_new('functionForm','functionForm','${acsCtx}/authorization/role!roleAddFunction.action','', _calback);
		}
		function _calback(){
			msg = '角色添加资源成功';
			if($('#_isAddOrRomove').attr('value')==1){
				msg = '角色移除资源成功';
			}
			parent.$('#role_msg').html('<span style="color:red;">'+msg+'</span>');
			parent.$("#role_msg").children("span").fadeOut(3500);
			parent.$.colorbox.close();
		}
		function ajax_new(formName, fromId, url, zone, callback){
			$("#"+fromId).attr("action", url);
			ajaxAnywhere.formName = formName;
			ajaxAnywhere.getZonesToReload = function() {
				return zone;
			};
			ajaxAnywhere.onAfterResponseProcessing = function () {
				if(typeof callback == "function"){
					callback();
				}
			};
			ajaxAnywhere.submitAJAX();
		}
		</script>
		</div>
	</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
