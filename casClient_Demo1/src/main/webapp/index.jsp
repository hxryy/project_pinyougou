<%--
  Created by IntelliJ IDEA.
  User: Ran
  Date: 2018/11/17
  Time: 19:16
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>casClient_Demo1测试</title>
</head>
<body>
欢迎登陆casClient工程1，登录名:<%=request.getRemoteUser()%><br>
<a href="http://localhost:9100/cas/logout?service=https://www.baidu.com">单点退出</a>
</body>
</html>
