<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="com.hrtx.global.SessionUtil" %>
<%@ page import="com.hrtx.global.PowerConsts" %>
<!DOCTYPE html>
	<head>
		<title>修改密码 - 靓号优选</title>
		<%@ include file="../common/basecss.jsp" %>
		<script type="text/javascript">
            $(function(){
                $("#submit").click(function(){
                    var options = {
                        type : "post",
                        url: "update-pwd",
                        success : function(data) {
                            alert("修改密码成功");
                        }
                    };
                    // 将options传给ajaxForm
                    $('#upform').ajaxSubmit(options);
                })
            })
		</script>
	</head>
	<body class="no-skin">
		<!-- #section:basics/navbar.layout -->
		<div id="navbar" class="navbar navbar-default">
			<jsp:include page="../common/top.jsp"></jsp:include>
		</div>

		<!-- /section:basics/navbar.layout -->
		<div class="main-container" id="main-container">
			<!-- #section:basics/sidebar -->
			<div id="sidebar" class="sidebar responsive">
				<jsp:include page="../common/menu.jsp"></jsp:include>
			</div>

			<!-- /section:basics/sidebar -->
			<div class="main-content">
				<div class="main-content-inner">
					<!-- #section:basics/content.breadcrumbs -->
					<div class="breadcrumbs" id="breadcrumbs">
						<ul class="breadcrumb">
							<li><i class="ace-icon fa fa-home home-icon"></i><a href="#">主页</a></li>
							<li><a href="javascript:void(0);">系统管理</a></li>
							<li class="active">修改密码</li>
						</ul><!-- /.breadcrumb -->
						<!-- /section:basics/content.searchbox -->
					</div>
					<div class="page-content">
						<div class="row">
							<div class="col-xs-6">
                                <form id="upform" class="form-horizontal " role="form">
                                    <div class="form-group">
                                        <label class="col-sm-2 control-label">原密码</label>
                                        <div class="col-sm-10"><input type="password" class="form-control" style="width: 200px;" name="originPwd"></div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-sm-2 control-label">新密码</label>
                                        <div class="col-sm-10"><input type="password" class="form-control" style="width: 200px;" name="pwd"></div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-sm-2 control-label">确认密码</label>
                                        <div class="col-sm-10"><input type="password" class="form-control" style="width: 200px;" name="confrimPwd"></div>
                                    </div>
                                    <div class="form-group">
                                        <div class="col-sm-offset-2 col-sm-10">
                                            <button type="button" class="btn btn-info btn-small fa fa-user-plus" id="submit">提交</button>
                                            <button type="button" class="btn btn-info btn-small fa fa-user-plus" id="reset">重置</button>
                                        </div>
                                    </div>
                                </form><!-- /form-panel -->
							</div>
						</div>
						
					</div>
				</div>
			</div><!-- /.main-content -->

			<div class="footer">
				<div class="footer-inner">
					<!-- #section:basics/footer -->
					<jsp:include page="../common/footer.jsp"/>
					<!-- /section:basics/footer -->
				</div>
			</div>
			<a href="#" id="btn-scroll-up" class="btn-scroll-up btn btn-sm btn-inverse"><i class="ace-icon fa fa-angle-double-up icon-only bigger-110"></i></a>
		</div><!-- /.main-container -->
	</body>
</html>
