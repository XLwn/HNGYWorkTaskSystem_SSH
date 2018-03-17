<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2017/11/17
  Time: 15:04
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>用户登录</title>

    <!-- Bootstrap core CSS -->
    <link href="./css/bootstrap.min.css" rel="stylesheet">
    <!-- Custom styles for this template -->
    <link href="./css/signin.css" rel="stylesheet">
    <script src="./JS/jquery.min.js"></script>
    <script type="text/javascript" src="./JS/Login.js"></script>

    <script src="https://cdn.bootcss.com/sweetalert/1.1.3/sweetalert.min.js"></script>
    <link href="https://cdn.bootcss.com/sweetalert/1.1.3/sweetalert.min.css" rel="stylesheet">


</head>

<body>
<div class="container">
    <form class="form-signin">
        <h2 class="form-signin-heading">用户登录</h2>
        <div class="form-group" id="d1">
            <label for="inputEmail" class="sr-only">帐号</label>
            <input type="text" id="inputEmail" class="form-control" placeholder="邮箱" required autofocus>
            <span id="helpBlock1" class="help-block" style="display:none">不得为空</span>
        </div>

        <div class="form-group" id="d2">
            <label for="inputPassword" class="sr-only">密码</label>
            <input type="password" id="inputPassword" class="form-control" placeholder="密码" required>
            <span id="helpBlock2" class="help-block" style="display:none">不得为空</span>
        </div>
        <div class="checkbox">
            <label>
                <input type="checkbox" value="remember-me" id="autoLogin"> 七天之内自动登录
            </label>
        </div>
        <button class="btn btn-lg btn-primary btn-block" id="btn_login" onclick="getLogin()" type="button">登 录</button>
    </form>

</div> <!-- /container -->

</body>
<script>
    $(document).keyup(function (event) {
        if (event.keyCode == 13) {
            $("#btlg").click();
        }
    });
</script>
</html>
