<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.hrtx.global.SessionUtil,com.hrtx.global.PowerConsts" %>
<!DOCTYPE html>
<head>
	<title>商家结算</title>
	<%@ include file="../common/basecss.jsp" %>
	<%--<script type="text/javascript" src="<%=basePath %>query-meal.js"></script>--%>
	<script type="text/javascript" src="<%=basePath %>admin/js/corp-settle.js"></script>
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
					<div class="col-xs-12 col-sm-6">
						<div class="widget-box transparent">
							<div class="widget-header widget-header-small header-color-blue2">
								<h4 class="widget-title smaller">
									<i class="ace-icon fa fa-lightbulb-o bigger-120"></i>
									基本信息
								</h4>
							</div>

							<div class="widget-body">
								<div class="widget-main padding-16">
									<div class="clearfix">
										<div class="grid2 center">
											<div class="easy-pie-chart percentage">
												<span class="percent balanceStr">0</span>元
											</div>
											<div class="space-2"></div>可用余额
										</div>

										<div class="grid2 center">
											<div class="easy-pie-chart percentage">
												<button class="btn btn-info btn-small financeWithdraw">提现</button>
											</div>
										</div>
									</div>
									<div class="hr hr-16"></div>
									<div class="clearfix">
										<div class="grid3 center">
											<div class="easy-pie-chart percentage">
												<span class="percent waitBalance">0</span>元
											</div>
											<div class="space-2"></div>待签收款
										</div>
										<div class="grid3 center">
											<div class="easy-pie-chart percentage">
												<span class="percent waitActiveBalance">0</span>元
											</div>
											<div class="space-2"></div>待结算活动款
										</div>
										<div class="grid3 center">
											<div class="easy-pie-chart percentage">
												<span class="percent waitBusiBalance">0</span>元
											</div>
											<div class="space-2"></div>待结算运营款
										</div>
									</div>
									<div class="hr hr-16"></div>
									<div class="clearfix">
										<div class="grid3 center">
											<div class="easy-pie-chart percentage">
												<span class="percent signBalance">0</span>元
											</div>
											<div class="space-2"></div>已签收款
										</div>
										<div class="grid3 center">
											<div class="easy-pie-chart percentage">
												<span class="percent hasActivebalance">0</span>元
											</div>
											<div class="space-2"></div>已结算活动款
										</div>
										<div class="grid3 center">
											<div class="easy-pie-chart percentage">
												<span class="percent hasBusibalance">0</span>元
											</div>
											<div class="space-2"></div>已结算运营款
										</div>
									</div>
									<div class="hr hr-16"></div>
								</div>
							</div>
						</div>
					</div>

					<div class="col-xs-12 col-sm-6">
						<div class="widget-box transparent">
							<div class="widget-header widget-header-small">
								<h4 class="widget-title smaller">
									<i class="ace-icon fa fa-bar-chart-o bigger-110"></i>
									收支明细
								</h4>
							</div>

							<div class="widget-body">
								<div class="widget-main">
									<div class="query" style="margin-bottom: -10px">
										<form class="form-inline pd5" role="form">
											<div class="form-group">
												<label class="control-label">时间</label>
												<div class="input-daterange input-group">
													<input type="text" class="input-sm form-control" name="startDate" onclick="WdatePicker({dateFmt : 'yyyy-MM-dd',startDate:'%y-%M-%d'});">
													<span class="input-group-addon"><i class="fa fa-exchange"></i></span>
													<input type="text" class="input-sm form-control" name="endDate"  onclick="WdatePicker({dateFmt : 'yyyy-MM-dd',startDate:'%y-%M-%d'});">
												</div>
											</div>
											<%--<button type="button" class="btn btn-info btn-small fa fa-user-plus pull-right" id="excel">导出</button>--%>
											<button type="button" class="btn btn-info btn-small fa fa-user-plus pull-right" id="query">查询</button>
										</form><!-- /form-panel -->
									</div>

									<h6 class="header smaller lighter blue"></h6>
									<div id="result"></div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div><!-- /#home -->
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
<div class="modal fade" id="financeWithdraw" tabindex="-1">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title">提现</h4>
			</div>
			<form role="form" class="form-horizontal">
				<input name="id" type="hidden">
				<div class="modal-body">
					<div class="form-group">
						<label class="col-xs-2 control-label">提现到</label>
						<div class="col-xs-6">
							<select class="form-control" name="accountId">
								<option value="-1">请选择...</option>
							</select>
						</div>
					</div>
					<div class="form-group">
						<label class="col-xs-2 control-label">当前余额</label>
						<div class="col-xs-6">
							<input type="hidden" name="canWithDrawAmt" value="0">
							<div style="padding: 6px 8px;"><span class="canuser-balance" style="color: red;font-weight: bold;">1000.00</span><span>&nbsp;&nbsp;元</span></div>
						</div>
					</div>
					<div class="form-group">
						<label class="col-xs-2 control-label">提现金额</label>
						<div class="col-xs-6">
							<div class="input-group">
								<input type="text" class="form-control " name="withDrawAmt" placeholder="请输入提现金额">
								<span class="input-group-btn">
									<button type="button" class="btn btn-link btn-sm all-with-draw">全部提现</button>
								</span>
							</div>
						</div>

					</div>
					<div class="form-group">
						<label class="col-xs-2 control-label">验证码</label>
						<div class="col-xs-6">
							<div class="input-group">
								<input type="text" class="form-control search-query" name="smsCode" placeholder="请输入短信验证码">
								<span class="input-group-btn">
									<button type="button" class="btn btn-default send-sms" style="border: 0px">获取验证码</button>
								</span>
							</div>
						</div>
					</div>
				</div>
				<div class="modal-footer center">
					<button type="button" class="btn btn-success btn-with-draw">确认提现</button>
				</div>
			</form>
		</div><!-- /.modal-content -->
	</div><!-- /.modal -->
