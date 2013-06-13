function fileQueueError4(file, errorCode, message) {
	try {
		var imageName = "<font color='red'>文件上传错误</font>";
		var errorName = "";
		if (errorCode === SWFUpload.errorCode_QUEUE_LIMIT_EXCEEDED) {
			errorName = "You have attempted to queue too many files.";
		}

		if (errorName !== "") {
			alert(errorName);
			return;
		}
		
		switch (errorCode) {
		case SWFUpload.QUEUE_ERROR.ZERO_BYTE_FILE:
			imageName = "文件大小为0";
			break;
		case SWFUpload.QUEUE_ERROR.FILE_EXCEEDS_SIZE_LIMIT:
			imageName = "文件大小超过限制";
			break;
		case SWFUpload.QUEUE_ERROR.ZERO_BYTE_FILE:
		case SWFUpload.QUEUE_ERROR.INVALID_FILETYPE:
		default:
			alert(message);
			break;
		}
		addReadyFileInfo(file.id,file.name,imageName,"无法上传");

	} catch (ex) {
		this.debug(ex);
	}
}

/**
 * 当文件选择对话框关闭消失时，如果选择的文件成功加入上传队列，
 * 那么针对每个成功加入的文件都会触发一次该事件（N个文件成功加入队列，就触发N次此事件）。
 * @param {} file
 * id : string,			    // SWFUpload控制的文件的id,通过指定该id可启动此文件的上传、退出上传等
 * index : number,			// 文件在选定文件队列（包括出错、退出、排队的文件）中的索引，getFile可使用此索引
 * name : string,			// 文件名，不包括文件的路径。
 * size : number,			// 文件字节数
 * type : string,			// 客户端操作系统设置的文件类型
 * creationdate : Date,		// 文件的创建时间
 * modificationdate : Date,	// 文件的最后修改时间
 * filestatus : number		// 文件的当前状态，对应的状态代码可查看SWFUpload.FILE_STATUS }
 */
function fileQueued4(file){
	fileDialogBefore4();
	if(!this.customSettings.isUpload){
		this.cancelUpload(file.id, false);
	}
	addReadyFileInfo(file.id,file.name,"成功加载到上传队列");
}

function fileDialogComplete4(numFilesSelected, numFilesQueued) {
	
	try {
		if (numFilesQueued > 0) {
			
		}
	} catch (ex) {
		this.debug(ex);
	}
}
//重写
function fileDialogBefore4(){}
function uploadProgress4(file, bytesLoaded) {

	try {
		var percent = Math.ceil((bytesLoaded / file.size) * 100);

		var progress = new FileProgress(file,  this.customSettings.upload_target);
		progress.setProgress(percent);
		if (percent === 100) {
			progress.setStatus("");//正在创建缩略图...
			progress.toggleCancel(false, this);
		} else {
			progress.setStatus("正在上传...");
			progress.toggleCancel(true, this);
		}
	} catch (ex) {
		this.debug(ex);
	}
}

function uploadSuccess4(file, serverData) {
	try {
		uploadSuccess_callback(file, serverData);
		var progress = new FileProgress(file,  this.customSettings.upload_target);
		addFileInfo(file.id,"文件上传完成");
	} catch (ex) {
		this.debug(ex);
	}
}


function uploadComplete4(file) {
	try {
		/*  I want the next upload to continue automatically so I'll call startUpload here */
		if (this.getStats().files_queued > 0) {
			this.startUpload();
		} else {
			var progress = new FileProgress(file,  this.customSettings.upload_target);
			progress.setComplete();
			progress.setStatus("<font color='red'>所有文件上传完毕!</b></font>");
			progress.toggleCancel(false);
			rewriteMethod4();
		}
	} catch (ex) {
		this.debug(ex);
	}
}
//重写
function rewriteMethod4(){}
function uploadError4(file, errorCode, message) {
	var imageName =  "<font color='red'>文件上传出错!</font>";
	var progress;
	try {
		switch (errorCode) {
		case SWFUpload.UPLOAD_ERROR.FILE_CANCELLED:
			try {
				progress = new FileProgress(file,  this.customSettings.upload_target);
				progress.setCancelled();
				progress.setStatus("<font color='red'>取消上传!</font>");
				progress.toggleCancel(false);
			}
			catch (ex1) {
				this.debug(ex1);
			}
			break;
		case SWFUpload.UPLOAD_ERROR.UPLOAD_STOPPED:
			try {
				progress = new FileProgress(file,  this.customSettings.upload_target);
				progress.setCancelled();
				progress.setStatus("<font color='red'>停止上传!</font>");
				progress.toggleCancel(true);
			}
			catch (ex2) {
				this.debug(ex2);
			}
		case SWFUpload.UPLOAD_ERROR.UPLOAD_LIMIT_EXCEEDED:
			imageName = "<font color='red'>文件大小超过限制!</font>";
			break;
		default:
			alert("文件个数超过大小"+message);
			break;
		}
		addFileInfo(file.id,imageName);
	} catch (ex3) {
		this.debug(ex3);
	}

}

