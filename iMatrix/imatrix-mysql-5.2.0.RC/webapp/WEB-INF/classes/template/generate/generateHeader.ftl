<%@ page contentType="text/html;charset=UTF-8" import="com.norteksoft.product.util.ContextUtils,java.text.SimpleDateFormat,java.util.Calendar,com.norteksoft.product.util.SystemUrls"%>
<%@ include file="/common/taglibs.jsp"%>
<%  
	SimpleDateFormat fmt = new SimpleDateFormat("yyyy年MM月dd日");
	Calendar cal = Calendar.getInstance();
	int day = cal.get(Calendar.DAY_OF_WEEK);
	String weekDay = null;
	switch(day){
	case Calendar.MONDAY: weekDay="星期一";break;
	case Calendar.TUESDAY: weekDay="星期二";break;
	case Calendar.WEDNESDAY: weekDay="星期三";break;
	case Calendar.THURSDAY: weekDay="星期四";break;
	case Calendar.FRIDAY: weekDay="星期五";break;
	case Calendar.SATURDAY: weekDay="星期六";break;
	case Calendar.SUNDAY: weekDay="星期日";break;
	}
%>

<div id="header" class="ui-north">
	<menu:firstMenu showNum="3"></menu:firstMenu>
	<div id="header-logo">
	</div>
	<div id="honorific">
		<span><span class="man">&nbsp;</span><%=ContextUtils.getHonorificTitle()%>, 您好!</span>
		<span><span class="day">&nbsp;</span><%=fmt.format(cal.getTime())+"  "+weekDay%></span>
		<span><span class="pemessage"></span><a href="#"  onclick="openMessage();" >个人消息</a></span>
		<span onclick="updatePassword();"><a href="#"><span class="password">&nbsp;</span>修改密码</a></span>  
		<span onclick="changeStyle(event, this);"><a href="#"><span class="theme">&nbsp;</span>换肤</a></span> 
		<span ><a href="${ctx}/j_spring_security_logout"><span class="exit">&nbsp;</span>退出</a></span> 
	</div>
</div>
<script>
function updatePassword(){
	window.open("<%=SystemUrls.getBusinessPath("acs")%>/organization/user!updateUserPassword.action",'',"top=300,left=400,toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes,width=400,height=300");
}
</script>