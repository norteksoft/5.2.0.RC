<html>
<head>
<TITLE>403 - 缺少权限</TITLE>
		<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
        <meta http-equiv="Cache-Control" content="no-store"/>
        <meta http-equiv="Pragma" content="no-cache"/>
        <meta http-equiv="Expires" content="0"/>
        
        <script type="text/javascript" src="${resourceCtx}/js/jquery-all-1.0.js"></script>
<STYLE type=text/css>
A:link {
COLOR: green; TEXT-DECORATION: none;
}
A:visited {
	COLOR: green; TEXT-DECORATION: none
}
A:active {
	COLOR: green; TEXT-DECORATION: none
}
A:hover {
	COLOR: #6f9822; TEXT-DECORATION: none
}
.text {
	FONT-FAMILY: ""; COLOR: #555555; FONT-SIZE: 14px; TEXT-DECORATION: none
}
.STYLE1 {
	FONT-SIZE: 13px
}
.STYLE2 {
	FONT-SIZE: 12px
}
.STYLE3 {
	FONT-SIZE: 11px
}
</STYLE>
<script type="text/javascript">
	$().ready(function(){
		var iframes=window.parent.$('iframe');
		if(iframes.length>0){
			var isIE = $.browser.msie && !$.support.opacity;
			if (isIE) {
				var loc=window.parent.location.href;
				if(loc.indexOf("exception-handle")<0){
					window.parent.location.reload();
				}
			}
		}
	});
</script>
</head>
<BODY>
<TABLE height="100%" cellSpacing=0 cellPadding=0 width="100%"
	align=center border=0>

	<TR>
		<TD vAlign="center" align="middle">
		<TABLE cellSpacing=0 cellPadding=0 width=500 align=center border=0>

			<TR>
				<TD width=17 height=17><IMG height=17
					src="${resourceCtx}/images/co_01.gif" width=17></TD>
				<TD width=466 background="${resourceCtx}/images/bg01.gif"></TD>
				<TD width=17 height=17><IMG height=17
					src="${resourceCtx}/images/co_02.gif" width=17></TD>
			</TR>
			<TR>
				<TD background=${resourceCtx}/images/bg02.gif></TD>
				<TD>
				<TABLE class=text cellSpacing=0 cellPadding=10 width="100%"
					align=center border=0>
					<TR>
						<TD>
						<TABLE cellSpacing=0 cellPadding=0 width="100%" border=0>
							<TR>
								<#if expired ? exists>
								<TD ><IMG src="${resourceCtx}/images/warn.jpg" width=128 height=128 style="float: right;"></TD>
								<TD >
									<P  style="FONT-SIZE: 24px;font-weight: bold;margin:8px 0;font-family:黑体;">
										该用户已经在别处登录<br>请重新登录。
									</P>
								</TD>
					     		<#elseif sessionFail ? exists>
					     		<TD ><IMG src="${resourceCtx}/images/expired.jpg" width=128 height=128 style="float: right;"></TD>
								<TD >
									<P  style="FONT-SIZE: 24px;font-weight: bold;margin:8px 0;font-family:黑体;">
										登录超时<br>请重新登录。
									</P>
								</TD>
								<#elseif exceed ? exists>
								<TD ><IMG src="${resourceCtx}/images/warn.jpg" width=128 height=128 style="float: right;"></TD>
								<TD >
									<P  style="FONT-SIZE: 24px;font-weight: bold;margin:8px 0;font-family:黑体;">
										产品已过期或超出同时在线人数限制<br>请重新登录。
									</P>
								</TD>
					     		<#elseif forbidden ? exists>
								<TD ><IMG src="${resourceCtx}/images/warn.jpg" width=128 height=128 style="float: right;"></TD>
								<TD >
									<P  style="FONT-SIZE: 24px;font-weight: bold;margin:8px 0;font-family:黑体;">
										您已经被禁用，<br>请联系管理员。
									</P>
								</TD>
					     		<#else>
					     		<TD ><IMG src="${resourceCtx}/images/403.jpg" width=128 height=128 style="float: right;"></TD>
								<TD >
									<P  style="FONT-SIZE: 24px;font-weight: bold;margin:8px 0;font-family:黑体;">
										对不起<br>
			                       		您没有查看权限。
									</P>
									<p style="FONT-SIZE: 12px;COLOR: #777;margin:8px 0;"> 权限 403 错误</p>
								</TD>
								</#if>
			                      </TD>
							</TR>
						</TABLE>
						</TD>
					</TR>
					<TR>
						<TD style="border-top: 1px solid #ddd;">
						<TABLE class=text cellSpacing=0 cellPadding=0 width="100%"
							border=0>
								<TR>
									<TD align="center">
										<BR><p style="margin:8px 0;">
										<a href="${ctx}/j_spring_security_logout"">重新登录</a>
										</p>
									</TD>
								</TR>
							</TABLE>
							</TD>
						</TR>
					</TABLE>
				</TD>
				<TD background="${resourceCtx}/images/bg03.gif"></TD>
			</TR>
			<TR>
				<TD width=17 height=17><IMG height=17
					src="${resourceCtx}/images/co_03.gif" width=17></TD>
				<TD background="${resourceCtx}/images/bg04.gif" height=17></TD>
				<TD width=17 height=17><IMG height=17
					src="${resourceCtx}/images/co_04.gif" width=17></TD>
			</TR>
		</TABLE>
		<TABLE class=text cellSpacing=0 cellPadding=0 width=500 align=center
			border=0>
			<TR>
				<TD></TD>
			</TR>
			<TR>
				<TD align="middle"></TD>
			</TR>
		</TABLE>
		</TD>
	</TR>
	</TBODY>
</TABLE>
</BODY>
</html>