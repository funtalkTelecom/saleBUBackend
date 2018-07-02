<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.hrtx.global.SessionUtil,com.hrtx.global.PowerConsts" %>
<!DOCTYPE html>
	<head>
		<title>号码管理</title>
		<%@ include file="../common/basecss.jsp" %>
		<%--<script type="text/javascript" src="<%=basePath %>query-meal.js"></script>--%>
        <link rel="stylesheet" href="<%=basePath %>project/js/zTree_v3/css/zTreeStyle/zTreeStyle.css" type="text/css">
		<script type="text/javascript" src="<%=basePath %>admin/js/number-query.js"></script>

        <script type="text/javascript" src="<%=basePath %>project/js/zTree_v3/js/checkboxTree.js"></script>
        <script type="text/javascript" src="<%=basePath %>project/js/zTree_v3/js/jquery.ztree.core.js"></script>
        <script type="text/javascript" src="<%=basePath %>project/js/zTree_v3/js/jquery.ztree.excheck.js"></script>
        <script type="text/javascript" src="<%=basePath %>project/js/chosen.jquery.min.js"></script>
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
							<li><a href="javascript:void(0);">号码管理</a></li>
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
											<input type="text" class="form-control" style="width:130px;" name="numbers">
										</div>
										<div class="form-group" style="">
											<label class="control-label">号段</label>
											<input type="text" class="form-control" style="width:130px;" name="numberBlock">
										</div>
										<div class="form-group" style="">
											<label class="control-label">地市</label>

                                            <input type="text" class="form-control" name="gSaleCityStr" id="gSaleCityStr" readonly>
                                            <input type="hidden" class="form-control" name="gSaleCity" id="gSaleCity">
                                            <div id="gSaleCityContent" style="z-index: 999999;">
                                                <div id="menuContent" class="menuContent" style="display:none; position: absolute;z-index: 999999;">
                                                    <ul id="cityTree" strObj="gSaleCityStr" valObj="gSaleCity" class="ztree" style="height:auto;max-height:500px;margin-top:0; width:180px; height: auto;overflow:auto;"></ul>
                                                </div>
                                            </div>
										</div>
										<div class="form-group" style="">
											<label class="control-label">状态</label>
											<select class="chosen-select form-control" name="qstatus" id="qstatus">
											</select>
										</div>
										<c:if test="<%=SessionUtil.hasPower(PowerConsts.NUMBERMOUDULE_COMMON_ADDTAG)%>">
											<button type="button" class="btn btn-info btn-small fa fa-user-plus pull-right" data-toggle="modal" data-target="#editTags" >设置标签</button>
										</c:if>
										<button type="button" class="btn btn-info btn-small fa fa-user-plus pull-right" id="export">导出</button>
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
		<div class="modal fade" id="numberInfo" tabindex="-1">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">
							&times;
						</button>
						<h4 class="modal-title">
							添加/修改号码
						</h4>
					</div>
					<form role="form" class="form-horizontal">
						<input name="id" type="hidden">
						<div class="modal-body">
							<div class="form-group">
								<label class="col-xs-2 control-label">key_id</label>
								<div class="col-xs-10">
									<input type="text" class="form-control input-xxlarge" name="keyId">
								</div>
							</div>
							<div class="form-group">
								<label class="col-xs-2 control-label">key_value</label>
								<div class="col-xs-10">
									<input type="text" class="form-control input-xxlarge" name="keyValue">
								</div>
							</div>
							<div class="form-group">
								<label class="col-xs-2 control-label">key_group</label>
								<div class="col-xs-10">
									<input type="text" class="form-control input-xxlarge" name="keyGroup">
								</div>
							</div>
							<div class="form-group">
								<label class="col-xs-2 control-label">seq</label>
								<div class="col-xs-10">
									<input type="text" class="form-control input-xxlarge" name="seq">
								</div>
							</div>

							<div class="form-group">
								<label class="col-xs-2 control-label">note</label>
								<div class="col-xs-10">
									<textarea type="text" style="width: 468px" class="form-control input-xxlarge" name="note" ></textarea>
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
		<div class="modal fade" id="editTags" tabindex="-1" style="overflow: auto" >
			<div class="modal-dialog" style="width:300px;">
				<div class="modal-content" style="width: 300px; max-height:720px; overflow: auto">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">
							&times;
						</button>
						<h4 class="modal-title">
							号码列表
						</h4>
					</div>
					<form role="form" class="form-horizontal">
						<div class="modal-body">
							<div class="form-group">
								<div class="col-xs-12">
									<textarea name="numResource" id="numResource" class="form-control" style="height:400px;resize: none;"></textarea>
                                    <span style="color:red">注:输入多个号码时，请使用换行隔开</span>
								</div>
							</div>
							<div class="form-group" id="numberTags">
							</div>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-danger">清空标签</button>
							<button type="button" class="btn btn-success">确定</button>
							<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
						</div>
					</form>
				</div><!-- /.modal-content -->
			</div><!-- /.modal -->
		</div>
		<script type="text/javascript">
            var p_addTag = <%=SessionUtil.hasPower(PowerConsts.NUMBERMOUDULE_COMMON_ADDTAG)%>;
		</script>
	</body>
</html>
