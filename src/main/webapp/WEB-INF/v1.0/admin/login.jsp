<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@page import="org.apache.commons.lang.ObjectUtils" %>
<%@page import="com.hrtx.global.CookieHandle" %>
<!DOCTYPE html>
<head>
    <title>靓号优选</title>
    <%@ include file="common/basecss.jsp" %>
    <link rel="stylesheet" href="<%=basePath %>project/css/logins.css">
</head>
<body class="login-layout" style="background:url(<%=basePath %>project/images/login-b.jpg) no-repeat center;background-size:auto auto;">
    <%--<div class="main-container">--%>
    <%--<div class="main-content">--%>
        <%--<div class="row">--%>
            <%--<div class="col-sm-10 col-sm-offset-1">--%>
                <%--<div class="login-container">--%>
                    <%--<div class="center">--%>
                        <%--<h1>--%>
                            <%--&lt;%&ndash;<i class="ace-icon fa fa-leaf green"></i>&ndash;%&gt;--%>
                            <%--&lt;%&ndash;<span class="red">Ace</span>&ndash;%&gt;--%>
                            <%--<span class="white" id="id-text2">靓号优选</span>--%>
                        <%--</h1>--%>
                    <%--</div>--%>

                    <%--<div class="space-6"></div>--%>

                    <%--<div class="position-relative">--%>
                        <%--<div id="login-box"--%>
                             <%--class="login-box visible widget-box no-border">--%>
                            <%--<div class="widget-body">--%>
                                <%--<div class="widget-main">--%>
                                    <%--<h4 class="header blue lighter bigger">--%>
                                        <%--<i class="ace-icon fa fa-coffee green"></i>用户登录--%>
                                    <%--</h4>--%>

                                    <%--<div class="space-6"></div>--%>

                                    <%--<form action="login" method="post" id="loginForm">--%>
                                        <%--<fieldset>--%>
                                            <%--<div id="errormsg">${errormsg}</div>--%>
                                            <%--<label class="block clearfix">--%>
													<%--<span class="block input-icon input-icon-right">--%>
														<%--<input type="text" name="loginName"--%>
                                                               <%--value="<%=ObjectUtils.toString(new CookieHandle().getCookieValue(request, "loginName"))  %>"--%>
                                                               <%--placeholder="Username" class="form-control"--%>
                                                               <%--id="loginName">--%>
														<%--<i class="ace-icon fa fa-user"></i> </span>--%>
                                            <%--</label>--%>

                                            <%--<label class="block clearfix">--%>
													<%--<span class="block input-icon input-icon-right">--%>
														<%--<input type="password" name="pwd" value=""--%>
                                                               <%--placeholder="Password" class="form-control" id="pwd">--%>
														<%--<i class="ace-icon fa fa-lock"></i>--%>
													<%--</span>--%>
                                            <%--</label>--%>

                                            <%--<div class="space"></div>--%>

                                            <%--<div class="clearfix">--%>
                                                <%--&lt;%&ndash;<label class="inline">--%>
                                                    <%--<input type="checkbox" name="rem-me" value="1" class="ace"/>--%>
                                                    <%--<span class="lbl"> Remember Me</span>--%>
                                                <%--</label>&ndash;%&gt;--%>
                                                <%--<input type="hidden" name="redirectURL"--%>
                                                       <%--value=<%=request.getParameter("redirectURL")==null?"":request.getParameter("redirectURL")%>>--%>
                                                <%--<button id="login" type="button"--%>
                                                        <%--class="width-35 pull-right btn btn-sm btn-primary">--%>
                                                    <%--<i class="ace-icon fa fa-key"></i>--%>
                                                    <%--<span class="bigger-110">登录</span>--%>
                                                <%--</button>--%>
                                            <%--</div>--%>

                                            <%--<div class="space-4"></div>--%>
                                        <%--</fieldset>--%>
                                    <%--</form>--%>

                                <%--</div>--%>
                        <%--</div>--%>
                        <%--<!-- /.login-box -->--%>
                    <%--</div>--%>
                    <%--<!-- /.position-relative -->--%>

                    <%--&lt;%&ndash;<div class="navbar-fixed-top align-right">--%>
                        <%--<br/>--%>
                        <%--&nbsp;--%>
                        <%--<a id="btn-login-dark" href="#">Dark</a> &nbsp;--%>
                        <%--<span class="blue">/</span> &nbsp;--%>
                        <%--<a id="btn-login-blur" href="#">Blur</a> &nbsp;--%>
                        <%--<span class="blue">/</span> &nbsp;--%>
                        <%--<a id="btn-login-light" href="#">Light</a> &nbsp; &nbsp; &nbsp;--%>
                    <%--</div>&ndash;%&gt;--%>
                <%--</div>--%>
            <%--</div>--%>
            <%--<!-- /.col -->--%>
        <%--</div>--%>
        <%--<!-- /.row -->--%>
    <%--</div>--%>
    <%--<!-- /.main-content -->--%>
