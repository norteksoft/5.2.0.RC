var okEnsure;
var singleId;
var singleName;
var singleType;
var singleLoginName;
var sInfor;
var singleDepartmentName;
var singleDepartmentId;
var	singleWorkGroupName;
var	singleWorkGroupId;
var	singleEmail;
var	singleHonorificName;
var	singleWeight;
var singleUserDeptName;

var mId;
var mName;
var mLoginName;
var infor;
var userInfo;
var mTreeType;
var mType;
var mDepartmentName;
var mDepartmentId;
var mWorkGroupName;
var mWorkGroupId;
var mEmail;
var mHonorificName;
var mWeight;


var acsSystemUrl;
var hiddenInputId;
var showInputId;
var loginNameId;
var treeType;
var isAppend;
var mode;
var onlineVisible;
var departmentDisplayType;
var userWithoutDeptVisible;

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
	mode = paramater.mode;
	onlineVisible = paramater.onlineVisible;
	departmentDisplayType = paramater.departmentDisplayType;
    userWithoutDeptVisible = paramater.userWithoutDeptVisible;
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
	var rootUrl=acsSystemUrl;if(typeof(appRoot)!='undefined'&&appRoot!=''){rootUrl=appRoot;}
	if(typeof(departmentDisplayType)=='undefined') departmentDisplayType='NAME';
	if(typeof(userWithoutDeptVisible)=='undefined') userWithoutDeptVisible=false;
	var url = rootUrl+"/portal/popTree.action?treeType="+treeType
			+'&multiple='+multiple+'&hiddenInputId='
			+hiddenInputId+'&showInputId='+showInputId
			+'&callBack='+callBack+'&treeTypeJson='+treeTypeJson
			+'&leafPage='+leafPage+'&defaultTreeValue='+defaultTreeValue
			+'&isAppend='+isAppend+'&loginNameId='+loginNameId
			+'&formId='+formId+'&mode='+mode+'&onlineVisible='+onlineVisible+"&departmentDisplayType="+departmentDisplayType+"&userWithoutDeptVisible="+userWithoutDeptVisible;
      $.colorbox({href:encodeURI(url),iframe:true, innerWidth:innerWidth, innerHeight:400,overlayClose:false,title:title,onClosed:function(){
	  if(okEnsure=='OK'){callBack.call();okEnsure='';}
	  }});
	
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
	  
	  
	  var url = acsUrl+"/portal/removeOptionTree.action";
	 // var hiddId = getHiddenInputValueFromInput();
	   var removeStaffJson = createRemoveJson(showId,loginNId);
	   var removeStaffJsonOld = $("#removeStaffJson").val();
	   if(typeof(removeStaffJsonOld)=='undefined'){
	      $("#"+formId).append("<input type='hidden' id='removeStaffJson' name='removeStaffJson' value='"+removeStaffJson+"'/> ");
	   }else{
	      $("#removeStaffJson").attr("value",removeStaffJson);
	   }
	   //removeStaffJson="+removeStaffJson
	   url=url+"?hiddenInputId="
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
	 
	  if(getHiddenInputValueFromInput().indexOf("+#")==-1){
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
        var departmentIds = ids.split("+#")[0].split("==")[1];
		var groupIds = ids.split("+#")[1].split("==")[1];
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
    function getId(){alert("方法调用修改：请把【getId()】更换为【jstree.getId()】");}	
	function getName(){alert("方法调用修改：请把【getName()】更换为【jstree.getName()】");}
		
	function getType(){alert("方法调用修改：请把【getType()】更换为【jstree.getType()】");}
	function getLoginName(){alert("方法调用修改：请把【getLoginName()】更换为【jstree.getLoginName()】");}
	function getSingleInfor(){alert("方法调用修改：请把【getSingleInfor()】更换为【jstree.getSingleInfor()】");}
	
	//部门名称
	function getDepartmentName(){alert("方法调用修改：请把【getDepartmentName()】更换为【jstree.getDepartmentName()】");}
	//部门id
	function getDepartmentId(){alert("方法调用修改：请把【getDepartmentId()】更换为【jstree.getDepartmentId()】");}
	//工作组名称
	function getWorkGroupName(){alert("方法调用修改：请把【getWorkGroupName()】更换为【jstree.getWorkGroupName()】");}
	//工作组id
	function getWorkGroupId(){alert("方法调用修改：请把【getWorkGroupId()】更换为【jstree.getWorkGroupId()】");}
	//用户邮件
	function getEmail(){alert("方法调用修改：请把【getEmail()】更换为【jstree.getEmail()】");}
	//用户尊称
	function getHonorificName(){alert("方法调用修改：请把【getHonorificName()】更换为【jstree.getHonorificName()】");}
	//用户权重
	function getWeight(){alert("方法调用修改：请把【getWeight()】更换为【jstree.getWeight()】");}
	//获取用户部门名称
	function getUserDepartmentName(){alert("方法调用修改：请把【getUserDepartmentName()】更换为【jstree.getUserDepartmentName()】");}
	
	
	
	
	function getIds(){alert("方法调用修改：请把【getIds()】更换为【jstree.getIds()】");}
	function getNames(){alert("方法调用修改：请把【getNames()】更换为【jstree.getNames()】");}
	function getLoginNames(){alert("方法调用修改：请把【getLoginNames()】更换为【jstree.getLoginNames()】");}
	function getInfos(){alert("方法调用修改：请把【getInfos()】更换为【jstree.getInfos()】");}
	function getUserInfos(){alert("方法调用修改：请把【getUserInfos()】更换为【jstree.getUserInfos()】");}
	function getTreeType(){alert("方法调用修改：请把【getTreeType()】更换为【jstree.getTreeType()】");}
	function getTypes(){alert("方法调用修改：请把【getTypes()】更换为【jstree.getTypes()】");}
	function getDepartmentNames(){alert("方法调用修改：请把【getDepartmentNames()】更换为【jstree.getDepartmentNames()】");}
	//部门id
	function getDepartmentIds(){alert("方法调用修改：请把【getDepartmentIds()】更换为【jstree.getDepartmentIds()】");}
	//工作组名称
	function getWorkGroupNames(){alert("方法调用修改：请把【getWorkGroupNames()】更换为【jstree.getWorkGroupNames()】");}
	//工作组id
	function getWorkGroupIds(){alert("方法调用修改：请把【getWorkGroupIds()】更换为【jstree.getWorkGroupIds()】");}
	//用户邮件
	function getEmails(){alert("方法调用修改：请把【getEmails()】更换为【jstree.getEmails()】");}
	//用户尊称
	function getHonorificNames(){alert("方法调用修改：请把【getHonorificNames()】更换为【jstree.getHonorificNames()】");}
	//用户权重
	function getWeights(){alert("方法调用修改：请把【getWeights()】更换为【jstree.getWeights()】");}
	
	var jstree={
	getId:function(){return singleId;},	
	getName:function(){return singleName;},	
		
	getType:function(){return singleType;},
	getLoginName:function(){return singleLoginName;},
	getSingleInfor:function(){return sInfor;},
	
	//部门名称
	getDepartmentName:function(){return singleDepartmentName;},
	//部门id
	getDepartmentId:function(){return singleDepartmentId;},
	//工作组名称
	getWorkGroupName:function(){ return singleWorkGroupName;},
	//工作组id
	getWorkGroupId:function(){ return singleWorkGroupId;},
	//用户邮件
	getEmail:function(){ return singleEmail;},
	//用户尊称
	getHonorificName:function(){ return singleHonorificName;},
	//用户权重
	getWeight:function(){return singleWeight;},
	//获取用户部门名称
	getUserDepartmentName:function(){return singleUserDeptName;},
	
	
	
	
	getIds:function(){return mId;},
	getNames:function(){return mName;},
	getLoginNames:function(){return mLoginName;},
	getInfos:function(){return infor;},
	getUserInfos:function(){return userInfo;},
	getTreeType:function(){return mTreeType;},
	getTypes:function(){return mType;},
	getDepartmentNames:function(){return mDepartmentName;},
	//部门id
	getDepartmentIds:function(){return mDepartmentId;},
	//工作组名称
	getWorkGroupNames:function(){return mWorkGroupName;},
	//工作组id
	getWorkGroupIds:function(){return mWorkGroupId;},
	//用户邮件
	getEmails:function(){ return mEmail; },
	//用户尊称
	getHonorificNames:function(){return mHonorificName;},
	//用户权重
	getWeights:function(){return mWeight;}
};
	