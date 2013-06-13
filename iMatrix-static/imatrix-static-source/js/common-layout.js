var myLayout;
var isUsingComonLayout=true;
	$(document).ready(function () {
	if(!isUsingComonLayout)return;
		myLayout = $('body').layout({
			north__paneSelector:'#header',
			north__size: 66,
			west__size: 180,
			north__spacing_open: 31,
			north__spacing_closed: 31,
			west__spacing_open: 6,
			west__spacing_closed: 6,
			center__minSize:500,
			resizable: false,
			paneClass: 'ui-layout-pane',
			north__resizerClass: 'ui-layout-resizer',
			west__onresize: $.layout.callbacks.resizePaneAccordions,
			center__onresize:contentResize

		});
	});
	
		/**
	 * 隐藏/展开header事件
	 */
	function headerChange(obj){
		myLayout.toggle('north');
		if(myLayout.state['north'].isClosed){
			$(obj).attr('class', 'show-header');
			$(obj).attr('title', '展开');
				
		}else{
			$(obj).attr('class', 'hid-header');
			$(obj).attr('title', '隐藏');	
		}	
	}	
	
	
	/**/
	function onloadFun(){
		  if(window.$){
		    $(document).ready(function(){
		      /*去除链接，button，image button的点击时虚线框*/
		      $("a,input[type='button'],input[type='checkbox'],input[type='submit'],input[type='radio'],area,img,button").bind("focus",function(){
		        if(this.blur){
		          this.blur();
		        }
		      })
		    })
		  } else {
		    setTimeout("onloadFun()",1000);
		  }
		}
		//onloadFun();
		
	/*2011-11-07*/
		function selectSystems(id){
			$('#styleList').hide();
			if($('#sysTableDiv').attr('id')!='sysTableDiv'){
				var table = "<div id='sysTableDiv'><table id='systemTable'><tbody><tr><td><a>文档管理</a></td></tr><tr><td><a>内容管理</a></td></tr><tr><td><a>远程教育</a></td></tr></tbody></table></div>";
				$('body').append(table);
				addSysClickEvent();
			}
			$('#sysTableDiv').show();
			var position = $("#"+id).position();
			$('#sysTableDiv').css('top', (position.top+36)+'px');
			$('#sysTableDiv').css('right', '0px');
		}
		function addSysClickEvent() {
			$('#systemTable tbody tr td a').click(function(){
				var title = $('#lastSys').children('span').text().replace('<span><span class="flat">', '');
				$('#lastSys').html('<span><span class="flat"></span>'+$(this).html()+'</span>');
				$(this).html(title);
			});
		}
		
/*2012-06-26*//*更多按钮下拉框鼠标离开事件*/
		function sysTableDivhide() {
			$("#selectNumen").hover(
					function (over) {
						selectSystems("selectNumen");
					  },
					function (out) {
						  $("#sysTableDiv").hide();
						  $("#sysTableDiv").hover(
							function (over) {
								selectSystems("selectNumen");
							  },
							function (out) {
								  $("#sysTableDiv").hide();
							  }
							); 
						  
					  }
					); 
		}
/*2012-02-17*/ /*隐藏浮动框*/
		$(document).ready(function() {
			sysTableDivhide();
			$("body").click( function () {
				$('#sysTableDiv').hide();
				$('#styleList').hide();
			}); 
			getContentHeight();
			$('IFrame').attr('allowTransparency', 'true');
			//firefox中改变iFrame层次结构时，iframe会重新加载。比如：待办事宜列表加载两次
			//$('IFrame').wrap("<div class='iframebody'></div>");
			/*logo更换*/
			/*更改时，需注意图片宽度更改像素*//*图片高度为66px*/
			$("#header-logo ").css("background-image",'url("'+resourceRoot+'/images/logo.png")');
			$("#header-logo ").css("width",'350px');
		});
		
/** ========== */
	$(document).ready(function () {
		var h = $('#header-resizer').width() - $('#header-resizer .sec-forms').width() - $('#header-resizer .hid-header').width() - 100;
		$('#header-resizer .fix-menu').css('width', h+'px');
		$('#header-resizer .scroll-right-btn').css('left', h+25+'px');
		var lis = $('#header-resizer .scroll-menu').children('li');
		var l = 0;
		for(var i=0;i<lis.length;i++){
			l += $(lis[i]).width();
		}
		$('#header-resizer .scroll-menu').css('width', l+4+'px');
		if(l < h) {
			$('#header-resizer .scroll-left-btn').remove();
			$('#header-resizer .scroll-right-btn').remove();
		}
	});
	function _scrollLeft(){
		var l = $('#header-resizer .scroll-menu').position().left+$('#header-resizer .scroll-menu').width() - $('#header-resizer .fix-menu').width();
		if(l>0){
			$('#header-resizer .scroll-menu').animate({left : "-=" + 120 +"px"},600);
		}
	}
	function _scrollRight(){
		if($('#header-resizer .scroll-menu').position().left < 0){
			$('#header-resizer .scroll-menu').animate({left : "+=" + 120 +"px"},600);
		}
	}
