<!DOCTYPE HTML>
<html>
<style>
    #img{
        -webkit-transform-origin:bottom left;
        -webkit-transform: rotateY(60deg);
    }
</style>
<body>
    <img id="img" src="static/img/j.png"/>
    <br/>
    <input id="local"/>
    <hr>
    <input id="text" type="text" /><button onclick="send()">Send</button>    <button onclick="closeWebSocket()">Close</button>
    <div id="message">
    </div>
    <script src="static/modules/jquery-1.11.3.min.js"></script>
    <script type="text/javascript">

        $("#local").val(123);

        $(document).mousemove(function(e){
            $("#local").val(e.pageX+"_"+e.pageY);
            $("#img").css("-webkit-transform","rotateY(60deg) translate("+e.pageX/30+"px,0px)");
        });

        var websocket = null;
        $(document).ready(function () {

            //判断当前浏览器是否支持WebSocket
            if('WebSocket' in window){
                websocket = new WebSocket("ws://localhost:8090/websocket");
            }
            else{
                alert('Not support websocket')
            }

            //连接发生错误的回调方法
            websocket.onerror = function(){
                setMessageInnerHTML("error");
            };

            //连接成功建立的回调方法
            websocket.onopen = function(event){
                setMessageInnerHTML("open");
            }

            //接收到消息的回调方法
            websocket.onmessage = function(){
                setMessageInnerHTML(event.data);
            }

            //连接关闭的回调方法
            websocket.onclose = function(){
                setMessageInnerHTML("close");
            }

            //监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
            window.onbeforeunload = function(){
                websocket.close();
            }
        });


        //将消息显示在网页上
        function setMessageInnerHTML(innerHTML){
            document.getElementById('message').innerHTML += innerHTML + '<br/>';
        }

        //关闭连接
        function closeWebSocket(){
            websocket.close();
        }

        //发送消息
        function send(){
            var message = document.getElementById('text').value;
            websocket.send(message);
        }


    </script>


    </body>
</html>