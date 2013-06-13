<table id="searchTbTwo"><tr><td >
	<input id="searchInputTwo" /></td><td ><a class="search-btn" href="#" onclick="search_fun_two();" ><b class="ui-icon ui-icon-search"></b></a>
</td></tr></table>
<div id="${treeId}" class="demo">
<script type="text/javascript">
//<---------------解析树节点的分隔符-------------->
var split_one = "~~";
var split_two = "==";
var split_three = "*#";
var split_four = "|#";
var split_five = "+#";
var split_six = "~#";
var split_seven = "**";
var split_eight = "=#";
//<---------------单选树标签第二版-------------->
	function singleTreeTwo(type){
		$.ajaxSetup({cache:false});
		   $("#${treeId}").bind("search.jstree",function(e,data){
			$.jstree.rollback(data.rlbk); 
         }).bind("select_node.jstree",function(e){
					id=$(".jstree-clicked").parent().attr("id");})
		    .jstree({
			"json_data":{
					"ajax" : { "url" : "${actionUrl}?treeType="+type,
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
				"search" : {
						"ajax" : {
							"url" : "${searchUrl}?treeType="+type,
							"async":true,
							// You get the search string as a parameter
							"data" : function (str) {
								return { 
									"searchValue" : str 
								}; 
							},
							"success":function(data){
								$("#${treeId}").find("li").find("a").removeClass("jstree-search");  
								var arr=eval(data);
								for(var i=0;i<arr.length;i++){
										var deptInfos = arr[i].split(";");
										var deptInfo = deptInfos[0];
										var parentInfo = deptInfos[1];
										$.jstree._reference($("#${treeId}")).open_node($("li[id="+deptInfo+"]"),
										function(){
											//打开子部门节点
											for(var j=0;j<arr.length;j++){
												var jdeptInfos = arr[j].split(";");
												var jdeptInfo = jdeptInfos[0];
												var jparentInfo = jdeptInfos[1];
												if(jparentInfo!=""){
													$.jstree._reference($("li[id="+jparentInfo+"]")).open_node($("li[id="+jdeptInfo+"]"),
													function(){
														var result = $("#${treeId}").find("a" +  ":" +"contains" + "(" + $("#searchInput").attr("value") + ")");
														result.addClass("jstree-search");
													},true);
												}
											}
											//添加选中样式
											var result = $("#${treeId}").find("a" +  ":" +"contains" + "(" + $("#searchInput").attr("value") + ")");
											result.addClass("jstree-search");
										},true);
										
								}
								
							}
						}
					},
				 "types" :{
						"types" : {
							"company" : {
								"icon" : {
									"image" : "${resourceCtx}/widgets/jstree/themes/root.gif"
								}
							},
							"folder" : {
								"icon" : {
									"image" : "${resourceCtx}/widgets/jstree/themes/folder.gif"
								}
							},
							"user" : {
								"icon" : {
									"image" : "${resourceCtx}/widgets/jstree/themes/file.gif"
								}
							}
						}
					 }, 
				"ui":{"select_limit":1},
			   "plugins" : [ "themes", "json_data","types","ui","search" ]
			});
	}
	
	function search_fun(){
		$("#${treeId}").jstree("search",$("#searchInputTwo").val()+'');
	}
var id="";	
	//treeType:user,department,workGroup,默认为"user"
	function getInfo(treeType){
		var currentTreeType="user";
		if(treeType!=""&&typeof (treeType)!="undefined"){
			currentTreeType=treeType;
		}
	 	if(id!=""){
	        var info="[";
			if(currentTreeType=="user"){
		     	var type=id.substring(0,id.indexOf(split_one));
		     	if(type=="user"){
		     	 info+="{type:"+"\""+id.substring(0,id.indexOf(split_one))+"\""
				     +",id:"+"\""+id.substring(id.indexOf(split_one)+2,id.indexOf(split_two))+"\""
				     +",name:"+"\""+id.substring(id.indexOf(split_two)+2,id.indexOf(split_three))+"\""
				     +",loginName:"+"\""+id.substring(id.indexOf(split_three)+2,id.indexOf(split_four))+"\""
				     +",parentType:"+"\""+id.substring(id.indexOf(split_five)+2,id.indexOf(split_eight))+"\""
				     +",parentName:"+"\""+id.substring(id.indexOf(split_four)+2,id.indexOf(split_five))+"\""+"},";
		     	}
			}else if(currentTreeType=="department"){
			   var type=id.substring(0,id.indexOf(split_one));
			   if(type=="department"){
			    info+="{type:"+"\""+id.substring(0,id.indexOf(split_one))+"\""
				     +",id:"+"\""+id.substring(id.indexOf(split_one)+2,id.indexOf(split_two))+"\""
				     +",name:"+"\""+id.substring(id.indexOf(split_two)+2,id.indexOf(split_three))+"\""+"},";
			   }
			}else if(currentTreeType=="workGroup"){
			   var type=id.substring(0,id.indexOf(split_one));
			   if(type=="workGroup"){
			    info+="{type:"+"\""+id.substring(0,id.indexOf(split_one))+"\""
				     +",id:"+"\""+id.substring(id.indexOf(split_one)+2,id.indexOf(split_two))+"\""
				     +",name:"+"\""+id.substring(id.indexOf(split_two)+2,id.indexOf(split_three))+"\""+"},";
			   }
			}
		    if(info.indexOf(",")>=0){
		    	info=info.substring(0,info.length-1);
		    }
	       info+="]";
	   return info;
	  }else{
	  	return "";
	  }
	}
	function getId(){
		var ids=id.substring(id.indexOf(split_one)+2,id.indexOf(split_two));
		return ids;
	}	

	function getType(){
		var type=id.substring(0,id.indexOf(split_one));
		return type;
	}	
		
	
	//部门名称
	function getDepartmentName(){
		var departmentName=id.substring(id.indexOf(split_three)+2,id.length);
		return departmentName;
	}
	//部门id
	function getDepartmentId(){
		var departmentId=id.substring(id.indexOf(split_one)+2,id.indexOf(split_two));
		return departmentId;
	}
	
	//工作组名称
	function getWorkGroupName(){
		var workGroupName=id.substring(id.indexOf(split_three)+2,id.length);
		return workGroupName;
	}
	//工作组id
	function getWorkGroupId(){
		var workGroupId=id.substring(id.indexOf(split_one)+2,id.indexOf(split_two));
		return workGroupId;
	}
	
	//用户名称
	function getName(){
		var name=id.substring(id.indexOf(split_two)+2,id.indexOf(split_three));
		return name;
	}
	//用户登录名
	function getLoginName(){
		var loginName=id.substring(id.indexOf(split_three)+2,id.indexOf(split_four));
		return loginName;
	}
	//用户邮件
	function getEmail(){
		var email=id.split(",")[1].split("*")[0];
		return email;
	}

	//用户尊称
	function getHonorificName(){
		var honorificName=id.split(split_eight)[1].split(split_seven)[1];
		return honorificName;
	}
	//用户权重
	function getWeight(){
		var weight=id.split(split_eight)[1].split(split_seven)[2];
		return weight;
	}
	//获取用户部门名称
	function getUserDepartmentName(){
		var deptName=id.substring(id.indexOf(split_five)+2,id.indexOf(split_six));
		return deptName;
	}	
</script>
</div>
	
	
	
	
	
	
	