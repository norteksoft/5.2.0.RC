var datepickerInput;
if(typeof($.jgrid)!='undefined')
jQuery.extend($.jgrid.defaults,{
	datatype: "json",
	jsonReader:{
		repeatitems:false
	},
	rowNum: 20,
	prmNames:{
		rows:'page.pageSize',
		page:'page.pageNo',
		sort:'page.orderBy',
		order:'page.order'
	},
	autowidth: true,
	height: 330,
	pager: '#pager', 
	viewrecords: true, 
	sortorder: "desc",
	shrinkToFit:false,
	multiselect: true,
	mtype:"POST"
});

function jgridDelRow(tableId,url,name) {
	var ids = jQuery("#"+tableId).getGridParam('selarrrow');
	if(ids==''){
		alert(delRowWarning);
	}else if(ids==0){
		jQuery("#"+tableId).jqGrid('delRowData', ids[0]);
	}else{
		var prmt = {};
		if(name) prmt[name]=ids.join(',');
		else prmt['ids']=ids.join(',');
		$.post(url, prmt, function(data) {
			jQuery("#"+tableId).jqGrid().trigger("reloadGrid"); 
		});
	}
}

/************************************************************
 模块名称: 公用JS
 编写时间: 2010年6月13日
 编    程: 张清欣
 说    明: 无
 ************************************************************/

/*---------------------------------------------------------
函数名称:
参          数:
功          能:onLoad
------------------------------------------------------------*/
$(document).ready( function() {	
	//liudongxia 日期控件清空有用
	$(".ui-datepicker-close").live("click", function(){$(datepickerInput).attr("value" , "");});
	//liudongxia 结束
	hideDashedBox();
	$("body").click( function () {
		//parent.$('#sysTableDiv').hide();
		//parent.$('#styleList').hide();
	}); 
});

/*---------------------------------------------------------
函数名称:hideDashedBox
参          数:
功          能:IE下自动去掉虚框
------------------------------------------------------------*/
function hideDashedBox(){
	var as = $("a");
	for(var i = 0; i < as.length; i++){
		$(as[i]).attr('hideFocus', 'true');
	}
}

/*
 * ajaxAnyWhere 提交表单的公共方法
 * @param form 表单id （必须给的参数）
 * @param url 请求url （如果给空串，将使用Form的action属性上的url）
 * @param zoons 替换的区域名，多个区域之间以逗号隔开 （必须参数）
 * @param ajaxCallback  回调方法名 （可选）
 * @param removeSearchFlag 是否去除查询，默认是true
 * @param arg1 回调函数的参数1 （可选）
 * @param arg2 回调函数的参数2 （可选）
 * @param arg3 回调函数的参数3 （可选）
 */
 var buttonFlag=0;
