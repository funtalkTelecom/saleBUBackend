<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="com.hrtx.global.SessionUtil" %>
<%@ page import="com.hrtx.global.PowerConsts" %>
<!DOCTYPE html>
	<head>
		<title>用户列表 - 靓号优选</title>
		<%@ include file="../common/basecss.jsp" %>
		<script type="text/javascript">
            var add_p ='<%=SessionUtil.hasPower(PowerConsts.SYSTEMMOUULE_USERLIST_ADD)%>';
		</script>
		<script type="text/javascript" src="<%=basePath %>admin/js/query-user.js"></script>
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
							<li><a href="javascript:void(0);">系统管理</a></li>
							<li class="active">用户列表</li>
						</ul><!-- /.breadcrumb -->
						<!-- /section:basics/content.searchbox -->
					</div>
					<div class="page-content">
						<div class="row">
							<div class="col-xs-12">
                                <div class="query" style="margin-bottom: -10px">
                                    <form class="form-inline pd5 " role="form">
                                        <div class="form-group">
                                            <label class="control-label">账号</label>
                                            <input type="text" class="form-control" style="width: 200px;" name="loginName">
                                        </div>
                                        <button type="button" class="btn btn-info btn-small fa fa-user-plus" id="query">查询</button>
                                        <button type="button" class="btn btn-info btn-small fa fa-user-plus" id="reset">重置</button>
                                        <c:if test="<%=SessionUtil.hasPower(PowerConsts.SYSTEMMOUULE_USERLIST_ADD)%>">
                                            <button type="button" class="btn btn-info btn-small fa fa-user-plus pull-right" data-toggle="modal" data-target="#myModal">添加</button>
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

			<!-- 模态框（Modal） -->
			<div class="modal fade" id="myModal" tabindex="-1">
				<div class="modal-dialog" style="width: 750px;">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal" aria-hidden="true">
								&times;
							</button>
							<h4 class="modal-title" id="myModalLabel">
								用户添加
							</h4>
						</div>
						<form role="form" class="form-horizontal">
							<div class="modal-body">
								<div class="form-group">
									<input type="hidden" class="form-control" name="id">
									<label class="col-xs-2 col-sm-2 control-label">登录帐号</label>
									<div class="col-xs-4 col-sm-4">
										<input type="text" class="form-control loginName" name="loginName" placeholder="请输入登录帐号">
									</div>
									<label class="col-xs-2 col-sm-2 control-label">用户名</label>
									<div class="col-xs-4 col-sm-4">
										<input type="text" class="form-control name" name="name" placeholder="请输入用户名">
									</div>
								</div>
								<div class="form-group">
									<label class="col-xs-2 col-sm-2 control-label">公司名称</label>
									<div class="col-xs-4 col-sm-4">
                                        <select name="corpId" class="form-control">
                                            <option value="0">请选择</option>
                                            <c:forEach items="${corps}" varStatus="i" var="item">
                                                <option value="${item.id}">${item.name}</option>
                                            </c:forEach>
                                        </select>
									</div>
									<label class="col-xs-3 col-sm-2 control-label">电话</label>
									<div class="col-xs-3 col-sm-4">
										<input type="text" class="form-control" name="phone">
									</div>
								</div>
								<div class="form-group">
									<label class="col-xs-3 col-sm-2 control-label">角色</label>
									<div class="col-xs-9 col-sm-10">
										<c:forEach items="${roles}" varStatus="i" var="item" >
											<label class="checkbox-inline">
												<input type="checkbox" name="roles"  class="pmid${item.id}" value="${item.id}">${item.name}
											</label>
										</c:forEach>
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

			<div class="footer">
				<div class="footer-inner">
					<!-- #section:basics/footer -->
					<jsp:include page="../common/footer.jsp"/>
					<!-- /section:basics/footer -->
				</div>
			</div>
			<a href="#" id="btn-scroll-up" class="btn-scroll-up btn btn-sm btn-inverse"><i class="ace-icon fa fa-angle-double-up icon-only bigger-110"></i></a>
		</div><!-- /.main-container -->
	</body>
</html>
