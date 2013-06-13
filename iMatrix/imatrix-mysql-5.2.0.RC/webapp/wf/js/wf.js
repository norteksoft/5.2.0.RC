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
//公用的返回调用。如果本页面没有back()方法的实现，就调用父页面的，父页面也没有就关闭本窗口。
function back_main(){
	try{
		back();
	}catch(message){
		try{
			window.parent.back();
		}catch(message){
			//这种情况应该是自己做为独立页面被打开
			window.close();
		}
	}
}

//返回调用,保持页数。
function goBack(form,url,zone,jemesaId){
	//getPageStateAttr(jemesaId, form);
	ajaxSubmit(form, url, zone);
}

//去多少页
function class_go(form,url,zone,pagesize,inputId,ajaxCallback){
	var str=$("#"+inputId).attr("value");
	if(str==null&&str==""){
		return;
	}
	if(parseInt(str)<=0){
		$("#"+inputId).attr("value","");
		return;
	}
	if(parseInt(str)>pagesize){
		alert("输入的值超过范围");
		$("#"+inputId).attr("value","");
		return;
	}
	ajaxSubmit(form,url+str,zone,ajaxCallback);
}

//菜单选中
function selete(obj){
	if(obj!=null){
		$(".index").removeClass("selectind");
		$(obj).parent("li").addClass("selectind");
	}
}

function initIframe(iframeId){
    $("#"+iframeId).contents().find("body").css("background-color","transparent");
    $("#"+iframeId).contents().find('body').ready(function(){
		$("#"+iframeId,parent.document).height($("#"+iframeId).contents().find('.widget-place').height()+10);
	   parent.automaticHeight();
	}) ;
}


//作用：让点击菜单选中。 所以点击菜单页签时，要调用该方法
//obj为菜单提供链接的<a>标签
function selectmenu(obj){
	var li =$(obj).parent().parent().children("li");
	for(var  i=0;i<li.length;i++){
		$(li[i]).removeClass("selected");//.attr("class","");
	}
	$(obj).parent().addClass("selected");
}
//ss=setInterval(getFormContent,600)" onblur="clearInterval(ss)"
var formContentControlTimer ;
var areaId,areaTipId,areaMaxNum;
function fouceHandler(areaId,areaTipId,areaMaxNum){
	this.areaId=areaId;
	this.areaTipId=areaTipId;
	this.areaMaxNum=areaMaxNum;
	formContentControlTimer=setInterval(FormContentControl,600);
}
function blurHandler(){
	clearInterval(formContentControlTimer);
}
function FormContentControl(){
	var len = $("#"+areaId).html().length;
	if(len>areaMaxNum)$("#"+areaId).html($("#"+areaId).html().substring(0,areaMaxNum));
	$('#'+areaTipId).html("最大输入"+areaMaxNum+"字,你已经输入了"+len);
}
function resetJmessaPageNo(arg){
	if(arg=="" || typeof(arg)=="undefined")	arg="page";	
	changePageNoState(arg);
}

function getEsc(str){
	for(var i=0;i<str.length ;i++){
		str = str.replace("<","&lt;");
		str = str.replace(">","&gt;");
		str = str.replace("'","&apos;");
		str = str.replace('"',"&quot;");
	}
	return str;
}

