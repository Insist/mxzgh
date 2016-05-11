<%@page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
<style>
    #img{
        -webkit-transform-origin:bottom left;
        -webkit-transform: rotateY(60deg);
    }
</style>
<body>
    <input name="reg_username"/><br>
    <input type="password" name="reg_password"/><br>
    <input type="password" name="reg_passwordAg"/><br>
    <input type="button" onclick="submit_reg()" value="注册"> <label id="reg_message"></label>
    <hr>
    <input name="log_username"/><br>
    <input type="password" name="log_password"/><br>
    <input type="button" onclick="login_reg()" value="登录"> <label id="log_message"></label>
    <script src="/static/modules/jquery-1.11.3.min.js"></script>
    <script src="/static/modules/json2.js"></script>
    <script src="/static/modules/global.js"></script>
    <script type="text/javascript">
        function submit_reg(){
            var username = $("input[name='reg_username']").val();
            var password = $("input[name='reg_password']").val();
            var passwordAg = $("input[name='reg_passwordAg']").val();
            if(username.length<6){
                alert("账号错误");
                return;
            }
            if(password.length<6||password!=passwordAg){
                alert("密码错误");
                return;
            }
            var htmlobj = $.ajax({url:BASE_URL+"/user/register.do",data:{
                username:username,
                password:password
            },type:"post",async:false});
            var result = JSON.parse(htmlobj.responseText);
            $("#reg_message").html(result.message);
        }

        function login_reg(){
            var username = $("input[name='log_username']").val();
            var password = $("input[name='log_password']").val();
            if(username.length<6){
                alert("账号错误");
                return;
            }
            if(password.length<6){
                alert("密码错误");
                return;
            }
            var htmlobj = $.ajax({url:BASE_URL+"/user/login.do",data:{
                username:username,
                password:password
            },type:"post",async:false});
            var result = JSON.parse(htmlobj.responseText);
            if(result.code==0){
                window.location = BASE_URL+"/game.do";
            }else{
                $("#log_message").html(result.message);
            }
        }
    </script>
</body>