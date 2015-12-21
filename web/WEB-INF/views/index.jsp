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
    <script src="static/modules/jquery-1.11.3.min.js"></script>
    <script type="text/javascript">

        $("#local").val(123);

        $(document).mousemove(function(e){
            $("#local").val(e.pageX+"_"+e.pageY);
            $("#img").css("-webkit-transform","rotateY(60deg) translate("+e.pageX/30+"px,0px)");
        });

    </script>


    </body>
</html>