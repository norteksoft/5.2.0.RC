function showMessage(e) {
	var textLength = $.trim($(e).text()).length;
	if (textLength > 0) {
		var top = getTop(e) + e.offsetHeight;
		var left = getLeft(e);// + e.offsetWidth;
		$("#course-msg").css("position", "absolute");
		$("#course-msg").css("top", top);
		if (window.ActiveXObject)
			$("#course-msg").css("left", left - 215);
		else
			$("#course-msg").css("left", left);
		$("#course-msg").css("z-index", 1000);
		$("#course-msg").removeClass("dsplnone");
		$("#c-name").text($(e).text());
	}
}


function tabsMove(obj){
	$(obj).attr("src",webRoot+"/images/mclose1.jpg");
}

function tabsOut(obj){
	$(obj).attr("src",webRoot+"/images/mclose.jpg");
}
var webpageId;
function tabsClick(obj,val){
	webpageId=val;
	$("#tabsDivDefault").hide();
	$("#tabsDiv").css("position", "absolute");
	$("#tabsDiv").css("top",getTop(obj)+20);
	$("#tabsDiv").css("left",getLeft(obj));
	$("#tabsDiv").css("z-index",1000);
	$("#tabsDiv").show();
}
//默认【首页】设置
function tabsDefaultClick(obj,val){
	webpageId=val;
	$("#tabsDiv").hide();
	$("#tabsDivDefault").css("position", "absolute");
	$("#tabsDivDefault").css("top",getTop(obj)+20);
	$("#tabsDivDefault").css("left",getLeft(obj));
	$("#tabsDivDefault").css("z-index",1000);
	$("#tabsDivDefault").show();
}

function tabsAOver(obj){
	$(obj).css("background","#EFEFEF");
}

function tabsAOut(obj){
	$(obj).css("background","#FFFFFF");
}

function bodyClick(){
	$("#tabsDiv").hide();
	//默认【首页】页签点击后
	$("#tabsDivDefault").hide();
}

function stopBubble(e) {
	e = e?e:window.event;   
	if (window.event) { // IE   
	    e.cancelBubble = true;    
	} else { // FF   
	    e.stopPropagation();    
	}
}

function openWin() {
	var url="../jsp/course-view.html";
	var w=window.screen.width; 
	var h= window.screen.height; 
	var winwidth=990; 
	var winheight=650; 
	var win= (h - winheight)/2; 
	var winleft= (w- winwidth)/2; 
	if (window.showModalDialog)
	{
	window.showModalDialog(url,'write',"dialogWidth:990px;dialogHeight:650px;resizable=no;status:no;scroll:no;dialogLeft:"+winleft+"px;dialogTop:"+win+"px");
	}else { 
		window.myAction=this; 
		var DialogWin = window.open(url, 'write',"top="+win+",left="+winleft+',width=990,height=650,modal=yes,toolbar =no, menubar=no,scrollbars=no, resizable=no, location=no, status=no,titlebar=no');
		window.onclick=function (){DialogWin.focus()}; 
	}
}
//删除页签
function delPage(obj){
	bodyClick();
	if(confirm('您确定要删除页签吗？')){
		$(obj).attr("href",webRoot+"/index/index!deleteWebpage.htm?webpageId="+webpageId);
	}
}

//作用：让点击菜单选中。 所以点击菜单页签时，要调用该方法
//obj为菜单提供链接的<a>标签
function selectmenu(obj){
	var li =$("#mySecNav").children("li");
	for(var  i=0;i<li.length;i++){
		$(li[i]).attr("class","index");
	}
	$(obj).parent().attr("class","index selectind");
}
//删除小窗体
function delTool(obj){
	var div = $(obj).parents("div");
	var input = $(div).children("input");
	
	var webId = $("#webpageId").attr("value");
	var toolId = $(input).attr("value");
	
	$("#tool").attr("value",toolId);
	$("#page").attr("value",webId);
	
	$("#delTools").submit();
}

