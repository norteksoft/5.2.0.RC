//个人消息弹窗		
function setMessageState(id){
	var url=imatrixRoot+"/portal/index/my-message-setState.htm?id="+id+"&callback=?";
	$.getJSON(
			url,
			function(data){
				getMeassagesTimeout();
			}
		);
}

function openMessageInput(id){
	$("#mess"+id).hide();
	$.colorbox({href:imatrixRoot+"/portal/index/my-message-view.htm?id="+id+"&isOpen=true",iframe:true, innerWidth:$(window).width()-40, innerHeight:$(window).height()-60,overlayClose:false,title:"个人消息管理"});
}

function messageOpen(url){
	window.open(url,"","fullscreen=1,scrollbars=yes,resizable=yes");
}
//taskWidgetId:待办事宜小窗体id
function taskMessageOpen(url,taskWidgetId){
	var flag=window.open(url,"","fullscreen=1,scrollbars=yes,resizable=yes");
	var time=(new Date()).getTime();
	$('body').everyTime('2s','timer'+time,function(){
		if(flag.closed){
			$('body').stopTime('timer'+time);
			//判断消息是否在portal中打开，如果是,刷新portal中任务列表
			if($('#portalMessage')!='undefined'&&$('#portalMessage').attr('value')=='portal'){
				loadWidgetContent(taskWidgetId, "widget-content-"+taskWidgetId);
			}
		}
	},0,true);
}
function getMeassagesTimeout(){
		var url=imatrixRoot+"/portal/index/my-message-getInfor.htm?callback=?";
		$.getJSON(
			url,
			function(data){
				if(data.msg=='error'){
					if($("#dialog").attr("id")=="dialog"){//判断是否有弹框
						$("#dialog").dialog("close");
					}
				}else{
					$("#dialog").html(data.msg);
					$("#dialog").dialog({stack:true,title:"<img style='float: left;width:16px;height:16px;' src='"+resourceRoot+"/images/mail.png'>个人消息",position:"right bottom",show:'1000',hide:'1000'});
				}
			}
		);
}

function openMessage(){
	$.colorbox({href:imatrixRoot+"/portal/index/my-message.htm",iframe:true, innerWidth:$(window).width()-40, innerHeight:$(window).height()-60,overlayClose:false,title:"个人消息管理"});
}
	
	$(function() {
		$("body").append('<div id="dialog" style="max-height:300px; overflow-y: auto;"></div>');//初始化
		//.dialog({ draggable: false,resizable:false })是否可以拖动
		//dialog({ height: 530,width:200 });设置高度
		getMessageInfor();
	});

function getMessageInfor(){
var url=imatrixRoot+"/portal/index/get-base-setting.htm?callback=?";
	$.getJSON(
			url,
			function(data){
				var result = data.msg.split("_");
				 if(result[0]=='true'){
					setTimeout('getMeassagesTimeout()', 1000);//定时取信息
					setInterval('getMeassagesTimeout()',parseInt(result[1])*1000);//定时取信息
				 }
			}
		);
}
