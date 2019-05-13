<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.hrtx.global.SessionUtil,com.hrtx.global.PowerConsts" %>
<!DOCTYPE html>
	<head>
		<title>订单管理</title>
		<%@ include file="../common/basecss.jsp" %>
		<%--<script type="text/javascript" src="<%=basePath %>query-meal.js"></script>--%>
		<script type="text/javascript" src="<%=basePath %>admin/js/order-query.js"></script>

		<link rel="stylesheet" href="<%=basePath %>project/js/zTree_v3/css/zTreeStyle/zTreeStyle.css" type="text/css">
		<link rel="stylesheet" href="<%=basePath %>project/js/kindeditor/themes/default/default.css" type="text/css">

		<script type="text/javascript" src="<%=basePath %>project/js/zTree_v3/js/checkboxTree.js"></script>
		<script type="text/javascript" src="<%=basePath %>project/js/zTree_v3/js/jquery.ztree.core.js"></script>
		<script type="text/javascript" src="<%=basePath %>project/js/zTree_v3/js/jquery.ztree.excheck.js"></script>
		<script type="text/javascript" src="<%=basePath %>project/js/kindeditor/kindeditor-all.js"></script>
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
							<li><a href="javascript:void(0);">订单管理</a></li>
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
											<label class="control-label">订单编号</label>
											<input type="text" class="form-control" style="width:170px;" name="orderId">
										</div>
										<div class="form-group">
											<label class="control-label">第三方订单号</label>
											<input type="text" class="form-control" style="width:150px;" name="thirdOrder">
										</div>
										<div class="form-group">
											<label class="control-label">收货人</label>
											<input type="text" class="form-control" style="width:100px;" name="personName">
										</div>
										<div class="form-group">
											<label class="control-label">号码</label>
											<input type="text" class="form-control" style="width:130px;" name="num">
										</div>
										<div class="form-group">
											<label class="control-label">时间</label>
											<input type="text" class="form-control" id="startTime" name="startTime" value="" readonly/>
											-
											<input type="text" class="form-control" id="endTime" name="endTime" value="" readonly/>
										</div>
										<div class="form-group" style="">
											<label class="control-label">状态</label>
											<select class="form-control" name="qstatus" id="qstatus">
											</select>
										</div>
										<button type="button" class="btn btn-info btn-small fa fa-user-plus pull-right" id="query">查询</button>
										<button type="button" class="btn btn-info btn-small fa fa-user-plus pull-right" id="reset">重置</button>
										<c:if test="<%=SessionUtil.hasPower(PowerConsts.ORDERMOUDULE_COMMON_CHECK)%>">
											<button type="button" class="btn btn-info btn-small fa fa-user-plus pull-right" id="batch-check" >审核发货</button>
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
		<div class="modal fade" id="orderInfo" tabindex="-1" style="overflow: auto">
			<div class="modal-dialog" style="width:90%">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">
							&times;
						</button>
						<h4 class="modal-title">
							订单详情
						</h4>
					</div>
					<form role="form" class="form-horizontal" enctype="multipart/form-data">
						<div class="modal-body">
							<div class="form-group">
								<label class="col-xs-2 control-label">订单编号</label>
								<div class="col-xs-3">
									<input type="text" class="form-control" name="orderId" id="orderId">
								</div>
								<label class="col-xs-2 control-label">用户名称</label>
								<div class="col-xs-3">
									<input type="text" class="form-control" name="consumerName">
								</div>
							</div>
							<div class="form-group">
								<label class="col-xs-2 control-label">状态</label>
								<div class="col-xs-3">
									<input type="text" class="form-control" name="status">
								</div>
								<label class="col-xs-2 control-label">添加时间</label>
								<div class="col-xs-3">
									<input type="text" class="form-control" name="addDate">
								</div>
							</div>
							<div class="form-group">
								<label class="col-xs-2 control-label">收货人</label>
								<div class="col-xs-3">
									<input type="text" class="form-control" name="personName">
								</div>
								<label class="col-xs-2 control-label">收货电话</label>
								<div class="col-xs-3">
									<input type="text" class="form-control" name="personTel">
								</div>
							</div>
							<div class="form-group">
								<label class="col-xs-2 control-label">收货地址</label>
								<div class="col-xs-3">
									<input type="text" class="form-control" name="address">
								</div>
								<label class="col-xs-2 control-label">运输方式</label>
								<div class="col-xs-3">
									<input type="text" class="form-control" name="shippingMenthod">
								</div>
							</div>
							<div class="form-group">
								<label class="col-xs-2 control-label">快递公司</label>
								<div class="col-xs-3">
									<input type="text" class="form-control" name="expressName">
								</div>
								<label class="col-xs-2 control-label">快递单号</label>
								<div class="col-xs-3">
									<input type="text" class="form-control" name="expressNumber">
								</div>
							</div>
							<div class="form-group">
								<label class="col-xs-2 control-label">发货时间</label>
								<div class="col-xs-3">
									<input type="text" class="form-control" name="deliverDate">
								</div>
								<label class="col-xs-2 control-label">通知出货时间</label>
								<div class="col-xs-3">
									<input type="text" class="form-control" name="noticeShipmentDate">
								</div>
							</div>
							<div class="form-group">
								<label class="col-xs-2 control-label">支付方式</label>
								<div class="col-xs-3">
									<input type="text" class="form-control" name="payMenthod">
								</div>
								<label class="col-xs-2 control-label">付款日期</label>
								<div class="col-xs-3">
									<input type="text" class="form-control" name="payDate">
								</div>
							</div>
							<div class="form-group">
								<label class="col-xs-2 control-label">第三方单号</label>
								<div class="col-xs-3">
									<input type="text" class="form-control" name="thirdOrder">
								</div>
							</div>
							<div class="form-group">
								<label class="col-xs-2 control-label">签收方式</label>
								<div class="col-xs-3">
									<input type="text" class="form-control" name="signType">
								</div>
								<label class="col-xs-2 control-label">签收时间</label>
								<div class="col-xs-3">
									<input type="text" class="form-control" name="signDate">
								</div>
							</div>
							<div class="form-group">
								<label class="col-xs-2 control-label">优惠券</label>
								<div class="col-xs-3">
									<input type="text" class="form-control" name="commission">
								</div>
								<label class="col-xs-2 control-label">运输费用</label>
								<div class="col-xs-3">
									<input type="text" class="form-control" name="shippingTotal">
								</div>
							</div>
							<div class="form-group">
								<label class="col-xs-2 control-label">子项小计</label>
								<div class="col-xs-3">
									<input type="text" class="form-control" name="subTotal">
								</div>
								<label class="col-xs-2 control-label">合计</label>
								<div class="col-xs-3">
									<input type="text" class="form-control" name="total">
								</div>
							</div>
							<div class="form-group">
								<label class="col-xs-2 control-label">摘要</label>
								<div class="col-xs-10">
									<textarea type="text" style="width: 468px" class="form-control input-xxlarge" name="conment" ></textarea>
								</div>
							</div>
							<%--item列表--%>
							<h3 class="header smaller lighter blue">列表</h3>
							<div id="itemResult" style="overflow: auto;">
							</div>
						</div>
						<div class="modal-footer">
							<%--<button type="button" class="btn btn-success">提交</button>--%>
							<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
						</div>
					</form>
				</div><!-- /.modal-content -->
			</div><!-- /.modal -->
		</div>

		<!-- 模态框（Modal） -->
		<div class="modal fade" id="receiptInfo" tabindex="-1" style="overflow: auto" >
			<div class="modal-dialog" style="width:700px;">
				<div class="modal-content" style="width: 700px; max-height:300px; overflow: auto">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">
							&times;
						</button>
						<h4 class="modal-title">
							收款信息
						</h4>
					</div>
					<form role="form" class="form-horizontal">
						<div class="modal-body">
							<div class="form-group">
								<input type="hidden" name="orderId" id="receiptInfo-orderId">
								<input type="hidden" name="orderType" id="receiptInfo-orderType">
								<label class="col-xs-2 control-label">付款账号</label>
								<div class="col-xs-3">
									<input type="text" class="form-control" name="payAccount">
								</div>
								<label class="col-xs-2 control-label">收款账号</label>
								<div class="col-xs-3">
									<input type="text" class="form-control" name="receivableAccount">
								</div>
							</div>

							<div class="form-group">
								<label class="col-xs-2 control-label">应收</label>
								<div class="col-xs-3">
									<input type="text" class="form-control" name="receivable" id="receivable" disabled="disabled">
								</div>
								<label class="col-xs-2 control-label">实收</label>
								<div class="col-xs-3">
									<input type="text" class="form-control" name="receipts" id="receipts">
								</div>
							</div>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-success">确定</button>
							<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
						</div>
					</form>
				</div><!-- /.modal-content -->
			</div><!-- /.modal -->
		</div>

		<!-- 模态框（Modal） -->
		<div class="modal fade" id="adjustInfo" tabindex="-1" style="overflow: auto" >
			<div class="modal-dialog" style="width:700px;">
				<div class="modal-content" style="width: 700px; max-height:300px; overflow: auto">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">
							&times;
						</button>
						<h4 class="modal-title">
							调价
						</h4>
					</div>
					<form role="form" class="form-horizontal">
						<div class="modal-body">
							<div class="form-group">
								<input type="hidden" name="orderId">
								<label class="col-xs-2 control-label">订单号</label>
								<div class="col-xs-3">
									<input type="text" class="form-control" disabled="disabled" name="orderId1">
								</div>
								<label class="col-xs-2 control-label">订单金额</label>
								<div class="col-xs-3">
									<input type="text" class="form-control" disabled="disabled" name="total">
								</div>
							</div>

							<div class="form-group">
								<label class="col-xs-2 control-label">调价金额</label>
								<div class="col-xs-3">
									<input type="text" class="form-control" name="adjustPrice">
								</div>
							</div>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-success">确定</button>
							<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
						</div>
					</form>
				</div><!-- /.modal-content -->
			</div><!-- /.modal -->
		</div>
		<!-- 模态框（Modal） -->
		<div class="modal fade" id="refundInfo" tabindex="-1" style="overflow: auto" >
			<div class="modal-dialog" style="width:700px;">
				<div class="modal-content" style="width: 700px; max-height:300px; overflow: auto">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">
							&times;
						</button>
						<h4 class="modal-title">
							退款信息
						</h4>
					</div>
					<form role="form" class="form-horizontal">
						<div class="modal-body">
							<div class="form-group">
								<input type="hidden" name="orderId" id="refund-orderId">
								<label class="col-xs-2 control-label">退款备注</label>
								<div class="col-xs-8">
									<textarea  id="refunReason" name="refunReason" style="width: 550px;height: 100px;"></textarea>
								</div>

							</div>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-success">确定</button>
							<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
						</div>
					</form>
				</div><!-- /.modal-content -->
			</div><!-- /.modal -->
		</div>
		<!-- 模态框（Modal） -->
		<div class="modal fade" id="cancelOrder" tabindex="-1" style="overflow: auto" >
			<div class="modal-dialog">
				<div class="modal-content" >
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">
							&times;
						</button>
						<h4 class="modal-title">
							取消订单
						</h4>
					</div>
					<form role="form" class="form-horizontal">
						<div class="modal-body">
							<div class="form-group">
								<div class="control-group">
									<input type="hidden" name="orderId" id="cancelOrderId">
									<label class="control-label bolder blue" style="display: block;text-align: center;">选择取消订单的原因</label>
									<div class="radio">
										<label>
											<input name="reason" type="radio" class="ace" reason="操作错误(商品、地址选错)" value="1">
											<span class="lbl"> 1、操作错误(商品、地址选错)</span>
										</label>
									</div>
									<div class="radio">
										<label>
											<input name="reason" type="radio" class="ace" reason="其他渠道有更低的价格" value="2">
											<span class="lbl"> 2、其他渠道有更低的价格</span>
										</label>
									</div>
									<div class="radio">
										<label>
											<input name="reason" type="radio" class="ace" reason="暂时不想买了" value="3">
											<span class="lbl"> 3、暂时不想买了</span>
										</label>
									</div>
									<div class="radio">
										<label>
											<input name="reason" type="radio" class="ace" reason="重复下单或者误下单" value="4">
											<span class="lbl"> 4、重复下单或者误下单</span>
										</label>
									</div>
									<div class="radio">
										<label>
											<input name="reason" type="radio" class="ace" reason="其他原因" value="5">
											<span class="lbl"> 5、其他原因</span>
										</label>
									</div>
									<textarea  id="reason" class="form-control input-xxlarge" style="display: none;width: 500px;height: 100px;margin-left: 40px;" ></textarea>
								</div>
							</div>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-success">确定</button>
							<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
						</div>
					</form>
				</div><!-- /.modal-content -->
			</div><!-- /.modal -->
		</div>

		<!-- 模态框（Modal） -->
		<div class="modal fade" id="check-modal" tabindex="-1" style="overflow: auto" >
			<div class="modal-dialog" style="width:700px;">
				<div class="modal-content" style="width: 700px; max-height:300px; overflow: auto">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">
							&times;
						</button>
						<h4 class="modal-title">
							审核发货
						</h4>
					</div>
					<form role="form" class="form-horizontal">
						<input type="hidden" name="temp">
						<input name="maskId" type="hidden" value="check-modal">
						<input type="hidden" name="mask" value="提交中...">
						<div class="modal-body">
							<div class="form-group">
								<label  class="col-xs-2 control-label">操作<i style="color:red">*</i></label>
								<div class="col-xs-8">
									<select class="form-control" name="status" >
										<option value="-1">请选择...</option>
										<option value="2">审核通过</option>
										<option value="7">审核不通过</option>
									</select>
								</div>
							</div>
							<div class="form-group" id="othernote">
								<label  class="col-xs-2 control-label">备注<i style="color:red">*</i></label>
								<div class="col-xs-8">
									<input type="text" class="form-control" name="checkConment" placeholder="请输入备注">
								</div>
							</div>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-success">确定</button>
							<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
						</div>
					</form>
				</div><!-- /.modal-content -->
			</div><!-- /.modal -->
		</div>

		<script type="text/javascript">
            var basePath = "<%=basePath %>";
            var p_query = <%=SessionUtil.hasPower(PowerConsts.ORDERMOUDULE_COMMON_QUEYR)%>;
            var p_receipt = <%=SessionUtil.hasPower(PowerConsts.ORDERMOUDULE_COMMON_RECEIPT)%>;
            var p_bindCard = <%=SessionUtil.hasPower(PowerConsts.ORDERMOUDULE_COMMON_BINDCARD)%>;
            var p_refund = <%=SessionUtil.hasPower(PowerConsts.ORDERMOUDULE_COMMON_REFUND)%>;
            var p_refund_live = <%=SessionUtil.hasPower(PowerConsts.ORDERMOUDULE_COMMON_REFUND_LIVE)%>;
            var p_check = <%=SessionUtil.hasPower(PowerConsts.ORDERMOUDULE_COMMON_CHECK)%>;
            var p_cancel = <%=SessionUtil.hasPower(PowerConsts.ORDERMOUDULE_COMMON_CANCEL)%>;
            var p_adjust = <%=SessionUtil.hasPower(PowerConsts.ORDERMOUDULE_COMMON_ADJUST)%>;
		</script>
	</body>
</html>
