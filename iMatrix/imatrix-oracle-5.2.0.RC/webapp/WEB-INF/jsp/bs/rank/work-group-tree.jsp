<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<html>
<head>
	<title>数据字典</title>
	
	<style type="text/css">
		a{text-decoration:none;}
	</style>
</head>
<body style="padding: 5px;">
	<aa:zone name="wf_task">
		<div style="width:auto; padding: 0; margin: 0;text-align: left;">
			<aa:zone name="wf_rank_tree">
				<acsTags:tree defaultable="true" treeId="groupTree" treeType="GROUP_TREE" multiple="true"></acsTags:tree>
			</aa:zone>
		</div>
	</aa:zone>
</body>
</html>