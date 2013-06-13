var isNewRuleType = true;
//新建规则类型
function createRuleType(){
	if(currentRuleTypeId == undefined){
		currentRuleTypeId="root";
	}
	isNewRuleType = true;
	$.colorbox({href:webRoot+"/authority/rule-type-input.htm?parentRuleTypeId="+currentRuleTypeId,iframe:true, innerWidth:350, innerHeight:200,overlayClose:false,title:"新建规则类型"});
}

//修改规则类型
function updateRuleType(){
	isNewRuleType = false;
	if(currentRuleTypeId=='' || currentRuleTypeId==null){
		$("#message").html("<font class=\"onError\"><nobr>请选择对应的规则类型！</nobr></font>");
		showMsg();
	}else if(currentRuleTypeId=='root'){
		$("#message").html("<font class=\"onError\"><nobr>请选择根节点以外的节点！</nobr></font>");
		showMsg();
	}else{
		$.colorbox({href:webRoot+"/authority/rule-type-input.htm?id="+currentRuleTypeId,iframe:true, innerWidth:350, innerHeight:200,overlayClose:false,title:"修改规则类型"});
	}
}

//新建或修改后回调（刷新主页tree）
function updateRuleTypeTree(id, name){
	if(isNewRuleType){
		$("#rule-type-tree").jstree("create","#"+currentRuleTypeId,"last",{ attr : {id : id}, data : name },function(){},true);
	}else{
		$("#rule-type-tree").jstree("refresh",-1);
	}
}

//删除
function deleterRuleType(){
	if(currentRuleTypeId=='' || currentRuleTypeId==null){
		$("#message").html("<font class=\"onError\"><nobr>请选择要删除的规则类型！</nobr></font>");
		showMsg();
	}else if(currentRuleTypeId=='root'){
		$("#message").html("<font class=\"onError\"><nobr>请选择根节点以外的节点！</nobr></font>");
		showMsg();
	}else{
		if(confirm("确认删除吗?")){
			$.get(webRoot+"/authority/rule-type-delete.htm?id="+currentRuleTypeId, function(data){
				if(data=="OK"){
					$("#message").html("<font class=\"onSuccess\"><nobr>"+"删除成功"+"</nobr></font>");
					showMsg();
					$("#rule-type-tree").jstree('remove');
				}else if(data=="NOT_DELETE_HAS_TYPE"){
					$("#message").html("<font class=\"onError\"><nobr>"+"此规则类别下含有子规则类别，不能删除！"+"</nobr></font>");
					showMsg();
				}else{
					$("#message").html("<font class=\"onError\"><nobr>"+"此规则类别下已经有数据规则，不能删除！"+"</nobr></font>");
					showMsg();
				}
				
			});
		}
	}
}

