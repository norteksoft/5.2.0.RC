<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
	<head>
		<title>自定义选择界面</title>
		<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
        <meta http-equiv="Cache-Control" content="no-store"/>
        <meta http-equiv="Pragma" content="no-cache"/>
        <meta http-equiv="Expires" content="0"/>
        
		<script type="text/javascript" src="${resourceCtx}/js/jquery-all-1.0.js"></script>
		<link   type="text/css" rel="stylesheet" href="${resourceCtx}/widgets/colorbox/colorbox.css" />
		
		<script type="text/javascript" src="${resourceCtx}/widgets/jqgrid/jqgrid-all-1.0.js"></script>
		<script type="text/javascript" src="${resourceCtx}/widgets/jqgrid/jqGrid.custom.js"></script>
		
		<script type="text/javascript" src="${resourceCtx}/widgets/jstree/jquery.jstree.js"></script>
		<link   type="text/css" rel="stylesheet" href="${resourceCtx}/widgets/jqgrid/ui.jqgrid.css" />
		<link   type="text/css" rel="stylesheet" href="${resourceCtx}/css/${theme}/jquery-ui-1.8.16.custom.css" id="_style"/>
		
		<script type="text/javascript">
			$(document).ready(function() {
				var url=parent._obj.url+"?"+parent._url_prn;
				parent._crn_obj=parent._obj;
				if(typeof(parent._obj.tree)!="undefined"){
					$("#btnDiv").html('<div class="opt-btn"><button class="btn" onclick="_ok();"><span><span>确定</span></span></button></div>');
					var html="<ul>";
					var i=0;
					for(var title in parent._obj.tree){
						i++;
						var treeobj=parent._obj.tree[title];
						if(i==1){
							parent._crn_obj=treeobj;
							var tmpurl=treeobj.url;
							if(parent._crn_obj.url.indexOf("?")>=0){
								var urlArr=parent._crn_obj.url.split("?");
								tmpurl=urlArr[0];
							}
							var tmpprnurl=_get_url_parameter();
							url=tmpurl+"?"+tmpprnurl;
						}
						html+='<li><a href="#tabs-1" onclick="changeViewSet(\''+title+'\');">'+title+'</a></li>';
					}
					html+='</ul>';
					$("#tagDiv").html(html);
					$( "#tabs" ).tabs();
				}else{
					if(!parent._crn_obj.mutiple){
						single_bind_select();
					}
				}
				if(parent._crn_obj.multiple){
					$("#btnDiv").html('<div class="opt-btn"><button class="btn" onclick="_ok();"><span><span>确定</span></span></button></div>');
				}
				create_tree(url);
			});
			
			function changeViewSet(title){
				parent._crn_obj=parent._obj.tree[title];
				var url="";
				var tmpurl=parent._crn_obj.url;
				if(parent._crn_obj.url.indexOf("?")>=0){
					var urlArr=parent._crn_obj.url.split("?");
					tmpurl=urlArr[0];
				}
				var tmpprnurl=_get_url_parameter();
				url=tmpurl+"?"+tmpprnurl;
				create_tree(url);
			}
			
			function create_tree(url){
				$.ajaxSetup({cache:false});
				var jstreeOption={
					"json_data":{
						"ajax" : { "url" : encodeURI(url),
									"data" : function (n) {
										 return { currentId : n!=-1 ? n.attr("id") : 0 };   
									}
								}
					   },
					   "themes" : {  
						 "theme" : "classic",  
						 "dots" : true,  
						 "icons" : true 
						},
					   "plugins" : [ "themes", "json_data" ,"ui" ]
				};
				if(typeof(parent._crn_obj.multiple)!="undefined"&&parent._crn_obj.multiple){
					jstreeOption.plugins=[ "themes", "json_data" ,"ui","checkbox" ];
				}
				$("#treeDiv").jstree(jstreeOption);
			}
			
			function single_bind_select(){
				$("#treeDiv").bind("select_node.jstree",function(e){
					parent._node_id=$(".jstree-clicked").parent().attr("id");
					parent._node_title=$("#treeDiv").jstree("get_text","#"+parent._node_id);
					if(typeof(parent._crn_obj.onsuccess)=="undefined"||parent._crn_obj.onsuccess==""){
						if(typeof(parent._obj.onsuccess)!="undefined"&&parent._obj.onsuccess!=""){
							parent._operate="ok";
							parent.$.colorbox.close();
						}else{
							var _inpObj=parent._crn_obj.inputObj;
							if(typeof(parent._crn_obj.inputObj)=="undefined"||parent._crn_obj.inputObj==""){
								_inpObj=parent._obj.inputObj;
							}
							parent.$("#"+_inpObj).attr("value",parent._node_title);
							parent.$.colorbox.close();
						}
					}else{
						if(parent._node_id=='_role'){
							alert("请选择角色");
						}else{
							parent._operate="ok";
							parent.$.colorbox.close();
						}
					} 
				});
			}
			
			function get_select_nodes_id(){
		    	var lists ;
		    	if(typeof(parent._crn_obj.multiple)=="undefined"||!parent._crn_obj.multiple){
			    	lists=$("#treeDiv").find("li a.jstree-clicked").parent();
		    	}else{
			    	lists=$("#treeDiv").find("li.jstree-checked");
		    	}
				var v="" ;
				for(var i=0; i<lists.length; i++){
					v+=$(lists[i]).attr("id");
					if(i!=lists.length-1)
						v+=";";
				}
				if(v!=""){
					var arr=v.split(";");
					return arr;
				}else{
					return "";
				}
			}
			
			function get_select_text(){
				var arr=get_select_nodes_id();
				for(var i=0; i<arr.length; i++){
					if(typeof(parent._node_title)=="undefined"||parent._node_title==""){
						parent._node_id=arr[i];
						parent._node_title=$("#treeDiv").jstree("get_text","#"+arr[i]);
					}else{
						parent._node_id=parent._node_id+","+arr[i];
						parent._node_title=parent._node_title+","+$("#treeDiv").jstree("get_text","#"+arr[i]);
					}
				}
			}
			
			function _ok(){
				get_select_text();
				if(typeof(parent._crn_obj.onsuccess)=="undefined"||parent._crn_obj.onsuccess==""){
					if(typeof(parent._obj.onsuccess)!="undefined"&&parent._obj.onsuccess!=""){
						parent._operate="ok";
						parent.$.colorbox.close();
					}else{
						var _inpObj=parent._crn_obj.inputObj;
						if(typeof(parent._crn_obj.inputObj)=="undefined"||parent._crn_obj.inputObj==""){
							_inpObj=parent._obj.inputObj;
						}
						parent.$("#"+_inpObj).attr("value",parent._node_title);
						parent.$.colorbox.close();
					}
				}else{
					parent._operate="ok";
					parent._onsuccess=parent._crn_obj.onsuccess;
					parent.$.colorbox.close();
				} 
			}
			
			function _get_url_parameter(){
				var _url_prn="";
				if(parent._crn_obj.url.indexOf("?")>=0){
					var urlArr=parent._crn_obj.url.split("?");
					_url_prn=urlArr[1];//'startDate='+startDate+'&endDate='+endDate
				}
				var paraObj=parent._crn_obj.postData;
				if(typeof(paraObj)=="undefined"){
					paraObj=parent._obj.postData;
				}
				if(typeof(paraObj)!="undefined"){
					for(var p in paraObj){
						// 方法
						if(typeof(paraObj[p])!="function"){
							// p 为属性名称，obj[p]为对应属性的值 
							if(_url_prn==""){
								_url_prn=p+"="+paraObj[p];
							}else{
								_url_prn=_url_prn+"&"+p+"="+paraObj[p];
							}
						}
					}
				}
				if(_url_prn==""){
					return parent._url_prn;
				}else{
					return _url_prn;
				}
			}
		</script>
	</head>
	
	<body>
		<div class="opt-body">
			<div id="btnDiv"></div>
			<div id="opt-content">
				<div id="tabs">
					<div id="tagDiv">
					</div>
					<div id="tabs-1">
						<div id="treeDiv" class="demo">
						</div>
					</div>
				</div>
			</div>
		</div>
	</body>
<script type="text/javascript" src="${resourceCtx}/widgets/colorbox/jquery.colorbox.js"></script>
</html>