<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<html>
<head>
	<title>数据字典</title>
	
	<%@ include file="/common/setting-colorbox-meta.jsp"%>
	<script type="text/javascript" src="${ctx}/widgets/tree/_lib/css.js"></script>
	<link rel="stylesheet" type="text/css" href="${ctx}/widgets/tree/tree_component.css" />
	<script type="text/javascript" src="${ctx}/widgets/tree/tree_component.js"></script>
	<style type="text/css">
		a{text-decoration:none;}
	</style>
</head>
<body style="padding: 5px;">
	<aa:zone name="wf_task">
		<div style="width:auto; padding: 0; margin: 0;text-align: left;">
			<aa:zone name="wf_rank_tree">
				<acsTags:tree defaultable="true" treeId="userTree" treeType="DEPARTMENT_TREE" multiple="true"></acsTags:tree>
			</aa:zone>
		</div>
	</aa:zone>
</body>
</html>