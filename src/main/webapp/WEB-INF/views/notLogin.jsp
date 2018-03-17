<%--
  Created by IntelliJ IDEA.
  User: 11098
  Date: 2017/12/02
  Time: 23:34
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <!-- jQuery, Bootstrap, jQuery plugins and Custom JS code -->
    <script src="./JS/jquery-2.2.0.min.js"></script>
</head>
<body>
登录失效，请<a href="/login">重新登录</a>,三秒后自动跳转到登录界面
</body>
</html>
<script>

    $(document).ready(function(){
        setTimeout(function () {
            window.location = "/login";
        }, 3000);
        //这里实现延迟5秒跳转
    });
</script>
