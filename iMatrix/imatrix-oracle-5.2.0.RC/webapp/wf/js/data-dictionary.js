function viewDict(id){
	$("#dict_id").attr("value", id);
	ajaxSubmit("defaultForm",  webRoot+"/engine/data-dictionary!input.htm", "dict_zone", validate);
	$("#dict_id").attr("value", "");
}
function deleteDict(url,type){
	var ids = jQuery("#main_table").getGridParam('selarrrow');
	if(ids==''){
		alert("请选择数据");
		return;
	}else{
		if(confirm("确认删除吗？")){
			$("#dictIds").attr("value",ids);
			setPageState();
			ajaxSubmit("defaultForm", url , "dict_zone","",type);
		}
	}
}
function deleteDictType(url){
	var ids = jQuery("#main_table").getGridParam('selarrrow');
	if(ids==''){
		alert("请选择数据");
		return;
	}else{
		if(confirm("引用该类型创建的数据字典也会被删除,确认删除吗？")){
			$("#dictIds").attr("value",ids);	
			setPageState();
			ajaxSubmit("defaultForm", url , "dict_zone","","page");
		}
	}
}
function getAllIds(redioId){
	var rds=$("input[name='dict_ids']:checked");
	var ids="";
	for(var i=0;i<rds.length;i++){
    	ids+=$(rds[i]).attr("value")+",";
    	}
     ids=ids.substring(0,ids.length-1);
     return ids;
    }

function validateReadio(){
    var ids=$("input[name='dict_ids']:checked");
    if(ids.length==0){
       return true;
    } 
       return false;
}
function addUsers(id){
	if($('#userNamesView').html()=="所有人员"){
		alert("已经是所有人员,请先清除再添加");return;
	}else{
		if(typeof(id)=='undefined' || id == "") id="0";
		$.colorbox({href:webRoot+'/engine/data-dictionary!addUsers.htm?id='+id+'&showPage="transactors"',iframe:true, innerWidth:700, innerHeight:400,overlayClose:false,title:"选择办理人",onClosed:function(){$("#addBtn").removeClass("cboxElement");}});
//		$.colorbox({href:webRoot+'/engine/data-dictionary!addUsers.htm?id='+id+'&showPage="transactors"',iframe:true, innerWidth:400, innerHeight:500,overlayClose:false,title:"选择办理人"});
//		init_tb(webRoot+'/engine/data-dictionary!addUsers.htm?id='+id+'&showPage="transactors"&TB_iframe=true&width=800&height=500','选择办理人');
	}
}
function idSlct(id){
	$("#dict_id").attr("value", id);
}
function saveDict(){
	var processes=$("input[name=processes]");
	var proMap=new Map();
	for(var i=0;i<processes.length;i++){
		proMap.put($(processes[i]).attr("value"),processes[i]);
	}
	for(var i=0;i<proMap.size();i++){
		for(var j=0;j<processDelInfo.size();j++){
			if(proMap.element(i)!=null){
				if(proMap.element(i).key==processDelInfo.element(j).value){
					$(proMap.element(i).value).remove();
					proMap.remove(proMap.element(i).key);
					i=i-1;
				}
			}
		}
	}
	processDelInfo.clear();
	$('#dictForm').submit();
}

function changeType(){
	var pId = $("#dict_useType").attr("value");
	if(pId == 0){
		$("#dict_operation").html($("#transactor_permission").html());
	}else if(pId == 1){
		$("#dict_operation").html($("#text_perimssion").html());
	}else{
		$("#dict_operation").html();
	}
}

var isProcessChanged = false;
function changeProcess(){
	isProcessChanged = true;
}
function setPid(pId){
	$("#dict_pid").attr("value", pId);
	if(pId == 0){
		$("#dict_tache").html("");
	}
	if(isProcessChanged && pId != 0){
		//请求环节
		$.post("data-dictionary!getWfdTaches.htm", "definitionId="+pId, changeTache);
		isProcessChanged = false;
	}
}
function changeTache(text){
	$("#dict_tache").html(text);
}

