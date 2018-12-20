<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.hrtx.global.SessionUtil,com.hrtx.global.PowerConsts" %>
<!DOCTYPE html>
	<head>
		<title>号码价格管理</title>
		<%@ include file="../common/basecss.jsp" %>
		<script type="text/javascript" src="<%=basePath %>project/js/jquery-ui-1.9.2.custom.js"></script>
		<script type="text/javascript" src="<%=basePath %>project/css/jquery-ui-1.9.2.custom.css"></script>
		<script type="text/javascript" src="<%=basePath %>admin/js/numprice-query.js"></script>
        <style>
            .ui-helper-hidden-accessible {
                display: none;
            }
        </style>

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
							<li><a href="javascript:void(0);">号码价格管理</a></li>
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
										<%--<div class="form-group" style="">--%>
											<%--<label class="control-label">代理商</label>--%>
											<%--<input type="text" class="form-control" style="width:130px;"  id="commpayName">--%>
											<%--<input type="hidden" class="form-control"  name="agentId" id="agentId">--%>
											<%--<input type="hidden" class="form-control"  id="agentCommpayName">--%>
										<%--</div>--%>
                                        <div class="form-group" style="">
                                            <label class="control-label">渠道</label>
                                            <select name="channel" class="form-control" id="channel">
                                                <option value="-1">请选择</option>
                                                <c:forEach items="${channel}" var="item">
                                                    <option value="${item.key}">${item.value}</option>
                                                </c:forEach>
                                            </select>
                                        </div>
										<button type="button" class="btn btn-info btn-small fa fa-user-plus " id="query">查询</button>
										<button type="button" class="btn btn-info btn-small fa fa-user-plus " id="reset">重置</button>
										<button type="button" class="btn btn-info btn-small fa fa-user-plus " id="export">导出</button>
                                        <c:if test="<%=SessionUtil.hasPower(PowerConsts.NUMPRICEMOUDULE_COMMON_EDIT)%>">
                                            <button type="button" class="btn btn-info btn-small fa fa-user-plus pull-right" data-toggle="modal" data-target="#myModal">设置代理商价格</button>
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
            <div class="modal fade" id="myModal" tabindex="-1">
                <div class="modal-dialog" style="width: 750px;">
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                                &times;
                            </button>
                            <h4 class="modal-title" id="myModalLabel">
                                设置代理商号码价格
                            </h4>
                        </div>
                        <form role="form" class="form-horizontal">
                            <div class="modal-body">
                                <div class="form-group">
                                    <input type="hidden" class="form-control" name="id">
                                    <label class="col-xs-2 col-sm-2 control-label">号码</label>
                                    <div class="col-xs-4 col-sm-4">
                                        <input type="text" maxlength="11" class="form-control resource" name="resource" id="resourceT" placeholder="请输入完整的号码">
                                    </div>
                                    <label class="col-xs-2 col-sm-2 control-label">代理商</label>
                                    <div class="col-xs-4 col-sm-4">
                                        <input type="text" class="form-control name" name="commpayName" placeholder="请输入代理商" id="commpayNameT">
                                        <input type="hidden" class="form-control" name="agentId" id="agentIdT">
                                        <input type="hidden" class="form-control" name="agentCommpayName" id="agentCommpayNameT">
                                    </div>
                                </div>
                                <%--<div class="form-group">--%>
                                    <%--<button type="button" class="btn btn-primary pull-right" style="margin-right:17px "  id="search">--%>
                                        <%--搜索--%>
                                    <%--</button>--%>
                                <%--</div>--%>
                                <div class="form-group">
                                    <label class="col-xs-3 col-sm-2 control-label">默认价格</label>
                                    <div class="col-xs-3 col-sm-4">
                                        <input type="text" class="form-control" name="phone" disabled id="price">
                                        <%--<span class="form-control" ></span>--%>
                                    </div>
                                    <label class="col-xs-3 col-sm-2 control-label">代理商价格</label>
                                    <div class="col-xs-3 col-sm-4">
                                        <input type="text" class="form-control" name="price" id="agentPrice">
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

		<script type="text/javascript">
            var np_edit = <%=SessionUtil.hasPower(PowerConsts.NUMPRICEMOUDULE_COMMON_EDIT)%>;
		</script>
	</body>
</html>
