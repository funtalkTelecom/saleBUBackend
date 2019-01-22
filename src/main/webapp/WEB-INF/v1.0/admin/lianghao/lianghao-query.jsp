<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.hrtx.global.SessionUtil,com.hrtx.global.PowerConsts" %>
<!DOCTYPE html>
	<head>
		<title>客服靓号</title>
		<%@ include file="../common/basecss.jsp" %>
		<script type="text/javascript" src="<%=basePath %>admin/js/lianghao-query.js"></script>
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
							<li class="active"></li>
						</ul><!-- /.breadcrumb -->
						<!-- /section:basics/content.searchbox -->
					</div>
					
					<div class="page-content">
						<div class="row">
							<div class="col-xs-12">
								<div class="query" style="margin-bottom: -10px">
									<form class="form-inline pd5" role="form">
										<div class="form-group" style="width: 100%">
											<div class="form-group" id="qnumberTags" style="width: 100%">
											</div>
										</div>
										<div class="form-group" style="">
											<label class="control-label">号码</label>
											<input type="text" class="form-control" style="width:130px;" name="resource">
										</div>
										<div class="form-group" style="">
											<label class="control-label">起止价格</label>
											<input type="number" class="form-control" style="width:100px;" name="sPrice">
											-
											<input type="number" class="form-control" style="width:100px;" name="ePrice">
										</div>
										<div class="form-group" style="">
											<label class="control-label">运营商</label>
											<select name="netType" class="form-control" id="netType">
												<option value="-1">请选择</option>
												<option value="1">电信</option>
												<option value="2">联通</option>
												<option value="3">移动</option>
											</select>
										</div>
										<button type="button" class="btn btn-info btn-small fa fa-user-plus pull-right" id="query">查询</button>
										<button type="button" class="btn btn-info btn-small fa fa-user-plus pull-right" id="reset">重置</button>
                                        <c:if test="<%=SessionUtil.hasPower(PowerConsts.LIANGHAOMOUDULE_COMMON_ADD)%>">
                                            <button type="button" class="btn btn-info btn-small fa fa-user-plus pull-right" data-toggle="modal" data-target="#batch-add" >批量下单</button>
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
        <div class="modal fade" id="batch-add" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                            &times;
                        </button>
                        <h4 class="modal-title">
                            批量下单
                        </h4>
                    </div>
                    <form role="form" class="form-inline" enctype="multipart/form-data">
						<input name="maskId" type="hidden" value="batch-add">
						<input type="hidden" name="mask" value="导入中...">
                        <div class="modal-body">
                            <div class="form-group">
                                <label class="sr-only">文件输入</label>
                                <input type="file" name="file" id="file" accept="application/vnd.ms-excel">
                                <p class="help-block"><a href="lianghao/download-batch-add-template">下载模板</a></p>
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
            var lh_freeze = <%=SessionUtil.hasPower(PowerConsts.LIANGHAOMOUDULE_COMMON_FREEZE)%>;
            var lh_add = <%=SessionUtil.hasPower(PowerConsts.LIANGHAOMOUDULE_COMMON_ADD)%>;
            var userId = <%=SessionUtil.getUserId()%>;
		</script>
	</body>
</html>
