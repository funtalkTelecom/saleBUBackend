<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.hrtx.global.SessionUtil,com.hrtx.global.PowerConsts" %>
<!DOCTYPE html>
<head>
	<title>代理商管理</title>
	<%@ include file="../common/basecss.jsp" %>
	<%--<script type="text/javascript" src="<%=basePath %>query-meal.js"></script>--%>
	<script type="text/javascript" src="<%=basePath %>admin/js/query-agent.js"></script>
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
					<li><a href="javascript:void(0);">代理商管理</a></li>
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
									<label class="control-label">企业名称</label>
									<input type="text" class="form-control" style="width:130px;" name="commpayName">
								</div>
								<div class="form-group">
									<label class="control-label">法人</label>
									<input type="text" class="form-control" style="width:130px;" name="person">
								</div>
								<div class="form-group">
									<label class="control-label">状态</label>
									<select  name="status" id="status" >
										<option value="-1">请选择</option>
										<option value="1">待审核</option>
										<option value="2" >审核通过</option>
										<option value="3" >审核未通过</option>
									</select>
								</div>
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
<div class="modal fade" id="checkModal" tabindex="-1">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">
					&times;
				</button>
				<h4 class="modal-title">
					审核
				</h4>
			</div>
			<form role="form" class="form-horizontal">
				<input id="id" name="id" type="hidden">
				<input id="addConsumerId" name="addConsumerId" type="hidden">
				<div class="modal-body">
					<div class="form-group">
						<label  class="col-xs-2 ">公司名称:</label>
						<div class="col-xs-3 ">
							<span class="scomm text-left"></span>
						</div>
						<label  class="col-xs-2 ">渠道:</label>
						<div class="col-xs-3 ">
							<span class="channelName text-left"></span>
						</div>
					</div>
					<div class="form-group">

						<label class="col-xs-2">法人</label>
						<div class="col-xs-3">
							<span class="person"></span>
						</div>
						<label class="col-xs-2 ">电话</label>
						<div class="col-xs-3">
							<span class="phone"></span>
						</div>
					</div>
					<div class="form-group">
						<label class="col-xs-2">注册地址</label>
						<div class="col-xs-8">
							<span class="address"></span>
						</div>
					</div>
					<div class="form-group">
						<label class="col-xs-2">营业执照</label>
						<div class="col-xs-10">
							<a id="adtradingImg" data-lightbox="tradingImg"><img id="tradingImg" style="max-width: 80px;max-height: 80px;"></a>
						</div>
					</div>
					<div class="form-group">
						<label class="col-xs-2 ">是否通过</label>
						<div class="col-xs-10">
							<select name="status" class="status">
								<option value="-1">请选择</option>
								<option value="2">通过</option>
								<option value="3">不通过</option>
							</select>
						</div>
					</div>
					<div class="form-group">
						<label class="col-xs-2 ">审核备注</label>
						<div class="col-xs-10">
							<textarea name="checkRemark"></textarea>
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

<div class="modal fade" id="channelModal" tabindex="-1">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">
					&times;
				</button>
				<h4 class="modal-title">
					修改
				</h4>
			</div>
			<form role="form" class="form-horizontal">
				<input id="ids" name="ids" type="hidden">
				<div class="modal-body">
					<div class="form-group">
						<label  class="col-xs-2 ">公司名称:</label>
						<div class="col-xs-3 ">
							<span class="scomm text-left"></span>
						</div>
						<label  class="col-xs-2 ">渠道:</label>
						<div class="col-xs-3 ">
							<span class="channelName text-left"></span>
						</div>
					</div>
					<div class="form-group">

						<label class="col-xs-2">法人</label>
						<div class="col-xs-3">
							<span class="person"></span>
						</div>
						<label class="col-xs-2 ">电话</label>
						<div class="col-xs-3">
							<span class="phone"></span>
						</div>
					</div>
					<div class="form-group">
						<label class="col-xs-2">注册地址</label>
						<div class="col-xs-8">
							<span class="address"></span>
						</div>
					</div>
					<div class="form-group">
						<label class="col-xs-2">营业执照</label>
						<div class="col-xs-10">
							<a id="chtradingImg" data-lightbox="tradingImg"><img id="ctradingImg" style="max-width: 80px;max-height: 80px;"></a>
						</div>
					</div>
					<div class="form-group">
						<label class="col-xs-2 ">渠道</label>
						<div class="col-xs-10">
							<select name="channelId" class="channelId">
								<option value="0">请选择</option>
								<c:forEach items="${channelList}" var="channel">
									<option value="${channel.channelId}" >${channel.channel}</option>
								</c:forEach>
							</select>
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

<!-- 模态框（Modal） -->
<div class="modal fade" id="DetailsModal" tabindex="-1">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">
					&times;
				</button>
				<h4 class="modal-title">
					 详情
				</h4>
			</div>
			<form role="form" class="form-horizontal">
				<div class="modal-body">
					<div class="form-group">
						<label  class="col-xs-2 ">公司名称:</label>
						<div class="col-xs-3 ">
							<span class="scomm text-left"></span>
						</div>
						<label  class="col-xs-2 ">渠道:</label>
						<div class="col-xs-3 ">
							<span class="channelName text-left"></span>
						</div>
					</div>
					<div class="form-group">

						<label class="col-xs-2">法人:</label>
						<div class="col-xs-3 ">
							<span class="person text-left"></span>
						</div>
						<label class="col-xs-2">电话:</label>
						<div class="col-xs-3">
							<span class="phone text-left"></span>
						</div>
					</div>
					<div class="form-group">
						<label class="col-xs-2">注册地址:</label>
						<div class="col-xs-8 ">
							<span class="address"></span>
						</div>
					</div>
					<div class="form-group">
						<label class="col-xs-2">营业执照:</label>
						<div class="col-xs-8">
							<a id="adtradingImgs" data-lightbox="tradingImg"><img id="tradingImgs" style="max-width: 80px;max-height: 80px;"></a>
						</div>
					</div>
					<div class="form-group">
						<label class="col-xs-2 ">审核备注:</label>
						<div class="col-xs-8 ">
							<span class="checkRemark"></span>
						</div>
					</div>
				</div>
			</form>
		</div><!-- /.modal-content -->
	</div><!-- /.modal -->
</div>

<script type="text/javascript">
    var p_check = <%=SessionUtil.hasPower(PowerConsts.AGENTMOUDULE_COMMON_CHECK)%>;
    var p_update = <%=SessionUtil.hasPower(PowerConsts.AGENTMOUDULE_COMMON_UPDATE)%>;
</script>
</body>
</html>
