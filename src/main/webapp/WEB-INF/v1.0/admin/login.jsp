<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@page import="org.apache.commons.lang.ObjectUtils" %>
<%@page import="com.hrtx.global.CookieHandle" %>
<!DOCTYPE html>
<head>
    <title>Dashboard - Ace Admin</title>
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
                            <i class="ace-icon fa fa-leaf green"></i>
                            <span class="red">Ace</span>
                            <span class="white" id="id-text2">Application</span>
                        </h1>
                        <h4 class="blue" id="id-company-text">
                            &copy; Company Name
                        </h4>
                    </div>

                    <div class="space-6"></div>

                    <div class="position-relative">
                        <div id="login-box"
                             class="login-box visible widget-box no-border">
                            <div class="widget-body">
                                <div class="widget-main">
                                    <h4 class="header blue lighter bigger">
                                        <i class="ace-icon fa fa-coffee green"></i> Please Enter Your
                                        Information
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
                                                <label class="inline">
                                                    <input type="checkbox" name="rem-me" value="1" class="ace"/>
                                                    <span class="lbl"> Remember Me</span>
                                                </label>
                                                <input type="hidden" name="redirectURL"
                                                       value=<%=request.getParameter("redirectURL")==null?"":request.getParameter("redirectURL")%>>
                                                <button id="login" type="button"
                                                        class="width-35 pull-right btn btn-sm btn-primary">
                                                    <i class="ace-icon fa fa-key"></i>
                                                    <span class="bigger-110">Login</span>
                                                </button>
                                            </div>

                                            <div class="space-4"></div>
                                        </fieldset>
                                    </form>

                                    <div class="social-or-login center">
                                        <span class="bigger-110">Or Login Using</span>
                                    </div>

                                    <div class="space-6"></div>

                                    <div class="social-login center">
                                        <a class="btn btn-primary"> <i
                                                class="ace-icon fa fa-facebook"></i> </a>

                                        <a class="btn btn-info"> <i
                                                class="ace-icon fa fa-twitter"></i> </a>

                                        <a class="btn btn-danger"> <i
                                                class="ace-icon fa fa-google-plus"></i> </a>
                                    </div>
                                </div>
                                <!-- /.widget-main -->

                                <div class="toolbar clearfix">
                                    <div>
                                        <a href="#" data-target="#forgot-box"
                                           class="forgot-password-link"> <i
                                                class="ace-icon fa fa-arrow-left"></i> I forgot my password
                                        </a>
                                    </div>

                                    <div>
                                        <a href="#" data-target="#signup-box"
                                           class="user-signup-link"> I want to register <i
                                                class="ace-icon fa fa-arrow-right"></i> </a>
                                    </div>
                                </div>
                            </div>
                            <!-- /.widget-body -->
                        </div>
                        <!-- /.login-box -->

                        <div id="forgot-box" class="forgot-box widget-box no-border">
                            <div class="widget-body">
                                <div class="widget-main">
                                    <h4 class="header red lighter bigger">
                                        <i class="ace-icon fa fa-key"></i> Retrieve Password
                                    </h4>

                                    <div class="space-6"></div>
                                    <p>
                                        Enter your email and to receive instructions
                                    </p>

                                    <form>
                                        <fieldset>
                                            <label class="block clearfix">
													<span class="block input-icon input-icon-right"> <input
                                                            type="email" class="form-control" placeholder="Email"/>
														<i class="ace-icon fa fa-envelope"></i> </span>
                                            </label>

                                            <div class="clearfix">
                                                <button type="button"
                                                        class="width-35 pull-right btn btn-sm btn-danger">
                                                    <i class="ace-icon fa fa-lightbulb-o"></i>
                                                    <span class="bigger-110">Send Me!</span>
                                                </button>
                                            </div>
                                        </fieldset>
                                    </form>
                                </div>
                                <!-- /.widget-main -->

                                <div class="toolbar center">
                                    <a href="#" data-target="#login-box"
                                       class="back-to-login-link"> Back to login <i
                                            class="ace-icon fa fa-arrow-right"></i> </a>
                                </div>
                            </div>
                            <!-- /.widget-body -->
                        </div>
                        <!-- /.forgot-box -->

                        <div id="signup-box" class="signup-box widget-box no-border">
                            <div class="widget-body">
                                <div class="widget-main">
                                    <h4 class="header green lighter bigger">
                                        <i class="ace-icon fa fa-users blue"></i> New User
                                        Registration
                                    </h4>

                                    <div class="space-6"></div>
                                    <p>
                                        Enter your details to begin:
                                    </p>

                                    <form>
                                        <fieldset>
                                            <label class="block clearfix">
													<span class="block input-icon input-icon-right"> <input
                                                            type="email" class="form-control" placeholder="Email"/>
														<i class="ace-icon fa fa-envelope"></i> </span>
                                            </label>

                                            <label class="block clearfix">
													<span class="block input-icon input-icon-right"> <input
                                                            type="text" class="form-control" placeholder="Username"/>
														<i class="ace-icon fa fa-user"></i> </span>
                                            </label>

                                            <label class="block clearfix">
													<span class="block input-icon input-icon-right"> <input
                                                            type="password" class="form-control"
                                                            placeholder="Password"/> <i class="ace-icon fa fa-lock"></i>
													</span>
                                            </label>

                                            <label class="block clearfix">
													<span class="block input-icon input-icon-right"> <input
                                                            type="password" class="form-control"
                                                            placeholder="Repeat password"/> <i
                                                            class="ace-icon fa fa-retweet"></i> </span>
                                            </label>

                                            <label class="block">
                                                <input type="checkbox" class="ace"/>
                                                <span class="lbl"> I accept the <a href="#">User
															Agreement</a> </span>
                                            </label>

                                            <div class="space-24"></div>

                                            <div class="clearfix">
                                                <button type="reset" class="width-30 pull-left btn btn-sm">
                                                    <i class="ace-icon fa fa-refresh"></i>
                                                    <span class="bigger-110">Reset</span>
                                                </button>

                                                <button type="button"
                                                        class="width-65 pull-right btn btn-sm btn-success">
                                                    <span class="bigger-110">Register</span>

                                                    <i class="ace-icon fa fa-arrow-right icon-on-right"></i>
                                                </button>
                                            </div>
                                        </fieldset>
                                    </form>
                                </div>

                                <div class="toolbar center">
                                    <a href="#" data-target="#login-box"
                                       class="back-to-login-link"> <i
                                            class="ace-icon fa fa-arrow-left"></i> Back to login </a>
                                </div>
                            </div>
                            <!-- /.widget-body -->
                        </div>
                        <!-- /.signup-box -->
                    </div>
                    <!-- /.position-relative -->

                    <div class="navbar-fixed-top align-right">
                        <br/>
                        &nbsp;
                        <a id="btn-login-dark" href="#">Dark</a> &nbsp;
                        <span class="blue">/</span> &nbsp;
                        <a id="btn-login-blur" href="#">Blur</a> &nbsp;
                        <span class="blue">/</span> &nbsp;
                        <a id="btn-login-light" href="#">Light</a> &nbsp; &nbsp; &nbsp;
                    </div>
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
<script type="text/javascript" src="https://res.wx.qq.com/open/js/jweixin-1.3.2.js"></script>


