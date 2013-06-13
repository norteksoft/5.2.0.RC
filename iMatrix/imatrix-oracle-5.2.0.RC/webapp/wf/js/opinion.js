	function addOpinion(){
		var editOpinion = $("#editOpinion");
		editOpinion.show();
	}

	function removeOpinion(){
		$("#opinion").attr("value","");
		var editOpinion = $("#editOpinion");
		editOpinion.hide();
	}
	
	function saveOpinion(){
		if($("#opinion").attr("value")==""){
			alert("请填写意见");
		}else{
			ajaxAnywhere.formName = "approvalViews";
			ajaxAnywhere.getZonesToReload = function(){
				return "opinionList";
			};
			ajaxAnywhere.onAfterResponseProcessing = function () {
				removeOpinion();
				$("#editedoption").attr("value",'true');
				$("#mustoption").attr("value",$("#mustFill").attr("value"));
			};
			ajaxAnywhere.submitAJAX();
		}
	}
	
	function validateOpinion(pleaseinput,successMsg,errorMsg) {
		$.formValidator.initConfig({formid:"approvalViews",onsuccess: function() {opinion_ajax_form();return false;},onerror:function(msg){}});
		$("#opinion").formValidator({onshow:pleaseinput,onfocus:pleaseinput,oncorrect:successMsg}).inputValidator({min:1,empty:{leftempty:false,emptyerror:errorMsg},onerror:errorMsg});
	}