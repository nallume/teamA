<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="ko">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%
String str = (String)request.getAttribute("resultStr");
%>
<title>Hello</title>
</head>
<body>
HELLO~ <br>
result : " <%=str %> " 
</body>
</html>