function ajaxAnyWhereSubmit(form, url, zoons, ajaxCallback,arg1,arg2,arg3){
	if(buttonFlag==0){
		buttonFlag=1;
		var formId = "#"+form;
		if(url != ""){
			$(formId).attr("action", url);
		}
		ajaxAnywhere.formName = form;
		ajaxAnywhere.handleException = function(type, details) {
			buttonFlag=0;
			var div = "<TABLE height=\"200\" cellSpacing=0 cellPadding=0 width=\"300\" align=\"center\" border=\"0\">" +
						"<TR>" +
							"<TD vAlign=\"center\" align=\"middle\">" +
							"错误类型：" + type+
							"<br/>错误信息：" +details+
							"</td>" +
						"</tr>" +
					"</table>";
			$.colorbox({html:div,title:"错误提示"});
		};
		ajaxAnywhere.handleHttpErrorCode = function(code){
			buttonFlag=0;
			if(code==403){
				$.colorbox({href:webRoot+'/common/thickbox_403.jsp',iframe:true, innerWidth:600, innerHeight:300,overlayClose:false,title:"没有权限"});
			}else{
				var div = "<TABLE height=\"100\" cellSpacing=0 cellPadding=0 width=\"300\" align=\"center\" border=\"0\">" +
							"<TR>" +
								"<TD vAlign=\"center\" align=\"middle\">" +
								"http Error code:" + code+
								"</td>" +
							"</tr>" +
						"</table>";
				$.colorbox({html:div});
			}
		};
		ajaxAnywhere.getZonesToReload = function() {
			return zoons;
		};
		ajaxAnywhere.onBeforeResponseProcessing = function() {
			//response被处理前
		};
		ajaxAnywhere.onAfterResponseProcessing = function () {
			buttonFlag=0;
			if(typeof(ajaxCallback) == "function"){
				if(arg3!=""||typeof(arg3)!="undefined"){
					ajaxCallback(arg1,arg2,arg3);
				}else if(arg2!=""||typeof(arg2)!="undefined"){
					ajaxCallback(arg1,arg2);
				}else if(arg1!=""||typeof(arg1)!="undefined"){
					ajaxCallback(arg1);
				}else{
					ajaxCallback();
				}
			}
			removeSearchBox();
		};
		ajaxAnywhere.submitAJAX();
	}
}
var aa={
	submit:	ajaxAnyWhereSubmit
};
//改变列表高度和宽度
function contentResize(){
 var searchContent = $getObjById("searchArea");
	var ids =[];
	var tableObj = $getObjByClass("ui-jqgrid-btable");
	tableObj.each(function(){
		//this.id.indexOf("_frozen")<0 判断是否含有冻结列，如果有则不给该子表计算大小
		if(this.id != undefined&&$("#"+this.id).closest("._custom_table").length<1&&this.id.indexOf("_frozen")<0){//$("#"+this.id).closest("._custom_table").length<1 判断是否是formGrid标签，如果是则不给该子表计算大小		
			ids.push(this.id);			
		}
	});
	var h,w;
	if(searchContent.length>0){
		//改变查询div的位置
		changePosition(); 
		//隐藏左侧栏时，重设查询框的宽度
		if($("#containerSearchInput").val()!="false"&&$("#containerSearchInput").val()!=undefined){//嵌入式
			//加12表示$("#search_box")比$("#parameter_Table")右边距宽出的距离，为了$("#parameter_Table")与列表组建对齐
			$("#search_box").css("width",$('#opt-content').width()+12);  
		}	
	}
	
	if(ids.length>0){
		h=_getTableHeight(ids);
		w=_getTableWidth();
	}
	for(var i=0;i<ids.length;i++){
		if($existObjByClass("ui-jqgrid-btable")){//同一页面调整列表大小
			$setJqGridSize(ids[i],h,w);
		}
		if($("#parameter_Table").height()>0){//固定查询 
			$('#gbox_'+ids[i]).css('top',$('#parameter_Table').height()+5);
		}else if($("#advanced_search_table_id").height()>0){//高级查询
			$('#gbox_'+ids[i]).css('top',205);
		}else{
			$('#gbox_'+ids[i]).css('top','');
		}
	}
	var trObj = $getObjByClass("jqgfirstrow");
	trObj.each(function(){
		$(this).css("height",0.01);
	});
	
	contentResizeCallback();
}

function contentResizeCallback(){}

