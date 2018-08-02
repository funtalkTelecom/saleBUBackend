<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<head>
    <script type="text/javascript" src="https://res.wx.qq.com/open/js/jweixin-1.3.2.js"></script>
</head>
<script type="text/javascript">
    wx.miniProgram.getEnv(function(res) {
        if(res.miniprogram) {
            wx.miniProgram.redirectTo({url: '/pages/temp/index?sourceId=${sourceId}'})
        }
    })
</script>
<body></body>
</html>