//将用户添加到页面
function addUser(user, view,type){
	if(view=="所有人员"){
		if(type==0){
			$('#userNamesView').html(view);
			$('#slcMan').html('');
			$('#slcMan').append('<input name="userNames" type="text" value="all_users" /><br/>');
		}
	}else{
		if(type==0){
			var userNames=$("input[name=userNames]");
			if($(userNames[0]).attr("value")=="all_users"){
				$(userNames[0]).remove();
				$('#userNamesView').html('');
			}
		}
		var lis = $('#slcMan').children('input');
		var isNeedAdd = true;
		for(var i=0; i<lis.length; i++){
			var info =$(lis[i]).attr('value');
			if(info.substring(0,info.indexOf(";"))==type){
				info=info.substring(info.indexOf("[")+1,info.indexOf("]"));
				if(user == info){
					isNeedAdd = false;
					break;
				}
			}
		}
		if(isNeedAdd){
			if(type==0){
				$('#userNamesView').html(getInfo($('#userNamesView').html(),view));
				$('#slcMan').append('<input name="userNames" type="text" value="0;'+view+'['+user+']" /><br/>');	
			}else if(type==1){
				$('#userNamesView').html(getInfo($('#userNamesView').html(),view));
				$('#slcMan').append('<input name="deptNames" type="text" value="1;'+view+'['+user+']" /><br/>');	
			}else if(type==2){
				$('#userNamesView').html(getInfo($('#userNamesView').html(),view));
				$('#slcMan').append('<input name="groupNames" type="text" value="2;'+view+'['+user+']" /><br/>');	
			}else if(type==3){
				$('#userNamesView').html(getInfo($('#userNamesView').html(),view));
				$('#slcMan').append('<input name="rankNames" type="text" value="3;'+view+'['+user+']" /><br/>');	
			}
		}
	}
}

//删除逗号后的信息
function getInfo(info,viewInfo){
	if(info==""){
		return viewInfo;
	}else{
		return info+","+viewInfo;
	}
}

function addInfo(userInfos,deptInfos,groupInfos,allUsers,rankInfos){
	var userInfoArr=new Array();
	var deptInfoArr=new Array();
	var groupInfoArr=new Array();
	var rankInfoArr=new Array();
	if(allUsers=="所有人员"){
		addUser("", "所有人员",0);
	}
	if(userInfos!=""){
		userInfoArr=userInfos.split(",");
	}
	if(deptInfos!=""){
		deptInfoArr=deptInfos.split(",");
	}
	if(groupInfos!=""){
		groupInfoArr=groupInfos.split(",");
	}
	if(rankInfos!=""){
		rankInfoArr=rankInfos.split(",");
	}
	var user="";
	var view="";
	for(var i=0;i<userInfoArr.length;i++){
		view=userInfoArr[i].substring(0,userInfoArr[i].indexOf("["));
		user=userInfoArr[i].substring(userInfoArr[i].indexOf("[")+1,userInfoArr[i].indexOf("]"));
		addUser(user, view,0);
	}
	for(var i=0;i<deptInfoArr.length;i++){
		view=deptInfoArr[i].substring(0,deptInfoArr[i].indexOf("["));
		user=deptInfoArr[i].substring(deptInfoArr[i].indexOf("[")+1,deptInfoArr[i].indexOf("]"));
		addUser(user, view,1);
	}
	for(var i=0;i<groupInfoArr.length;i++){
		view=groupInfoArr[i].substring(0,groupInfoArr[i].indexOf("["));
		user=groupInfoArr[i].substring(groupInfoArr[i].indexOf("[")+1,groupInfoArr[i].indexOf("]"));
		addUser(user, view,2);
	}
	for(var i=0;i<rankInfoArr.length;i++){
		view=rankInfoArr[i].substring(0,rankInfoArr[i].indexOf("["));
		user=rankInfoArr[i].substring(rankInfoArr[i].indexOf("[")+1,rankInfoArr[i].indexOf("]"));
		addUser(user, view,3);
	}
}

