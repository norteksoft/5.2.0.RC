
function postSuccess(){
	showMsg("successMessage");
}



function viewTask(taskId){
	$("#wf_form").attr("action", webRoot + "/engine/task!input.htm");
	$("#task_id").attr("value", taskId);
	ajaxAnywhere.formName = "wf_form";
	ajaxAnywhere.getZonesToReload = function() {
		return "wf_task";
	};
	ajaxAnywhere.onAfterResponseProcessing = function () {
		if($("#backbutton").length==1&&$("#backurlform").length==1){
			$("#backbutton").show();
		}
		automaticHeight();
	};
	ajaxAnywhere.submitAJAX();
}

function submitForm(transact,url){
	$("#wf_task_transact").attr("value", transact);
	if(url=="default" || url=="/engine/task!save.htm") {
		url = webRoot + "/engine/task!save.htm";
//		ajax("inputForm", url, "wf_task");
	}
//	else{
	$("#inputForm").attr("action",url);
	$("#inputForm").submit();
//	}
}

//提交
function submitEdit(){
	submitOperation();
}


//指定办理人
function assignTransactor(){
	transactTask("assignTransactor", webRoot + "/engine/task!assignTransactor.htm", "inputForm");
	
}

//签收
function signoff(){
	submitOperation();
}

//赞成
function agreement(){
	submitOperation();
}

//反对
function oppose(){
	submitOperation();
}

//弃权
function kiken(){
	submitOperation();
}

//交办
function assign(){
	submitOperation();
}

function getTaskId(){
	return $("#task_id").attr("value");
}

//选定办理人
function setTransactor(){
	transactTask("setTransactor", webRoot + "/engine/task!setTransactor.htm", "wf_condidates_form");
}

function transactTask(opt, action, form){
	$("#task_transact").attr("value", opt);
	$("#" + form).attr("action", action);
	ajaxAnywhere.formName = form;
	ajaxAnywhere.getZonesToReload = function() {
		return "wf_task";
	};
	ajaxAnywhere.onAfterResponseProcessing = function () {
		if($("#backbutton").length==1&&$("#backurlform").length==1){
			$("#backbutton").show();
		}
		automaticHeight();
	};
	ajaxAnywhere.submitAJAX();
}

function submitOpinion(){
	transactTask("",  webRoot + "/incoming/received-doc!submitOpinion.htm", "wf_opinion_form");
}

//交办
function assignTo(moreTransactor){
	var value=getInfo("user");
	if(value!="" && typeof value!="undefined"){
		if(moreTransactor=='true'){
			var arr=eval(value);
			for(var i=0;i<arr.length;i++){
				if(arr[i].type=="user" || arr[i].type=="allDepartment" || arr[i].type=="company"){
					if(arr[i].type=="user"){
						$('#wf_form').append('<input name="transactors" type="hidden" value="'+arr[i].loginName+'" />');
					}else if(arr[i].type=="allDepartment" || arr[i].type=="company"){
						$('#wf_form').append('<input name="transactors" type="hidden" value="all_user" />');
						break;
					}
				}
			}
		}else if(moreTransactor=='false'){
			var user=eval(info);
			var type=user[0].type;
			var loginName =user[0].loginName;
			if(type == "user"){
				$('#wf_form').append('<input name="transactors" type="hidden" value="'+loginName+'" />');
			}
		}
	}
	var tor=$("input[name=transactors]");
	if(tor.length>0){
		ajaxSubmit("wf_form",webRoot + "/engine/task!assignTo.htm","wf_task", postSuccess);
	}else{
		alert('请选择用户');
	}
}

function saveChioceTache(){
	$("#choiceForm").ajaxSubmit(function (result){
		var type=  result.substring(0,3);
		var content = result.substring(3,result.length);
		var joinchr = content.indexOf("?")?"&":"?";
		if(type=='OK:'){
			$.colorbox.close();
			//tb_remove();
		}else if(type=='MS:'){
			alert(content);
		}else if(type=='RU:'){
			$.colorbox({href:webRoot+content+joinchr,iframe:true, width:600, height:300,overlayClose:false,title:"指定办理人"});
//			init_tb(webRoot+content+joinchr+'TB_iframe=true&width=600&height=300','指定办理人');
		}else if(type=='TC:'){
			$.colorbox({href:webRoot+content+joinchr,iframe:true, width:600, height:300,overlayClose:false,title:"指定环节"});
		//	init_tb(webRoot+content+joinchr+'TB_iframe=true&width=600&height=300','指定环节');
		}
	});
}
