<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/setting-iframe-meta.jsp"%>
	<script type="text/javascript" src="${imatrixCtx}/widgets/swfupload/swfupload.js"></script>
	<script type="text/javascript" src="${imatrixCtx}/widgets/swfupload/handlers.js"></script>
	
	<!-- 树 -->
	<script type="text/javascript" src="${resourcesCtx}/js/staff-tree.js"></script>
	
	<title>签章设置</title>
	
<script type="text/javascript">

function _signatureSave(){
	var signatureId = $("#signatureId").val();
	var userId = $("#userId").val();
	$.ajax({
		   type: "POST",
		   url: webRoot+'/signature/signature-validate.htm',
		   data:{signatureId:signatureId,userId:userId},
		   success: function(data, textStatus){
			   if(data=='true'){
					$("#backMsg").html("<font class='onError'><nobr>此用户已经注册!</nobr></font>");
					showMsg("backMsg");
				}else{
					$("#signatureFormInput").submit();
				}
	      },
			error : function(XMLHttpRequest, textStatus) {
			}
	  }); 
}



	function saveAndValidateSignature(){
		$("#signatureFormInput").validate({
			submitHandler: function() {
				ajaxSubmit("signatureFormInput", webRoot+"/signature/signature-save.htm", "signature_zone", showSignatureMsg);
			},
			rules: {
				userName:"required"
			},
			messages: {
				userName:"必填"
			}
		});
	 }

	function showSignatureMsg(msg){
		if(msg != "") $("#backMsg").html(msg);
		$("#backMsg").show();
		setTimeout('$("#backMsg").hide("show");',3000);
		saveAndValidateSignature();
		_upload_signature_picture();
	}

	
	
	function chooseUser(){
		popTree({ title :'选择人员',
			innerWidth:'300',
			treeType:'MAN_DEPARTMENT_TREE',
			defaultTreeValue:'loginName',
			leafPage:'false',
			multiple:'false',
			hiddenInputId:"userId",
			showInputId:"userName",
			acsSystemUrl:webRoot,
			callBack:function(){customCallbackFun();}});
	}

	function customCallbackFun(){
		$("#userId").attr("value",jstree.getId());
		$("#userName").attr("value",jstree.getName());
	}

	function _upload_signature_picture(){
			swfu = new SWFUpload({
				upload_url: webRoot+"/signature/signature-picture-upload.htm",
				post_params: {"name" : "参数"},
				file_post_name : "Filedata", //是POST过去的$_FILES的数组名   () 建议使用这个默认值
				// File Upload Settings
				file_size_limit : "30 MB",	// 1000MB
				file_types : "*.bmp;*.gif;*.img;*.jpg;*.png",
				file_types_description : "所有文件",
				file_upload_limit : "0",
				file_queue_error_handler : fileQueueError,
				file_dialog_complete_handler : fileDialogComplete,//选择好文件后提交
				file_queued_handler : fileQueued,
				upload_progress_handler : uploadProgress,
				upload_error_handler : uploadError,
				upload_success_handler : uploadSuccess,
				upload_complete_handler : uploadComplete,
				// Button Settings
				button_image_url : imatrixRoot+"/images/annex.gif",
				button_placeholder_id : "spanButtonPlaceholder",
				button_width: 250,
				button_height: 18,
				button_text : '<span class="button">请点击这里上传图片(最大30MB)</span>',
				button_text_style : '.button {border:1px solid #91B8D2;color:#2970A6;  }',
				button_text_top_padding: 0,
				button_text_left_padding: 18,
				button_window_mode: SWFUpload.WINDOW_MODE.TRANSPARENT,
				button_cursor: SWFUpload.CURSOR.HAND,
				// Flash Settings
				flash_url : imatrixRoot+"/widgets/swfupload/swfupload.swf",
				custom_settings : {
					upload_target : "divFileProgressContainer",
					isUpload : true
				},
				// Debug Settings
				debug: false  //是否显示调试窗口
			});
		}

	function fileDialogBefore(){
		var id=$("#signatureId").attr("value");
		if(id==""||id==null){
			swfu.customSettings.isUpload=false;
			alert("请先保存此表单后,再上传图片!");
			swfu.eventQueue=[];
		}else{
			validateSign=true;
			swfu.customSettings.isUpload=true;
			swfu.setPostParams({"signatureId":id});
			swfu.startUpload();
		}
	}

	function rewriteMethod(){
		var url=webRoot+"/signature/signature-input.htm";
		ajaxSubmit('signatureFormInput',url,'signature_zone',_upload_signature_picture);
	}

	
	function createSignature(){
		ajaxSubmit('signatureForm','${settingCtx}/signature/signature-input.htm','signature_zone',signatureCallBack);
	}

	function signatureCallBack(){
		saveAndValidateSignature();
		_upload_signature_picture();

	}


	function updateSignature(){
		var signature=jQuery("#signature_table").getGridParam('selarrrow');
		if(signature.length<=0){
			$("#message").html("<font class=\"onError\"><nobr>请选择一条记录</nobr></font>");
			showMsg("message");
		}else if(signature.length>1){
			$("#message").html("<font class=\"onError\"><nobr>只能选择一条记录</nobr></font>");
			showMsg("message");
		}else{
			var signatureId = signature[0];
			ajaxSubmit("signatureForm",'${settingCtx}/signature/signature-input.htm?signatureId='+signatureId,'signature_zone',signatureCallBack); 
		}
	}


	function deleteSignature(){
		var signatures=jQuery("#signature_table").getGridParam('selarrrow');
		var signatureDeleteIds;
		if(signatures.length<=0){
			$("#message").html("<font class=\"onError\"><nobr>请选择一条记录</nobr></font>");
			showMsg("message");
		}else{
			for(var i=0;i<signatures.length;i++){
				if(i==0){
					signatureDeleteIds = signatures[i]+",";
				}else{
					signatureDeleteIds += signatures[i]+",";
				}
			}
			if(confirm("确定删除？")){
				setPageState();
				ajaxSubmit('signatureForm','${settingCtx}/signature/signature-picture-delete.htm?signatureDeleteIds='+signatureDeleteIds, 'signature_zone',signatureDeleteCallBack);
			}else{
				return false;
			} 
		}
	}

	function signatureDeleteCallBack(){
		$("#message").html("<font class='onSuccess'><nobr>删除成功</nobr></font>");
		showMsg("message");
	}


	function _imageView(ts1,cellval,opts){
		var vsrc=webRoot+"/signature/signature-picture-show.htm?signatureId="+opts.id;
		if(opts.pictureSrc==undefined||opts.pictureSrc==""||opts.pictureSrc=="&nbsp;"){
			vsrc=imatrixRoot+"/images/photo.jpg";
		}
		var v="<img style=\"height:1cm;width:2cm;\"  src=\""+vsrc+"\"/>";
		return v;
	}

	
</script>

</head>
<body>
<div class="ui-layout-center">
	<div class="opt-body">
	<form id="defaultForm" name="defaultForm"action="" method="post" ></form>
      <aa:zone name="signature_zone">
		<div class="opt-btn">
			<button  class='btn' onclick="iMatrix.showSearchDIV(this);"><span><span>查询</span></span></button>
			<button class='btn' onclick="createSignature()"><span><span>新建</span></span></button>
			<button class='btn' onclick="updateSignature()"><span><span>修改</span></span></button>
			<button class='btn' onclick="deleteSignature()"><span><span>删除</span></span></button>
		</div>
		<div style="display: none;" id="message"><s:actionmessage theme="mytheme" /></div>
		<div id="opt-content">
			<form action="" id="signatureForm" name="signatureForm" method="post">
            </form>
			<form id="gridForm" name="gridForm" action="">
				<view:jqGrid url="${settingCtx}/signature/signature.htm" code="BS_SIGNATURE" gridId="signature_table" pageName="signaturePage" submitForm="defaultForm"></view:jqGrid>
			</form>
		</div>
     </aa:zone>
	</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>