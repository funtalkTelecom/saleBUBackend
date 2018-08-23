<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<head>
	<title>首页 - 靓号优选</title>
	<%@ include file="common/basecss.jsp" %>
</head>
<body class="no-skin">
<div id="navbar" class="navbar navbar-default">
	<jsp:include page="common/top.jsp"></jsp:include>
</div>

<div class="main-container" id="main-container">
	<div id="sidebar" class="sidebar responsive">
		<jsp:include page="common/menu.jsp"></jsp:include>
	</div>

	<!-- /section:basics/sidebar -->
	<div class="main-content">
		<div class="main-content-inner">
			<!-- #section:basics/content.breadcrumbs -->
			<div class="breadcrumbs" id="breadcrumbs">
				<ul class="breadcrumb">
					<li><i class="ace-icon fa fa-home home-icon"></i><a href="/index">Home</a></li>
				</ul><!-- /.breadcrumb -->
				<!-- /section:basics/content.searchbox -->
			</div>

			<div class="page-content">
				<div class="row">
					<div class="col-xs-12">
						<div STYLE="text-align: center;font-size: 18px;">
							欢迎来到靓号优选后台管理！
						</div>
					</div>
				</div>

				<div class="row">
					<div class="col-xs-12">

					</div>
				</div>
			</div>
		</div>
	</div><!-- /.main-content -->

	<div class="footer">
		<div class="footer-inner">
			<!-- #section:basics/footer -->
			<jsp:include page="common/footer.jsp"/>
			<!-- /section:basics/footer -->
		</div>
	</div>
	<a href="#" id="btn-scroll-up" class="btn-scroll-up btn btn-sm btn-inverse"><i class="ace-icon fa fa-angle-double-up icon-only bigger-110"></i></a>
</div><!-- /.main-container -->
</body>
</html>
