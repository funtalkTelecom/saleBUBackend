<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<head>
	<title>Dashboard - Ace Admin</title>
	<%@ include file="common/basecss.jsp" %>
</head>
<body class="no-skin">
<!-- #section:basics/navbar.layout -->
<div id="navbar" class="navbar navbar-default">
	<jsp:include page="common/top.jsp"></jsp:include>
</div>

<!-- /section:basics/navbar.layout -->
<div class="main-container" id="main-container">
	<!-- #section:basics/sidebar -->
	<div id="sidebar" class="sidebar responsive">
		<jsp:include page="common/menu.jsp"></jsp:include>
	</div>

	<!-- /section:basics/sidebar -->
	<div class="main-content">
		<div class="main-content-inner">
			<!-- #section:basics/content.breadcrumbs -->
			<div class="breadcrumbs" id="breadcrumbs">
				<ul class="breadcrumb">
					<li><i class="ace-icon fa fa-home home-icon"></i><a href="#">主页</a></li>
					<li class="active">系统提示</li>
				</ul><!-- /.breadcrumb -->
				<!-- /section:basics/content.searchbox -->
			</div>

			<div class="page-content">
				<div class="row">
					<div class="col-xs-12">
						<h3 class="header smaller lighter blue">jQuery dataTables</h3>
						<div style="text-align: center;color: red;font-size: 16px;">
							${errormsg}
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
