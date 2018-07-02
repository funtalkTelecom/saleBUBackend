<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.hrtx.global.SessionUtil,com.hrtx.global.PowerConsts" %>
<!DOCTYPE html>
	<head>
		<title>商品管理</title>
		<%@ include file="../common/basecss.jsp" %>
		<%--<script type="text/javascript" src="<%=basePath %>query-meal.js"></script>--%>
		<script type="text/javascript" src="<%=basePath %>admin/js/goods-query.js"></script>

		<link rel="stylesheet" href="<%=basePath %>project/js/zTree_v3/css/zTreeStyle/zTreeStyle.css" type="text/css">
		<link rel="stylesheet" href="<%=basePath %>project/js/kindeditor/themes/default/default.css" type="text/css">

		<script type="text/javascript" src="<%=basePath %>project/js/zTree_v3/js/checkboxTree.js"></script>
		<script type="text/javascript" src="<%=basePath %>project/js/zTree_v3/js/jquery.ztree.core.js"></script>
		<script type="text/javascript" src="<%=basePath %>project/js/zTree_v3/js/jquery.ztree.excheck.js"></script>
		<script type="text/javascript" src="<%=basePath %>project/js/kindeditor/kindeditor-all.js"></script>
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
											<label class="control-label">商品名称</label>
											<input type="text" class="form-control" style="width:130px;" name="gName">
										</div>
										<div class="form-group">
											<label class="control-label">宣传语</label>
											<input type="text" class="form-control" style="width:130px;" name="gAd">
										</div>
										<div class="form-group" style="">
											<label class="control-label">是否竞拍</label>
											<select class="form-control" name="gIsAuc" id="gIsAuc">
											</select>
										</div>
										<c:if test="<%=SessionUtil.hasPower(PowerConsts.GOODSMOUDULE_COMMON_ADD)%>">
											<button type="button" class="btn btn-info btn-small fa fa-user-plus pull-right" data-toggle="modal" data-target="#goodsInfo" >上架商品</button>
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
		<div class="modal fade" id="goodsInfo" tabindex="-1" style="overflow: auto">
			<div class="modal-dialog" style="width:90%">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">
							&times;
						</button>
						<h4 class="modal-title">
							添加/修改商品
						</h4>
					</div>
					<form role="form" class="form-horizontal" enctype="multipart/form-data">
						<input name="gId" id="gId" type="hidden">
						<div class="modal-body">
							<div class="form-group">
								<label class="col-xs-2 control-label">大类</label>
								<div class="col-xs-3">
									<select class="form-control" name="gType1" id="gType1">
										<option value="-1">请选择...</option>
									</select>
								</div>
								<label class="col-xs-2 control-label" style="display: none">小类</label>
								<div class="col-xs-3">
									<select class="form-control" style="display: none" name="gType2" id="gType2">
										<option value="-1">请选择...</option>
									</select>
								</div>
							</div>
							<div class="form-group">
								<label class="col-xs-2 control-label">商品名称</label>
								<div class="col-xs-3">
									<input type="text" class="form-control" name="gName">
								</div>
								<label class="col-xs-2 control-label">宣传语</label>
								<div class="col-xs-3">
									<input type="text" class="form-control" name="gAd">
								</div>
							</div>

							<div class="form-group">
								<label class="col-xs-2 control-label">销售地市</label>
								<div class="col-xs-4">
									<input type="text" class="form-control" name="gSaleCityStr" id="gSaleCityStr" readonly>
									<input type="hidden" class="form-control" name="gSaleCity" id="gSaleCity">
									<div id="gSaleCityContent" style="z-index: 999999;">
										<div id="menuContent" class="menuContent" style="display:none; position: absolute;z-index: 999999;">
											<ul id="cityTree" strObj="gSaleCityStr" valObj="gSaleCity" class="ztree" style="height:auto;max-height:500px;margin-top:0; width:180px; height: auto;overflow:auto;"></ul>
										</div>
									</div>
								</div>
							</div>
							<div class="form-group">
								<label class="col-xs-2 control-label">是否竞拍</label>
								<div class="col-xs-2">
									<div class="control-group">
										<div class="radio col-xs-2" style="width: auto;">
											<label>
												<input name="gIsAuc" type="radio" class="ace" value="1">
												<span class="lbl">是</span>
											</label>
										</div>

										<div class="radio col-xs-2" style="width: auto;">
											<label>
												<input name="gIsAuc" type="radio" class="ace" value="0" checked>
												<span class="lbl">否</span>
											</label>
										</div>
									</div>
								</div>
								<div class="col-xs-4">
									<div class="control-group">
										<select class="form-control" id="gActive" name="gActive" onchange="activeChange(this)">
										</select>
									</div>
								</div>
							</div>
							<div class="form-group" style="display: none" id="isAucContent">
								<div class="control-group">
									<label class="col-xs-2 control-label">轮询时间(分钟)</label>
									<div class="col-xs-1" style="width:100px">
										<select class="form-control" name="gLoopTime" id="gLoopTime">
											<option value="-1">请选择...</option>
										</select>
									</div>
									<label class="col-xs-1 control-label" style="width:100px">起拍人数</label>
									<div class="col-xs-1" style="width:100px">
										<input type="text" class="form-control" name="gStartNum">
									</div>
									<label class="col-xs-1 control-label" style="width:100px">保证金</label>
									<div class="col-xs-1" style="width:100px">
										<input type="text" class="form-control" name="gDeposit">
									</div>
									<label class="col-xs-1 control-label" style="width:100px">每次加价</label>
									<div class="col-xs-1" style="width:100px">
										<input type="text" class="form-control" name="gPriceUp">
									</div>
								</div>
							</div>
							<div class="form-group">
								<label class="col-xs-2 control-label">是否打包</label>
								<div class="col-xs-4">
									<div class="control-group">
										<div class="radio col-xs-2" style="width: auto;">
											<label>
												<input name="gIsPack" type="radio" class="ace" value="1">
												<span class="lbl">是</span>
											</label>
										</div>

										<div class="radio col-xs-2" style="width: auto;">
											<label>
												<input name="gIsPack" type="radio" class="ace" value="0" checked>
												<span class="lbl">否</span>
											</label>
										</div>
									</div>
								</div>
							</div>
							<div class="form-group">
								<label class="col-xs-2 control-label">有效期</label>
								<div class="col-xs-8">
									<div class="control-group">
										<div class="col-xs-3">
											<input type="hidden" name="gStartTime" id="gStartTime">
											<input type="text" class="form-control" id="gStartTimePicker" onclick="WdatePicker({maxDate : '#F{$dp.$D(\'gEndTimePicker\',{s:-1})}',dateFmt : 'yyyy-MM-dd HH:mm:ss'});" readonly>
										</div>
										<div class="col-xs-3" style="width: 10px; padding: 0px; height: 34px; line-height: 34px;">
											-
										</div>
										<div class="col-xs-3">
											<input type="hidden" name="gEndTime" id="gEndTime">
											<input type="text" class="form-control" id="gEndTimePicker" onclick="WdatePicker({minDate : '#F{$dp.$D(\'gStartTimePicker\',{s:1})}',dateFmt : 'yyyy-MM-dd HH:mm:ss'});" readonly>
										</div>
									</div>
								</div>
							</div>
							<h3 class="header smaller lighter blue">商品属性</h3>
							<div class="form-group" id="gProperty">
							</div>

							<%--sku列表--%>
							<input type="hidden" name="skuJson" id="skuJson">
							<input type="hidden" name="delSkus" id="delSkus">
							<h3 class="header smaller lighter blue">SKU</h3>
							<div id="skuResult" style="overflow: visible;">
							</div>
							<%--图片上传--%>
							<h3 class="header smaller lighter blue">图片上传</h3>
							<div id="picUpload">
								<div class="form-group">
									<div class="col-xs-8">
										<div class="control-group">
										</div>
									</div>
								</div>
							</div>
							<%--富文本编辑--%>
							<h3 class="header smaller lighter blue">文本编辑</h3>
							<div id="kindeditor" style="overflow: auto;">
                                <textarea name="kindeditorContent" style="width:100%;height:200px;visibility:hidden;"></textarea>
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
		<div class="modal fade" id="saleNumInfo" tabindex="-1" style="overflow: auto" >
			<div class="modal-dialog" style="width:300px;">
				<div class="modal-content" style="width: 300px; max-height:700px; overflow: auto">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">
							&times;
						</button>
						<h4 class="modal-title">
							所售号码列表
						</h4>
					</div>
					<form role="form" class="form-horizontal">
						<div class="modal-body">
							<div class="form-group">
								<div class="col-xs-12">
									<textarea id="saleNum" class="form-control" style="height:500px;resize: none;"></textarea>
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
            var p_add = <%=SessionUtil.hasPower(PowerConsts.GOODSMOUDULE_COMMON_ADD)%>;
            var p_edit = <%=SessionUtil.hasPower(PowerConsts.GOODSMOUDULE_COMMON_EDIT)%>;
            var p_delete = <%=SessionUtil.hasPower(PowerConsts.GOODSMOUDULE_COMMON_DELETE)%>;
		</script>
	</body>
</html>
