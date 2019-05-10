<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.hrtx.global.SessionUtil,com.hrtx.global.PowerConsts" %>
<!DOCTYPE html>
<head>
	<title>收款账号管理</title>
	<%@ include file="../common/basecss.jsp" %>
	<%--<script type="text/javascript" src="<%=basePath %>query-meal.js"></script>--%>
	<script type="text/javascript" src="<%=basePath %>admin/js/query-account.js"></script>
	<script type="text/javascript" src="<%=basePath %>js/sha1.js"></script>
	<style>
		#accountInfo .wx-info .opacity1{opacity:1;}
		#accountInfo .wx-info .fa-check-square-o{font-size: 24px;}
		#accountInfo .wx-info .opacity1 .fa-check-square-o{color: #00B83F;}
		.wx-info,.bank-info{display: none}
	</style>
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
					<li><a href="javascript:void(0);">收款账号管理</a></li>
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
									<label class="control-label">户名</label>
									<input type="text" class="form-control" style="width:130px;" name="bankAccount">
								</div>
								<div class="form-group">
									<label class="control-label">账号</label>
									<input type="text" class="form-control" style="width:130px;" name="cardAccount">
								</div>
								<c:if test="<%=SessionUtil.hasPower(PowerConsts.ACCOUNTMOUDULE_COMMON_EDIT)%>">
									<button type="button" class="btn btn-info btn-small fa fa-user-plus pull-right" data-toggle="modal" data-target="#accountInfo" >添加</button>
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
<div class="modal fade" id="accountInfo" tabindex="-1">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">
					&times;
				</button>
				<h4 class="modal-title">
					添加/修改收款账号
				</h4>
			</div>
			<form role="form" class="form-horizontal">
				<input name="id" type="hidden">
				<div class="modal-body">
					<div class="form-group">
						<label class="col-xs-2 control-label">账户类型</label>
						<div class="col-xs-4">
							<select class="form-control" name="accountType">
								<option value="-1">请选择...</option>
								<option value="1">银行账户</option>
								<option value="2">微信账户</option>
							</select>
						</div>
					</div>
					<div class="form-group wx-info">
						<label class="col-xs-2 control-label">微信昵称</label>
						<div class="col-xs-4">
							<div class="input-group">
								<input type="text" class="form-control search-nick" placeholder="昵称关键字">
								<span class="input-group-btn">
									<button type="button" class="btn btn-default btn-sm nick-query">查询</button>
								</span>
							</div>
						</div>
						<div class="col-xs-6">
							<span class="help-inline">
								<span class="middle">您的微信需要先关注"靓号优选"小程序并授权允许获得您的微信昵称信息方可查询昵称</span>
							</span>
						</div>
					</div>
					<div class="form-group wx-info">
						<label class="col-xs-2 control-label">&nbsp;</label>
						<input type="hidden" name="consumer_id" value="0">
						<div class="col-xs-6">
							<ul class="ace-thumbnails clearfix">
								<li class="hidden">
									<div data-rel="colorbox" class="cboxElement">
										<img width="80" height="80" src="default-user.png">
										<div class="tags">
											<span class="label-holder">
												<span class="label label-info arrowed">乐山啦</span>
											</span>
										</div>
										<div class="text opacity1">
											<div class="inner">
												<i class="ace-icon fa fa-check-square-o" ></i>
											</div>
										</div>
									</div>
								</li>
							</ul>
						</div>
					</div>
					<div class="form-group bank-info">
						<label class="col-xs-2 control-label">银行</label>
						<div class="col-xs-4">
							<select class="form-control" name="cardBank" id="cardBankP">
								<option value="-1">请选择...</option>
							</select>
                            <input type="hidden" class="form-control" name="cardBankName">
						</div>
						<label class="col-xs-2 control-label">支行</label>
						<div class="col-xs-4">
							<input type="text" class="form-control" name="subbranchBank">
						</div>
					</div>
					<div class="form-group bank-info">
						<label class="col-xs-2 control-label">户名</label>
						<div class="col-xs-4">
							<input type="text" class="form-control input-xxlarge" name="bankAccount">
						</div>
						<label class="col-xs-2 control-label">账号</label>
						<div class="col-xs-4">
							<input type="text" class="form-control input-xxlarge" name="cardAccount">
						</div>
					</div>
					<div class="form-group">
						<label class="col-xs-2 control-label">验证码</label>
						<div class="col-xs-4">
							<div class="input-group">
								<input type="text" class="form-control" name="sms_code" placeholder="短信验证码">
								<span class="input-group-btn">
									<button type="button" class="btn btn-info btn-sm send-sms">获取验证码</button>
								</span>
							</div>
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

<li class="hidden li_demo">
<div data-rel="colorbox" class="cboxElement">
	<img width="80" height="80" src="default-user.png">
	<div class="tags">
		<span class="label-holder">
			<span class="label label-info arrowed wx-nick">乐山啦</span>
		</span>
	</div>
	<div class="text">
		<div class="inner">
			<i class="ace-icon fa fa-check-square-o" ></i>
		</div>
	</div>
</div>
</li>
<script type="text/javascript">
    var p_edit = <%=SessionUtil.hasPower(PowerConsts.ACCOUNTMOUDULE_COMMON_EDIT)%>;
    var p_delete = <%=SessionUtil.hasPower(PowerConsts.ACCOUNTMOUDULE_COMMON_DELETE)%>;
</script>
</body>
</html>
