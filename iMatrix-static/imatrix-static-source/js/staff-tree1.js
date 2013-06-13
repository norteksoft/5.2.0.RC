var okEnsure;
var singleId;
var singleName;
var singleType;
var singleLoginName;
var sInfor;

var mId;
var mName;
var mLoginName;
var infor;
var userInfo;
var mTreeType;

var acsSystemUrl;
var hiddenInputId;
var showInputId;
var loginNameId;
var treeType;
var isAppend;

//移除的参数
var hiddenId;
var showId;
var loginNId;
var acsUrl;
var type;
var formId;
function popTree(paramater){
	var title = paramater.title;
	var innerWidth = paramater.innerWidth;
	treeType = paramater.treeType;
	var defaultTreeValue = paramater.defaultTreeValue;
	var leafPage = paramater.leafPage;
	var treeTypeJson = paramater.treeTypeJson;
	var multiple = paramater.multiple;
	hiddenInputId = paramater.hiddenInputId;
	showInputId = paramater.showInputId;
	acsSystemUrl = paramater.acsSystemUrl;
	var callBack = paramater.callBack;
	isAppend = paramater.isAppend;
	loginNameId = paramater.loginNameId;
	formId = paramater.formId;
	
   
	if(hiddenInputId==""||hiddenInputId==null) {
	   hiddenInputId="NOHiddenInputId";
	}
	if(showInputId==""||showInputId==null){
	   showInputId="NOShowInputId";
	}
	if(loginNameId==""){loginNameId="noLoginName";}
	if(treeType==''||treeType==null){treeType="COMPANY" ;}
	if(leafPage=="true"&&treeTypeJson==''){alert("请设定页签参数!");return;}
	
	if(typeof(title)=='undefined'||typeof(innerWidth)=='undefined'){title='选择';innerWidth='300';}
	
	if(typeof(title)=='defaultTreeValue'||defaultTreeValue==''){defaultTreeValue="id";}
	
	//var url=webRoot+'/popTree.action?treeType='+treeType
	//var url='http://192.168.1.99:8000/acs/tags/tree!popTree.action?treeType='+treeType

	var url = acsSystemUrl+"/popTree.action?treeType="+treeType
			+'&multiple='+multiple+'&hiddenInputId='
			+hiddenInputId+'&showInputId='+showInputId
			+'&callBack='+callBack+'&treeTypeJson='+treeTypeJson
			+'&leafPage='+leafPage+'&defaultTreeValue='+defaultTreeValue
			+'&isAppend='+isAppend+'&loginNameId='+loginNameId
			+'&formId='+formId;

	$.colorbox({href:encodeURI(url),iframe:true, innerWidth:innerWidth, innerHeight:400,overlayClose:false,title:title,onClosed:function(){if(okEnsure=='OK'){callBack.call();okEnsure='';}}});
	
    }
	function removeOption(paramater){
	  hiddenId = paramater.hiddenId;
	  showId = paramater.showId;
	  loginNId = paramater.loginNId;
	  acsUrl = paramater.acsUrl;
	  type = paramater.type;
	  formId = paramater.formId;
	
	  //if(hiddenId=='') {hiddenId = hiddenInputId;}
	  //if(showId==''){showId = showInputId;}
	  //if(loginNId==''){ loginNId = loginNameId;}
	  //if(acsUrl==''){ acsUrl = acsSystemUrl;}
	 // if(type==''){ type = treeType;}
	  
	  if(hiddenId=='') {hiddenId="NOHiddenInputId";}
	  if(showId==''){alert("请输入显示域id");return;}
	  //if(typeof(loginNId)=='undefined'){ loginNId = loginNameId;}
	  if(acsUrl==''){ alert("请输入系统webRoot");return;}
	  if(type==''){ alert("请输入树的类型");return;}
	  if(formId==''){ alert("请输入formId");return;}
	  
	  
	  var url = acsUrl+"/removeOptionTree.action";
	 // var hiddId = getHiddenInputValueFromInput();
	   var removeStaffJson = createRemoveJson(showId,loginNId);
	   var removeStaffJsonOld = $("#removeStaffJson").val();
	   if(typeof(removeStaffJsonOld)=='undefined'){
	      $("#"+formId).append("<input type='hidden' id='removeStaffJson' name='removeStaffJson' value='"+removeStaffJson+"'/> ");
	   }else{
	      $("#removeStaffJson").attr("value",removeStaffJson);
	   }
	   //removeStaffJson="+removeStaffJson
	   url+=url+"?hiddenInputId="
	   +hiddenId+"&showInputId="
	   +showId+"&treeType="+type
	   +"&loginNameId="+loginNId;
       $.colorbox({href:encodeURI(url),iframe:true, innerWidth:400, innerHeight:400,overlayClose:false,title:"移除",onClosed:function(){}});
	
	}
	
	function getHiddenInputValueFromInput(){
	   if(hiddenId=='NOHiddenInputId'){
	     return $("#builtInInput").val();
	   }else{
	     return $("#"+hiddenId).val();
	   }
	}
	function createRemoveJson(showId,loginNId){
	  var hiddenIdValue = getHiddenInputValueFromInput().split(",");
	  var showIdValue = $("#"+showId).val().split(",");
	  var loginNameIdValue = "";
	
	  if((loginNId=='')||(typeof($("#"+loginNId).val())=='undefined')){
		  loginNameIdValue="noLoginName";
	  }else{
		  loginNameIdValue = $("#"+loginNId).val().split(",");
	  }
	 
	  if(getHiddenInputValueFromInput().indexOf("+")==-1){
		  var json = '[';
		  for(var i=0;i<hiddenIdValue.length;i++){
		      if(treeType=='DEPARTMENT_TREE'){
			   json = json + '{"id":"'+hiddenIdValue[i]+'","name":"'+showIdValue[i]+'","type":"department","loginName":""},';
			  }else if(treeType=='GROUP_TREE'){
			   json = json + '{"id":"'+hiddenIdValue[i]+'","name":"'+showIdValue[i]+'","type":"group","loginName":""},';
			  }else{
			   if(loginNameIdValue=="noLoginName"){
			      json = json + '{"id":"'+hiddenIdValue[i]+'","name":"'+showIdValue[i]+'","type":"user","loginName":""},';
			   }else{
			      json = json + '{"id":"'+hiddenIdValue[i]+'","name":"'+showIdValue[i]+'","type":"user","loginName":"'+loginNameIdValue[i]+'"},';
			   }
			  }
		  }
		  json = json.substring(0,json.length-1)+']';
	  }else{
	    var ids = hiddenIdValue;
        var departmentIds = ids.split("+")[0].split("==")[1];
		var groupIds = ids.split("+")[1].split("==")[1];
		var json = '[';
			if(departmentIds!=''){
			 var dids = departmentIds.split(",");
			 for(var i=0;i<dids.length;i++){
				  json = json + '{"id":"'+dids[i]+'","name":"'+showIdValue[i]+'","type":"department","loginName":""},';
			  }
			}
			if(groupIds!=''){
			 var gids = groupIds.split(",");
			 for(var i=0;i<gids.length;i++){
				  json = json + '{"id":"'+gids[i]+'","name":"'+showIdValue[i]+'","type":"group","loginName":""},';
			  }
			}
		    json = json.substring(0,json.length-1)+']';
	  }
	  return json;
	}
    function getId(){
		return singleId;
	}	
	function getName(){
		return singleName;
	}	
		
	function getType(){
		return singleType;
	}
	function getLoginName(){
		return singleLoginName;
	}
	function getSingleInfor(){
		return sInfor;
	}
	
	function getIds(){return mId;};
	function getNames(){return mName;};
	function getLoginNames(){return mLoginName;};
	function getInfos(){return infor;};
	function getUserInfos(){return userInfo;};
	function getTreeType(){return mTreeType;}

	
	