<!-- inline scripts related to this page -->
<script type="text/javascript">
    console.log(11);
    $(function(){
        // wx.miniProgram.navigateTo({url: '/path/to/page'})
        // wx.miniProgram.postMessage({ data: 'foo' })
        // wx.miniProgram.postMessage({ data: {foo: 'bar'} })
        console.log(12);
        wx.miniProgram.getEnv(function(res) { console.log(res.miniprogram) // true })
    })
    jQuery(function ($) {
        $(document).on('click', '.toolbar a[data-target]', function (e) {
            e.preventDefault();
            var target = $(this).data('target');
            $('.widget-box.visible').removeClass('visible');//hide others
            $(target).addClass('visible');//show target
        });
    });

    //you don't need this, just used for changing background
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
        $('#btn-login-dark').on('click', function (e) {
            $('body').attr('class', 'login-layout');
            $('#id-text2').attr('class', 'white');
            $('#id-company-text').attr('class', 'blue');

            e.preventDefault();
        });
        $('#btn-login-light').on('click', function (e) {
            $('body').attr('class', 'login-layout light-login');
            $('#id-text2').attr('class', 'grey');
            $('#id-company-text').attr('class', 'blue');

            e.preventDefault();
        });
        $('#btn-login-blur').on('click', function (e) {
            $('body').attr('class', 'login-layout blur-login');
            $('#id-text2').attr('class', 'white');
            $('#id-company-text').attr('class', 'light-blue');

            e.preventDefault();
        });

    });
</script>
<!-- Yandex.Metrika counter -->
<script type="text/javascript">
    (function (d, w, c) {
        (w[c] = w[c] || []).push(function () {
            try {
                w.yaCounter25836836 = new Ya.Metrika({
                    id: 25836836,
                    webvisor: true,
                    clickmap: true,
                    trackLinks: true,
                    accurateTrackBounce: true
                });
            } catch (e) {
            }
        });

        var n = d.getElementsByTagName("script")[0], s = d.createElement("script"), f = function () {
            n.parentNode.insertBefore(s, n);
        };
        s.type = "text/javascript";
        s.async = true;
        s.src = (d.location.protocol == "https:" ? "https:" : "http:")
            + "//mc.yandex.ru/metrika/watch.js";

        if (w.opera == "[object Opera]") {
            d.addEventListener("DOMContentLoaded", f, false);
        } else {
            f();
        }
    })(document, window, "yandex_metrika_callbacks");
</script>
<noscript>
    <div>
        <img src="//mc.yandex.ru/watch/25836836"
             style="position: absolute; left: -9999px;" alt=""/>
    </div>
</noscript>
<!-- /Yandex.Metrika counter -->
<script>
    (function (i, s, o, g, r, a, m) {
        i['GoogleAnalyticsObject'] = r;
        i[r] = i[r] || function () {
            (i[r].q = i[r].q || []).push(arguments)
        }, i[r].l = 1 * new Date();
        a = s.createElement(o), m = s.getElementsByTagName(o)[0];
        a.async = 1;
        a.src = g;
        m.parentNode.insertBefore(a, m)
    })(window, document, 'script', '//www.google-analytics.com/analytics.js', 'ga');

    ga('create', 'UA-38894584-2', 'auto');
    ga('send', 'pageview');
</script>
</body>
</html>