<%--</div>--%>
<%--<!-- /.main-container -->--%>

<%--<script src="<%=basePath %>project/js/md5.js"></script>--%>

<%--<!-- inline scripts related to this page -->--%>
<%--<script type="text/javascript">--%>
    <%--jQuery(function ($) {--%>
        <%--$("#login").click(function () {--%>
            <%--var loginName = $("#loginName").val();--%>
            <%--var pwd = $("#pwd").val();--%>
            <%--if($.trim(loginName) == '' || $.trim(pwd) == '') {--%>
                <%--$("#errormsg").html("用户名密码不能为空");--%>
                <%--return;--%>
            <%--}--%>
            <%--if(!(pwd.length == 24 && pwd.substring(22,24) == "==")) $("#pwd").val(b64_md5(pwd));--%>
            <%--$("#loginForm")[0].submit();--%>
        <%--})--%>
    <%--});--%>
<%--</script>--%>
    <div class="main-container">
        <div class="top-content">

            <div class="inner-bg">
                <div class="container">
                    <div class="row">
                        <div class="col-sm-8 col-sm-offset-2 text">
                            <h1><strong><p class="login-title" >靓号优选</p></strong></h1>
                            <div class="description">
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-sm-6 col-sm-offset-3 form-box">
                            <div class="form-top">
                                <div class="form-top-left">
                                    <h3>用户登录</h3>
                                </div>
                            </div>
                            <div class="form-bottom">
                                <form role="form" action="login.htm" method="post" class="login-form" id="loginForm">
                                    <div id="errormsg" style="color:#ccc;">${errormsg}</div>
                                    <!--  <input type="hidden" name="redirectURL" value="null" />-->
                                    <div class="form-group">
                                        <label class="sr-only" >用户名</label>
                                        <input type="text" name="loginName" placeholder="请输入用户名..." class="form-username form-control inputFrm"
                                               id="loginName" value=""/>
                                    </div>
                                    <div class="form-group">
                                        <label class="sr-only" >密码</label>
                                        <input type="password" name="pwd" placeholder="请输入密码..." class="form-password form-control inputFrm" id="pwd">
                                    </div>
                                    <button type="submit" class="btn" id="login">登录</button>
                                    <%--<p style="color:red" class="warm"></p>--%>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- /.main-container -->

        <script src="<%=basePath %>project/js/md5.js"></script>

        <!-- inline scripts related to this page -->
        <script type="text/javascript">

            // jQuery(function ($) {
            //     $("#login").click(function () {
            //         console.log(loginErrCount)
            //         var loginName = $("#loginName").val();
            //         var pwd = $("#pwd").val();
            //         if($.trim(loginName) == '' || $.trim(pwd) == '') {
            //             $("#errormsg").html("用户名密码不能为空");
            //             return;
            //         }
            //         if(loginErrCount>=3){
            //             if ($("#code").val()==""){
            //                 $("#errormsg").html("验证码不能为空");
            //                 return
            //             }
            //         }
            //         if(!(pwd.length == 24 && pwd.substring(22,24) == "==")) $("#pwd").val(b64_md5(pwd));
            //         $("#loginForm")[0].submit();
            //     });
            // });
            jQuery(function ($) {
            $("#login").click(function () {
            var loginName = $("#loginName").val();
            var pwd = $("#pwd").val();
            if($.trim(loginName) == '' || $.trim(pwd) == '') {
            $("#errormsg").html("用户名密码不能为空");
            return;
            }
            if(!(pwd.length == 24 && pwd.substring(22,24) == "==")) $("#pwd").val(b64_md5(pwd));
            $("#loginForm")[0].submit();
            })
            });
        </script>
</body>
</html>
