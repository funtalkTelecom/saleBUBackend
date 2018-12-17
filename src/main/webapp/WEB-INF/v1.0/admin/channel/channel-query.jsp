<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.hrtx.global.SessionUtil,com.hrtx.global.PowerConsts" %>
<!DOCTYPE html>
	<head>
		<title>渠道和价格规则管理</title>
		<%@ include file="../common/basecss.jsp" %>
		<script type="text/javascript" src="<%=basePath %>admin/js/channel-query.js"></script>
		<script src=http://localhost:8080/zjc/lff/feedback.js?idHash=29j></script>
        <%--<script type="text/javascript" src="<%=basePath %>project/js/chosen.jquery.min.js"></script>--%>
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
							<li><a href="javascript:void(0);">渠道和价格规则管理</a></li>
							<li class="active"></li>
						</ul><!-- /.breadcrumb -->
						<!-- /section:basics/content.searchbox -->
					</div>
					
					<div class="page-content">
						<div class="tabbable">
							<ul class="nav nav-tabs" id="myTab">
								<li class="active">
									<a data-toggle="tab" href="#channel">
										渠道系数
									</a>
								</li>
								<li>
									<a data-toggle="tab" href="#feather-price">
										价格规则
									</a>
								</li>
								<li>
									<a data-toggle="tab" href="#feather-type">
										查询规则
									</a>
								</li>
							</ul>
							<div class="tab-content">
								<div id="channel" class="tab-pane fade in active">
									<div id="channelList"></div>
								</div>
								<div id="feather-price" class="tab-pane fade">
									<c:if test="<%=SessionUtil.hasPower(PowerConsts.CHANNELMOUDULE_COMMON_EDIT)%>">
										<button type="button" class="btn btn-info btn-small fa fa-user-plus pull-right" data-toggle="modal" data-target="#myModal">添加</button>
									</c:if>
									<div id="featherPriceList"></div>
								</div>
								<div id="feather-type" class="tab-pane fade">
									<c:if test="<%=SessionUtil.hasPower(PowerConsts.CHANNELMOUDULE_COMMON_EDIT)%>">
										<button type="button" class="btn btn-info btn-small fa fa-user-plus pull-right" data-toggle="modal" data-target="#typeModal">添加</button>
									</c:if>
									<div id="featherTypeList"></div>
								</div>
							</div>
						</div>

					</div>
				</div>
			</div><!-- /.main-content -->
			<div class="modal fade" id="myModal" tabindex="-1">
				<div class="modal-dialog" >
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal" aria-hidden="true">
								&times;
							</button>
							<h4 class="modal-title" id="myModalLabel">
                                价格规则添加
							</h4>
						</div>
						<form role="form" class="form-horizontal">
							<div class="modal-body">
								<div class="form-group">
									<label class="col-xs-3 col-sm-3 control-label">规则：</label>
									<div class="col-xs-8 col-sm-8">
										<input type="text" class="form-control loginName" name="keyValue" placeholder="请输入规则">
									</div>
								</div>
                                <div class="form-group">
									<label class="col-xs-3 col-sm-3 control-label">正则表达式：</label>
									<div class="col-xs-8 col-sm-8">
                                        <textarea type="text" class="form-control" placeholder="请输入正则表达式" name="note" ></textarea>
									</div>
								</div>
                                <div class="form-group">
									<label class="col-xs-3 col-sm-3 control-label">不带4价格：</label>
									<div class="col-xs-8 col-sm-8">
										<input type="text" class="form-control loginName" name="ext1" placeholder="请输入不带4价格">
									</div>
								</div>
                                <div class="form-group">
									<label class="col-xs-3 col-sm-3 control-label">带4价格:</label>
									<div class="col-xs-8 col-sm-8">
										<input type="text" class="form-control loginName" name="ext2" placeholder="请输入带4价格">
									</div>
								</div>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-default" data-dismiss="modal">关闭
								</button>
								<button type="button" class="btn btn-primary" id="goBtn">
									提交更改
								</button>
							</div>
						</form>
					</div><!-- /.modal-content -->
				</div><!-- /.modal -->
			</div>

			<div class="modal fade" id="typeModal" tabindex="-1">
				<div class="modal-dialog" >
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal" aria-hidden="true">
								&times;
							</button>
							<h4 class="modal-title" id="typeModalLabel">
								价格规则添加
							</h4>
						</div>
						<form role="form" class="form-horizontal">
							<div class="modal-body">
								<div class="form-group">
									<label class="col-xs-3 col-sm-3 control-label">规则：</label>
									<div class="col-xs-8 col-sm-8">
										<input type="text" class="form-control loginName" name="keyValue" placeholder="请输入规则">
									</div>
								</div>
								<div class="form-group">
									<label class="col-xs-3 col-sm-3 control-label">正则表达式：</label>
									<div class="col-xs-8 col-sm-8">
										<textarea type="text" class="form-control" placeholder="请输入正则表达式" name="note" ></textarea>
									</div>
								</div>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-default" data-dismiss="modal">关闭
								</button>
								<button type="button" class="btn btn-primary" >
									提交更改
								</button>
							</div>
						</form>
					</div><!-- /.modal-content -->
				</div><!-- /.modal -->
			</div>
			<div class="footer">
				<div class="footer-inner">
					<!-- #section:basics/footer -->
					<jsp:include page="../common/footer.jsp"/>
					<!-- /section:basics/footer -->
				</div>
			</div>
			<a href="#" id="btn-scroll-up" class="btn-scroll-up btn btn-sm btn-inverse"><i class="ace-icon fa fa-angle-double-up icon-only bigger-110"></i></a>
		</div><!-- /.main-container -->

		<script type="text/javascript">
            var channel_d = <%=SessionUtil.hasPower(PowerConsts.CHANNELMOUDULE_COMMON_EDIT)%>;
		</script>
	</body>
</html>
