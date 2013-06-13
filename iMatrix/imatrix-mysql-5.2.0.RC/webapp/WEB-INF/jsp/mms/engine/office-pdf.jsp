<%@ page contentType="text/html; charset=utf-8" %>
<%@ page import="java.io.*,java.text.*,java.util.*,java.sql.*,java.text.SimpleDateFormat,java.text.DateFormat,java.util.Date,javax.servlet.*,javax.servlet.http.*" %>
<%@ include file="/common/mms-taglibs.jsp"%>
<html>
<head>
<%@ include file="/common/mms-meta.jsp"%>
<title>正文编辑</title>

<link type="text/css" rel="stylesheet" href="${ctx}/widgets/formValidator/validator.css"></link>
<script src="${ctx}/widgets/formValidator/formValidator.js" type="text/javascript" charset="UTF-8"></script>
<script src="${ctx}/widgets/formValidator/formValidatorRegex.js" type="text/javascript" charset="UTF-8"></script>

<script language=javascript>

//作用：显示操作状态
function StatusMsg(mString){
	window.status=mString;
}

//作用：载入iWebPDF
function Load(){   
  try{
    //以下属性必须设置，实始化iWebPDF
    
    webform.WebPDF.WebUrl= "../WebPdf";   //WebUrl:系统服务器路径，与服务器文件交互操作，如保存、打开文档 

    webform.WebPDF.RecordID=$('#recordId').attr('value');            //RecordID:本文档记录编号
    webform.WebPDF.FileName=$('#fileName').attr("value");            //FileName:文档名称
    webform.WebPDF.FileType=$("#fileType").attr("value");            //FileType:文档类型  .doc  .xls  .wps
   	webform.WebPDF.UserName=$("#userName").attr("value");//用户名
    webform.WebPDF.ShowTools = 0;               //工具栏可见（1,可见；0,不可见）
    webform.WebPDF.EnableTools("关闭文档", 0);
    webform.WebPDF.EnableTools("添加水印;批量验证", 0);
    if($("#downloadSetting").attr("value")=='true'){
    	webform.WebPDF.EnableTools("另存为", 1);             //是否允许保存当前文档（1,允许；0,不允许）
    }else{
    	webform.WebPDF.EnableTools("另存为", 0);              //是否允许保存当前文档（1,允许；0,不允许）
    }
    if($('#editType').attr("value").split(',').length=8&&$('#editType').attr("value").split(',')[1]=='1'){
   		$("#saveButton").hide();
   	}   
    if($("#printSetting").attr("value")=='true'){
    	 webform.WebPDF.PrintRight = 1;              //是否允许打印当前文档（1,允许；0,不允许）
    }else{
    	 webform.WebPDF.PrintRight = 0;              //是否允许打印当前文档（1,允许；0,不允许）
    }
   
    webform.WebPDF.AlterUser = false;           //是否允许由控件弹出提示框 true表示允许  false表示不允许
  
    webform.WebPDF.ShowBookMark = 1;			//是否显示书签树按钮（1,显示；0,不显示）
    webform.WebPDF.ShowSigns = 0;         	    //设置签章工具栏当前是否可见（1,可见；0,不可见）
    webform.WebPDF.SideWidth = 100;             //设置侧边栏的宽度
    webform.WebPDF.WebOpen();                   //打开该文档    交互OfficeServer的OPTION="LOADFILE"    <参考技术文档>
    StatusMsg(webform.WebPDF.Status);           //状态信息
    
    webform.WebPDF.Zoom = 100;                  //缩放比例
    webform.WebPDF.Rotate = 360;                //当显示页释放角度
    webform.WebPDF.CurPage = 1;                 //当前显示的页码
  }catch(e){
	  alert("请安装控件。如果没有安装信息，请将IE的安全性设置为允许经过了数字签名的控件执行");                         //显示出错误信息
  }
}

//作用：退出iWebPDF
function unLoad(){
  try{
    if (!webform.WebPDF.WebClose()){
      StatusMsg(webform.WebPDF.Status);
    }else{
      StatusMsg("关闭文档...");
    }
  }catch(e){
	  alert("关闭文档出错："+e);
  }
  try{
	 
	  	var workFlowInstanceId = $('#workFlowInstanceId').attr("value");
	    var taskId= $('#taskId').attr("value");
	    window.opener.freshParentDocumentListwj(webRoot+"/common/do-text.htm?workflowId="+workFlowInstanceId+"&taskId="+taskId);
  }catch(e){
  }
}