function $setJqGridSize(id,h,w){
	jQuery("#"+id).jqGrid('setGridHeight',h);
	jQuery("#"+id).jqGrid('setGridWidth',w);
}
//变为固定查询调整列表
function contentResizeForFixed(){
	searchResize("fixed");
}
//变为高级查询调整列表
function contentResizeForAdvanced(){
	searchResize("advanced");
}
function searchResize(tableType){
	changePosition();
	var searchDivHeight=0;
	if(tableType == "fixed"){
		if($('#parameter_Table').attr("id")=="parameter_Table"){
			searchDivHeight=$('#parameter_Table').height();
		}
	}
	var ids =[];
	var tableObj = $getObjByClass("ui-jqgrid-btable");
	tableObj.each(function(){
		if(this.id != undefined){
			ids.push(this.id);
		}
	});
	for(var i=0;i<ids.length;i++){
		jQuery("#"+ids[i]).jqGrid('setGridHeight',_getTableHeight(ids));
		jQuery("#"+ids[i]).jqGrid('setGridWidth',_getTableWidth());
		if(tableType=="fixed"){
			if(searchDivHeight==0){
				$('#gbox_'+ids[i]).css('top',205);
			}else{
				$('#gbox_'+ids[i]).css('top',searchDivHeight+5);
			}
		}else if(tableType=="advanced"){
			$('#gbox_'+ids[i]).css('top',205);
		}
	}
}
function _getTableHeight(ids){
	if($getObjById('parameter_Table').height()>0){//固定查询
		if($('#parameter_Table').height()>0){
			heightOfSearchDiv=$('#fixedSearchZoon').height()+4;   //4为$('#fixedSearchZoon')内边框，约数
		}
	}else if($("#advanced_search_table_id").height()>0){//高级查询
		heightOfSearchDiv=$("#searchZoon").height()+13;           //13为$('#searchZoon')内边框，约数
	}else if($("#advanced_search_table_id").height()==0){//高级查询
		heightOfSearchDiv=0;
	}else{//查询中点确定按钮(移除)
		heightOfSearchDiv=0;
	}
	var h=0;
	if(ids.length>1){
		h=$('.ui-layout-center').height()-185-heightOfSearchDiv;
		
	}else{
		h=$('.ui-layout-center').height()-110-heightOfSearchDiv;//110px包括opt-button、gqgrid的表头、表尾和一些内边框，外边框的和
	}
	
	var layoutCenterHeight = $('.ui-layout-center').height();
		if($(".opt-btn").height()>0){
			$("#opt-content").css("height",layoutCenterHeight-55); 
		}else{
			$("#opt-content").css("height",layoutCenterHeight-20); 
		}
	
	if($('.ui-jqgrid-sdiv').length==1){//列表中含有一个合计行时
		h=h-$('.ui-jqgrid-sdiv').height();
	}else if($('.ui-jqgrid-sdiv').length>1){//列表中含有多个合计行时
		h=h-$('.ui-jqgrid-sdiv').height()*$('.ui-jqgrid-sdiv').length;
	}
	var tableHeight=h/ids.length;
	var  tabObj = $getObjByClass("widget-place");//带有小窗体的页面,portal中
	
	if(tabObj.length>0){
		tableHeight=tableHeight-45;
	}	
	var tabObj = $getObjByClass("ui-tabs");
	if(tabObj.length>0){
		tableHeight=tableHeight-20;
	}
	$("#opt-content").css("overflow","auto");
	return tableHeight;
}

function _getTableWidth(){
	var w = $('.ui-layout-center').width()-30;
	if($.browser.msie){
		var obj=document.getElementById("ui-layout-center"); 
		if(obj!=null&&typeof(obj)!='undefined'){
			if(obj.scrollHeight>obj.clientHeight||obj.offsetHeight>obj.clientHeight){ 
				w = $('.ui-layout-center').width()-45;
			} 
		}
	}
	var tabObj = $getObjByClass("widget-place");//带有小窗体的页面
	if(tabObj.length>0){
		w=w-25;
	}
	tabObj = $getObjByClass("ui-tabs");	//带有页签的页面
	if(tabObj.length>0){
		w=w-20;
	}
	return w;
}
//判断调整方法contentResize()是在父页面调用的还是在子页面调用的
function $getObjById(id){
	return $getObj('#'+id);
}
function $getObjByClass(className){
	return $getObj('.'+className);
}
function $getObj(str){
	var obj=$(str);
	//if(obj.length<1){//当前页面上没有，需要查找子页面
	//	obj = $('#myIFrame').contents().find(str);
	//}
	return obj;
}
//判断当前页面是否存在对象
function $existObjById(id){
	return getObjectForTable('#'+id);
}
function $existObjByClass(className){
	return getObjectForTable('.'+className);
}
function getObjectForTable(str){
	if($(str).length<1){
		return false;
	}else{
		return true;
	}
}

//显示提示信息，3秒后隐藏
function showMsg(id){
	if(id==undefined)id="message";
	$("#"+id).show();
	setTimeout('$("#'+id+'").hide();',3000);
}

/**
 * 弹框iframe内容区滚动条
 */