//设置参数
function setParameter(obj){
	var div = $(obj).parents("div");
	var input = $(div).children("input");
	var webId = $("#webpageId").attr("value");
	var toolId = $(input).attr("value");
	var imgId = $(obj).attr("id");
	if(imgId != "") return;
	$(obj).attr("id", "id_"+toolId);
	$.ajax({
		data:{widgetId:toolId},
		cache:false,
		type:"post",
		url:webRoot+"/index/index!parameterSet.htm",
		success:function(data, textStatus){
			if(data=="false"){
				alert("该小窗体没有参数");
			}else{
				if(data==null){
					window.location.reload();
				}
				$("#"+toolId).prepend(data);
			}
		},
		error:function(){
			    window.location.reload();
		}
	});
	
}

//ajaxAnywhere提交
function ajaxSubmit(form, url, zoons, ajaxCallback){
	var formId = "#"+form;
	if(url != ""){
		$(formId).attr("action", url);
	}
	ajaxAnywhere.formName = form;
	ajaxAnywhere.getZonesToReload = function() {
		return zoons;
	};
	ajaxAnywhere.onAfterResponseProcessing = function () {
		if(typeof(ajaxCallback) == "function"){
			ajaxCallback();
		}
	};
	ajaxAnywhere.submitAJAX();
}


//最大化
function max(obj){
	var div = $(obj).parents("div");
	var input = $(div).children("input");
	var toolId = $(input).attr("value");
	$("#tool").attr("value",toolId);
	alert(toolId);
	ajaxSubmit("delTools",webRoot+"/index/index!max.htm","max_zone");
	
} 

//员工查询
function doQuery(){
	var userName=$("#userName").val();
	var userDepart=$("#userDepart").val();
	var userSex=$("#userSex").val();//女:0男:1
	if(userName==""&&userDepart==""&&userSex==""){
		alert("请输入查询条件");
	}else{
	popWindowPortal(encodeURI(webRoot+"/public/public!getQueryResult.htm?userName="+userName+"&userDepart="+userDepart+"&userSex="+userSex));
	}
}

function taskList(){}

//显示提示信息，3秒后隐藏
function showMsg(id,time){
	if(id==undefined)id="message";
	$("#"+id).show();
	if(time==undefined)time=3000;
	setTimeout('$("#'+id+'").hide();',time);
}

//小窗体参数设置
function _widget_parameter_set(widget,webpageId){
	var div = widget.find('.widget-header').parents("div");
	var widgetId = $(div).attr("id").split('-')[1];
	$.ajax({
		data:{widgetId:widgetId,webpageId:webpageId},
		cache:false,
		type:"post",
		url:webRoot+"/index/index!parameterSet.htm",
		success:function(data, textStatus){
			if(data=="false"){
				alert("该小窗体没有参数");
			}else{
				if(data==null){
					window.location.reload();
				}
				widget.find('.widget-editbox').html(data);
			}
		},
		error:function(){
			    window.location.reload();
		}
	});
}

//提交FORM
function doSubmit(id){
	if(!submitCheckboxValue(id))return;
	ajaxSubmit("parameterForm_"+id,webRoot+"/index/index!save.htm","",callbback(id));
}
function callbback(id){
	$("#message_"+id).html("<font color=\"#13CD13\"><nobr>保存成功!</nobr></font>");
	setTimeout("$('#identifierwidget-"+id+"').find('.widget-editlink').click()",50);
	setTimeout('loadWidgetContent('+id+', "widget-content-'+id+'");',100);
}
//参数设置输入校验
function submitCheckboxValue(id){
	var showNum = $("#textId_"+id).val();
	if( typeof(showNum)=='undefined')showNum = $("#otherTextId_"+id).val();
	var intege1="^[0-9]\\d*$";	//正整数
	if(showNum==''){
		$("#message_"+id).html("<font color=\"#FF0000\"><nobr>请输入显示条数!</nobr></font>");
		showMsg("message_"+id);
		return false;
	}else if(showNum>parseInt(500)){
		$("#message_"+id).html("<font color=\"#FF0000\"><nobr>显示条数不能大于500条!</nobr></font>");
		showMsg("message_"+id);
		return false;
	}else if(showNum.match( intege1 )==null){
		$("#message_"+id).html("<font color=\"#FF0000\"><nobr>请输入数字!</nobr></font>");
		showMsg("message_"+id);
		return false;
	}else{
		return true;
	}
}

