<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.hrtx.global.SessionUtil,com.hrtx.global.PowerConsts" %>
<!DOCTYPE html>
	<head>
		<title>套餐管理</title>
		<%@ include file="../common/basecss.jsp" %>
		<%--<script type="text/javascript" src="<%=basePath %>query-meal.js"></script>--%>
		<script type="text/javascript" src="<%=basePath %>admin/js/query-meal.js"></script>
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
							<li><a href="javascript:void(0);">销售管理</a></li>
							<li class="active"></li>
						</ul><!-- /.breadcrumb -->
						<!-- /section:basics/content.searchbox -->
					</div>
					
					<div class="page-content">
						<div class="row">
							<div class="col-xs-12">
								<%--<h6 class="header smaller lighter blue">--%>
								<%--</h6>--%>
							<%--<div class="table-header">--%>
									<%--Results for "Latest Registered Domains"--%>
								<%--</div>--%>
								<%--<div class="table-responsive">--%>
								<%--<div class="dataTables_borderWrap">--%>

								<div class="query" style="margin-bottom: -10px">
									<form class="form-inline pd5" role="form">
										<div class="form-group">
											<label class="control-label">套餐ID</label>
											<input type="text" class="form-control" style="width:130px;" name="mealId">
										</div>
										<div class="form-group">
											<label class="control-label">套餐名称</label>
											<input type="text" class="form-control" style="width:130px;" name="mealName">
										</div>
										<button type="button" class="btn btn-info btn-small fa fa-user-plus pull-right" id="query">查询</button>
										<button type="button" class="btn btn-info btn-small fa fa-user-plus pull-right" id="reset">重置</button>
										<c:if test="<%=SessionUtil.hasPower(PowerConsts.MEALMOUDULE_COMMON_IMPORT)%>">
											<button id="modal-adduser" type="button" class="btn btn-info btn-small fa fa-user-plus pull-right" data-toggle="modal" data-target="#importModal" >导入</button>
										</c:if>
									</form><!-- /form-panel -->
								</div>

                                <h6 class="header smaller lighter blue"></h6>
								<div id="result">
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

		<!-- 模态框（Modal） -->
		<div class="modal fade" id="importModal" tabindex="-1">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">
							&times;
						</button>
						<h4 class="modal-title">
							导入
						</h4>
					</div>
					<form role="form" class="form-inline" enctype="multipart/form-data">
						<div class="modal-body">
							<div class="form-group">
								<label class="sr-only">文件输入</label>
								<input type="file" name="file" id="file">
								<p class="help-block"><a href="meal/download-meal-template">下载模板</a></p>
							</div>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-success">提交</button>
							<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
						</div>
					</form>
				</div><!-- /.modal-content -->
			</div><!-- /.modal -->
		</div>

		<!-- 模态框（Modal） -->
		<div class="modal fade" id="mealInfo" tabindex="-1">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">
							&times;
						</button>
						<h4 class="modal-title">
							修改套餐
						</h4>
					</div>
					<form role="form" class="form-horizontal">
						<input name="mid" type="hidden">
						<div class="modal-body">
							<div class="form-group">
								<label class="col-xs-2 control-label">套餐名称</label>
								<div class="col-xs-4">
									<input type="text" class="form-control" name="mealName">
								</div>
								<label class="col-xs-2 control-label">套餐ID</label>
								<div class="col-xs-4">
									<input type="text" class="form-control" name="mealId">
								</div>
							</div>
							<div class="form-group">
								<label class="col-xs-2 control-label">销售地市</label>
								<div class="col-xs-4">
									<select class="form-control" name="saleCity" id="fcity">
										<option value="-1">请选择...</option>
										<option value="0">不区分</option>
									</select>
								</div>
								<label class="col-xs-2 control-label">销售渠道</label>
								<div class="col-xs-4">
									<input type="text" class="form-control" name="saleType" >
								</div>
							</div>

							<div class="form-group">
								<label class="col-xs-2 control-label">套餐描述</label>
								<div class="col-xs-4">
									<%--<input type="text" class="form-control" name="mealDesc" >--%>

									<textarea type="text" style="width: 468px" class="form-control" name="mealDesc" ></textarea>
								</div>
							</div>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-success">提交</button>
							<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
						</div>
					</form>
				</div><!-- /.modal-content -->
			</div><!-- /.modal -->
		</div>
		<script type="text/javascript">
            var p_edit = <%=SessionUtil.hasPower(PowerConsts.MEALMOUDULE_COMMON_EDIT)%>;
            var p_import = <%=SessionUtil.hasPower(PowerConsts.MEALMOUDULE_COMMON_IMPORT)%>;
		</script>
	</body>
</html>
