function _back(url){
	window.location=url;
}

function _change_div(obj){
	$('ul.tag li').attr('class', '');
	$(obj).parent('li').addClass('selected');
	if($(obj).html()=="表单信息"){
		$("#formDiv").css("display","block");
		$("#viewDiv").css("display","none");
		$("#unreadDiv").css("display","none");
	}else if($(obj).html()=="回执信息"){
		$("#formDiv").css("display","none");
		$("#viewDiv").css("display","block");
		$("#unreadDiv").css("display","none");
	}else if($(obj).html()=="未读人员"){
		$("#formDiv").css("display","none");
		$("#viewDiv").css("display","none");
		$("#unreadDiv").css("display","block");
	}
}