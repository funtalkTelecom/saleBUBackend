<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE html>
	<head>
		<title>Dashboard - Ace Admin</title>
		<%@ include file="common/basecss.jsp" %>
	</head>
	<body class="no-skin">
		<!-- #section:basics/navbar.layout -->
		<div id="navbar" class="navbar navbar-default">
			<jsp:include page="common/top.jsp"></jsp:include>
		</div>

		<!-- /section:basics/navbar.layout -->
		<div class="main-container" id="main-container">
			<!-- #section:basics/sidebar -->
			<div id="sidebar" class="sidebar responsive">
				<jsp:include page="common/menu.jsp"></jsp:include>
			</div>

			<!-- /section:basics/sidebar -->
			<div class="main-content">
				<div class="main-content-inner">
					<!-- #section:basics/content.breadcrumbs -->
					<div class="breadcrumbs" id="breadcrumbs">
						<ul class="breadcrumb">
							<li><i class="ace-icon fa fa-home home-icon"></i><a href="#">Home</a></li>
							<li><a href="#">Other Pages</a></li>
							<li class="active">Blank Page</li>
						</ul><!-- /.breadcrumb -->
						<!-- /section:basics/content.searchbox -->
					</div>
					
					<div class="page-content">
						<div class="row">
							<div class="col-xs-12">
								<h3 class="header smaller lighter blue">jQuery dataTables</h3>
								<div class="table-header">
									Results for "Latest Registered Domains"
								</div>
								<!-- <div class="table-responsive"> -->
								<!-- <div class="dataTables_borderWrap"> -->
								<div>
									<table id="sample-table-3" class="table table-striped table-bordered table-hover">
									</table>
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
					<jsp:include page="common/footer.jsp"/>
					<!-- /section:basics/footer -->
				</div>
			</div>
			<a href="#" id="btn-scroll-up" class="btn-scroll-up btn btn-sm btn-inverse"><i class="ace-icon fa fa-angle-double-up icon-only bigger-110"></i></a>
		</div><!-- /.main-container -->

		<script type="text/javascript">
		jQuery(function($) {
			 $('#sample-table-3').DataTable({
			  "ordering":false,//禁用排序	
		      "processing": true,
		      "serverSide": true,//打开后台分页
		      "ajax": {
		           "url": "page-demo",
		           "type": "POST"
		           ,
		           "data": function ( d ) {
		        	   var data={};//d参数过多，保留需要的
		      		   data.start=d.start;
		      		   data.length=d.length;
		      		   data.draw=d.draw;
		      		   data.a="22";//新增参数
		      		   return data;
				     }/**/
		      },
		      //data的字段与你后端传递过来的要对应  
		      "columns" : [
					{"data" : "id"}
					,{"data" : "created" }
					,{"data" : "anothername"}
					,{"data" : "anothe1"}
					,{"data" : "anothe2"}
					,{"data" : "anothe3"} 
			      ],
		       "aoColumns": [  
		         {  
		        	 "title": "Engine1",
		        	 "data": "id",
		             "render": function(data, type, full) {  
		                 return "<a href='/MiniWms/company/page?=" + data + "'>Update</a>";  
		             }  
		         },{ "title": "Engine2",   "data": "created" }
		         ,{ "title": "Engine3",   "data": "anothername" }
		         ,{ "title": "Engine4",   "data": "anothe1" }
		         ,{ "title": "Engine5",   "data": "anothe2" }
		         ,{ "title": "Engine6",   "data": "anothe3" }
		     ]  
		    }); 
			
		})
		</script>
	</body>
</html>
