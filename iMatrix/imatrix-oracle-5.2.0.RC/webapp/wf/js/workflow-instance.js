function successSubmit(opt){
	if(opt=="saveWorkflow"){//第一个环节的保存操作
		startWorkflow();
	}else if(opt=="submitWorkflow"){//编辑式/提交,当是第一环节提交
		submit_task();
	}else if(opt=="saveTask"){//分发
		saveTask();
	}else if(opt=="distribute"){//分发
		distribute();
	}else if(opt=="approve"){//同意
		approve();
	}else if(opt=="refuse"){//不同意
		refuse();
	}else if(opt=="assign"){//交办
		assign();
	}else if(opt=="kiken"){//弃权
		kiken();
	}else if(opt=="oppose"){//拒绝
		oppose();
	}else if(opt=="agreement"){//赞同
		agreement();
	}else if(opt=="signoff"){//签收
		signoff();
	}else if(opt=="submit"){//编辑式/提交
		submitEdit();
	}
}

function startWorkflow(){
	ajaxSubmit("inputForm", webRoot + "/engine/workflow!save.htm", "wf_task",startWfCallBack);
}
function startWfCallBack(){
	$("#successMessage").show("show");
	setTimeout('$("#successMessage").hide("show");',3000);
}

function submit_task(){
	ajaxAnywhere.formName = "inputForm";
	ajaxAnywhere.getZonesToReload = function() {
		return "wf_task";
	};
	ajaxAnywhere.onAfterResponseProcessing = function () {
		if($("#backbutton").length==1&&$("#backurlform").length==1){
			$("#backbutton").show();
		}
		var m=$("#excepMessage").find("td").html();
		if(m==null||m==""){
			$("#successMessage").show();
			setTimeout('$("#successMessage").hide();',3000);
		}
	    var n=$("#nullAssignmentException").attr("value");
		if(typeof n !="undefined" && n!=""){
			$("#successMessage").hide();
			$.colorbox({href:webRoot+"/engine/workflow!choseTransactor.htm",iframe:true, width:700, height:400,overlayClose:false,title:"选择办理人"});
//			init_tb(webRoot+"/engine/workflow!choseTransactor.htm?TB_iframe=true&width=700&height=400","选择办理人");
		}
		showMsg("successMessage");
		//automaticHeight();
	};
	ajaxAnywhere.submitAJAX();
}

function input_ajax_form(){
	var url = $("#inputForm").attr("action");
	if(url.indexOf('task!submit.htm')>=0){
		$("#saveFormButton").hide();
		$("#submitFormButton").hide();
	}
	ajaxAnywhere.formName = "inputForm";
	ajaxAnywhere.getZonesToReload = function(){
		return "wf_task";
	};
	ajaxAnywhere.onAfterResponseProcessing = function () {
		automaticHeight();
		$("#successMessage").show("show");
		setTimeout('$("#successMessage").hide("show");',3000);
	};
	ajaxAnywhere.submitAJAX();
}
workflowButtonGroup.btnSubmitWorkflow.click = function(taskId){
	$("#opt").attr("value","submitWorkflow");
	saveInputForm(webRoot+'/engine/workflow!submit.htm');
};
//workflowButtonGroup.btnStartWorkflow.click = function(taskId){
//	saveInputForm(webRoot+'/engine/workflow!save.htm');
//};
function saveInputForm(url){
	var n=$("#nullAssignmentException").attr("value");
	if(n!=""){
		$("#nullAssignmentException").attr("value","");
	}
	$("#inputForm").attr("action",url);
	$("#inputForm").submit();
	var m=$("#excepMessage").find("td").html();
	if(m!=""){
		$("#excepMessage").remove();
	}
}

function opinion_ajax_form(){
	$("#approvalViews").ajaxSubmit(function (id){
		$("#message").show("show");
		setTimeout('$("#message").hide("show");',3000);
   });
}

function ajaXAnyWhere_u(url,zone,formName){
	$("#"+formName).attr("action",url);
	ajaxAnywhere.formName = formName;
	ajaxAnywhere.getZonesToReload = function(){
		return zone;
	};
	ajaxAnywhere.onAfterResponseProcessing = function () {
		if($("#backbutton").length==1&&$("#backurlform").length==1){
			$("#backbutton").show();
		}
		automaticHeight();
	};
	ajaxAnywhere.submitAJAX();
}


function setTree(list,selectId){
	var selected = false;
	for(var i = 0; i < list.length; i++){
		//是否本元素
		if($(list[i]).children('a').attr('class').indexOf('clicked') != -1){
			$($(list[i]).children('a')).click();
			selected = true;
		}else{
			//是否有子元素
			var subList = $(list[i]).children('ul').children("li");
			if(subList.length == 0){
				selected = false;
			}else{
				selected = setTree(subList);
			}
		}
		if(selected) break;
	}
	return selected;
}

function viewMyDelegate(ts1,cellval,opts,rwdat,_act){
	var v="<a  href=\"#\" hidefocus=\"true\" onclick=\"_click_fun("+opts.id+");\">" + ts1 + "</a>";
	return v;
}

function _click_fun(id){
	ajaxSubmit('defaultForm',webRoot+'/engine/workflow!input.htm?taskId='+id,'myprocess');
}








