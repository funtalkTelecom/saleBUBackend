<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE html>
	<head>
		<title>角色授权 - 靓号优选</title>
		<%@ include file="../common/basecss.jsp" %>
		<link rel="stylesheet" href="<%=basePath %>project/css/checktree/tree.css" type="text/css" />
		<script type="text/javascript" src="<%=basePath %>project/js/jquery.tree.js"></script>
		<script type="text/javascript" src="<%=basePath %>admin/js/query-role.js"></script>
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
							<li class="active">角色授权</li>
						</ul><!-- /.breadcrumb -->
						<!-- /section:basics/content.searchbox -->
					</div>
					
					<div class="page-content" id="isMask">
						<div class="row">
							<div class="col-xs-12">
								<%--<h3 class="header smaller lighter blue">jQuery dataTables</h3>--%>
								<%--<div class="table-header">
									Results for "Latest Registered Domains"
								</div>--%>
								<!-- <div class="table-responsive"> -->
								<!-- <div class="dataTables_borderWrap"> -->
								<div class="detail block">
									<div class="col-xs-6" style="padding-right:0px;">
										<div style="display: block;overflow: hidden;">
											<input type="hidden" id="roleId">
											<input type="hidden" id="property">
											<div class="col-xs-6" style="height: 500px;min-height: 500px;border: 1px solid #ccc">
												<div class="form-inline pd5">
									                <div class="form-group" >
									                    <label>角色名称</label>
									                    <input type="text" id="name" class="form-control"/>
									                </div>
									                <input type="button" id="save" value="保存角色" class="btn btn-sm btn-info" disabled="disabled">
									            </div>
												<div id="result" style="height: 450px;min-height: 450px;overflow-y: scroll;border: 1px solid #ccc"></div>
											</div>
											<div class="col-xs-6" style="height: 500px;min-height: 500px;border: 1px solid #ccc;border-left: 0px;border-right: 0px;">
												<div class="form-inline pd5">
									                <div class="form-group" >
									                    <label>用户名称</label>
									                    <input type="text" id="username" class="form-control"/>
									                </div>
									                <input type="button" id="query" value="查询" class="btn btn-sm btn-info"/>
									            </div>
												<div id="userresult" style="height: 450px;max-height: 450px;overflow-y: scroll;border: 1px solid #ccc"></div>
											</div>
										</div>
										<div style="display: block;"><input type="button" id="submit" value="授权" class="btn btn-info"></div>
									</div>
									<div class="tree col-xs-6" style="margin: auto;" >
										<div id="tlabel"></div>
										<div id="tree"></div>
									</div>
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
					<jsp:include page="../common/footer.jsp"/>
					<!-- /section:basics/footer -->
				</div>
			</div>
			<a href="#" id="btn-scroll-up" class="btn-scroll-up btn btn-sm btn-inverse"><i class="ace-icon fa fa-angle-double-up icon-only bigger-110"></i></a>
		</div><!-- /.main-container -->
	</body>
</html>