function submitRemove(){
	ajaxSubmit("dictForm", webRoot+"/engine/data-dictionary!removeDictUser.htm", "dict_zone");
}
function selectMan(id){
	var man = id.split(",");
	if(man[0] == "USER"){
		$("#wf_transactor").attr("value", man[2]);
	}
}
function saveCallback(msg){
	if(msg != "") $("#backMsg").html(msg);
	$("#backMsg").show();
	setTimeout('$("#backMsg").hide("show");',3000);
	validate();
}

function validate(){
	$("#dictForm").validate({
		submitHandler: function() {
			ajaxSubmit("dictForm",  webRoot+"/engine/data-dictionary!save.htm", "dict_zone", saveCallback);
		},
		rules: {
			info: "required",
			displayIndex:{
				required:true,
				digits:true,
				min:1
				}
		},
		messages: {
			info: "必填",
			displayIndex:{
				required:"必填",
				digits:"请输入整数",
				min: "请输入大于等于1的数字"
				}
		}
	});
}
		
function setDictTypeId(typeObj){
	$("#dict_tid").attr("value", $(typeObj.options[typeObj.selectedIndex]).attr('typeId'));
	$("#dict_noid").attr("value", $(typeObj.options[typeObj.selectedIndex]).attr('typeNo'));
}
function setProcessType(processType){
	if(processType==0){
		$("#addProcess").css("display","none");
		$("#processTb").css("display","none");
	}else if(processType==1){
		$("#addProcess").css("display","inline");
		addProcessTb();
	}
}

function viewDictType(opt,id){
	if(opt=="add"){
		$("#dict_id").attr("value","");
	}else {
		$("#dict_id").attr("value",id);
	}
	ajaxSubmit("defaultForm", webRoot+"/engine/data-dictionary-type!input.htm", "dict_zone", validateType);
}

function validateType(){
	$("#dictTypeForm").validate({
		submitHandler: function() {
			ajaxSubmit("dictTypeForm", webRoot+"/engine/data-dictionary-type!save.htm", "dict_zone", showTypeMsg);
		},
		rules: {
			no: "required",
			name:"required"
		},
		messages: {
			no: "必填",
			name:"必填"
		}
	});
}

function validateTypeNo(){
	$.ajax({
		   type: "POST",
		   url: "data-dictionary-type!validateTypeNo.htm",
		   data: "no="+$("#no").attr("value"),
		   success: function(data){
				if(data=="true"){
					alert('该编号已存在');
					$("#no").attr("value","");
				}
		   }
		}); 
}

function showTypeMsg(msg){
	if(msg != "") $("#backTypeMsg").html(msg);
	$("#backTypeMsg").show();
	setTimeout('$("#backTypeMsg").hide("show");',3000);
	validateType();
}

function save_type_form(){
	if($("#id").val()==""){
		$.ajax({
			   type: "POST",
			   url: "data-dictionary-type!validateTypeNo.htm",
			   data: "no="+$("#no").attr("value"),
			   success: function(data){
			   		if(data=="true"){
			   			alert('该编号已存在');
		   				$("#no").attr("value","");
			   		}else{
			   			$('#dictTypeForm').submit();
			   		}
			   }
			}); 
	}else{
		$('#dictTypeForm').submit();
	}
}

//弹出流程树
function addProcesses(id,btnId){
	if(typeof(id)=='undefined' || id == "") id="0";
	$.colorbox({href:webRoot+'/engine/data-dictionary!addProcesses.htm?id='+id,iframe:true, width:700, height:500,overlayClose:false,title:"选择流程及环节",onClosed:function(){$("#"+btnId).removeClass("cboxElement");}});
	//init_tb(webRoot+'/engine/data-dictionary!addProcesses.htm?id='+id+'&TB_iframe=true&width=700&height=500','选择流程及环节');
}
function addProcessTb(){
	$("#processRange").html('');
	$('#processDiv').html('');
	$("#processDiv").append('<table class="leadTable" id="processTb" style="width:440px;"><thead><tr><th style="width:300px;">流程</th><th style="width:140px;">操作</th></tr></thead><tbody id="processViewTb"></tbody></table>');
}
function addProcess(info,viewInfo){
	var processId=info.substring(0,info.indexOf(";"));
	$('#processRange').append('<input name="processes" type="text" value="'+info+'" />');
	$('#processViewTb').append('<tr><td>'+viewInfo+'</td><td><a href="#" onclick="deleteDictProcess(this,\''+processId+'\',\''+viewInfo+'\',\''+info+'\');">'+'删除'+'</td></tr>');
}

