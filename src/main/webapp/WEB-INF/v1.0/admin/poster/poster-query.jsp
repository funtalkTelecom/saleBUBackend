<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.hrtx.global.SessionUtil,com.hrtx.global.PowerConsts" %>
<!DOCTYPE html>
	<head>
		<title>海报管理</title>
		<%@ include file="../common/basecss.jsp" %>
		<%--<script type="text/javascript" src="<%=basePath %>query-meal.js"></script>--%>
		<script type="text/javascript" src="<%=basePath %>admin/js/poster-query.js"></script>
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
							<li><a href="javascript:void(0);">海报管理</a></li>
							<li class="active"></li>
						</ul><!-- /.breadcrumb -->
						<!-- /section:basics/content.searchbox -->
					</div>
					
					<div class="page-content">
						<div class="row">
							<div class="col-xs-12">
								<div class="query" style="margin-bottom: -10px">
									<form class="form-inline pd5" role="form">
										<div class="form-group">
											<label class="control-label">标题</label>
											<input type="text" class="form-control" style="width:130px;" name="title">
										</div>
										<div class="form-group">
											<label class="control-label">备注</label>
											<input type="text" class="form-control" style="width:130px;" name="remark">
										</div>
										<c:if test="<%=SessionUtil.hasPower(PowerConsts.POSTERMOUDULE_COMMON_ADD)%>">
											<button type="button" class="btn btn-info btn-small fa fa-user-plus pull-right" data-toggle="modal" data-target="#posterInfo" >添加</button>
										</c:if>
										<button type="button" class="btn btn-info btn-small fa fa-user-plus pull-right" id="query">查询</button>
										<button type="button" class="btn btn-info btn-small fa fa-user-plus pull-right" id="reset">重置</button>
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
		<div class="modal fade" id="posterInfo" tabindex="-1">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">
							&times;
						</button>
						<h4 class="modal-title">
							添加/修改海报
						</h4>
					</div>
					<form role="form" class="form-horizontal">
						<input name="id" type="hidden">
						<input name="pic" type="hidden">
						<div class="modal-body">
							<div class="form-group">
								<label class="col-xs-2 control-label">标题</label>
								<div class="col-xs-4">
									<input type="text" class="form-control" name="title">
								</div>
								<label class="col-xs-2 control-label">位置</label>
								<div class="col-xs-4">
									<select class="form-control" name="position" id="posterPosition">
										<option value="-1">请选择...</option>
									</select>
								</div>
							</div>
							<div class="form-group">
								<label class="col-xs-2 control-label">图片${bean.pic}</label>
								<div class="col-xs-10">
									<input type="file" name="file" id="file">
									<c:if test="bean.pic!=null and bean.pic!=''">
										<img src="<%=basePath%>admin/posterImages/${bean.pic}"/>
									</c:if>
								</div>
							</div>
							<div class="form-group">
								<label class="col-xs-2 control-label">url</label>
								<div class="col-xs-10">
									<input type="text" class="form-control input-xxlarge" name="url">
								</div>
							</div>
							<div class="form-group">
								<label class="col-xs-2 control-label">开始时间</label>
								<div class="col-xs-4">
									<input type="text" class="form-control" id="startTime" name="startTime" value="" readonly/>
								</div>
								<label class="col-xs-2 control-label">结束时间</label>
								<div class="col-xs-4">
									<input type="text" class="form-control" id="endTime" name="endTime" value="" readonly/>
								</div>
							</div>

							<div class="form-group">
								<label class="col-xs-2 control-label">备注</label>
								<div class="col-xs-10">
									<textarea type="text" style="width: 468px" class="form-control input-xxlarge" name="remark" ></textarea>
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
            var basePath = "<%=basePath %>";
            var p_edit = <%=SessionUtil.hasPower(PowerConsts.POSTERMOUDULE_COMMON_EDIT)%>;
            var p_delete= <%=SessionUtil.hasPower(PowerConsts.POSTERMOUDULE_COMMON_DELETE)%>;
		</script>
	</body>
</html>
