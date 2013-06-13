<html>
<head>
<TITLE>消息失效提醒</TITLE>
		<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
        <meta http-equiv="Cache-Control" content="no-store"/>
        <meta http-equiv="Pragma" content="no-cache"/>
        <meta http-equiv="Expires" content="0"/>
        
        <script type="text/javascript" src="${resourceCtx}/js/jquery-all-1.0.js"></script>
<STYLE type=text/css>
A:link {
	COLOR: #555555;
	TEXT-DECORATION: none
}

A:visited {
	COLOR: #555555;
	TEXT-DECORATION: none
}

A:active {
	COLOR: #555555;
	TEXT-DECORATION: none
}

A:hover {
	COLOR: #6f9822;
	TEXT-DECORATION: none
}

.text {
	FONT-SIZE: 12px;
	COLOR: #555555;
	FONT-FAMILY: "";
	TEXT-DECORATION: none
}

.STYLE1 {
	font-size: 13px
}

.STYLE2 {
	font-size: 12px
}

.STYLE3 {
	font-size: 11px
}
</STYLE>
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
				<TD width=316 background="${resourceCtx}/images/bg01.gif"></TD>
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
								<TD width=20></TD>
								<TD><IMG height=50 src="${resourceCtx}/images/message-error.jpg" width=400></TD>
							</TR>
						</TABLE>
						</TD>
					</TR>
					<TR>
						<TD>
						<TABLE cellSpacing=0 cellPadding=0 width="100%" border=0>
							<TR>
								<TD background="${resourceCtx}/images/bg03.gif" height=1></TD>
							</TR>
							</TBODY>
						</TABLE>
						<BR>
						<TABLE class=text cellSpacing=0 cellPadding=0 width="100%"
							border=0>
								<TR>
									<TD width=20></TD>
									<TD align="center">
					     			<span style="color:red">${errorInfo}</span>
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