var processDelInfo=new Map();

function deleteDictProcess(obj,proId,proInfo,info){
	$(obj).parent().parent().remove(); 
	processDelInfo.put(info,proId+";"+proInfo);
}

function doQuery(obj){
	if($(obj).html() == "查询"){
		$(obj).html('取消查询');

		$('#queryDiv').css('display', 'block');
	}else{
		$("#queryTypeName").attr("value","");
		$("#queryTypeNo").attr("value","");
		$("#queryName").attr("value","");
		$(obj).html('查询');
		$('#queryDiv').css('display', 'none');
	}
}

function searchDataDict(){
	ajaxSubmit("queryForm", webRoot+"/engine/data-dictionary.htm", "dict_list_zone");
}

function getFormContent(){
	if($("#remark").html().length>255){
		var content=$("#remark").html();
		$("#remark").html(content.substring(0,255));
	}
}

function viewDictRank(opt,id){
	if(opt=="add"){
		$("#dict_id").attr("value","");
	}else {
		$("#dict_id").attr("value",id);
	}
	ajaxSubmit("defaultForm", webRoot+"/engine/data-dictionary-rank!input.htm", "dict_zone", validateRank);
}

function validateRank(){
	$.formValidator.initConfig({formid:"dictRankForm",onsuccess: function() {
		ajaxSubmit("dictRankForm", webRoot+"/engine/data-dictionary-rank!save.htm", "dict_zone", showRankMsg);
		return false;},onerror:function(){ }});
	$("#title").formValidator({onshow:'请输入',onfocus:'必填',oncorrect:'成功'}).inputValidator({min:1,onerror:"请输入"});
	$("#superiorUserName").formValidator({onshow:'请选择',onfocus:'必填',oncorrect:'成功'}).inputValidator({min:1,onerror:"请选择"});
	$("#userNames").formValidator({onshow:'请选择',onfocus:'必填',oncorrect:'成功'}).inputValidator({min:1,onerror:"请选择"});
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
		//$.colorbox({href:webRoot+'/engine/data-dictionary-rank!showUserTree.htm?id='+id,iframe:true, width:400, height:500,overlayClose:false,title:"组织结构树"});
		$("#"+btnId).colorbox({href:webRoot+'/engine/data-dictionary-rank!showUserTree.htm?id='+id,iframe:true, width:400, height:500,overlayClose:false,title:"组织结构树",onClosed:function(){$("#"+btnId).removeClass("cboxElement");}});
		//init_tb(webRoot+'/engine/data-dictionary-rank!showUserTree.htm?id='+id+'&TB_iframe=true&width=400&height=500','组织结构树');
	}else if(btnId=="selectSuperiorUser"){
		$("#"+btnId).colorbox({href:webRoot+'/engine/data-dictionary-rank!showSuperiorUserTree.htm?id='+id,iframe:true, width:400, height:500,overlayClose:false,title:"组织结构树",onClosed:function(){$("#"+btnId).removeClass("cboxElement");}});
		//init_tb(webRoot+'/engine/data-dictionary-rank!showSuperiorUserTree.htm?id='+id+'&TB_iframe=true&width=400&height=500','组织结构树');
	}
}
function dataDicBack(form,url,zoons){
	ajaxSubmit(form,url,zoons); 
}
//查看所有数据
function viewData(ts1,cellval,opts,rwdat,_act){
	var v="<a  href=\"#\" hidefocus=\"true\" onclick=\"viewDict("+opts.id+");\">" + ts1 + "</a>";
	return v;
}
//查看类型管理
function viewType(ts1,cellval,opts,rwdat,_act){
	var v="<a  href=\"#\" hidefocus=\"true\" onclick=\"viewDictType('view',"+opts.id+");\">" + ts1 + "</a>";
	return v;
}