function getContentHeight_ColorIframe(){
	var windowHeigth = $(window).height();
	$(".opt-body").css("height",windowHeigth);//iframe内容区高度
	var hh = $('.opt-body').height();
		$('IFrame').attr('scrolling', 'no');/*隐藏滚动条*/
		if($(".cbox-btn").height()>0){
			$("#opt-content").css("height",hh-55); 
		}else{
			$("#opt-content").css("height",hh-20); 
		}	
}
/**
 *iframe内容与外面区域相等
 */
//function getHeightIframe(){
//}

/**
 * 隐藏查询弹框
 */
function HideSearchBox(){			
	$('#search_box').css("display","none"); 
}
/**
 * 隐藏查询弹框
 */
function HideSuperSearchBox(){			
	$('#searchZoon').css("display","none"); 
}

 /**
 * 得到刷新区域内content高度，回调用
 */
function getContentHeight(){
	var h = $('.ui-layout-center').height();
	if($(".opt-btn").height()>0){
		$("#opt-content").css("height",h-55); 
	}else{
		$("#opt-content").css("height",h-20); 
	}		
}
function setFormgridHeight(tableId,height){
	jQuery("#"+tableId).jqGrid('setGridHeight',height);
}

//var styles = ['oa', '时代科技','black', '时代灰色','green', '绿色世界','sky-blue', '蓝天白云','red', '欢快节日'];
function changeStyle(event, obj){
	   var url=imatrixRoot+"/portal/index/start-using-theme.htm?callback=?";
	    event.cancelBubble = true;
		$.getJSON(
			url,
			function(data){
				var styles = [];
			if(data.msg!= ''){
				styles=data.msg.split(",");
			}
			$('#sysTableDiv').hide();
			if($('#styleList').css("display")=="none"||typeof($('#styleList').css("display"))=="undefined"){
				//event.cancelBubble = true;
				if($('#styleList').attr('id')!='styleList'){
				var table = "<div id='styleList'>";
					for(var i=0;i<styles.length;i=i+2){
						table += ("<div class='styleTable'><a><img src='"+resourceRoot+"/images/"+styles[i]+".gif'/><br/><span>"+styles[i+1]+"</span></a></div>");
					}
					table += "</div>";
					$('body').append(table);
					addStytleClickEvent();
				}
				$('#styleList').show();
				var position = $(obj).position();
				$('#styleList').css('top', '66px');
				$('#styleList').css('right', '2px');
				$('#styleList').css('height', 'auto');//换肤弹框高度自动大小
			}else{
				$('#styleList').hide();
			}
			}
		);
	
}
function addStytleClickEvent() {
$('.styleTable a').click(function(){
	var array = $(this).children('img').attr('src').split('/');
	var style = array[array.length-1].replace('.gif','');
	//把主题保存后台 wj
	var form = '<form id="saveThemeForm" name="saveThemeForm" action="'+imatrixRoot+'/portal/index/index!saveTheme.htm" method="post">'+
					'<input type="hidden" id="themeName" name="themeName" value="'+style+'"/>'+
					'<input type="hidden" id="_header_url" name="url" value="'+window.location.href+'"/></form>';
	$('#header').before(form);
	$('#saveThemeForm').submit();

});
}

