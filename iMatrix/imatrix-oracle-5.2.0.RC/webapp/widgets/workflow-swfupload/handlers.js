
function cancelQueue(instance) {
	instance.stopUpload();
	var stats;
	do {
		stats = instance.getStats();
		instance.cancelUpload();
	} while (stats.files_queued !== 0);
}

function fileDialogStart() { }
function fileQueued(file) {
	try {
		var allSize = 0;
		var trs = $($('#'+this.customSettings.fileListId).children('tbody')).children('tr');
		for(var i = 0; i < trs.length; i++){
			allSize =Number(allSize) + Number($($($(trs[i]).children('td')[1]).children('input')).attr('value'));
		}
		if(this.settings.file_upload_limit != '0' && this.settings.file_upload_limit < (Number(trs.length+1))){
			alert('最多允许上传 ['+ (this.settings.file_upload_limit) +'] 个文件');
			this.cancelUpload(file.id, false);
		//文件总大小控制
		}else if(this.settings.file_size_limit != '0' && this.settings.file_size_limit*1024 < (Number(allSize) + Number(file.size))){
			alert('文件超过了大小');
			this.cancelUpload(file.id, false);
		}else{
			//列表中插入行
			var tr ="<tr id='"+file.id+"'>" +
					"<td style='width:300px;'>"+file.name+"</td><td style='width:60px;'>" +
						"<input type='hidden' value='"+file.size+"'/>"+
						"<a href='#' onclick=\"removeFromQuene('"+file.id+"', '"+this.movieName+"')\">删除</a></td></tr>";
			$('#'+this.customSettings.fileListId).append(tr);
		}
	} catch (ex) {
		this.debug(ex);
	}
}
/**
 * 从待上传队列中删除文件ID
 */
function removeFromQuene(fileId, instanceName){
	SWFUpload.instances[instanceName].cancelUpload(fileId, false);
	deleteCallBack(fileId);
}
function deleteCallBack(fileId){}

function fileQueueError(file, errorCode, message) {
	try {
		var errormessage = "出错啦！";
		var errorName = "";
		if (errorCode === SWFUpload.errorCode_QUEUE_LIMIT_EXCEEDED) {
			errorName = "你上传的文件太多拉.";
		}

		if (errorName !== "") {
			alert(errorName);
			return;
		}

		switch (errorCode) {
		case SWFUpload.QUEUE_ERROR.ZERO_BYTE_FILE:
			errormessage = "上传文件大小为0字节";
			break;
		case SWFUpload.QUEUE_ERROR.FILE_EXCEEDS_SIZE_LIMIT:
			errormessage = "上传文件超出大小限制！";
			break;
		case SWFUpload.QUEUE_ERROR.QUEUE_LIMIT_EXCEEDED:
			errormessage = "超出一次允许上传文件的个数，每次允许上传的文件个数是"+message;
			break;
		case SWFUpload.QUEUE_ERROR.INVALID_FILETYPE:
			errormessage = "无效的文件类型！";
			break;
		default:
			errormessage = message;
			break;
		}

		alert(errormessage);

	} catch (ex) {
		this.debug(ex);
	}
}

function fileDialogComplete(numFilesSelected, numFilesQueued) {
	try {
		if (this.getStats().files_queued > 0) {
			
		}
	} catch (ex)  {
        this.debug(ex);
	}
}

function uploadStart(file) {
	try {
		var progress = new FileProgress(file, this.movieName, this.customSettings.progressTarget);
		progress.setFileName(file.name);
		progress.setStatus("正在上传 ...");
		progress.toggleCancel(true, this);
		this.addPostParam('fileSize', file.size);
	} catch (ex) {
	}
	
	return true;
}

function uploadProgress(file, bytesLoaded, bytesTotal) {
	try {
		var percent = Math.ceil((bytesLoaded / bytesTotal) * 100);
		var progress = new FileProgress(file, this.movieName, this.customSettings.progressTarget);
		progress.setProgress(percent);
	} catch (ex) {
		this.debug(ex);
	}
}

function uploadSuccess(file, serverData) {
	try {
		var progress = new FileProgress(file, this.movieName, this.customSettings.progressTarget);
		progress.setComplete();
		uploadSuccessCallBack(file.id, serverData);
	} catch (ex) {
		this.debug(ex);
	}
}
function uploadSuccessCallBack(fileId, data){}
function uploadComplete(file) {
	try {
		if (this.getStats().files_queued === 0) {
			var progress = new FileProgress(file, this.movieName, this.customSettings.progressTarget);
			progress.setComplete();
			progress.setFileName('');
			progress.setStatus("<font color='green'>所有文件上传完毕!</b></font>");
			progress.toggleCancel(false);
		} else {	
			this.startUpload();
		}
	} catch (ex) {
		this.debug(ex);
	}

}

