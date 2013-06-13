<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/setting-iframe-meta.jsp"%>
	<title>用户上下级管理</title>
	
	<script type="text/javascript">
	
	function createRank(){
		viewDictRank('add');
		//var url = '${settingCtx}/rank/input.htm';
	}
	function updateRank(){
		var url = '${settingCtx}/rank/input.htm';
	}
	function deleteRank(){
		deleteDict('${settingCtx}/rank/delete.htm','page');
		//var url = '${settingCtx}/rank/delete.htm';
	}
	
	function deleteDict(url,type){
		var ids=jQuery("#_rank_table").getGridParam('selarrrow');
		if(ids.length==0){
			alert("请选择数据"); return;
		}
		
		if(confirm("确认删除吗？")){
			$.ajax({
				data:{dictIds:ids.join(",")},
				type:"post",
				url:url,
				beforeSend:function(XMLHttpRequest){},
				success:function(data, textStatus){
					if(data=='ok'){
						setPageState();
						ajaxSubmit('defaultForm',webRoot+'/rank/list-data.htm','dict_zone');
					}else{
						alert(data);
					}
				},
				complete:function(XMLHttpRequest, textStatus){},
		        error:function(){
	
				}
			});
		}
	}

	function validateReadio(){
		var ids=$("input[name^='jqg__rank_table']:checked");
	    if(ids.length==0){
			return true;
	    } 
		return false;
	}

	function viewDictRank(opt,id){
		if(opt=="add"){
			$("#dict_id").attr("value","");
		}else {
			$("#dict_id").attr("value",id);
		}
		ajaxSubmit("defaultForm", webRoot+"/rank/input.htm", "dict_zone", validateRank);
	}

	function validateRank(){
		$("#dictRankForm").validate({
			submitHandler: function() {
				ajaxSubmit("dictRankForm", webRoot+"/rank/save.htm", "dict_zone", showRankMsg);
			},
			rules: {
				title:"required",
				name: "required",
				userNames: "required"
			},
			messages: {
				title:"必填",
				name: "必填",
				userNames:"必填"
			}
		});
	}

	function showRankMsg(msg){
		if(msg != "") $("#backMsg").html(msg);
		$("#backMsg").show();
		setTimeout('$("#backMsg").hide("show");',3000);
		validateRank();
	}

	function chooseUser(btnId,id){
		if(typeof(id)=='undefined' || id == "") id="0";
		if(btnId=="selectUser"){
			$("#"+btnId).colorbox({href:webRoot+'/rank/user-tree.htm?id='+id,iframe:true, width:400, height:500,overlayClose:false,title:"组织结构树",onClosed:function(){$("#"+btnId).removeClass("cboxElement");}});
		}else if(btnId=="selectSuperiorUser"){
			$("#"+btnId).colorbox({href:webRoot+'/rank/superior-user-tree.htm?id='+id,iframe:true, width:400, height:500,overlayClose:false,title:"组织结构树",onClosed:function(){$("#"+btnId).removeClass("cboxElement");}});
		}
	}

	function ajaxSubmit(form, url, zoons, ajaxCallback,arg){
		if(typeof(ajaxCallback) == "function"){
			if(arg!=""){
				ajaxAnyWhereSubmit(form, url, zoons, ajaxCallback,arg);
			}else if(typeof(arg)!="undefined"){
				ajaxAnyWhereSubmit(form, url, zoons, ajaxCallback,arg);
			}else{
				ajaxAnyWhereSubmit(form, url, zoons, ajaxCallback);
			}
		}else{
			ajaxAnyWhereSubmit(form, url, zoons);
		}
	}
	function viewRank(ts1,cellval,opts,rwdat,_act){
		var value="<a  href=\"#\" hidefocus=\"true\" onclick=\"viewDictRank('view',"+opts.id+");\">" + ts1 + "</a>";
		return value;
	}
	</script>
</head>
<body>
<div class="ui-layout-center">
	<div class="opt-body">
		<form id="defaultForm" name="defaultForm"action="">
			<input id="dict_id" type="hidden" name="id"></input>
			<input id="dictIds" name="dictIds" type="hidden"></input>
		</form>
		<form action="" name="rankform" id="rankform" method="post">
			<input type="hidden" name="id" id="_id"/>
		</form>
		<aa:zone name="dict_zone">
			<div class="opt-btn">
				<button class="btn" onclick="createRank();"><span><span>新建</span></span></button>
				<button class="btn" onclick="deleteRank();"><span><span >删除</span></span></button>
			</div>
			<div id="opt-content" class="form-bg">
				<view:jqGrid url="${settingCtx}/rank/list-data.htm" code="BS_RANK" gridId="_rank_table"></view:jqGrid>
			</div>
		</aa:zone>
	</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>