//index.jsp中用到的js
function _initialWidget(){
	$.fn.EasyWidgets({
		callbacks : {
			onChangePositions : function(positions){
				if(positions!=$('#widgetPosition').val()){
					$.post(webRoot+"/index/index!savePositions.htm", { webpageId: $('#webpageId').attr('value'), positions: positions },
						function(data){
							var posStrs = positions.split("|");
							var newPos = positions;
							if(posStrs.length==1){//1栏
								newPos = newPos+"|widget-place-center=|widget-place-right=";
							}else if(posStrs.length==2){//2栏
								newPos = posStrs[0]+"|widget-place-center=|"+posStrs[1];
							}
							$('#widgetPosition').val(newPos);
						});
				}
			},
			onClose : function (link, widget){
				var id=widget.attr('id').split('-')[1];
				var positions = deletePostion(id);
				$.post(webRoot+"/index/index!delete.htm", { webpageId: $('#webpageId').attr('value'), widgetId: id, positions: positions },
						function(data){
							$('#widgetPosition').val(positions);
						});
			},
			onRefreshPositions : function(){
				return $('#widgetPosition').val();
			},
			onAdd : function(widget, placeId){  },
			onEdit : function(link, widget){
				var webpageId = $("#webpageId").val();
				_widget_parameter_set(widget,webpageId);
				//widget.find('.widget-editbox').html('<input type="button" value="Close" class="widget-close-editbox">');
			},
			onCancelEdit : function(link, widget){
				widget.find('.widget-editbox').html('');
			},
			onCollapse: function(link, widget){
				var id=widget.attr('id').split('-')[1];
				$("#table_"+id).css("display","none");
				var div = $("#table_"+id).parent();
				$(div).css("display","none");;
			}
		},
		i18n : {
		      editText : '<span class="edit"> </span>',
		      closeText : '<span class="close"> </span>',
		      extendText : '<span class="extend"> </span>',
		      collapseText : '<span class="collapse"> </span>',
		      cancelEditText : '<span class="cancel"> </span>'
		    }
	});
}
function loadWidgetContents(){
	//获得widgetDiv中以identifierwidget-开头的div集合
	var __widgetIdInputs = $("#widgetDiv").find("div[id^='identifierwidget-']");
	var _widgetIds = "";
	var _widgetIdArr = new Array();
	var n=0;var j=0;
	for(var i=0;i<__widgetIdInputs.length;i++){
		var divId=$(__widgetIdInputs[i]).attr("id");
		var _widgetId = divId.split("-")[1];
		var contentDivId = "widget-content-"+_widgetId;
		var iframeable = $("#"+contentDivId).attr("iframeable");
		if(iframeable=="true"){
			var widgetUrl = $("#"+contentDivId).attr("widgetUrl");
			$("#contentIFrame-"+widgetId).attr("src",widgetUrl);
		}else{
			j++;
			n++;
			if(_widgetIds==""){
				_widgetIds = _widgetId;
			}else{
				_widgetIds = _widgetIds+","+_widgetId;
			}
			if(j/5.0==1||n==__widgetIdInputs.length){
				_widgetIdArr.push(_widgetIds);
				_widgetIds="";
				j=0;
			}
		}
	}
	for(var t=0;t<_widgetIdArr.length;t++){
		loadWidgetContent(_widgetIdArr[t], "",1);
	}
}

