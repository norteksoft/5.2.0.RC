function doQuery(obj){
		if($(obj).html() == "查询"){
			$(obj).html('取消查询');

			$('#queryDiv').css('display', 'block');
		}else{
			$(obj).html('查询');
			$("#hiddenDiv").html("");
			$('#queryDiv').css('display', 'none');
		}
	}
	function fieldCng(obj){
		var typeValue = $(obj).attr('value').split('-');
		if(typeValue == ''){
			$('#cdsOpt').html('');
			$('#cdtSpan').html('');
			return;
		}
		if(typeValue[0]=='TEXT'){
			$('#cdsOpt').html('<option value="=">等于</option><option value="<>">不等于</option>');
			$('#cdtSpan').html('<input id="condition" type="text"/>');
		}else if(typeValue[0]=='DATE'){
			$('#cdsOpt').html('<option value=">">大于</option><option value="<">小于</option><option value="=">等于</option>');
			$('#cdtSpan').html('<input id="condition" type="text" readonly="readonly"  onfocus="WdatePicker({dateFmt:\'yyyy-MM-dd\',el:\'condition\'})"/>');
		}else if(typeValue[0]=='TIME'){
			$('#cdsOpt').html('<option value=">">大于</option><option value="<">小于</option><option value="=">等于</option>');
			$('#cdtSpan').html('<input id="condition" type="text" readonly="readonly" onfocus="WdatePicker({dateFmt:\'yyyy-MM-dd HH:mm\',el:\'condition\'})" />');
		}else {
			$('#cdsOpt').html('<option value="=">等于</option><option value=">">大于</option><option value="<">小于</option>');
			$('#cdtSpan').html('<input id="condition" type="text"  onkeyup="value=value.replace(/[^0-9\.]/g,\'\');"/>');
		}
		
	}
	function addCds(){
		if(typeof($('#condition').attr('value'))=='undefined' || $('#condition').attr('value') == ''){
			$("#message").html("<font class=\"onError\"><nobr>请输入条件</nobr></font>");
			showMsg("message");
			return;
		}
		var addCdn = $('#fieldCng').attr('value').split('-')[1] + '  ' +
					$('#cdsOpt').attr('value') +  '  ' +
					$('#condition').attr('value');
		var tr = '<tr class="str">' 
				+'<td style="border: 0px;">'+getHiddenInput('fieldIds',$('#fieldCng').attr('value').split('-')[1]) + $('#fieldCng option:selected').text() +'</td>'
				+'<td style="border: 0px;">'+getHiddenInput('operates',$('#cdsOpt').attr('value')) + $('#cdsOpt option:selected').text() +'</td>'
				+'<td style="border: 0px;">'+getHiddenInput('searchValues',$('#condition').attr('value')) + getEsc($('#condition').attr('value')) +'</td>'
				+'<TD style="border: 0px;"><a class="delete" href="#" >删除</a></TD>';
		
		$(tr).appendTo("#conditiontable").find(".delete").click(function(){
		             $(this).parent().parent().remove(); });
	}
	function getHiddenInput(name,value){
		return '<input type="hidden" name="'+name+'" value="'+value+'"/>';
	}

	function searchForm(){
		ajaxSubmit('search_form',webRoot+'/engine/workflow-definition!search.htm','monitorList,totalMessage');
	}

	//更改办理人
	function changeTransactor(btnId){
		var ids = jQuery("#main_table").getGridParam('selarrrow');
		if(ids==''){
			$("#message").html("<font class=\"onError\"><nobr>请选择一条记录</nobr></font>");
			showMsg("message");
		}else if(ids.length > 1){
			$("#message").html("<font class=\"onError\"><nobr>只能选择一条记录</nobr></font>");
			showMsg("message");
		}else{
			var workflowId = ids[0];
			var rowData=jQuery("#main_table").jqGrid('getRowData',workflowId );
			var state=rowData['processState'];
			if(state=="SUBMIT"){
				$("#"+btnId).colorbox({href:webRoot+'/engine/task!changeTransactor.htm?instanceId='+workflowId,iframe:true, width:300, height:500,overlayClose:false,title:"选择",onClosed:function(){$("#"+btnId).removeClass("cboxElement");}});
			}else{
				alert("流程已结束或已取消,无法再更改办理人");
			}
		}
	}
	//环节跳转/弹窗“选择环节”
	function backView(btnId,pos){
		var ids = jQuery("#main_table").getGridParam('selarrrow');
		if(ids==''){
			$("#message").html("<font class=\"onError\"><nobr>请选择一条记录</nobr></font>");
			showMsg("message");
		}else if(ids.length > 1){
			$("#message").html("<font class=\"onError\"><nobr>只能选择一条记录</nobr></font>");
			showMsg("message");
		}else{
			var workflowId = ids[0];//$(workflowIds[0]).attr("value");
			if(pos=="monitorManager"){
				$("#"+btnId).colorbox({href:webRoot+'/engine/task!backView.htm?instanceId='+workflowId+'&type='+$("#wf_type1").attr("value")+'&definitionCode='+$("#wf_name1").attr("value")+'&position='+pos,iframe:true, width:300, height:400,overlayClose:false,title:"选择",onClosed:function(){$("#"+btnId).removeClass("cboxElement");}});
			}else{
				$("#"+btnId).colorbox({href:webRoot+'/engine/task!backView.htm?instanceId='+workflowId+'&position='+pos,iframe:true, width:300, height:400,overlayClose:false,title:"选择",onClosed:function(){$("#"+btnId).removeClass("cboxElement");}});
			}
		}
	}
	
	//删除监控实例
	function delete_monitor_workflow(position){
		var ids = jQuery("#main_table").getGridParam('selarrrow');
		if(ids==''){
			$("#message").html("<font class=\"onError\"><nobr>请选择记录</nobr></font>");
			showMsg("message");
		}else{
			if(confirm("确认删除这些实例吗？")){
				var prmt = '';
                for(var i=0;i<ids.length;i++){
                    if(prmt != '') prmt += '&';
                    prmt+=('workflowIds='+ids[i]);
                }
    			$("#position").attr("value",position);
                $.post(webRoot+"/engine/workflow-definition!deleteWorkflow.htm?"+prmt, "", function(data) {
                	$("#message").html("<font class=\"onError\"><nobr>"+data+"</nobr></font>");
    				showMsg("message");
        			jQuery("#main_table").jqGrid().trigger("reloadGrid");
        		});
			}
		}
	}
	
	function end_workflow(){
		var ids = jQuery("#main_table").getGridParam('selarrrow');
		if(ids==''){
			$("#message").html("<font class=\"onError\"><nobr>请选择记录</nobr></font>");
			showMsg("message");
		}else{
			if(confirm("确认结束这些实例吗？")){
				 var prmt = '';
                 for(var i=0;i<ids.length;i++){
                     if(prmt != '') prmt += '&';
                     prmt+=('workflowIds='+ids[i]);
                 }
                 $.post(webRoot+"/engine/workflow-definition!endWorkflow.htm?"+prmt, "", function(data) {
                 	$("#message").html("<font class=\"onError\"><nobr>"+data+"</nobr></font>");
     				showMsg("message");
         			jQuery("#main_table").jqGrid().trigger("reloadGrid");
         		});
			}
		}
	}
	//环节跳转/弹窗的关闭时的回调方法
	function backViewClose(wfdId,pos,type,defCode){
		if(pos=="monitor"){
			ajaxSubmit('wf_form', webRoot+'/engine/workflow-definition!monitor.htm?wfdId='+wfdId, 'wf_definition'); 
		}else if(pos=="monitorManager"){
			$("#wf_type1").attr('value', type);
			$("#wf_name1").attr('value', defCode);
			ajaxSubmit('wf_form', webRoot+'/engine/workflow-definition!monitorDefintion.htm', 'wf_definition'); 
		}
	}
	
	function end_workflow_def(){
		var ids = jQuery("#main_table").getGridParam('selarrrow');
		if(ids==''){
			$("#message").html("<font class=\"onError\"><nobr>请选择记录</nobr></font>");
			showMsg("message");
			return;
		}else{
			if(confirm("确认结束这些实例吗？")){
				 var prmt = '';
                for(var i=0;i<ids.length;i++){
                    if(prmt != '') prmt += '&';
                    prmt+=('workflowIds='+ids[i]);
                }
                $.post(webRoot+"/engine/workflow-definition!endWorkflow.htm?"+prmt, "", function(data) {
                	$("#message").html("<font class=\"onError\"><nobr>"+data+"</nobr></font>");
    				showMsg("message");
        			jQuery("#main_table").jqGrid().trigger("reloadGrid");
        		});
			}
		}
	}
	/**
	 * 流程监控/查看表单
	 * @param url
	 * @param zone
	 * @param formName
	 * @param obj
	 * @return
	 */
	function selectList(url,zone,formName,obj){
		selete(obj);
		ajaxSubmit(formName,url,zone,automaticHeight);
	}
	
	function view_standard_form(url,id){
		if(url==""||typeof (url)=="undefined"){//查看自定义表单
			url=webRoot+'/engine/workflow!monitorView.htm?workflowId='+workflowId;
			var childWin = window.open(url,'',"top=0,left=0,toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes,width="+screen.availWidth+",height="+screen.availHeight);
		}else{
			if(url.indexOf("?")!=-1){
				url = url+id;
			}else{
				url = url + '?id='+id;
			}
			url = processUrl(url);
		}
	}
	
	//根据系统code获取url
	function processUrl(url){
		if(url.indexOf("http://")!=-1){
			var childWin = window.open(url,'',"top=0,left=0,toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes,width="+screen.availWidth+",height="+screen.availHeight);
		}else{
			var index = url.indexOf("/");
			var code = url.substring(0, index);
			$.ajax({
				   type: "POST",
				   url: "obtain-system-url.htm",
				   data: "systemCode="+code,
				   success: function(systemUrl){
					if(systemUrl==""||typeof(systemUrl)=='undefined'){
						alert("路径不正确");
					}else{
					    url = systemUrl + url.substring(index, url.length);
						var childWin = window.open(url,'',"top=0,left=0,toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes,width="+screen.availWidth+",height="+screen.availHeight);
					}
					
				   }
				});
		}
	}
	
	function fieldManagerCng(obj){
		var typeValue = $(obj).attr('value').split('-');
		if(typeValue == ''){
			$('#cdsOpt').html('');
			$('#cdtSpan').html('');
			return;
		}
		if(typeValue[0]=='TEXT'){
			if(typeValue[1]=='processState'||typeValue[1]=='process_state'){
				$('#cdsOpt').html('<option value="=">等于</option><option value="<>">不等于</option>');
				$('#cdtSpan').html('<select id="condition"><option value="1">已提交</option><option value="2">已结束</option><option value="3">已取消</option></select>');
			}else{
				$('#cdsOpt').html('<option value="=">等于</option><option value="<>">不等于</option><option value="like">包含</option><option value="not like">不包含</option>');
				$('#cdtSpan').html('<input id="condition" type="text"/>');
			}
		}else if(typeValue[0]=='DATE'){
			$('#cdsOpt').html('<option value=">">大于</option><option value="<">小于</option><option value="=">等于</option>');
			$('#cdtSpan').html('<input id="condition" type="text" readonly="readonly"  onfocus="WdatePicker({dateFmt:\'yyyy-MM-dd\',el:\'condition\'})"/>');
		}else if(typeValue[0]=='TIME'){
			$('#cdsOpt').html('<option value=">">大于</option><option value="<">小于</option><option value="=">等于</option>');
			$('#cdtSpan').html('<input id="condition" type="text" readonly="readonly" onfocus="WdatePicker({dateFmt:\'yyyy-MM-dd HH:mm\',el:\'condition\'})" />');
		}else {
				$('#cdsOpt').html('<option value="=">等于</option><option value=">">大于</option><option value="<">小于</option>');
				$('#cdtSpan').html('<input id="condition" type="text"  onkeyup="value=value.replace(/[^0-9]/g,\'\');"/>');
			
		}
		
	}
	
	function addManagerCds(){
		if(typeof($('#condition').attr('value'))=='undefined' || $('#condition').attr('value') == ''){
			$("#message").html("<font class=\"onError\"><nobr>请输入条件</nobr></font>");
			showMsg("message");
			return;
		}
		var typeValue = $('#fieldCng').attr('value').split('-');
		var searchVal="";
		if(typeValue[1]=='processState'||typeValue[1]=='process_state'){
			searchVal='<td style="border: 0px;">'+getHiddenInput('searchValues',$('#condition').val()) + getEsc($('#condition option:selected').text()) +'</td>';
		}else{
			searchVal='<td style="border: 0px;">'+getHiddenInput('searchValues',$('#condition').attr('value')) + getEsc($('#condition').attr('value')) +'</td>';
		}
			
		var tr = '<tr class="str">' 
			+'<td style="border: 0px;">'+getHiddenInput('enNames',$('#fieldCng').attr('value').split('-')[1])
			+getHiddenInput('chNames',$('#fieldCng option:selected').text())
			+getHiddenInput('dataTypes',$('#fieldCng').attr('value').split('-')[0])
			+ $('#fieldCng option:selected').text() +'</td>'
				+'<td style="border: 0px;">'+getHiddenInput('operates',$('#cdsOpt').attr('value')) + $('#cdsOpt option:selected').text() +'</td>'
				+searchVal
				+'<TD style="border: 0px;"><a class="delete" href="#" >删除</a></TD>';
		
		$(tr).appendTo("#conditiontable").find(".delete").click(function(){
		             $(this).parent().parent().remove();
		             setHiddenDiv();
		});
		setHiddenDiv();
	}
	
	function searchManagerForm(){
		$("#def_type").attr('value', $("#wf_type").attr('value'));
		$("#def_name").attr('value', $("#wf_name").attr('value'));
		ajaxSubmit('search_form',webRoot+'/engine/workflow-definition!searchManager.htm','monitorList',searchCallBack);
	}
	
	function setHiddenDiv(){
		if($("#searchbutton").html() == "取消查询"){
			$("#hiddenDiv").html($("#searchDiv").html());
			$("#hiddenDiv").find("table[id='conditiontable']").removeAttr("id");
		}
	}
	
	function searchCallBack(){
		setHiddenDiv();
	}
	
	function setValue(){
		$("#wf_type1").attr('value', $("#wf_type").attr('value'));
		$("#wf_name1").attr('value', $("#wf_name").attr('value'));
	}
	
	//应急处理按钮
	function urgen_done(){
		
		var ids = jQuery("#main_table").getGridParam('selarrrow');
		if(ids==''){
			$("#message").html("<font class=\"onError\"><nobr>请选择一条记录</nobr></font>");
			showMsg("message");
		}else if(ids.length > 1){
			$("#message").html("<font class=\"onError\"><nobr>只能选择一条记录</nobr></font>");
			showMsg("message");
		}else{
			var rowData=jQuery("#main_table").jqGrid('getRowData',ids[0] );
			var url = rowData['formUrgenUrl'];
			var id= rowData['dataId'];
			if(url!=""&& typeof (url)!="undefined"){
				if(url.indexOf("?")!=-1){
					url = url+id;
				}else{
					url = url + '?id='+id;
				}
				url = processUrl(url);
				var childWin = window.open(url,'',"top=0,left=0,toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes,width="+screen.availWidth+",height="+screen.availHeight);
			}else{
				alert("此流程没有应急方案");
			}
		}
	}
	
	function taskJumpAssignTransactor(workflowId,taskName,wfdId,pos,type,defCode){
		if(pos=="monitorManager"){
			$.colorbox({href:webRoot+'/engine/task!taskJumpAssignTransactor.htm?workflowId='+workflowId+"&backto="+taskName+"&type="+type+"&definitionCode="+defCode+"&position="+pos,iframe:true, width:300, height:500,overlayClose:false,title:"选择"});
		}else{
			$.colorbox({href:webRoot+'/engine/task!taskJumpAssignTransactor.htm?workflowId='+workflowId+"&backto="+taskName+"&wfdId="+wfdId+"&position="+pos,iframe:true, width:300, height:500,overlayClose:false,title:"选择"});
		}
	}
	
	function taskJumpChoiceTransactor(canChoiceTransacators,workflowId,taskName,wfdId,pos,type,defCode){
		if(pos=="monitorManager"){
		$.colorbox({href:webRoot+'/engine/task!taskJumpChoiceTransactor.htm?canChoiceTransacators='+canChoiceTransacators+'&workflowId='+workflowId+'&backto='+taskName+"&type="+type+"&definitionCode="+defCode+"&position="+pos,iframe:true, width:300, height:400,overlayClose:false,title:"选择"});
		}else{
			$.colorbox({href:webRoot+'/engine/task!taskJumpChoiceTransactor.htm?canChoiceTransacators='+canChoiceTransacators+'&workflowId='+workflowId+'&backto='+taskName+"&wfdId="+wfdId+"&position="+pos,iframe:true, width:300, height:400,overlayClose:false,title:"选择"});
		}
	}
	
	//增加办理人
	function addTransactor(){
		var ids = jQuery("#main_table").getGridParam('selarrrow');
		if(ids==''){
			$("#message").html("<font class=\"onError\"><nobr>请选择一条记录</nobr></font>");
			showMsg("message");
		}else if(ids.length > 1){
			$("#message").html("<font class=\"onError\"><nobr>只能选择一条记录</nobr></font>");
			showMsg("message");
		}else{
			var workflowId = ids[0];
			var rowData=jQuery("#main_table").jqGrid('getRowData',workflowId );
			var state=rowData['processState'];
			if(state=="SUBMIT"){
				$.colorbox({href:webRoot+'/engine/task!addTransactor.htm?instanceId='+workflowId,iframe:true, width:300, height:500,overlayClose:false,title:"选择"});
			}else{
				alert("流程已结束或已取消,无法再增加办理人");
			}
		}
	}
	
	//减少办理人
	function delTransactor(btnId){
		var ids = jQuery("#main_table").getGridParam('selarrrow');
		if(ids==''){
			$("#message").html("<font class=\"onError\"><nobr>请选择一条记录</nobr></font>");
			showMsg("message");
		}else if(ids.length > 1){
			$("#message").html("<font class=\"onError\"><nobr>只能选择一条记录</nobr></font>");
			showMsg("message");
		}else{
			var workflowId = ids[0];
			var rowData=jQuery("#main_table").jqGrid('getRowData',workflowId );
			var state=rowData['processState'];
			if(state=="SUBMIT"){
				$.colorbox({href:webRoot+'/engine/task!delTransactor.htm?instanceId='+workflowId,iframe:true, width:300, height:500,overlayClose:false,title:"选择"});
			}else{
				alert("流程已结束或已取消,无法再减少办理人");
			}
		}
	}
	
	function viewWFDM(ts1,cellval,opts,rwdat,_act){
			// <input name="position" id="position" type="hidden" value=""/>
			var v="<a  href=\"#\" hidefocus=\"true\" onclick=\"view_standard_form('"+opts.formUrl+"',"+opts.dataId+");\">" + ts1 + "</a>";
			return v;
	}
