<table id="searchTbTwo"><tr><td >
	<input id="searchInputTwo" /></td><td ><a class="search-btn" href="#" onclick="search_fun_two();" ><b class="ui-icon ui-icon-search"></b></a>
</td></tr></table>
<div id="${treeId}" class="demo" type="${treeType}">
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
//<---------------多选树标签第二版-------------->
var multTreeTwo;
	function multipleTreeTwo(type){
		$.ajaxSetup({cache:false});
		
		multTreeTwo=$("#${treeId}").bind("search.jstree",function(e,data){
			$.jstree.rollback(data.rlbk); 
        }).jstree({
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
			   "plugins" : [ "themes", "json_data","checkbox","types","search" ]
			});
		
	}
	function search_fun_two(){
		$("#${treeId}").jstree("search",$("#searchInputTwo").val());
	}
function allUsers(id){
    	var lists = $("#"+id).find("li.jstree-checked");
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
	
	function selectHandle(NODE,TREE_OBJ,obj){
		var lists=obj.find("li");
		for(var i=0;i<lists.length;i++){
			var object=$(lists[i]).is("li") ? $(lists[i]) : $(lists[i]).parent();
			selectHandle($(lists[i]),TREE_OBJ,object);
		}
		obj.find("li").andSelf().children("a").removeClass("unchecked").removeClass("undetermined").addClass("checked");
	}
	
	function selectSubDeptUser(treeObj,obj){
		$("#${treeId}").bind("open_node.jstree",function(){
			var lists=$(obj).find("li");
			for(var i=0;i<lists.length;i++){
				selectSubDeptUser(treeObj,$(lists[i]).is("li") ? $(lists[i]) : $(lists[i]).parent());
			}
			$(obj).find("li").andSelf().removeClass("jstree-unchecked jstree-undetermined").addClass("jstree-checked");
			if($(treeObj).data.ui) { $(treeObj).data.ui.last_selected = $(obj); }
			$(treeObj).data.checkbox.last_selected = $(obj);
		});
	}
	
	//treeType:user,department,workGroup,默认为"user"
	function getInfo(treeType){
			var currentTreeType="user";
			if(treeType!=""&&typeof (treeType)!="undefined"){
				currentTreeType=treeType;
			}
	          var arr=allUsers("${treeId}");
	          if(arr!=""){
		            var info="[";
					if(currentTreeType=="user"){
				     	for(var i=0; i<arr.length; i++){
					     	var type=arr[i].substring(0,arr[i].indexOf(split_one));
					     	if(type=="company"){
					     	  info+="{type:"+"\""+arr[i].substring(0,arr[i].indexOf(split_one))+"\""
							     +",id:"+"\""+arr[i].substring(arr[i].indexOf(split_one)+2,arr[i].indexOf(split_two))+"\""
							     +",name:"+"\""+arr[i].substring(arr[i].indexOf(split_two)+2,arr[i].indexOf(split_three))+"\""+"},";
							     break;
					     	}else if(type=="allDepartment"){
					     	 info+="{type:"+"\""+arr[i].substring(0,arr[i].indexOf(split_one))+"\""
							     +",id:"+"\""+arr[i].substring(arr[i].indexOf(split_one)+2,arr[i].indexOf(split_two))+"\""
							     +",name:"+"\""+arr[i].substring(arr[i].indexOf(split_two)+2,arr[i].indexOf(split_three))+"\""+"},";
							     break;
					     	}else if(type=="allWorkGroup"){
					     	 info+="{type:"+"\""+arr[i].substring(0,arr[i].indexOf(split_one))+"\""
							     +",id:"+"\""+arr[i].substring(arr[i].indexOf(split_one)+2,arr[i].indexOf(split_two))+"\""
							     +",name:"+"\""+arr[i].substring(arr[i].indexOf(split_two)+2,arr[i].indexOf(split_three))+"\""+"},";
							     break;
					     	}else{
							  info+="{type:"+"\""+arr[i].substring(0,arr[i].indexOf(split_one))+"\""
							     +",id:"+"\""+arr[i].substring(arr[i].indexOf(split_one)+2,arr[i].indexOf(split_two))+"\""
							     +",name:"+"\""+arr[i].substring(arr[i].indexOf(split_two)+2,arr[i].indexOf(split_three))+"\""
							     +",loginName:"+"\""+arr[i].substring(arr[i].indexOf(split_three)+2,arr[i].indexOf(split_four))+"\""
							     +",parentType:"+"\""+arr[i].substring(arr[i].indexOf(split_five)+2,arr[i].indexOf(split_eight))+"\""
							     +",parentName:"+"\""+arr[i].substring(arr[i].indexOf(split_four)+2,arr[i].indexOf(split_five))+"\""+"},";
							}
					    }
					}else if(currentTreeType=="department"){
						for(var i=0;i<arr.length;i++){
						   var type=arr[i].substring(0,arr[i].indexOf(split_one));
						    if(type=="company"){
					     	  info+="{type:"+"\""+arr[i].substring(0,arr[i].indexOf(split_one))+"\""
							     +",id:"+"\""+arr[i].substring(arr[i].indexOf(split_one)+2,arr[i].indexOf(split_two))+"\""
							     +",name:"+"\""+arr[i].substring(arr[i].indexOf(split_two)+2,arr[i].indexOf(split_three))+"\""+"},";
							     break;
					     	}else if(type=="allDepartment"){
					     	 info+="{type:"+"\""+arr[i].substring(0,arr[i].indexOf(split_one))+"\""
							     +",id:"+"\""+arr[i].substring(arr[i].indexOf(split_one)+2,arr[i].indexOf(split_two))+"\""
							     +",name:"+"\""+arr[i].substring(arr[i].indexOf(split_two)+2,arr[i].indexOf(split_three))+"\""+"},";
							     break;
					     	}else if(type=="department"){
						    info+="{type:"+"\""+arr[i].substring(0,arr[i].indexOf(split_one))+"\""
							     +",id:"+"\""+arr[i].substring(arr[i].indexOf(split_one)+2,arr[i].indexOf(split_two))+"\""
							     +",name:"+"\""+arr[i].substring(arr[i].indexOf(split_two)+2,arr[i].indexOf(split_three))+"\""+"},";
						   }
					   }
					}else if(currentTreeType=="workGroup"){
						for(var i=0;i<arr.length;i++){
						   var type=arr[i].substring(0,arr[i].indexOf(split_one));
						   if(type=="workGroup"){
						    info+="{type:"+"\""+arr[i].substring(0,arr[i].indexOf(split_one))+"\""
							     +",id:"+"\""+arr[i].substring(arr[i].indexOf(split_one)+2,arr[i].indexOf(split_two))+"\""
							     +",name:"+"\""+arr[i].substring(arr[i].indexOf(split_two)+2,arr[i].indexOf(split_three))+"\""+"},";
						   }
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
    function getUserInfoWithTreeId(id){
		var arr=allUsers(id);
      if(arr!=""){
            var info="[";
	     	for(var i=0; i<arr.length; i++){
		     	type=arr[i].substring(0,arr[i].indexOf(split_one));
				  info+="{type:"+"\""+arr[i].substring(0,arr[i].indexOf(split_one))+"\""
							     +",id:"+"\""+arr[i].substring(arr[i].indexOf(split_one)+2,arr[i].indexOf(split_two))+"\""
							     +",name:"+"\""+arr[i].substring(arr[i].indexOf(split_two)+2,arr[i].indexOf(split_three))+"\""
							     +",loginName:"+"\""+arr[i].substring(arr[i].indexOf(split_three)+2,arr[i].indexOf(split_four))+"\""
							     +",parentName:"+"\""+arr[i].substring(arr[i].indexOf(split_five)+2,arr[i].indexOf(split_six))+"\""
							     +",parentId:"+"\""+arr[i].substring(arr[i].indexOf(split_six)+2,arr[i].indexOf(split_eight))+"\""
							     +",parentType:"+"\""+arr[i].substring(arr[i].indexOf(split_four)+2,arr[i].indexOf(split_five))+"\""+"},";
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
    	function getInfoWithTreeId(id){
	          var arr=allUsers(id);
	          if(arr!=""){
		            var info="[";
			     	for(var i=0; i<arr.length; i++){
				     	type=arr[i].substring(0,arr[i].indexOf(split_one));
						  info+="{type:"+"\""+arr[i].substring(0,arr[i].indexOf(split_one))+"\""
						     +",id:"+"\""+arr[i].substring(arr[i].indexOf(split_one)+2,arr[i].indexOf(split_two))+"\""
						     +",name:"+"\""+arr[i].substring(arr[i].indexOf(split_two)+2,arr[i].indexOf(split_three))+"\""
						     +",loginName:"+"\""+arr[i].substring(arr[i].indexOf(split_three)+2,arr[i].indexOf(","))+"\""+"},";
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
    //用户ids
    function getIds(){
		var arr=allUsers("${treeId}");
		var ids="";
		for(var i=0; i<arr.length; i++){
		    var id =  arr[i].substring(arr[i].indexOf(split_one)+2,arr[i].indexOf(split_two));
		    var type=arr[i].substring(0,arr[i].indexOf(split_one));
		    if(type=='user'&&ids.indexOf(id+",")<0){
		    	ids+=arr[i].substring(arr[i].indexOf(split_one)+2,arr[i].indexOf(split_two))+",";
		    }
		}
		return ids.substring(0,ids.length-1);    
    }	
    //用户名称
    function getNames(){
    	var arr=allUsers("${treeId}");
	    var names="";
     	for(var i=0; i<arr.length; i++){
     	 	var name =  arr[i].substring(arr[i].indexOf(split_one)+2,arr[i].indexOf(split_two));
     	 	var type=arr[i].substring(0,arr[i].indexOf(split_one));
		    if(type=='user'&&names.indexOf(name+",")<0){
		    	names+=arr[i].substring(arr[i].indexOf(split_two)+2,arr[i].indexOf(split_three))+",";
		    }
     	}
     	return names.substring(0,names.length-1); 
    }
    //用户登录名
	function getLoginNames(){
		var arr=allUsers("${treeId}");
		var loginNames="";
		for(var i=0; i<arr.length; i++){
			var loginName =  arr[i].substring(arr[i].indexOf(split_one)+2,arr[i].indexOf(split_two));
		    var type=arr[i].substring(0,arr[i].indexOf(split_one));
		    if(type=='user'&&loginNames.indexOf(loginName+",")<0){
		    	loginNames+=arr[i].substring(arr[i].indexOf(split_three)+2,arr[i].indexOf(split_four))+",";
		    }
		}
		return loginNames.substring(0,loginNames.length-1);   
	}
    //部门名称
	function getDepartmentNames(){
		var arr=allUsers("${treeId}");
	    var departmentNames="";
     	for(var i=0; i<arr.length; i++){
     		departmentNames+=arr[i].substring(arr[i].indexOf(split_three)+2,arr[i].length)+",";
     	}
     	return departmentNames.substring(0,departmentNames.length-1); 
	}
	//部门id
	function getDepartmentIds(){
		var arr=allUsers("${treeId}");
	    var departmentIds="";
     	for(var i=0; i<arr.length; i++){
     		departmentIds+=arr[i].substring(arr[i].indexOf(split_one)+2,arr[i].indexOf(split_two))+",";
     	}
     	return departmentIds.substring(0,departmentIds.length-1); 
	}
	
	//工作组名称
	function getWorkGroupNames(){
		var arr=allUsers("${treeId}");
	    var workGroupNames="";
     	for(var i=0; i<arr.length; i++){
     		workGroupNames+=arr[i].substring(arr[i].indexOf(split_three)+2,arr[i].length)+",";
     	}
     	return workGroupNames.substring(0,workGroupNames.length-1); 
	}
	//工作组id
	function getWorkGroupIds(){
		var arr=allUsers("${treeId}");
	    var workGroupIds="";
     	for(var i=0; i<arr.length; i++){
     		workGroupIds+=arr[i].substring(arr[i].indexOf(split_one)+2,arr[i].indexOf(split_two))+",";
     	}
     	return workGroupIds.substring(0,workGroupIds.length-1); 
	}
	
	//用户邮件
	function getEmails(){
		var arr=allUsers("${treeId}");
		var emails="";
		for(var i=0; i<arr.length; i++){
			var email =  arr[i].substring(arr[i].indexOf(split_one)+2,arr[i].indexOf(split_two));
		    var type=arr[i].substring(0,arr[i].indexOf(split_one));
		    if(type=='user'&&emails.indexOf(email+",")<0){
		    	emails+=arr[i].split(split_eight)[1].split(split_seven)[0]+",";
		    }
		}
		return emails.substring(0,emails.length-1);    
	}
	
	//用户尊称
	function getHonorificNames(){
		var arr=allUsers("${treeId}");
		var honorificNames="";
		for(var i=0; i<arr.length; i++){
			var honorificName =  arr[i].substring(arr[i].indexOf(split_one)+2,arr[i].indexOf(split_two));
		    var type=arr[i].substring(0,arr[i].indexOf(split_one));
		    if(type=='user'&&honorificNames.indexOf(honorificName+",")<0){
		    	honorificNames+=arr[i].split(split_eight)[1].split(split_seven)[1]+",";
		    }
		}
		return honorificNames.substring(0,honorificNames.length-1);   
	}
	
    //用户权重
	function getWeights(){
		var arr=allUsers("${treeId}");
		var weights="";
		for(var i=0; i<arr.length; i++){
			var weight =  arr[i].substring(arr[i].indexOf(split_one)+2,arr[i].indexOf(split_two));
		    var type=arr[i].substring(0,arr[i].indexOf(split_one));
		    if(type=='user'&&weights.indexOf(weight+",")<0){
		    	weights+=arr[i].split(split_eight)[1].split(split_seven)[2]+",";
		    }
		}
		return weights.substring(0,weights.length-1);    
	}
</script>
</div>
	
	
	
	
	
	
	