function loadWidgetContent(widgetIds, contentDivId,pageNo){
	if(typeof(contentDivId)!='undefined'&&contentDivId!=''){
		var iframeable = $("#"+contentDivId).attr("iframeable");
		if(iframeable=="true"){
			var widgetUrl = $("#"+contentDivId).attr("widgetUrl");
			$("#contentIFrame-"+widgetId).attr("src",widgetUrl);
		}else{
			loadWidgetDivContent(widgetIds,pageNo);
		}
	}else{
		loadWidgetDivContent(widgetIds,pageNo);
	}
}

function loadWidgetDivContent(widgetIds,pageNo){
	var webpageId=$("#_webpageId").attr("value");
	if(webpageId==""||typeof webpageId=='undefined'){
		webpageId = $("#webpageId").attr("value");
	}
	$.ajax({
		data:{widgetIdStrs:widgetIds, webpageId:webpageId,pageNo:pageNo},
		type:"post",
		async: true,
		url:webRoot+"/index/index!input.htm",
		success:function(data, textStatus){
			//data:{widgetId1:html}@#$%{widgetId2:html}@#$%....};
			var results= data.split("}@#$%");
			for(var i=0;i<results.length;i++){
				if(results[i]!=""&&typeof(results[i])!='undefined'){
					var widgetId = results[i].substring(results[i].indexOf("{")+1,results[i].indexOf(":"));
					var html = results[i].substring(results[i].indexOf(":")+1);
					//alert("data="+data);
					if(html=="error"){
						$("#widget-content-"+widgetId).html('<p style="text-align:center;margin:0;"><img src="../../images/loaderror.png"></p>');
					}else{
						//alert("contentDivId="+contentDivId);
						$("#widget-content-"+widgetId).html('<div class="contentleadTable" >'+html+'</div>');
					}
				}
			}
				
		},
		error:function(data){
			//data:widgetId1,widgetId2,....小窗体id的集合
			//获得widgetDiv中以identifierwidget-开头的div集合
			var __widgetIdInputs = $("#widgetDiv").find("div[id^='identifierwidget-']");
			for(var i=0;i<__widgetIdInputs.length;i++){
				var divId=$(__widgetIdInputs[i]).attr("id");
				var _widgetId = divId.split("-")[1];
				var contentDivId = "widget-content-"+_widgetId;
				$("#"+contentDivId).html('<p style="text-align:center;margin:0;"><img src="../../images/loaderror.png"></p>');
			}
		}
		});
}

function registerWidget(){
	$.colorbox({href:webRoot+"/index/show-register-widget.htm",iframe:true, innerWidth:$(window).width()-40, innerHeight:$(window).height()-60,overlayClose:false,title:"注册小窗体"});
}
//个人设置
function baseSetting(){
	$.colorbox({href:webRoot+"/index/base-setting-input.htm",iframe:true, innerWidth:400, innerHeight:400,overlayClose:false,onClosed:function(){baseSettingCallBack();},title:"个人设置"});
}

//个人设置窗口关闭回调
function baseSettingCallBack(){
	var url = window.location.toString();
	if(url.indexOf ("#")>0){
		url = url.substring(0,url.length-1);
	}
	window.location = url;
}
function _webpageChange(obj, url,webpageId){
	$(".addwg").show();
	if($(obj).attr('id')!=$("li.sec-selected").attr('id')){
		$("li.sec-selected").removeClass("sec-selected");
		$(obj).addClass("sec-selected");
		$("#_webpageId").attr("value",webpageId);
		ajaxAnyWhereSubmit('webpageChangeForm', url, 'webpage_zone', _webpageChangeCallback);
	}
}
function _webpageChangeCallback(){
	_initialWidget();
	loadWidgetContents();
}
function removeSearchBox(){}

function addTheme(){
	$.colorbox({href:webRoot+"/index/add-theme.htm",iframe:true, innerWidth:$(window).width()-500, innerHeight:$(window).height()-160,overlayClose:false,onClosed:function(){baseSettingCallBack();},title:"添加主题"});
}
