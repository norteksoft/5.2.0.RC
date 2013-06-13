function add()
	{
		var field = $('#dataField').attr("value");
		var fieldName=$('#dataField').find("option:selected").text();
		
	  var dataSrc = $('#dataSrc').attr("value");
		if(field=="")
		{
			alert("请选择数据库字段");
			return;
		}	
		var control=$('#itemTitle').attr("value");
		if(control=="")
		{
			alert("请填写映射控件名称");
			return;
		}
		var rows=$('#map_tbl').find("tr");
		for(var i=1;i<rows.length;i++)
	  	{
			  var tds=$(rows[i]).find("td");
			  var dataField = $(tds[0]).text();    
			  if(dataField==field)
			  {
				  alert("已经添加！");
				  return;
			  }
		}
		  var queryFlag = $("#isQuery").attr("checked")==true ? "1" : "0"; 
		  var queryFlagDesc = queryFlag=="1" ? "是" : "否";
		  
		 //alert($("#map_tbl>tbody>tr:last").attr("id"));//获得tr的id
		  var str = "";
		  if($("#isQuery").css("display")=="none"){
			  str="<tr><td >"+field+"</td><td >"+fieldName+"</td><td >"+control+"</td><td ><a href=\"#\" onclick=\"del(this)\">删除</a></td></tr>";
		  }else{
			  str="<tr><td >"+field+"</td><td >"+fieldName+"</td><td >"+control+"</td><td >"+queryFlagDesc+"</td><td ><a href=\"#\" onclick=\"del(this)\">删除</a></td></tr>";
		  }
		  $("#map_tbl").append(str);
		  $("#dataField ").val("");
		  $('#itemTitle').attr("value","");
	}
		
	function del(obj)
	{
		$(obj).parent().parent().remove();  
	}	


	 function getData(formPropertyPage){
		 $.ajax({
			   type: "POST",
			   url: "form-view!getTabelColumns.htm",
			   data:{tableName:$('#dataSrc').attr("value"),formControlType:formPropertyPage},
			   success: function(text, textStatus){
				   $("#dataField").html(text);
				   if($("#isQuery").css("display")=="none"){
					   $("#queryProperty").html(text); 
				   }
		      },
				error : function(XMLHttpRequest, textStatus) {
					alert(textStatus);
				}
		  }); 
		 $('#dataSrcName').attr("value",$('#dataSrc').find("option:selected").text());
		 if($("#isQuery").css("display")=="none"){
			   $("#queryProperty").attr("value",""); 
			   $("#referenceControl").attr("value",""); 
		   }
		 var rows=$('#map_tbl').find("tr");
			for(var i=rows.length-1;i>=1;i--)
		  	{
			  $(rows[i]).remove();
			}
	 }
	 
	 function getListControlData(){
		 $.ajax({
			   type: "POST",
			   url: "form-view!getTabelColumns.htm",
			   data:{dataTableId:$('#dataSrc').attr("value"),formControlType:"LIST_CONTROL"},
			   success: function(text, textStatus){
				   var len=$("#contentTable").find("tbody").find("tr").length;
				   for(var i=0;i<len;i++){
					   $("#item_"+(i+1)).attr("value","");
					   $("#size_"+(i+1)).attr("value","10");
					   $("#sum_"+(i+1)).attr("checked","");
					   $("#cal_"+(i+1)).attr("value","");
					   $("#dataField_"+(i+1)).html(text);
				   }
				   $("#dataField").html(text);
		      },
				error : function(XMLHttpRequest, textStatus) {
					alert(textStatus);
				}
		  }); 
	 }
	 
	 function listControlAddRow(){
		 $("#contentTable").find("tbody").append($("#mata").find("tbody").html());
		 var len=$("#contentTable").find("tbody").find("tr").length;
		 $($("#contentTable").find("tbody").find("td[id='serNum']")).html(len);
		 $($("#contentTable").find("tbody").find("td[id='serNum']")).attr("id","serNum_"+len);
		 $($("#contentTable").find("tbody").find("input[id='item']")).attr("id","item_"+len);
		 $($("#contentTable").find("tbody").find("input[id='size']")).attr("id","size_"+len);
		 $($("#contentTable").find("tbody").find("input[id='sum']")).attr("id","sum_"+len);
		 $($("#contentTable").find("tbody").find("input[id='cal']")).attr("id","cal_"+len);
		 $($("#contentTable").find("tbody").find("select[id='dataField']")).attr("id","dataField_"+len);
		 sortOrder();
	 }
	 
	 function listControlDeleteRow(obj){
		 var len=$("#contentTable").find("tbody").find("tr").length;
		 //删除
		 if(len>1){
			 $(obj).parent().parent().remove();
		 }
		 sortOrder();
	 }
	 
	//排序
	 function sortOrder(){
	 	$("#contentTable").find("td[id^='serNum_']").each(function(i){
	 		$(this).html(i+1);
	 		var serNum=$("#contentTable").find("td[id^='serNum_']")[i];
			var item= $("#contentTable").find("input[id^='item_']")[i];
			var size=$("#contentTable").find("input[id^='size_']")[i];
			var sum=$("#contentTable").find("input[id^='sum_']")[i];
			var cal= $("#contentTable").find("input[id^='cal_']")[i];
			var dataField=$("#contentTable").find("select[id^='dataField_']")[i];
			$(serNum).attr("id","serNum_"+(i+1));
			$(item).attr("id","item_"+(i+1));
			$(size).attr("id","size_"+(i+1));
			$(sum).attr("id","sum_"+(i+1));
			$(cal).attr("id","cal_"+(i+1));
			$(dataField).attr("id","dataField_"+(i+1));
	 	});
	 }