</div>

<script type="text/javascript">

	$(function(){
        $.ajax({async:false,type : "get", url:"account/list-account", data:{}, success:function(data){
            var _datas=data;
			var html="<option value='-1'>请选择...</option>";
			for (var index = 0; index < _datas.length; index++) {
			    var _obj=_datas[index];
			    var val_str="";
			    if(_obj["accountType"]==2)val_str="昵称["+_obj["bankAccount"]+"]的微信余额";
			    if(_obj["accountType"]==1)val_str="["+_obj["bankAccount"]+"]"+_obj["cardBankName"]+" 卡号["+_obj["cardAccountHidden"]+"]";
				html+="<option value='"+_obj["id"]+"'>"+val_str+"</option>";
			}
            $("select[name='accountId']").html(html);
		}});

        function loadCorpData(){
            $.ajax({async:false,type : "get", url:"corp/count-order-data", data:{}, success:function(data){
				$("input[name='canWithDrawAmt']").val(data.data.balance);
				$(".canuser-balance").html(data.data.balanceStr);

				$(".balanceStr").html(data.data.balanceStr);
				$(".waitBalance").html(data.data.waitBalance);
				$(".signBalance").html(data.data.signBalance);
				$(".waitActiveBalance").html(data.data.waitActiveBalance);
				$(".hasActivebalance").html(data.data.hasActivebalance);
				$(".waitBusiBalance").html(data.data.waitBusiBalance);
				$(".hasBusibalance").html(data.data.hasBusibalance);
			}});
		}

        loadCorpData();

	    $(".financeWithdraw").click(function () {
            $(".btn-with-draw").html("确认提现");
            $(".btn-with-draw").removeAttr("disabled");
            $('#financeWithdraw').modal('show');
        });
        $(".send-sms").click(function () {
            $.post("sms/ack-corp",{},function(data){
                alert(data.data);
            },"json");
        });
        $(".all-with-draw").click(function () {
            $("input[name='withDrawAmt']").val($("input[name='canWithDrawAmt']").val());
        });
        $(".btn-with-draw").click(function () {
            var _bool=confirm("确认提现？");
            if(!_bool)return;
            var accountId=$("select[name='accountId']").val();
            var withDrawAmt=$("input[name='withDrawAmt']").val();
            var smsCode=$("input[name='smsCode']").val();
            $.post("corp/finance-withdraw",{"accountId":accountId,withDrawAmt:withDrawAmt,smsCode:smsCode,t:new Date().getTime(),noRepeat:1},function(data){
                alert(data.data);
                if(data.code==200){
                    loadCorpData();
                    dataList.reload();
                }
            },"json");
        });
	})

</script>
</body>
</html>
