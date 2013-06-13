<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<div id="accordion" class="basic">
	<h3><a href="${settingCtx}/rank/list-data.htm" id="rank-manager">用户上下级管理</a></h3>
	<div></div>
	<h3><a href="${settingCtx}/options/option-group.htm" id="option-group">选项组管理</a></h3>
	<div>
		<div class="demo" id="option_content" style="margin-top: 10px;"></div>
	</div>
	<h3><a href="${settingCtx}/holiday/holiday.htm" id="mms-holiday">节假日设置</a></h3>
	<div></div>
	<h3><a href="${settingCtx}/options/job-info.htm" id="job-info">定时设置</a></h3>
	<div>
		<div class="demo" id="job_info_tree" style="margin-top: 10px;"></div>
	</div>
	<h3><a href="${settingCtx}/options/import-definition.htm" id="import-manager">导入管理</a></h3>
	<div></div>
	<h3><a href="${settingCtx}/options/internation.htm" id="internation-manager">国际化设置</a></h3>
	<div></div>
	<h3><a href="${settingCtx}/signature/signature.htm" id="signature-manager">签章设置</a></h3>
	<div></div>
</div>
<script type="text/javascript">
	$().ready(function () {
		$("#accordion").accordion({fillSpace:true, change:accordionChange});
	});
	function accordionChange(event,ui){
		var url=ui.newHeader.children("a").attr("href");
		if(url=="${settingCtx}/options/job-info.htm"){
			if($("#job_info_tree").html()==""){
				createjobInfoTree("job_info_tree","${settingCtx}/options/job-info-dataTableStandardSysTree.htm");
			}
		}else if(url=="${settingCtx}/options/option-group.htm"){
			if($("#option_content").html()==""){
				createjobInfoTree("option_content","${settingCtx}/options/system-tree.htm");
			}
		}
		$("#myIFrame").attr("src",ui.newHeader.children("a").attr("href"));
	}

	//创建页面树菜单
	function createjobInfoTree(treeId,url){
		$.ajaxSetup({cache:false});
		$("#"+treeId).bind("loaded.jstree",function(e){
			var menuId=$("#menuId").attr("value");
			if(typeof (menuId)!="undefined" && menuId!=""){
				$("#"+$("#menuId").attr("value")).children("a").attr("class","jstree-clicked");
			}
		}).bind("select_node.jstree",function(e){
			var clickNodes=$("#"+treeId).find(".jstree-clicked");
			var node;
			if(clickNodes.length>0){
				node=clickNodes[0];
			}
			selectNode( $(node).parent().attr("id"),treeId);
		}).jstree({
			"json_data":{
					"ajax" : { "url" : url,
						"data" : function (n) {  
							return { };   
						}
					}
		   },
		   "themes" : {  
			  "theme" : "default",  
			  "dots" : true,  
			  "icons" : true 
			 },
			 "ui" : {  "select_multiple_modifier" : "alt" },
			 "plugins" : [ "themes", "json_data" ,"ui"]
		});
	}
	function selectNode(currentId,treeId){
		if(treeId=="job_info_tree"){
			$("#myIFrame").attr("src","${settingCtx}/options/job-info.htm?systemId="+currentId);
		}else if(treeId=="option_content"){
			if(currentId!="all_system"){
				$("#myIFrame").attr("src","${settingCtx}/options/option-group.htm?systemId="+currentId);
			}else{
				$("#myIFrame").attr("src","${settingCtx}/options/option-group.htm");
			}
		}
	}
</script>
