
<%@page import="org.jasig.cas.util.PropUtils"%><%@ page session="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%
	String imatrix = PropUtils.getProp("host.imatrix");//?type=auto&name=pt&pwd=1
%>
正在登陆系统... ...
<% response.sendRedirect(imatrix); %>
