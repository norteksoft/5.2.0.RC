<%@ page contentType="text/html; charset=utf-8" %>
<%@ page import="java.io.*,java.text.*,java.util.*,java.sql.*,java.text.SimpleDateFormat,java.text.DateFormat,java.util.Date,javax.servlet.*,javax.servlet.http.*" %>
<%@ include file="/common/mms-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
<%@ include file="/common/mms-meta.jsp"%>
<title>正文编辑</title>

<link type="text/css" rel="stylesheet" href="${ctx}/widgets/formValidator/validator.css"></link>
<script src="${ctx}/widgets/formValidator/formValidator.js" type="text/javascript" charset="UTF-8"></script>
<script src="${ctx}/widgets/formValidator/formValidatorRegex.js" type="text/javascript" charset="UTF-8"></script>


<script language="javascript" for=WebOffice event="OnMenuClick(vIndex,vCaption)">

	if (vIndex==1){  //打开本地文件
    	WebOpenLocal();
	}else if(vIndex==2){
		saveLocal();
	}else if(vIndex==3){
		$('#webformId').submit();
	}else if(vIndex==5){
		showTrace();
	}else if(vIndex==6){
		hideTrace();
	}else if(vIndex==8){
		closeOffice();
	}else if(vIndex==9){
		WebOpenPrint();
	}
</script>
<script type="text/javascript">
//作用：显示操作状态
function StatusMsg(mString){
  webform.StatusBar.value=mString;
}
//作用：载入iWebOffice
function Load(){
	
  try{
    //以下属性必须设置，实始化iWebOffice
    webform.WebOffice.WebUrl=webRoot+"/WebOffice";             //WebUrl:系统服务器路径，与服务器文件交互操作，如保存、打开文档，重要文件
    webform.WebOffice.RecordID=$('#recordId').attr('value');            //RecordID:本文档记录编号
    webform.WebOffice.Template=$('#templateId').attr('value');            //Template:模板编号
    webform.WebOffice.FileName=$('#fileName').attr("value");            //FileName:文档名称
    webform.WebOffice.FileType=$("#fileType").attr("value");            //FileType:文档类型  .doc  .xls  .wps
    webform.WebOffice.EditType=$('#editType').attr("value");            //EditType:编辑类型  方式一、方式二  <参考技术文档>
                                                           //第一位可以为0,1,2,3 其中:0不可编辑;1可以编辑,无痕迹;2可以编辑,有痕迹,不能修订;3可以编辑,有痕迹,能修订；
                                                           //第二位可以为0,1 其中:0不可批注,1可以批注。可以参考iWebOffice2003的EditType属性，详细参考技术白皮书
   	webform.WebOffice.UserName=$("#userName").attr("value");//用户名
   	
    webform.WebOffice.MaxFileSize = 4 * 1024;               //最大的文档大小控制，默认是8M，现在设置成4M。
    webform.WebOffice.Language="CH";					    //Language:多语言支持显示选择   CH 简体 TW繁体 EN英文
    webform.WebOffice.ShowWindow = true;                  //控制显示打开或保存文档的进度窗口，默认不显示

    if($("#printSetting").attr("value")=='true'){
	    webform.WebOffice.Print="1";	//允许打印
    }else{
    	webform.WebOffice.Print="0";	//允许打印
    }
    	webform.WebOffice.ShowMenu="1";
        webform.WebOffice.AppendMenu("1","打开本地文件(&L)");
        webform.WebOffice.AppendMenu("2","保存本地文件(&S)");	
        webform.WebOffice.AppendMenu("3","保存到服务器(&U)");	
        webform.WebOffice.AppendMenu("4","-");	
        webform.WebOffice.AppendMenu("5","显示痕迹");	
        webform.WebOffice.AppendMenu("6","隐藏痕迹");
        webform.WebOffice.AppendMenu("7","-");	
        webform.WebOffice.AppendMenu("8","关闭窗口");
        webform.WebOffice.AppendMenu("9","打印文档");
        webform.WebOffice.DisableMenu("宏(&M);选项(&O)...");    //禁止菜单
        webform.WebOffice.DisableMenu("关于我们(&A)...");
        if($("#editType").attr("value").split(',')[1]=='0'){//编辑权限
        	webform.WebOffice.EnableMenu("打开本地文件(&L)"); 
	    	webform.WebOffice.EnableMenu("保存到服务器(&U)");    
	    }else{
	    	webform.WebOffice.DisableMenu("打开本地文件(&L)"); 
	    	webform.WebOffice.DisableMenu("保存到服务器(&U)");    
		 }
        if($("#fileType").attr("value")==".doc"&&$("#editType").attr("value").split(',')[2]=='1'){//编辑痕迹权限
        	 webform.WebOffice.DisableMenu("显示痕迹");    
        }else{
        	 webform.WebOffice.DisableMenu("显示痕迹");    
        	 webform.WebOffice.DisableMenu("隐藏痕迹");    
        }       
        if($("#downloadSetting").attr("value")=='true'){//下载权限
        	webform.WebOffice.EnableMenu("保存本地文件(&S)");    
        }else{
        	 webform.WebOffice.DisableMenu("保存本地文件(&S)");    
        }
    //WebSetRibbonUIXML(); 
      if($('#editType').attr("value").split(',').length=8&&$('#editType').attr("value").split(',')[1]=='1'){//无编辑权限
  		$("#saveButton").hide();
  		if($("#printSetting").attr("value")=='true'){
		    webform.WebOffice.ToolsSpace=1;
	  		webform.WebOffice.ShowToolBar=1;
	  		webform.WebOffice.Print="1";	//允许打印
	  		WebToolsEnable('Standard',2521,true);
	  		webform.WebOffice.EnableMenu("打印文档");  
	    }else{
	  		webform.WebOffice.ToolsSpace=0;
	  		webform.WebOffice.ShowToolBar=2;
	  		WebToolsEnable('Standard',2521,false);
	  		webform.WebOffice.DisableMenu("打印文档");
	    }
   	 }
      if($('#editType').attr("value").split(',').length=8&&$('#editType').attr("value").split(',')[1]=='0'){
    	  if($("#printSetting").attr("value")=='true'){
    		    webform.WebOffice.ToolsSpace=1;
    	  		webform.WebOffice.ShowToolBar=1;
    	  		webform.WebOffice.Print="1";	//允许打印
    	  		WebToolsEnable('Standard',2521,true);
    	  		webform.WebOffice.EnableMenu("打印文档");  
    	    }else{
    	  		WebToolsEnable('Standard',2521,false);
    	  		webform.WebOffice.DisableMenu("打印文档");
    	    }
   	 }                                       //控制OFFICE2007的选项卡显示
    webform.WebOffice.WebOpen();    
     StatusMsg(webform.WebOffice.Status);                    //状态信息
  }
  catch(e){
    alert("请安装控件。如果没有安装信息，请将IE的安全性设置为允许经过了数字签名的控件执行");                                   //显示出错误信息
  }
}

