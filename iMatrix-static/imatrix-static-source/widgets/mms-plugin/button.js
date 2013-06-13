function saveButton(){
	$('#buttonSaveForm').submit();
}

function saveButtonCallBack(){
	showMsg();
	validateBtns();
}

function validateBtnCode(codeId,btnId){
	if($("#"+btnId).val()=="" || typeof($("#"+btnId).val())=="undefined"){
		$("#"+codeId).blur(function(){
			$.ajax({
				   type: "POST",
				   url: "button!validateCode.htm",
				   data: "code="+$("#"+codeId).attr("value")+"&pageId="+$("#page_id1").attr("value"),
				   success: function(data){
				   		if(data=="true"){
				   			alert('编号 '+$("#"+codeId).attr("value")+' 已存在');
			   				$("#"+codeId).attr("value","");
			   				$("#"+codeId).focus();
				   		}
				   }
				}); 
		});
	}
}
///////////////////////////
//插入一行
function insertRow(){
	if($("#contentTable").find("input[name='columnIdRadio'][ checked=true]").length==0){
		$("#contentTable").find("tbody").append($("#mata").find("tbody").html());
	}else{
		$("#contentTable").find("input[name='columnIdRadio'][ checked=true]").parent().parent().before($("#mata").find("tbody").html());
	}
	sortOrder();
}
//追加一行
function appendRow(){
	$("#contentTable").find("tbody").append($("#mata").find("tbody").html());
	sortOrder();
}
//删除一行
function deleteRow(){
	if($("#contentTable").find("input[name='columnIdRadio'][ checked=true]").length!=0){
		$("#contentTable").find("input[name='columnIdRadio'][ checked=true]").parent().parent().remove();
	}
	sortOrder();
}
//向上移动
function prevRow(){
	if($("#contentTable").find("input[name='columnIdRadio'][ checked=true]").length!=0){
		var num=$("#contentTable").find("input[name='columnIdRadio'][ checked=true]").parent().parent().find("input[name='displayOrder']").attr("value");
		if(num>1){
			$("#contentTable").find("input[name='columnIdRadio'][ checked=true]").parent().parent()
			.prev().before($("#contentTable").find("input[name='columnIdRadio'][ checked=true]").parent().parent());
			$("#contentTable").find("input[name='displayOrder'][value="+num+"]").parent().parent().find("input[name='columnIdRadio']").attr("checked",true);
		}
	}
	sortOrder();
}
//向下移动
function nextRow(){
	if($("#contentTable").find("input[name='columnIdRadio'][ checked=true]").length!=0){
		var len=$("#contentTable").find("input[name='columnIdRadio']").length;
		var num=$("#contentTable").find("input[name='columnIdRadio'][ checked=true]").parent().parent().find("input[name='displayOrder']").attr("value");
		if(num<len){
			$("#contentTable").find("input[name='columnIdRadio'][ checked=true]").parent().parent()
			.next().after($("#contentTable").find("input[name='columnIdRadio'][ checked=true]").parent().parent());
			$("#contentTable").find("input[name='displayOrder'][value="+num+"]").parent().parent().find("input[name='columnIdRadio']").attr("checked",true);
		}
	}
	sortOrder();
}

function validateBtns(){
	$.formValidator.initConfig({formid:"buttonSaveForm",onsuccess: function() {successSave();
	return false;},onerror:function(msg){}});
	sortOrder();
}

function successSave(){alert(11);
	//列表组建需调的方法
	getSubTableDatas("buttonSaveForm");
	
	ajaxSubmit("buttonSaveForm",  webRoot+"/module/button!saveButtons.htm", "contentZone", saveButtonCallBack);
}
//排序
function sortOrder(){
	$("#contentTable").find("input[name='displayOrder']").each(function(i){
		$(this).attr("value",i+1);
		var columnIdRadio =  $("#contentTable").find("input[name='columnIdRadio']")[i];
		var columnId =  $("#contentTable").find("input[name='columnId']")[i];
		var code =  $("#contentTable").find("input[name='code']")[i];
		var name =  $("#contentTable").find("input[name='name']")[i];
		var toPageCode =  $("#contentTable").find("input[name='toPageCode']")[i];
		var toPageId =  $("#contentTable").find("input[name='toPageId']")[i];
		var event=$("#contentTable").find("textArea[name='event']")[i];
		$(columnIdRadio).attr("id","columnIdRadio"+i);
		$(columnId).attr("id","columnId"+i);
		$(code).parent().find("span").remove();
		$(name).parent().find("span").remove();
		$(toPageCode).parent().find("span").remove();
		$(code).attr("id","code"+i);
		$(code).after("<span id='code"+i+"Tip'></span>");
		$(name).attr("id","name"+i);
		$(name).after("<span id='name"+i+"Tip'></span>");
		if(typeof($(toPageCode).attr("id"))=="undefined"||$(toPageCode).attr("id")==""){
			$($(toPageCode).nextAll()[1]).click(function(){selectView(i);});
		}
		$(toPageCode).attr("id","toPageCode"+i);
		$(toPageCode).after("<span id='toPageCode"+i+"Tip'></span>");
		$(toPageId).attr("id","toPageId"+i);
		if(typeof($(event).attr("id"))=="undefined"||$(event).attr("id")==""){
			$($(event).nextAll()[0]).click(function (){init_tb1(webRoot+'/module/button!showEvent.htm?currentDisplayOrder='+i+'&TB_iframe=true&width=600&height=400');});
		}
		$(event).attr("id","event"+i);
		$("#code"+i).formValidator({onshow:"必填",onfocus:"请输入",oncorrect:"正确"}).inputValidator({min:1,empty:{emptyerror:'错误'},onerror:'错误'});
		$("#name"+i).formValidator({onshow:"必填",onfocus:"请输入",oncorrect:"正确"}).inputValidator({min:1,empty:{emptyerror:'错误'},onerror:'错误'});
		validateBtnCode("code"+i,"columnId"+i);
	});
}
/////////////////////////
function selectView(index){
	init_tb1(webRoot+'/module/button!selectPage.htm?pageId='+$("#page_id1").attr("value")+'&menuId='+$("#menuId").attr("value")+'&currentDisplayOrder='+index+'&TB_iframe=true&width=400&height=500','选择页面');
}

