<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.hrtx.global.SessionUtil,com.hrtx.global.PowerConsts" %>
<!DOCTYPE html>
	<head>
		<title>下单</title>
		<%@ include file="../common/basecss.jsp" %>
		<script type="text/javascript" src="<%=basePath %>admin/js/lianghao-add-order.js"></script>
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
							<li><a href="javascript:void(0);">客服靓号</a></li>
							<li class="active">下单</li>
						</ul><!-- /.breadcrumb -->
					</div>
					
					<div class="page-content">
						<div class="row">
							<form role="form" class="form-horizontal" id="form">
								<input name="id" type="hidden" value="${numPriceAgent.id}">
								<input name="mask" type="hidden" value="提交中...">
								<div class="modal-body">
									<div class="form-group">
										<label class="col-xs-2 control-label">手机号码</label>
										<div class="col-xs-4">
											<input type="text" class="form-control" name="phone" disabled="disabled" value="${numPriceAgent.resource}">
										</div>
										<label class="col-xs-2 control-label">套餐名称</label>
										<div class="col-xs-4">
											<select class="form-control" name="mealId">
												<option value="">请选择...</option>
												<c:forEach items="${mealList}" var="item">
													<option value="${item.mid}">${item.mealName}</option>
												</c:forEach>
											</select>
										</div>
									</div>
									<div class="form-group">
										<label class="col-xs-2 control-label">第三方订单号</label>
										<div class="col-xs-4">
											<input type="text" class="form-control" name="thirdOrder">
										</div>
										<label class="col-xs-2 control-label">BOSS开户工号</label>
										<div class="col-xs-4">
											<input type="text" class="form-control" name="bossNum" value="${bossNum}">
										</div>
									</div>
									<div class="form-group">
										<label class="col-xs-2 control-label">客户名称</label>
										<div class="col-xs-4">
											<input type="text" class="form-control" name="phoneConsumer">
										</div>
									</div>
									<div class="form-group">
										<label class="col-xs-2 control-label">客户证件类型</label>
										<div class="col-xs-4">
											<select class="form-control" name="phoneConsumerIdType">
												<c:forEach items="${types}" var="item">
													<option value="${item.keyId}">${item.keyValue}</option>
												</c:forEach>
											</select>
										</div>
										<label class="col-xs-2 control-label">客户证件编码</label>
										<div class="col-xs-4">
											<input type="text" class="form-control" name="phoneConsumerIdNum">
										</div>
									</div>
									<div class="form-group">
										<label class="col-xs-2 control-label">邮寄联系人</label>
										<div class="col-xs-4">
											<input type="text" class="form-control" name="personName">
										</div>
										<label class="col-xs-2 control-label">邮寄联系电话</label>
										<div class="col-xs-4">
											<input type="text" class="form-control" name="personTel">
										</div>
									</div>
									<div class="form-group">
										<label class="col-xs-2 control-label">邮寄地址</label>
										<div class="col-xs-10">
											<input type="text" class="form-control" name="address">
										</div>
									</div>
									<div class="form-group">
										<label class="col-xs-2 control-label">备注</label>
										<div class="col-xs-10">
											<textarea type="text" style="width: 468px" class="form-control input-xxlarge" name="conment"></textarea>
										</div>
									</div>
								</div>
								<div style="text-align: center">
									<button type="button" class="btn btn-success" id="addOrder">提交</button>
									<button type="button" class="btn btn-default" onclick="refresh('lianghao/lianghao-query')">关闭</button>
								</div>
							</form>
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

		<script type="text/javascript">
            var lh_freeze = <%=SessionUtil.hasPower(PowerConsts.LIANGHAOMOUDULE_COMMON_FREEZE)%>;
            var lh_add = <%=SessionUtil.hasPower(PowerConsts.LIANGHAOMOUDULE_COMMON_ADD)%>;
            var userId = <%=SessionUtil.getUserId()%>;
		</script>
	</body>
</html>