//作用：禁止或启用工具 参数1表示工具条名称  参数2表示工具条铵钮的编号  （名称和编号均可查找VBA帮助）
//参数3为false时，表示禁止  参数3为true时，表示启用
function WebToolsEnable(ToolName,ToolIndex,Enable){
try{
webform.WebOffice.WebToolsEnable(ToolName,ToolIndex,Enable);
StatusMsg(webform.WebOffice.Status);
}
catch(e){
alert(e.description);
}
}

//作用：打印文档
function WebOpenPrint(){
  try{
    webform.WebOffice.WebOpenPrint();
    StatusMsg(webform.WebOffice.Status);
  }
  catch(e){
    alert(e.description);
  }
}
//作用：退出iWebOffice
  function UnLoad(){
    try{
		if (webform.WebOffic&&!webform.WebOffice.WebClose()){
		  StatusMsg(webform.WebOffice.Status);
		}
		else{
		  StatusMsg("关闭文档...");
		}
    }catch(e){
      alert("关闭文档出错："+e);
    }
    try{
		var workFlowInstanceId = $('#workFlowInstanceId').attr("value");
   	 	var taskId= $('#taskId').attr("value");
   	    window.opener.freshParentDocumentListwj(webRoot+"/common/do-text.htm?taskId="+taskId);
   	    
    }catch(e){
    }
  }
  

//作用：保存文档
function SaveDocument(){
	if(webform.WebOffice.FileName==""){
		webform.WebOffice.FileName = $("#newFileName").attr("value");
	}
	webform.WebOffice.ExtParam="COMPANYID:"+$('#companyId').attr("value")+";WORKFLOWID:"+$('#workFlowInstanceId').attr("value")
		+";TASKNAME:"+$('#taskName').attr("value")+
		";TASKMODE:"+$('#taskMode').attr("value")+
		";TRUENAME:"+$('#authorName').attr("value");
	
  //webform.WebOffice.WebSetMsgByName("MyDefine1","自定义变量值1");  //设置变量MyDefine1="自定义变量值1"，变量可以设置多个  在WebSave()时，一起提交到OfficeServer中
  if (!webform.WebOffice.WebSave()){    //交互OfficeServer的OPTION="SAVEFILE"  注：WebSave()是保存复合格式文件，包括OFFICE内容和手写批注文档；如只保存成OFFICE文档格式，那么就设WebSave(true)
    StatusMsg(webform.WebOffice.Status);
    return false;
  }
  else{
    StatusMsg(webform.WebOffice.Status);
    $("#recordId").attr("value",webform.WebOffice.WebGetMsgByName("DOCUMENTID"));
    Load();
    myShowMsg();
    return false;
  }
}

