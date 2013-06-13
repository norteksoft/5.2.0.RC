<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/mms-taglibs.jsp"%>
<div id="accordion" >
	<h3><a href="permission-data.htm" id="permission-manager">数据授权</a></h3>
	<div>
		<div class="demo" id="permission-manager_content" style="margin-top: 10px;"></div>
	</div>
		
	<h3><a href="data-rule.htm" id="data-rule-manager">数据规则</a></h3>
	<div>
		<div class="demo" id="data-rule-manage_content" style="margin-top: 10px;"></div>
	</div>
	<h3><a href="rule-type.htm" id="datatable-manager">规则类别</a></h3>
	<div>
		<div class="demo" id="data_table_manage_content" style="margin-top: 10px;"></div>
	</div>
</div>
<style>
.jstree-classic.jstree-focused { background:none; }
</style>
<script type="text/javascript">
	$(function () {
		$("#accordion").accordion({fillSpace:true, change:accordionChange});
	});
	function accordionChange(event,ui){
		var url=ui.newHeader.children("a").attr("href");
		if(url=="permission-data.htm"){
			if(jQuery.jstree._reference("#permission-manager_content")!=null){
				jQuery.jstree._reference("#permission-manager_content").destroy();
			}
			ruleTypeDataTree('permission-manager_content','/authority/rule-type-data-tree.htm');
		}else if(url=="data-rule.htm"){
			if(jQuery.jstree._reference("#data-rule-manage_content")!=null){
				jQuery.jstree._reference("#data-rule-manage_content").destroy();
			}
			ruleTypeDataTree('data-rule-manage_content','/authority/data-rule-type-tree.htm');
		}else if(url=="rule-type.htm"){
			url = webRoot+"/authority/rule-type.htm";
		}
		$("#myIFrame").attr("src",url);
	}

	function ruleTypeDataTree(treeId,url){
		$.ajaxSetup({cache:false});
		$("#"+treeId).bind("select_node.jstree",function(e){
			selectNode($("#"+treeId).find(".jstree-clicked").parent().attr("id"),treeId);
		}).jstree({
			"json_data":{
					"ajax" : { "url" : webRoot+url,
						"data" : function (n) {  
							return { };   
						}
					}
		   },
		   "themes" : {  
			  "theme" : "classic",  
			  "dots" : true,  
			  "icons" : true 
			 },
			 "ui":{ "select_multiple_modifier" : "alt"},
			 "types" :{
					"types" : {
						"root" : {
							"icon" : {
								"image" : "${mmsCtx}/images/root.gif"
							}
						},
						"ruleType" : {
							"icon" : {
								"image" : "${mmsCtx}/images/ruleType.gif"
							}
						},
						"dataRule" : {
							"icon" : {
								"image" : "${mmsCtx}/images/dataRule.gif"
							}
						}
					}
				 },
			 "plugins" : [ "themes", "json_data" ,"ui","types"]
		});
	}

	function selectNode(currentId,treeId){
		if(treeId=='permission-manager_content'){
			if(currentId.indexOf("dataRule_")>=0){
				var dataRuleId=currentId.substring(currentId.indexOf("_")+1);
				$("#myIFrame").attr("src","${mmsCtx}/authority/permission-data.htm?dataRuleId="+dataRuleId);
			}else{
				$("#myIFrame").attr("src","${mmsCtx}/authority/permission-data.htm");
			}
		}
		if(treeId=='data-rule-manage_content'){
			if(currentId.indexOf("ruleType_")>=0){
				var ruletypeId=currentId.substring(currentId.indexOf("_")+1);
				$("#myIFrame").attr("src","${mmsCtx}/authority/data-rule.htm?ruletypeId="+ruletypeId);
			}else{
				$("#myIFrame").attr("src","${mmsCtx}/authority/data-rule.htm");
			}
		}
	}
</script>
