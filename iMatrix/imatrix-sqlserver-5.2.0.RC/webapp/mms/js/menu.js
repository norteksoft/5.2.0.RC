//创建菜单树
function createMenuTree(chirldId){
	$.ajaxSetup({cache:false});
	$("#menu-tree").bind("select_node.jstree",function(e){
			currentMenuId=$(".jstree-clicked").parent().attr("id");
		}).jstree({
		"json_data":{
				"ajax" : { "url" : webRoot+"/module/menu!menuTree.htm",
					"data" : function (n) {  
						return { currentMenuId : n!=-1 ? n.attr("id") : 0 };   
					}
				}
	   },
	   "themes" : {  
		  "theme" : "classic",  
		  "dots" : true,  
		  "icons" : true 
		 },
		 "ui":{
			 "select_multiple_modifier" : "alt"
		 },
		 "plugins" : [ "themes", "json_data" ,"ui","crrm"]
	});
}

var currentMenuId ;
var newCreate = true;
//创建菜单
function createMenu(){
	newCreate = true;
	if(!currentMenuId||typeof(currentMenuId)=="undefined"){
		$("#message").html("<font class=\"onError\"><nobr>请选择一个父菜单</nobr></font>");
		showMsg();
		return;
    }
	$.colorbox({href:webRoot+"/module/menu-input.htm?parentMenuId="+currentMenuId,iframe:true, innerWidth:500, innerHeight:300,overlayClose:false,title:"创建菜单"});
}

var isCreateSystem = false;

//创建系统
function createSystem(){
	currentMenuId=null;
	isCreateSystem = true;
	newCreate = false;
	$("#menu-tree").jstree("deselect_all");
	$.colorbox({href:webRoot+"/module/menu-input.htm?parentMenuId="+currentMenuId+"&isCreateSystem="+isCreateSystem,iframe:true, innerWidth:500, innerHeight:350,overlayClose:false,title:"创建系统"});
}
//判断是否选择一个菜单
function isSelected(){
	if(!currentMenuId||typeof(currentMenuId)=="undefined"){
		$("#message").html("<font class=\"onError\"><nobr>请选择一个菜单</nobr></font>");
		showMsg();
		return false;
	}else{
		return true;
	}
}

//修改菜单
function updateMenu(){
	if(isSelected()){
		newCreate = false;
		$.get(webRoot+"/module/menu-input.htm?menuId="+currentMenuId,function(data){
			if(data=="false"){
				$("#message").html("<font class=\"onError\"><nobr>"+"菜单已启用不能修改"+"</nobr></font>");
				$("#message").show();
				setTimeout('$("#message").hide();',3000);
			}else{
				$.colorbox({href:webRoot+"/module/menu-input.htm?menuId="+currentMenuId,iframe:true, innerWidth:500, innerHeight:300,overlayClose:false,title:"修改菜单"});
			}
		});
	}
}
//删除表单
function deleteMenu(){
	if(isSelected()){
		if(confirm("确认删除吗?")){
			$.get(webRoot+"/module/menu!delete.htm?menuId="+currentMenuId, function(data){
				if(data=="false"){
					$("#message").html("<font class=\"onError\"><nobr>"+"不能删除已启用的一级菜单"+"</nobr></font>");
					$("#message").show();
					setTimeout('$("#message").hide();',3000);
				}else if(data=="success"){
					$("#menu-tree").jstree('remove');
				}else{
					$("#message").html("<font class=\"onError\"><nobr>"+data+"</nobr></font>");
					$("#message").show();
					setTimeout('$("#message").hide();',3000);
				}
			});
		}
	}
}
//启用表单
function enableMenu(){
	if(isSelected()){
		$.get(webRoot+"/module/menu!enable.htm?menuId="+currentMenuId, function(data){
		//$.tree_reference('menu-tree').selected;
		var menus = data.split(",");
			for(var i = 0 ;i<menus.length;i++){
				var menuMessage = menus[i].split("=");
				$("#menu-tree").jstree("rename_node","#"+menuMessage[0],menuMessage[1]);
			}
		});
	}
}
//禁用表单
function disableMenu(){
	if(isSelected()){
		$.get(webRoot+"/module/menu!disableMenu.htm?menuId="+currentMenuId, function(data){
			$("#menu-tree").jstree("rename_node","#"+currentMenuId,data);
		});
	}
}

function updateTree(chirldId,chirldName){
	if(newCreate){
		$("#menu-tree").jstree("create","#"+currentMenuId,"first",{ attr : {id : chirldId}, data : chirldName },function(){},true);
	}else{
		$("#menu-tree").jstree("refresh",-1);
	}
}

//页面验证
function  validate_menu(){
	$("#menuForm").validate({
		submitHandler: function() {
			ajax_submit_form();
		},
		rules: {
			code: "required",
			name:"required",
			displayOrder:{
				required:true,
				number:true
			}
		},
		messages: {
			code: "必填",
			name:"必填",
			displayOrder:{
				required:"必填",
				number:"请输入数字"
			}
		}
	});
}

function ajax_submit_form(){
	$("#menuForm").ajaxSubmit(function (data){
		var datas = data.split(":");
		if(datas[0]=="msg"){
			$("#message").html("<font class=\"onError\"><nobr>" +datas[1]+"</nobr></font>");
			showMsg();
		}else if(datas[0]=="enable"){
			var menus = datas[1].split(",");
			var crnMenus=datas[2].split("-");
			parent.updateTree(crnMenus[0],crnMenus[1]);
			for(var i = 0 ;i<menus.length;i++){
				var menuMessage = menus[i].split("=");
				parent.$("#menu-tree").jstree("rename_node","#"+menuMessage[0],menuMessage[1]);
			}
				parent.$.colorbox.close();
		}else{
			parent.updateTree(datas[0],datas[1]);
			parent.$.colorbox.close();
		}
	});
}
function saveAndEnable(){
	$("#enableState").attr("value","ENABLE");
	$("#menuForm").submit();
}
function save(){
	$("#menuForm").submit();
}

//选择系统时自动填值
function choseSys(){
	var system=$("#choseSystems").val();
	var sysMessage = system.split(",");//${id},${code},${businessName},${businessPath}
	if(sysMessage!=''){
		$("#choseSystemId").attr("value",sysMessage[0]);
		$("#code").attr("readOnly","true");
		$("#code").attr("value",sysMessage[1]);
		$("#name").attr("value",sysMessage[2]);
		$("#url").attr("value",sysMessage[3]);
	}else{
		$("#choseSystemId").attr("value","");
		$("#code").removeAttr("readOnly");
		$("#code").attr("value","");
		$("#name").attr("value","");
		$("#url").attr("value","#this");
	}
}