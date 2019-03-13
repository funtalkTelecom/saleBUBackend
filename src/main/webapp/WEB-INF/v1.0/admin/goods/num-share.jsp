<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.hrtx.global.SessionUtil,com.hrtx.global.PowerConsts" %>
<%Long now = System.currentTimeMillis();%>
<!DOCTYPE html>
	<head>
		<title>号码推广计划</title>
		<%@ include file="../common/basecss.jsp" %>
		<script type="text/javascript" src="<%=basePath %>admin/js/num-share.js?<%=now%>"></script>
	</head>
	<style>
		.ace-icon
	</style>
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
							<li><a href="javascript:void(0);">商品管理</a></li>
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
											<label class="control-label">号码</label>
											<input type="text" class="form-control" style="width:130px;" name="num">
										</div>
										<div class="form-group" style="">
											<label class="control-label">状态</label>
											<select class="form-control" name="status">
												<option value="-1">请选择...</option>
												<option value="0">删除</option>
												<option value="1">草稿</option>
												<option value="2">有效</option>
												<option value="3">失效</option>
												<option value="4">过期</option>
											</select>
										</div>
										<c:if test="<%=SessionUtil.hasPower(PowerConsts.GOODSMOUDULE_COMMON_ADD)%>">
											<button type="button" class="btn btn-info btn-small fa fa-user-plus pull-right" data-toggle="modal" id="opengoodsInfo" data-target="#add-promotion-plan" >创建计划</button>
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
		<div class="modal fade" id="add-promotion-plan" tabindex="-1" style="overflow: auto" >
			<div class="modal-dialog" style="width:600px;">
				<div class="modal-content" style="width:600px; max-height:520px; overflow: auto">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true"> &times; </button>
						<h4 class="modal-title"> 推广计划 </h4>
					</div>
					<form role="form" class="form-horizontal" id="promotion-plan-form">
						<div class="modal-body">
							<div class="form-group">
								<label class="col-xs-2 control-label">参与方式</label>
								<div class="col-xs-4">
									<input type="hidden" class="form-control" name="id" id="promotion-plan-id">
									<input type="hidden" class="form-control" name="operation" id="operation-id">
									<select class="form-control" name="promotion" id="promotion-id">
										<option value="0">全部号码参与</option>
										<option value="1">销售价格段号码参与</option>
										<option value="2">指定号码参与</option>
									</select>
								</div>
								<label class="col-xs-2 control-label" >奖励方案</label>
								<div class="col-xs-4">
									<select class="form-control" name="awardWay" id="awardWay-id">
										<option value="-1">请选择...</option>
										<option value="1">固定金额</option>
										<option value="2">销售价比例</option>
									</select>
								</div>
							</div>
							<div class="form-group plan-price" style="display: none">
								<label class="col-xs-2 control-label">价格范围</label>
								<div class="col-xs-8">
									<div class="input-daterange input-group">
										<div class="input-group">
											<input type="text" class="form-control" name="beginPrice">
											<span class="input-group-addon">元</span>
										</div>
										<span class="input-group-addon"><i class="fa fa-exchange"></i></span>
										<div class="input-group">
											<input type="text" class="form-control" name="endPrice">
											<span class="input-group-addon">元</span>
										</div>
									</div>
								</div>
							</div>
							<div class="form-group plan-nums" style="display: none">
								<label class="col-xs-2 control-label">号码集合</label>
								<div class="col-xs-8">
									<textarea id="promotion-plan-num" class="form-control" name="nums" style="resize: none;"></textarea>
									<span style="color:red">注:输入多个号码时，每行一个号码</span>
								</div>
							</div>
							<div class="form-group">
								<label class="col-xs-2 control-label">奖励金额</label>
								<div class="col-xs-3" style="padding-right: 0px;">
									<div class="input-group">
										<input type="text" class="form-control" name="award">
										<span class="input-group-addon award-sign">元</span>
									</div>
								</div>
								<label class="col-xs-2 control-label" style="padding-left: 0px;width:14%;">奖励上限</label>
								<div class="col-xs-1" style="padding-left: 0px;padding-right: 0px;">
									<select class="form-control" name="isLimit"  id="isLimit-id">
										<option value="0">无</option>
										<option value="1">有</option>
									</select>
								</div>
								<label class="col-xs-2 control-label" style="padding-left: 0px;width:14%;">奖励限额</label>
								<div class="col-xs-2" style="padding-left: 0px;width:20.666666667%;">
									<div class="input-group">
										<input type="text" class="form-control" name="limitAward" id="limitAward-id">
										<span class="input-group-addon">元</span>
									</div>
								</div>
							</div>
							<div class="form-group">
								<label class="col-xs-2 control-label">生效时间</label>
								<div class="col-xs-8">
									<div class="input-daterange input-group">
										<input type="text" class="input-sm form-control" name="beginDate" onclick="WdatePicker({dateFmt : 'yyyy-MM-dd HH:mm:ss',startDate:'%y-%M-%d 00:00:00'});">
										<span class="input-group-addon"><i class="fa fa-exchange"></i></span>
										<input type="text" class="input-sm form-control" name="endDate" onclick="WdatePicker({dateFmt : 'yyyy-MM-dd HH:mm:ss',startDate:'%y-%M-%d 23:59:59'});">
									</div>
								</div>
							</div>
							<div class="form-group detail-info hidden">
								<label class="col-xs-2 control-label">添加人</label>
								<div class="col-xs-4">
									<input type="text" class="form-control" name="addUserStr">
								</div>
								<label class="col-xs-2 control-label" >添加时间</label>
								<div class="col-xs-4">
									<input type="text" class="form-control" name="addDate">
								</div>
							</div>
							<div class="form-group detail-info hidden">
								<label class="col-xs-2 control-label">更新人</label>
								<div class="col-xs-4">
									<input type="text" class="form-control" name="updateUserStr">
								</div>
								<label class="col-xs-2 control-label" >更新时间</label>
								<div class="col-xs-4">
									<input type="text" class="form-control" name="updateDate">
								</div>
							</div>
							<div class="form-group">
								<label class="col-xs-2 control-label" >温馨提示</label>
								<div class="col-xs-10">
									<div><small>当存在多个符合奖励的推广方案时，系统将按最精准优先奖励，参与方式的优先级按指定号码>价格段>全量号码进行奖励，但只会奖励其中一种。</small></div>
								</div>
							</div>
						</div>
						<div class="modal-footer center">
							<button type="button" class="btn  btn-default draft-promotion-plan">保存草稿</button>
							<button type="button" class="btn  btn-success publish-promotion-plan">发布推广</button>
						</div>
					</form>
				</div><!-- /.modal-content -->
			</div><!-- /.modal -->
		</div>
		<script type="text/javascript">
            var pp_edit = <%=SessionUtil.hasPower(PowerConsts.GOODSMOUDULE_COMMON_ADD)%>;
            $(function() {
			})
		</script>
	</body>
</html>