//作用：保存文档
function SaveDocument(){
	if(webform.WebPDF.RecordID==""){
		webform.WebPDF.RecordID="newFile";
	}
	if(webform.WebPDF.FileName==""){
		webform.WebPDF.FileName = $("#newFileName").attr("value");
	}
	webform.WebPDF.ExtParam="COMPANYID:"+$('#companyId').attr("value")+";WORKFLOWID:"+$('#workFlowInstanceId').attr("value")
		+";TASKNAME:"+$('#taskName').attr("value")+
		";TASKMODE:"+$('#taskMode').attr("value")+
		";TRUENAME:"+$('#authorName').attr("value");
  //webform.WebPDF.WebSetMsgByName("mydefine1","自定义变量值");  //设置变量MyDefine1="自定义变量值1"，变量可以设置多个  在WebSave()时，一起提交到OfficeServer中
  if (!webform.WebPDF.WebSave()){               //交互OfficeServer的OPTION="SAVEFILE"
    StatusMsg(webform.WebPDF.Status);
    return false;
  }else{
    StatusMsg(webform.WebPDF.Status);
    $("#recordId").attr("value",webform.WebPDF.WebGetMsgByName("DOCUMENTID"));
    Load();
    return true;
  }
}

//关闭OFFICE
function closeOffice(){
	this.close();
}
function validate(){
	$.formValidator.initConfig({formid:"webformId",onsuccess: function() {SaveDocument();return false;},onerror:function(msg){}});
	$("#newFileName").formValidator({onshow:"必填",onfocus:"必填"}).inputValidator({min:1,empty:{leftempty:false,emptyerror:"必填"},onerror:"必填"});
}
</script>
</head>
<body bgcolor="#ffffff" onLoad="Load()" onUnload="unLoad()">  <!--引导和退出iWebPDF-->
   <input type="hidden" id="userName" value="${currentUserName }"/>
 <form id="webformId" name="webform" method="post" action=""  >  <!--保存iWebPDF后提交表单信息-->
	<p class="buttonP" align="left">
		<a href="#" onclick="$('#webformId').submit();" class="btnStyle" id="saveButton">保存</a>
		<s:if test="fileName==null">
			<span id="newFileNameSpan" >请输入文件名：<input id="newFileName" type="text"></input><span id="newFileNameTip"></span></span>
		</s:if>
		<script>validate();</script>
	</p>
	<input type="hidden" id="taskId" name="taskId" value="${taskId}">
    <input type="hidden" id="recordId" name="id" value="${id}"/>
    <input type="hidden" name="creator" value="${creator }"/>
    <input type="hidden"  id="authorName" name="creatorName" value="${creatorName }"/>
    <input type="hidden" id="fileType" name="fileType" value="${fileType}">
    <input type="hidden" id="workFlowInstanceId" name="workFlowId" value="${workflowId}">
    <input type="hidden" id="fileName" name="fileName" value="${fileName}">
    <input type="hidden" id="editType" name="editType" value="${editType }"/>
    <input type="hidden" id="taskName" name="taskName" value="${taskName }"/>
    <input type="hidden" id="taskMode" name="taskMode" value="${taskMode }"/>
    <input type="hidden" id="companyId" name="companyId" value="${companyId }"/>
    <input type="hidden" id="printSetting" name="printSetting" value="${printSetting }"/>
    <input type="hidden" id="downloadSetting" name="downloadSetting" value="${downloadSetting }"/>
    <input type=hidden name=StatusBar readonly class="IptStyleBlack" style="WIDTH:75%">
     <script type="text/javascript">
    	var pdfheight = ($(window).height()-40);
    	if($('#editType').attr("value").split(',').length=8&&$('#editType').attr("value").split(',')[1]=='1'){
    		pdfheight = ($(window).height()-10);
       	 }    
    </script>
    <script src="${ctx}/widgets/iWebOffice/iWebPDF.js"></script>
  </form>
</body>
</html>