function myShowMsg(){
	$("#successMessage").html("保存成功");
	$("#successMessage").show();
	setTimeout('$("#successMessage").hide();',3000);
}

//作用：存为本地文件
function saveLocal(){
	try{
		webform.WebOffice.WebSetProtect(false,'');
		if(webform.WebOffice.WebObject.Application.ActiveDocument){
			//清除痕迹
			webform.WebOffice.WebObject.Application.ActiveDocument.AcceptAllRevisions();
		}
		webform.WebOffice.WebSaveLocal();
		if(webform.WebOffice.WebObject){
		   //返回上一步
		    webform.WebOffice.WebObject.Undo();
		}
		webform.WebOffice.WebSetProtect(true,'');
		StatusMsg(webform.WebOffice.Status);
	}catch(e){
		alert(e.description);
	}
}

//关闭OFFICE
function closeOffice(){
	if(webform.WebOffice.Modify){
		if($("#newFileName").attr("value")!=""){
			if(confirm("保存当前内容吗?")){
				SaveDocument();
			}
			this.close();
		}else{
			if(!confirm("若保存当前内容,请填写文件名,要保存吗?")){
				this.close();
			}
		}
	}else{
		this.close();
	}
}
function validate(){
	$.formValidator.initConfig({formid:"webformId",onsuccess: function() {SaveDocument();return false;},onerror:function(msg){}});
	$("#newFileName").formValidator({onshow:"必填",onfocus:"必填"}).inputValidator({min:1,empty:{leftempty:false,emptyerror:"必填"},onerror:"必填"});
}
function showTrace(){
	webform.WebOffice.WebShow(true);//在文档为保护状态是不会报错
	 webform.WebOffice.DisableMenu("显示痕迹");    //禁止菜单
	 webform.WebOffice.EnableMenu("隐藏痕迹");    //禁止菜单
}

function hideTrace(){
	webform.WebOffice.WebShow(false);
	 webform.WebOffice.DisableMenu("隐藏痕迹");    //禁止菜单
	 webform.WebOffice.EnableMenu("显示痕迹");    //禁止菜单
}
</script>
</head>
<body bgcolor="#ffffff" onLoad="Load()" onUnload="UnLoad()" >  <!--引导和退出iWebOffice-->
    <input type="hidden" id="userName" value="${currentUserName }"/>
    <form id="webformId" name="webform" method="post" action=""  >  <!--保存iWebOffice后提交表单信息-->
  
	<p class="buttonP" align="left"  >
		<a href="#" onclick="$('#webformId').submit();" class="btnStyle"  id="saveButton">保存</a>
		<s:if test="fileName==null">
			<span id="newFileNameSpan" >请输入文件名：<input id="newFileName" type="text"></input><span id="newFileNameTip"></span></span>
		</s:if>
		<span id="successMessage" class="onSuccess"></span>
		<script>validate();</script>
	</p>
	
    <input type="hidden" id="recordId" name="id" value="${id}"/>
     <input type="hidden"  name="creator" value="${creator }"/>
     <input type="hidden"  id="authorName" name="creatorName" value="${creatorName }"/>
    <input type="hidden" id="templateId" name="templateId" value="${templateId}"/>
    <input type="hidden" id="fileType" name="fileType" value="${fileType}">
    <input type="hidden" id="taskId" name="taskId" value="${taskId}">
    <input type="hidden" id="workFlowInstanceId" name="workFlowId" value="${workflowId}">
    <input type="hidden" id="fileName" name="fileName" value="${fileName}">
    <input type="hidden" id="editType" name="editType" value="1,0,0,0,0,0,1,1"/>
    <input type="hidden" id="taskName" name="taskName" value="${taskName }"/>
    <input type="hidden" id="taskMode" name="taskMode" value="${taskMode }"/>
    <input type="hidden" id="companyId" name="companyId" value="${companyId }"/>
    <input type="hidden" id="printSetting" name="printSetting" value="${printSetting }"/>
    <input type="hidden" id="downloadSetting" name="downloadSetting" value="${downloadSetting }"/>
    <input type=hidden name=StatusBar readonly class="IptStyleBlack" style="WIDTH:75%">
    <script type="text/javascript">
    	var officeheight = ($(window).height()-55);
    	if($('#editType').attr("value").split(',').length=8&&$('#editType').attr("value").split(',')[1]=='1'){
    		officeheight = ($(window).height()-25);
       	 }    
    </script>
    <script src="${ctx}/widgets/iWebOffice/iWebOffice2003.js"></script>
  </form>
</body>
</html>
						