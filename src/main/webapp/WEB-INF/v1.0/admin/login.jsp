<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@page import="org.apache.commons.lang.ObjectUtils" %>
<%@page import="com.hrtx.global.CookieHandle" %>
<!DOCTYPE html>
<head>
    <title>靓号优选</title>
    <%@ include file="common/basecss.jsp" %>
</head>
<body class="login-layout">
<div class="main-container">
    <div class="main-content">
        <div class="row">
            <div class="col-sm-10 col-sm-offset-1">
                <div class="login-container">
                    <div class="center">
                        <h1>
                            <%--<i class="ace-icon fa fa-leaf green"></i>--%>
                            <%--<span class="red">Ace</span>--%>
                            <span class="white" id="id-text2">靓号优选</span>
                        </h1>
                    </div>

                    <div class="space-6"></div>

                    <div class="position-relative">
                        <div id="login-box"
                             class="login-box visible widget-box no-border">
                            <div class="widget-body">
                                <div class="widget-main">
                                    <h4 class="header blue lighter bigger">
                                        <i class="ace-icon fa fa-coffee green"></i>用户登录
                                    </h4>

                                    <div class="space-6"></div>

                                    <form action="login" method="post" id="loginForm">
                                        <fieldset>
                                            <div id="errormsg">${errormsg}</div>
                                            <label class="block clearfix">
													<span class="block input-icon input-icon-right">
														<input type="text" name="loginName"
                                                               value="<%=ObjectUtils.toString(new CookieHandle().getCookieValue(request, "loginName"))  %>"
                                                               placeholder="Username" class="form-control"
                                                               id="loginName">
														<i class="ace-icon fa fa-user"></i> </span>
                                            </label>

                                            <label class="block clearfix">
													<span class="block input-icon input-icon-right">
														<input type="password" name="pwd" value=""
                                                               placeholder="Password" class="form-control" id="pwd">
														<i class="ace-icon fa fa-lock"></i>
													</span>
                                            </label>

                                            <div class="space"></div>

                                            <div class="clearfix">
                                                <%--<label class="inline">
                                                    <input type="checkbox" name="rem-me" value="1" class="ace"/>
                                                    <span class="lbl"> Remember Me</span>
                                                </label>--%>
                                                <input type="hidden" name="redirectURL"
                                                       value=<%=request.getParameter("redirectURL")==null?"":request.getParameter("redirectURL")%>>
                                                <button id="login" type="button"
                                                        class="width-35 pull-right btn btn-sm btn-primary">
                                                    <i class="ace-icon fa fa-key"></i>
                                                    <span class="bigger-110">登录</span>
                                                </button>
                                            </div>

                                            <div class="space-4"></div>
                                        </fieldset>
                                    </form>

                                </div>
                        </div>
                        <!-- /.login-box -->
                    </div>
                    <!-- /.position-relative -->

                    <%--<div class="navbar-fixed-top align-right">
                        <br/>
                        &nbsp;
                        <a id="btn-login-dark" href="#">Dark</a> &nbsp;
                        <span class="blue">/</span> &nbsp;
                        <a id="btn-login-blur" href="#">Blur</a> &nbsp;
                        <span class="blue">/</span> &nbsp;
                        <a id="btn-login-light" href="#">Light</a> &nbsp; &nbsp; &nbsp;
                    </div>--%>
                </div>
            </div>
            <!-- /.col -->
        </div>
        <!-- /.row -->
    </div>
    <!-- /.main-content -->
</div>
<!-- /.main-container -->

<script src="<%=basePath %>project/js/md5.js"></script>

<!-- inline scripts related to this page -->
<script type="text/javascript">
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
