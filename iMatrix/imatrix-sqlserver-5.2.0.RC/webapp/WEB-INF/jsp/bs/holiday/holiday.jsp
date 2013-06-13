<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/setting-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/setting-iframe-meta.jsp"%>
	<title>节假日设置</title>
	
	<script language="javascript" type="text/javascript" src="${imatrixCtx}/widgets/calendar/WdatePicker.js"></script>
	
	<script type="text/javascript">
		
		function createHoliday(){
			ajaxSubmit('holidayForm', '${settingCtx}/holiday/holiday-input.htm', 'holiday_zone');
		}
		function saveHoliday(){
			$('#holidayInputForm').submit();
		}
		function returnHoliday(){
			ajaxSubmit('holidayForm', '${settingCtx}/holiday/holiday.htm', 'holiday_zone');
		}
		function ajaxSubmit(form, url, zoons, ajaxCallback){
			var formId = "#"+form;
			if(url != ""){ $(formId).attr("action", url); }
			ajaxAnywhere.formName = form;
			ajaxAnywhere.getZonesToReload = function() { return zoons; };
			ajaxAnywhere.onAfterResponseProcessing = function () {
				if(typeof(ajaxCallback) == "function"){ ajaxCallback(); }
			};
			ajaxAnywhere.submitAJAX();
		}
		function calChange(t, adt){
			var y = Number($('#_year').attr('value'));
			var m = Number($('#_month').attr('value'));
			if(t=='M'){ m+=Number(adt); }else if(t=='Y'){ y+=Number(adt); }
			if(m<1){ y-=1; m=12; }else if(m>12){ y+=1; m=1; }
			if(m<10) m='0'+m;
			$('#targetDate').attr('value', y+'-'+m+'-01');
			ajaxSubmit('calendarForm', '${settingCtx}/holiday/holiday.htm', 'holiday_zone');
		}
		function initCalendar(){
			var cal = eval($('#calendarDays').val())[0];
			$('#_year').attr('value', cal.year);
			$('#_month').attr('value', cal.month+1);
			var calStr = "<tr>"; var i=1;
			for(;i<cal.firstWeekday;i++){ calStr+="<td></td>"; }
			i=8-i; var j = 0;
			for(;j<i;j++){
				if(cal.days[j].isHoliday){ calStr+="<td style='color:#FF2F2F;'>"+cal.days[j].day+"</td>";
				}else{ calStr+="<td>"+cal.days[j].day+"</td>"; }
			}
			var x = 0;
			for(;j<cal.days.length;j++){
				if(x%7==0){ calStr+="</tr><tr>"; } x++;
				if(cal.days[j].isHoliday){ calStr+="<td style='color:#FF2F2F;'>"+cal.days[j].day+"</td>";
				}else{ calStr+="<td>"+cal.days[j].day+"</td>"; }
			}
			calStr+="</tr>";
			$('#calendarBody').html(calStr);
		}
		function showMessage(id, msg){
			if(msg != ""){
				$("#"+id).html(msg);
			}
			$("#"+id).show("show");
			setTimeout('$("#'+id+'").hide("show");',3000);
		}
	</script>
	<style type="text/css">
		.calendardate{border:1px solid #C5D9E8; }
		table.calendar { clear: both; border-collapse: collapse; color: #000;font: normal sans-serif 12px;font-weight: 400;}
		table.calendar caption{ line-height:24px; padding: 2px 4px; border:1px solid #C5D9E8; }
		table.calendar caption input{ text-align: center; border: 0px #fff; background: #fff; width: 30px;}
		table.calendar thead tr, table.calendar tbody tr { height: 22px; }
		table.calendar thead tr th, table.calendar tbody tr td{text-align: center;width: 24px; line-height: 22px;}
		table.calendar thead tr th {background-color : #BDEBEE;}
		table.calendar caption a.navImg{ cursor:pointer;display:block;height:16px;width:16px; margin-top: 4px;}
		table.calendar caption a.navImgl,table.calendar caption a.navImgll {float:left;}
		table.calendar caption a.navImgr,table.calendar caption a.navImgrr {float:right;}
		table.calendar caption a.navImgl{background:url("${imatrixCtx}/widgets/calendar/skin/default/img.gif") no-repeat scroll -16px 0 transparent;}
		table.calendar caption a.navImgll{background:url("${imatrixCtx}/widgets/calendar/skin/default/img.gif") no-repeat scroll 0px 0 transparent;}
		table.calendar caption a.navImgr{background:url("${imatrixCtx}/widgets/calendar/skin/default/img.gif") no-repeat scroll -48px 0 transparent;}
		table.calendar caption a.navImgrr{background:url("${imatrixCtx}/widgets/calendar/skin/default/img.gif") no-repeat scroll -32px 0 transparent;}
	</style>
</head>
	
<body>
<div class="ui-layout-center">
	<div class="opt-body">
		<aa:zone name="holiday_zone">
			<div class="opt-btn">
				<button class="btn" onclick="saveHoliday();"><span><span>保存</span></span></button>
			</div>
			<div id="msg" style="padding-left: 6px;color: red;"></div>
			<form name="calendarForm" id="calendarForm"><input type="hidden" id="targetDate" name="targetDate" value=""> </form>
			<form name="holidayInputForm" id="holidayInputForm" method="post">
			<input type="hidden" name="id" value="${id}">
			<p style="padding: 6px 10px;font-size: 15px;color: red;">提示：日历中红色显示即为节假日</p>
			<table class="input" style="margin: 5px;">
				<tr>
					<td rowspan="3" style="padding-right:6px;">
						<aa:zone name="holiday_calendar">
						<input value="${specialDates}" name="calendarDays" id="calendarDays" type="hidden"/>
						<div class="calendardate">
						<table class="calendar" >
							<caption> 
								<a class="navImg navImgll" onclick="calChange('Y', -1);"></a><a class="navImg navImgl" onclick="calChange('M', -1);"></a>
								<a class="navImg navImgr" onclick="calChange('Y', 1);"></a><a class="navImg navImgrr" onclick="calChange('M', 1);"></a>
								<input id="_year" readonly="readonly">&nbsp;年<input id="_month" readonly="readonly">月 
							</caption>
							<thead>
							<tr><th>日</th><th>一</th><th>二	</th><th>三</th><th>四</th><th>五</th><th>六</th></tr>
							</thead>
							<tbody id="calendarBody">
							</tbody>
						</table>
						</div>
						<script type="text/javascript">
							$().ready(function(){
								initCalendar();
							});
						</script>
						</aa:zone>
					</td>
					<td>开始日期：</td>
					<td><input readonly="readonly" name="startDate" id="_startDate" value=""
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',readOnly:true})"> <span style="color: red;padding: 4px;">*</span> </td>
				</tr>
				<tr>
					<td>结束日期：</td><td><input readonly="readonly" name="endDate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',readOnly:true})"> </td>
				</tr>
				<tr>
					<td>设置为：</td>
					<td><input type="radio" name="dateType" checked="checked" value="HOLIDAY">节假日
						<input type="radio" name="dateType" value="WORKING_DAY">工作日
					</td>
				</tr>
			</table>
			</form>
			<script type="text/javascript">
			 $(function(){
				 $("#holidayInputForm").validate({
					submitHandler: function() {
						ajaxSubmit('holidayInputForm', '${settingCtx}/holiday/holiday-save.htm', 'holiday_zone');
					},
					rules: {
						startDate: "required"
					},
					messages: {
						startDate: "必填"
					}
				 });
			});
			</script>
		</aa:zone>
	</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>