function uploadError(file, errorCode, message) {
	try {
		var progress = new FileProgress(file, this.movieName, this.customSettings.progressTarget);
		progress.setError();
		progress.toggleCancel(false);

		switch (errorCode) {
		case SWFUpload.UPLOAD_ERROR.HTTP_ERROR:
			progress.setStatus("Upload Error: " + message);
			this.debug("Error Code: HTTP Error, File name: " + file.name + ", Message: " + message);
			break;
		case SWFUpload.UPLOAD_ERROR.MISSING_UPLOAD_URL:
			progress.setStatus("Configuration Error");
			this.debug("Error Code: No backend file, File name: " + file.name + ", Message: " + message);
			break;
		case SWFUpload.UPLOAD_ERROR.UPLOAD_FAILED:
			progress.setStatus("Upload Failed.");
			this.debug("Error Code: Upload Failed, File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			break;
		case SWFUpload.UPLOAD_ERROR.IO_ERROR:
			progress.setStatus("Server (IO) Error");
			this.debug("Error Code: IO Error, File name: " + file.name + ", Message: " + message);
			break;
		case SWFUpload.UPLOAD_ERROR.SECURITY_ERROR:
			progress.setStatus("Security Error");
			this.debug("Error Code: Security Error, File name: " + file.name + ", Message: " + message);
			break;
		case SWFUpload.UPLOAD_ERROR.UPLOAD_LIMIT_EXCEEDED:
			progress.setStatus("Upload limit exceeded.");
			this.debug("Error Code: Upload Limit Exceeded, File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			break;
		case SWFUpload.UPLOAD_ERROR.SPECIFIED_FILE_ID_NOT_FOUND:
			progress.setStatus("File not found.");
			this.debug("Error Code: The file was not found, File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			break;
		case SWFUpload.UPLOAD_ERROR.FILE_VALIDATION_FAILED:
			progress.setStatus("Failed Validation.  Upload skipped.");
			this.debug("Error Code: File Validation Failed, File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			break;
		case SWFUpload.UPLOAD_ERROR.FILE_CANCELLED:
			if (this.getStats().files_queued === 0) {
			}
			progress.setStatus("Cancelled");
			progress.setCancelled();
			break;
		case SWFUpload.UPLOAD_ERROR.UPLOAD_STOPPED:
			progress.setStatus("Stopped");
			break;
		default:
			progress.setStatus("Unhandled Error: " + error_code);
			this.debug("Error Code: " + errorCode + ", File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			break;
		}
	} catch (ex) {
        this.debug(ex);
    }
}

function FileProgress(file, instanceId, targetID) {
this.fileProgressID = instanceId+targetID;

this.opacity = 100;
this.height = 0;


this.fileProgressWrapper = document.getElementById(this.fileProgressID);
if (!this.fileProgressWrapper) {
	this.fileProgressWrapper = document.createElement("div");
	this.fileProgressWrapper.className = "progressWrapper";
	this.fileProgressWrapper.id = this.fileProgressID;

	this.fileProgressElement = document.createElement("div");
	this.fileProgressElement.className = "progressContainer";

	var progressCancel = document.createElement("a");
	progressCancel.className = "progressCancel";
	progressCancel.href = "#";
	progressCancel.style.visibility = "hidden";
	progressCancel.appendChild(document.createTextNode(" "));

	var progressText = document.createElement("div");
	progressText.className = "progressName";
	progressText.appendChild(document.createTextNode(file.name));

	var progressBar = document.createElement("div");
	progressBar.className = "progressBarInProgress";

	var progressStatus = document.createElement("div");
	progressStatus.className = "progressBarStatus";
	progressStatus.innerHTML = "&nbsp;";

	this.fileProgressElement.appendChild(progressCancel);
	this.fileProgressElement.appendChild(progressText);
	this.fileProgressElement.appendChild(progressStatus);
	this.fileProgressElement.appendChild(progressBar);

	this.fileProgressWrapper.appendChild(this.fileProgressElement);

	document.getElementById(targetID).appendChild(this.fileProgressWrapper);
} else {
	this.fileProgressElement = this.fileProgressWrapper.firstChild;
	this.reset();
}

this.height = this.fileProgressWrapper.offsetHeight;
this.setTimer(null);


}

FileProgress.prototype.setTimer = function (timer) {
this.fileProgressElement["FP_TIMER"] = timer;
};
FileProgress.prototype.getTimer = function (timer) {
return this.fileProgressElement["FP_TIMER"] || null;
};

FileProgress.prototype.reset = function () {
this.fileProgressElement.className = "progressContainer";

this.fileProgressElement.childNodes[2].innerHTML = "&nbsp;";
this.fileProgressElement.childNodes[2].className = "progressBarStatus";

this.fileProgressElement.childNodes[3].className = "progressBarInProgress";
this.fileProgressElement.childNodes[3].style.width = "0%";

this.appear();	
};

FileProgress.prototype.setProgress = function (percentage) {
this.fileProgressElement.className = "progressContainer green";
this.fileProgressElement.childNodes[3].className = "progressBarInProgress";
this.fileProgressElement.childNodes[3].style.width = percentage + "%";

this.appear();	
};
FileProgress.prototype.setComplete = function () {
this.fileProgressElement.className = "progressContainer blue";
this.fileProgressElement.childNodes[3].className = "progressBarComplete";
this.fileProgressElement.childNodes[3].style.width = "";

var oSelf = this;
//自动隐藏成功
this.setTimer(setTimeout(function () {
	oSelf.disappear();
}, 3000));
};
FileProgress.prototype.setError = function () {
this.fileProgressElement.className = "progressContainer red";
this.fileProgressElement.childNodes[3].className = "progressBarError";
this.fileProgressElement.childNodes[3].style.width = "";

var oSelf = this;
this.setTimer(setTimeout(function () {
	oSelf.disappear();
}, 3000));
};
FileProgress.prototype.setCancelled = function () {
this.fileProgressElement.className = "progressContainer";
this.fileProgressElement.childNodes[3].className = "progressBarError";
this.fileProgressElement.childNodes[3].style.width = "";

var oSelf = this;
this.setTimer(setTimeout(function () {
	oSelf.disappear();
}, 2000));
};
FileProgress.prototype.setStatus = function (status) {
this.fileProgressElement.childNodes[2].innerHTML = status;
};
FileProgress.prototype.setFileName = function (name) {
	this.fileProgressElement.childNodes[1].innerHTML = name;
};

//Show/Hide the cancel button
FileProgress.prototype.toggleCancel = function (show, swfUploadInstance) {
this.fileProgressElement.childNodes[0].style.visibility = show ? "visible" : "hidden";
if (swfUploadInstance) {
	var fileID = this.fileProgressID;
	this.fileProgressElement.childNodes[0].onclick = function () {
		swfUploadInstance.cancelUpload(fileID);
		return false;
	};
}
};

FileProgress.prototype.appear = function () {
if (this.getTimer() !== null) {
	clearTimeout(this.getTimer());
	this.setTimer(null);
}

if (this.fileProgressWrapper.filters) {
	try {
		this.fileProgressWrapper.filters.item("DXImageTransform.Microsoft.Alpha").opacity = 100;
	} catch (e) {
		// If it is not set initially, the browser will throw an error.  This will set it if it is not set yet.
		this.fileProgressWrapper.style.filter = "progid:DXImageTransform.Microsoft.Alpha(opacity=100)";
	}
} else {
	this.fileProgressWrapper.style.opacity = 1;
}
	
this.fileProgressWrapper.style.height = "";

this.height = this.fileProgressWrapper.offsetHeight;
this.opacity = 100;
this.fileProgressWrapper.style.display = "";

};

//Fades out and clips away the FileProgress box.
FileProgress.prototype.disappear = function () {

var reduceOpacityBy = 15;
var reduceHeightBy = 4;
var rate = 30;	// 15 fps

if (this.opacity > 0) {
	this.opacity -= reduceOpacityBy;
	if (this.opacity < 0) {
		this.opacity = 0;
	}

	if (this.fileProgressWrapper.filters) {
		try {
			this.fileProgressWrapper.filters.item("DXImageTransform.Microsoft.Alpha").opacity = this.opacity;
		} catch (e) {
			// If it is not set initially, the browser will throw an error.  This will set it if it is not set yet.
			this.fileProgressWrapper.style.filter = "progid:DXImageTransform.Microsoft.Alpha(opacity=" + this.opacity + ")";
		}
	} else {
		this.fileProgressWrapper.style.opacity = this.opacity / 100;
	}
}

if (this.height > 0) {
	this.height -= reduceHeightBy;
	if (this.height < 0) {
		this.height = 0;
	}

	this.fileProgressWrapper.style.height = this.height + "px";
}

if (this.height > 0 || this.opacity > 0) {
	var oSelf = this;
	this.setTimer(setTimeout(function () {
		oSelf.disappear();
	}, rate));
} else {
	this.fileProgressWrapper.style.display = "none";
	this.setTimer(null);
}
};