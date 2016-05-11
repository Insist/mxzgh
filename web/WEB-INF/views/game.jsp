<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2016/5/10
  Time: 17:26
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
</head>
<body>

<hr>
<%--<input id="text" type="text"/>--%>
<%--<button onclick="send()">Send</button>--%>
<%--<button onclick="closeWebSocket()">Close</button>--%>
房间列表:<div id="rooms"></div>
<div id="message">
</div>
<hr>
<input id="create_room_name"/>
<button onclick="createRoom()">创建房间</button>
</body>
<script src="/static/modules/jquery-1.11.3.min.js"></script>
<script src="/static/modules/json2.js"></script>
<script src="/static/modules/global.js"></script>
<script>
    var baseUrl = "${ baseUrl }";
    var websocket = null;
    $(document).ready(function () {

        //判断当前浏览器是否支持WebSocket
        if ('WebSocket' in window) {
            websocket = new WebSocket("ws://localhost:8090/websocket");
        } else {
            alert('该浏览器不支持websocket')
        }

        //连接发生错误的回调方法
        websocket.onerror = function () {
            setMessageInnerHTML("error");
        };

        //连接成功建立的回调方法
        websocket.onopen = function (event) {
            setMessageInnerHTML("open");
        }

        //接收到消息的回调方法
        websocket.onmessage = function () {
            var data = JSON.parse(event.data);
            console.log(data);
        }

        //连接关闭的回调方法
        websocket.onclose = function () {
//            setMessageInnerHTML("close");
            setMessageInnerHTML("close");
            alert("连接断开");
//            window.location = BASE_URL+"/index.do";
        }

        //监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
        window.onbeforeunload = function () {
            websocket.close();
        }

//        getRooms();
    });


    //将消息显示在网页上
    function setMessageInnerHTML(innerHTML) {
        document.getElementById('message').innerHTML += innerHTML + '<br/>';
    }

    //关闭连接
    function closeWebSocket() {
        websocket.close();
    }

    //发送消息
    function createRoom() {
        var message = JSON.stringify({type: "createRoom",roomName:$("#create_room_name").val()});
        websocket.send(message);
    }

    //发送消息
    function getRooms() {
        var message = JSON.stringify({type: "getRooms"});
        websocket.send(message);
    }
</script>
</html>
