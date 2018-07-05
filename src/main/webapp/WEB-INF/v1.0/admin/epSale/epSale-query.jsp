<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.hrtx.global.SessionUtil,com.hrtx.global.PowerConsts" %>
<!DOCTYPE html>
	<head>
		<title>竟拍活动管理</title>
		<%@ include file="../common/basecss.jsp" %>
		<%--<script type="text/javascript" src="<%=basePath %>query-meal.js"></script>--%>
		<script type="text/javascript" src="<%=basePath %>admin/js/epSale-query.js"></script>
		<link rel="stylesheet" href="<%=basePath %>project/js/kindeditor/themes/default/default.css" type="text/css">
		<script type="text/javascript" src="<%=basePath %>project/js/kindeditor/kindeditor-all.js"></script>
        <%--<<script type="text/javascript" src="<%=basePath %>project/js/sockjs.min.js"></script>
        <script type="text/javascript" src="<%=basePath %>project/js/stomp.js"></script>--%>
	</head>
	<body class="no-skin" onload="disconnect()">
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
							<li><a href="javascript:void(0);">竟拍活动管理</a></li>
							<li class="active"></li>
						</ul><!-- /.breadcrumb -->
						<!-- /section:basics/content.searchbox -->
					</div>

					<div class="page-content">
						<div class="row">
							<div class="col-xs-12">
								<div class="query" style="margin-bottom: -10px">
									<form class="form-inline pd5 " role="form">
										<div class="form-group">
											<label class="control-label">标题</label>
											<input type="text" class="form-control" style="width: 200px;" name="title">
										</div>
										<div class="form-group  hidden">
											<label class="control-label">备注</label>
											<input type="text" class="form-control" style="width:130px;" name="remark">
										</div>
										<button type="button" class="btn btn-info btn-small fa fa-user-plus" id="query">查询</button>
										<button type="button" class="btn btn-info btn-small fa fa-user-plus" id="reset">重置</button>
										<c:if test="<%=SessionUtil.hasPower(PowerConsts.EPSALEMOUDULE_COMMON_ADD)%>">
											<button type="button" class="btn btn-info btn-small fa fa-user-plus pull-right" data-toggle="modal" data-target="#epSaleInfo" >添加</button>
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
                <!-- test   websocket  hidden  start-->
				<div id="websocket" class="hidden">
					<input id="text" type="text"/>
					<button onclick="send()">发送消息</button>
					<hr/>
					<button onclick="openWebSocket()">开启WebSocket连接</button>
					<button onclick="closeWebSocket()">关闭WebSocket连接</button>
					<hr/>
					<div id="message"></div>
				</div>
                <!-- test   websocket  hidden  end-->
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
		<div class="modal fade" id="epSaleInfo" tabindex="-1">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">
							&times;
						</button>
						<h4 class="modal-title">
							添加/修改竟拍活动
						</h4>
					</div>
					<form role="form" class="form-horizontal">
						<input name="id" type="hidden">
						<div class="modal-body">
							<div class="form-group">
								<label class="col-xs-2 control-label">标题</label>
								<div class="col-xs-10 col-sm-10">
									<input type="text" class="form-control" name="title" id="title" placeholder="请填写标题" required>
								</div>
							</div>
							<div class="form-group">
								<label class="col-xs-2 control-label">开始时间</label>
								<div class="col-xs-4">
                                    <input type="hidden" name="startTime" id="startTime">
                                    <input type="text" class="form-control" id="startTimePicker" onclick="WdatePicker({maxDate : '#F{$dp.$D(\'endTimePicker\',{s:-1})}',dateFmt : 'yyyy-MM-dd HH:mm:ss'});" readonly>
								</div>
								<label class="col-xs-2 control-label">结束时间</label>
								<div class="col-xs-4">
                                    <input type="hidden" name="endTime" id="endTime">
                                    <input type="text" class="form-control" id="endTimePicker" onclick="WdatePicker({minDate : '#F{$dp.$D(\'startTimePicker\',{s:1})}',dateFmt : 'yyyy-MM-dd HH:mm:ss'});" readonly>
								</div>
							</div>
							<div class="form-group">
								<label class="col-xs-2 control-label">最迟付款时间</label>
								<div class="col-xs-4">
									<input type="hidden" name="lastPayTime" id="lastPayTime">
                                    <input type="text" class="form-control" id="lastPayTimePicker" onclick="WdatePicker({minDate : '#F{$dp.$D(\'endTimePicker\',{s:1})}',dateFmt : 'yyyy-MM-dd HH:mm:ss'});" readonly>
								</div>
								<label class="col-xs-2 control-label hidden">显示条件</label>
								<div class="col-xs-2 hidden">
								   <select  name="isShow">
									   <option value="-1">请选择...</option>
									   <option value="1">是</option>
									   <option value="0">否</option>
								   </select>
								</div>
							</div>
						<div class="form-group">
							<label class="col-xs-2 control-label">竟拍规则</label>
							<div id="kindeditor" style="overflow: auto;" class="col-xs-10">
								<textarea  name="epRule" style="width:100%;height:200px;visibility:hidden;"></textarea>
							</div>
						</div>

						<h3 class="header smaller lighter blue">图片上传</h3>
						<div id="picUpload">
							<div class="form-group">
								<div class="col-xs-8">
									<div class="control-group">
									</div>
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
		<script type="text/javascript">
            var basePath = "<%=basePath %>";
            var p_edit = <%=SessionUtil.hasPower(PowerConsts.EPSALEMOUDULE_COMMON_EDIT)%>;
            var p_delete= <%=SessionUtil.hasPower(PowerConsts.EPSALEMOUDULE_COMMON_DELETE)%>;

            //**************************websocket**************start****************
           /***
            var websocket = null;
            //判断当前浏览器是否支持WebSocket
            if ('WebSocket' in window) {
                websocket = new WebSocket("ws://localhost:8091/websocket/989303317700030649/1013001752720441344");
            }
            else {
                alert('当前浏览器 Not support websocket')
            }
            websocket.onopen= function () {
                setMessageInnerHTML('开启WebSocket连接');
            }

            //连接发生错误的回调方法
            websocket.onerror = function () {
                setMessageInnerHTML("WebSocket连接发生错误");
            };

            //接收到消息的回调方法
            websocket.onmessage = function (event) {
                setMessageInnerHTML(event.data);
            }

            //将消息显示在网页上
            function setMessageInnerHTML(innerHTML) {
                document.getElementById('message').innerHTML += innerHTML + '<br/>';
            }

            //开启WebSocket连接
            function openWebSocket() {
                websocket.onopen= function () {
                    setMessageInnerHTML('开启WebSocket连接');
                }
            }

            //关闭WebSocket连接
            function closeWebSocket() {
                websocket.close();
                setMessageInnerHTML('关闭WebSocket连接');
            }

            //发送消息
            function send() {
                var message = document.getElementById('text').value;
                websocket.send(message);
            }
           ****/
            //**************************websocket**************end****************
		</script>
	</body>
</html>