var iMatrix={
	showSearchDIV:function(obj){
		if($("#searchArea").length<1 && $("#custom_field_list").length<1){//列表设置中启用不查询功能
			alert("请在列表设置中启用查询功能！");
			return;
		}else if($("#searchArea").length==1 && $("#custom_field_list").length==1){//列表设置中启用自定义查询功能
			if(!customSearch){
				if($("#customSearchZoon").children().length>0){
					$('#fixedSearchZoon').children().remove();
					packagingCustomSearch();
					$('#fixedSearchZoon').append($('#customSearchZoon').children());
					customSearch=true;
				}else{
					alert("您的页面中没有自定义查询区域！");
					return;
				}
			}
		}else{//列表设置中启用内查询功能
			if($("#customSearchZoon").length>0){
				$('#customSearchZoon').remove();
			}
		}
		publicShowSearchDiv(obj);
	},
	
	export_Data:function(url){
		if(typeof(url)!='undefined'&&url!=""){
			if($("#_loading_div_id").length==0){
				$(".opt-btn").append('<div id="_loading_div_id" class="loading">正在加载...</div>');
			}
			$("#_loading_div_id").show();
			
			if($("#_exportable_sign").val()=='false'){
				alert("请在列表设置中启用导出功能！");
				return;
			}
			var rows= jQuery("#"+$("#_main_grid_id").val()).jqGrid('getRowData');
			if(rows.length==0){
				alert("无导出数据！");
				$("#_loading_div_id").hide();
				return;
			}
			
			
			if(url.indexOf("?")>=0){
				url=url+"&exportParameters=export_data";
			}else{
				url=url+"?exportParameters=export_data";
			}
			var col=jQuery("#"+editableGridOptions.gridId).jqGrid("getGridParam", "dynamicColumnNames");
			var coll=jQuery("#"+editableGridOptions.gridId).jqGrid("getGridParam", "postData");
			if(col.length>0){
				var dynamicColumnName="";
				for(var i=0;i<col.length;i++){
					if(col[i].indexOf("<input")==-1){
						if(dynamicColumnName!=''){
							dynamicColumnName+=",";
						}
						dynamicColumnName+=col[i];
					}
				}
				submit_export('_dynamic_export_form',coll,url,dynamicColumnName);
			}else{
				submit_export('_dynamic_export_form',coll,url,'');
			}
		}	
	},
	
	getFormGridDatas:function(formId,tableId){
		var notSave=isHasEdit(tableId);
		if(!notSave){
			$("#"+formId).find("input[name='subTableVals']").remove();
			var fieldName=$("#"+tableId).jqGrid('getGridParam','myattrName');
			var results="";
			var collectionObj=getOneGridDatas(formId,tableId,fieldName);
			if(collectionObj!="")results=results+collectionObj+";";
			if(results.indexOf(";")>=0)results=results.substring(0,results.length-1);
			$("#"+formId).append("<textarea name='subTableVals' style='display:none;'>"+results+"</textarea>");

		}
		return !notSave;
	},
	
	getSubTableDatas:function(formId){
		var notSave=isHasEdit();
		if(!notSave){
			$("#"+formId).find("input[name='subTableVals']").remove();
			var controls=$("input[pluginType='STANDARD_LIST_CONTROL']");
			var results="";
			for(var i=0;i<controls.length;i++){
				var tableId="tb_"+$(controls[i]).attr("id");
				var indexname=$("#"+tableId).jqGrid('getGridParam','indexname');
				var collectionObj=getOneGridDatas(formId,tableId,$(controls[i]).attr("name"));		
				if(collectionObj!="")results=results+collectionObj+";";
			}
			if(results.indexOf(";")>=0)results=results.substring(0,results.length-1);
			$("#"+formId).append("<textarea name='subTableVals' style='display:none;'>"+results+"</textarea>");

		}
		return !notSave;
	
	},
	
	addRow:function(byEnter){
		if (byEnter == undefined) {
			byEnter = false;
		}
		if ((!editing && !byEnter) || byEnter) {
	//		取消对其他行的选中，新增行会自动被选中
			editableGrid.resetSelection();
			editableGrid.jqGrid('addRow', {
				rowID : "0",
				position : "last",
				addRowParams : editParams
			});
			//新增一行时，当前选中行的id为0
			lastsel=0;
			makeEditable(false);
		}else{
			alert("请先完成编辑！");
		}
	},
	
	delRow:function(){
		if (editing) {
			alert("请先完成编辑！");
			return;
		}
		var ids = editableGrid.getGridParam('selarrrow');
		if (ids.length < 1) {
			alert("请选中需要删除的记录！");
			return;
		}
		if(confirm("确定要删除吗？")){
			var params={};
			params=$.extend(true,params,editableGridOptions.extraParams,{
				deleteIds : ids.join(',')
			});
			$.post(editableGridOptions.deleteUrl, params, function(data) {
				// ids数组的长度是会自动变小的(实际是jqgrid内部的一个数组)
				if (ids.length>0) {
					editableGrid.jqGrid().trigger("reloadGrid"); 
				}
				if(data!=''&&data!=null){
					alert(data);
				}
			});
		}
		
	}

};

