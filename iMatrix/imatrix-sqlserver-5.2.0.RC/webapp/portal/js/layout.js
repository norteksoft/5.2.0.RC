var myLayout;
//	$(document).ready(function () {
//		myLayout = $('body').layout({
//			north__paneSelector:'#header',
//			north__size: 66,
//			north__spacing_open: 31,
//			north__spacing_closed: 31,
//			center__minSize:500,
//			resizable: false,
//			paneClass: 'ui-layout-pane',
//			north__resizerClass: 'ui-layout-resizer'
//			,center__onresize : contentResize
//
//		});
//	});
//	function contentResize(){ _init_message_notice(); }

	
	function deletePostion(id){
		//widget-place-left=identifierwidget-4|widget-place-center=identifierwidget-1|widget-place-right=identifierwidget-2,identifierwidget-3
		var positions=$('#widgetPosition').val().split('|');
		var newPostion="";
		for(var i=0;i<positions.length;i++){
			position=positions[i]+',';
			if(position.indexOf('identifierwidget-'+id+',')<0){
				newPostion+=positions[i];
			}else{
				newPostion+=position.replace('identifierwidget-'+id+',','');
				if(newPostion.lastIndexOf(',')==(newPostion.length-1)){
					newPostion=newPostion.substr(0,newPostion.length-1);
				}
			}
			if(i!=(positions.length-1)){newPostion+='|';}
		}
		return newPostion;
	}
	
	function selectedNode(obj){
		window.location = $(obj).children('a').attr('href');
	}

	function popWindow(obj,url,key,width,height){
		url = processUrl(url);
		var taskid=url.substring(url.indexOf("=")+1,url.length);
		$('#widgetFlagId').attr("value",key);
		//var str="top=60px,left=60px,toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes,width=800,height=600";
		var str="fullscreen=1,toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=false,resizable=yes";
		if(width!="" &&  width!=undefined){
			str="top=50px,left=50px,toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes,width="+width+",height="+height;
		}
		var flag=window.open(url,'',str);
		if(width!="" &&  width!=undefined){
			flag .moveTo(50, 50);
			flag.resizeTo(width, height);
		}else{
			flag .moveTo(0, 0);
			flag.resizeTo(screen.width, screen.height);
		}
		var time=(new Date()).getTime();
		if(obj!='')
			$('body').everyTime('2s','timer'+time,function(){
				if(flag.closed){
					$('body').stopTime('timer'+time);
					var id=$($(obj).parents('.widget-content')[0]).attr('id');
					if(typeof(id)!='undefined'){
						var ids = id.split('-');
						if(ids.length>=3){
							loadWidgetContent(id.split('-')[2], id);
						}
					}
		        }
	    },0,true);
	}
	
	function processUrl(url){
		if(url.indexOf("http://")!=-1){
			return url;
		}else{
			var index = url.indexOf("/");
			var code = url.substring(0, index);
			var obj = $("#"+code).attr('id')!=undefined ? $("#"+code) : $("#"+code.toUpperCase());
			url = $($(obj).children()[0]).attr('href') + url.substring(index, url.length);;
			return url;
		}
	}
	function addWidget(){
		$.colorbox({href:webRoot+"/index/index!addWidget.htm?webpageId="+$('#webpageId').val(),iframe:true, innerWidth:600, innerHeight:500,overlayClose:false,title:"添加小窗体"});
	}
	function addWidgetToPage(_ids, _names, _placeId, html){
		var placeCode;
		switch(_placeId){
		case '0': placeCode="widget-place-left"; break;
		case '1': placeCode="widget-place-center"; break;
		case '2': placeCode="widget-place-right"; break;
		}
		var postions = $('#widgetPosition').attr('value');
		var array = postions.split('|');
		var wids=_ids.split(',');
		var newPostion = "";
		for(var i=0;i<array.length;i++){
			if(array[i].indexOf(placeCode)>=0){
				var ids = array[i].split('=');
				newPostion += (ids[0]+'=');
				if(ids[1]!=''){ newPostion += (ids[1]+','); }
				for(var j=0;j<wids.length;j++){
					newPostion +=('identifierwidget-'+wids[j]);
					if(j!=(wids.length-1)){newPostion+=',';}
				}
			}else{
				newPostion+=array[i];
			}
			if(i!=2) newPostion+="|";
		}
		$('#widgetPosition').val(newPostion);
		$('#_widgetCode').val(_ids);
		$('#_position').val(_placeId);
		$('#widgetForm').submit();
	}
	function addPage(){
		$.colorbox({href:webRoot+"/index/index!addWebpage.htm",iframe:true, innerWidth:500, innerHeight:300,overlayClose:false,title:"添加页签"});
	}
	function alterWebpage(obj){
		//var id = $(obj).parent().parent().attr('pageid');
		$.colorbox({href:webRoot+"/index/index!addWebpage.htm?webpageId="+webpageId,iframe:true, innerWidth:500, innerHeight:200,overlayClose:false,title:"修改页签"});
	}
	function delPage(obj){
		bodyClick();
		if(confirm('您确定要删除页签吗？')){
			//var id = $(obj).parent().parent().attr('pageid');
			$('#deleteWebpageId').attr('value', webpageId);
			$('#webpageForm').attr('action', webRoot+'/index/index!deleteWebpage.htm');
			$('#webpageForm').submit();
		}
	}
	function getTop(e) {
		var offset = e.offsetTop;
		if (e.offsetParent != null)
			offset += getTop(e.offsetParent);
		return offset;
	}
	function getLeft(e) {
		var offset = e.offsetLeft;
		if (e.offsetParent != null)
			offset += getLeft(e.offsetParent);
		return offset;
	}
	function stopBubble(e) {
		e = e?e:window.event;   
		if (window.event) { // IE   
		    e.cancelBubble = true;    
		} else { // FF   
		    e.stopPropagation();    
		}
	}
	
	
	//动态刷新某个窗口
	function freshWidget(widgetId){
		$.ajax({
			data:{widgetId:widgetId},
			type:"post",
			url:"${ctx}/index/index!input.htm",
			beforeSend:function(XMLHttpRequest){
				$("#"+widgetId).html('<img alt="" src="${ctx}/images/loading.gif"/>');
			},
			success:function(data, textStatus){
				$("#"+widgetId).html(data);
			},
	        error:function(){
				 $("#"+widgetId).height(16);
	             $("#"+widgetId).html("<p style=\"color:rgb(0,10000,10000);\">抱歉，动态刷新小窗体有误！</p>");
			}
		});
	}