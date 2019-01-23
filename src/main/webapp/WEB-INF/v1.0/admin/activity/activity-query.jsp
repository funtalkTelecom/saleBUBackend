<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.hrtx.global.SessionUtil,com.hrtx.global.PowerConsts" %>
<!DOCTYPE html>
	<head>
		<title>活动管理</title>
		<%@ include file="../common/basecss.jsp" %>
		<%--<script type="text/javascript" src="<%=basePath %>query-meal.js"></script>--%>
		<script type="text/javascript" src="<%=basePath %>admin/js/activity-query.js"></script>
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
							<li><a href="javascript:void(0);">活动管理</a></li>
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

										<button type="button" class="btn btn-info btn-small fa fa-user-plus" id="query">查询</button>
										<button type="button" class="btn btn-info btn-small fa fa-user-plus" id="reset">重置</button>
										<c:if test="<%=SessionUtil.hasPower(PowerConsts.ACTIVITYMOUDULE_COMMON_ADD)%>">
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
						<button type="button" class="close" id="close">
							&times;
						</button>
						<h4 class="modal-title">
							添加/修改活动
						</h4>
					</div>
					<form role="form" class="form-horizontal">
						<input name="id" type="hidden">
						<input type="hidden" id="strjson" name="strjson" >
						<div class="modal-body">
							<div class="form-group">
								<label class="col-xs-2 control-label">标题</label>
								<div class="col-xs-10 col-sm-10">
									<input type="text" class="form-control" name="title" id="title" placeholder="请填写标题" required>
								</div>
							</div>
							<div class="form-group">
								<label class="col-xs-2 control-label">代理商</label>
								<div class="col-xs-4">
									<select id="agentId"  name="agentId" class="form-control">
										<option value="0">请选择</option>
										<c:forEach items="${agentList}" var="item">
											<option value="${item.id}">${item.name}</option>
										</c:forEach>
									</select>
								</div>
								<label class="col-xs-2 control-label">活动类型</label>
								<div class="col-xs-4">
									<select class="form-control" name="type" id="type">
										<option value="0">选择</option>
										<option value="1">秒杀</option>
									</select>
								</div>
							</div>
							<div class="form-group">
								<label class="col-xs-2 control-label">活动日期</label>
								<div class="col-xs-2 ">
									<input type="hidden" name="beginDates"  id="beginDates">
									<input class="form-control" name="gStartTime" id="gStartTime" style="width: 150px;" type="text" onclick="WdatePicker({maxDate : '#F{$dp.$D(\'gEndTime\',{s:-1})}',dateFmt : 'yyyy-MM-dd'});"/>
								</div>
								<div class="col-xs-2 ">
									<select  class="form-control" id="startH"  name="startH">
										<option value="-1">选择开始时间</option>
                                        <c:forEach items="${beginHList}" var="item">
                                            <option value="${item.keyId}" keyValue="${item.keyValue}">${item.keyValue}</option>
                                        </c:forEach>
									</select>
								</div>
								<label class="col-xs-1 control-label">到</label>
								<div class="col-xs-2">
									<input type="hidden" name="endDates"  id="endDates">
									<input class="form-control" name="gEndTime" id="gEndTime"  style="width: 150px;" type="text" onclick="WdatePicker({ minDate : '#F{$dp.$D(\'gStartTime\',{s:1})}',dateFmt : 'yyyy-MM-dd'});"/>
								</div>
								<div class="col-xs-2 ">
									<select  class="form-control" id="endH"  name="endH" >
										<option value="-1">选择结束时间</option>
                                        <c:forEach items="${endHList}" var="item" >
                                            <option value="${item.keyId}" keyValue="${item.keyValue}">${item.keyValue}</option>
                                        </c:forEach>
									</select>
								</div>
							</div>
							<h3 class="header smaller lighter blue"></h3>
							<div class="form-group" id="numDivs" >
								<label class="col-xs-2 control-label">活动号码</label>
								<div class="col-xs-6 ">
									<textarea id="saleNum" name="saleNum" style="width: 350px;height: 100px;" ></textarea>
								</div>
								<label class="col-xs-4 control-label">
									<button type="button" id="checkAdd" class="btn btn-success">添加</button>
								</label>
							</div>
							<div  id="NumsDiv" class="form-group">
								<div class="col-xs-12 ">
									<table class="table table-striped table-bordered table-hover" border="0" cellpadding="0" cellspacing="0">
										<thead><tr align="center">
											<td><strong>号码</strong></td>
											<td><strong>原价</strong></td>
											<td><strong>秒杀价</strong></td>
											<td><strong>操作</strong></td>
										</tr></thead>
										<tbody>

										</tbody>
									</table>
								</div>
							</div>
				   </div>
					<div class="modal-footer">
						<button type="button" class="btn btn-success">提交</button>
						<button type="button" class="btn btn-default" id="modColse">关闭</button>
					</div>
					</form>
				</div><!-- /.modal-content -->
			</div><!-- /.modal -->

		</div>
		<script type="text/javascript">
            var basePath = "<%=basePath %>";
            var p_edit = <%=SessionUtil.hasPower(PowerConsts.ACTIVITYMOUDULE_COMMON_ADD)%>;
            var p_cancel= <%=SessionUtil.hasPower(PowerConsts.ACTIVITYMOUDULE_COMMON_CANCEL)%>;

		</script>
	</body>
</html>
