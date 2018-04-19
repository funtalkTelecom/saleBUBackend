<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.hrtx.dto.Menu"%>
				<script type="text/javascript">
					try{ace.settings.check('sidebar' , 'fixed')}catch(e){}
					var myMenu;
					window.onload = function() {
						myMenu = new SDMenu("my_menu");
						myMenu.init();
					};
				</script>
				<%
				String path = request.getContextPath();
				String basePath = request.getScheme() + "://"
						+ request.getServerName() + ":" + request.getServerPort()
						+ path + "/";
				List<Menu> mainMenus = (List<Menu>)session.getAttribute("mainMenus");
				Map<Integer, List<Menu>> childMends = (Map<Integer, List<Menu>>)session.getAttribute("childMends");
				%>
				<div class="sidebar-shortcuts" id="sidebar-shortcuts">
					<div class="sidebar-shortcuts-large" id="sidebar-shortcuts-large">
						<button class="btn btn-success">
							<i class="ace-icon fa fa-signal"></i>
						</button>

						<button class="btn btn-info">
							<i class="ace-icon fa fa-pencil"></i>
						</button>

						<!-- #section:basics/sidebar.layout.shortcuts -->
						<button class="btn btn-warning">
							<i class="ace-icon fa fa-users"></i>
						</button>

						<button class="btn btn-danger">
							<i class="ace-icon fa fa-cogs"></i>
						</button>

						<!-- /section:basics/sidebar.layout.shortcuts -->
					</div>

					<div class="sidebar-shortcuts-mini" id="sidebar-shortcuts-mini">
						<span class="btn btn-success"></span>

						<span class="btn btn-info"></span>

						<span class="btn btn-warning"></span>

						<span class="btn btn-danger"></span>
					</div>
				</div><!-- /.sidebar-shortcuts -->
				<ul class="nav nav-list" id="my_menu">
					<%
					for (Iterator iterator = mainMenus.iterator(); iterator.hasNext();) {
						Menu m = (Menu) iterator.next();
						%>
						<li class="main_menu">
							<a href="javascript:void(0);" class="dropdown-toggle" ><%--class="active open dropdown-toggle"--%>
								<i class="menu-icon fa fa-tachometer"></i>
								<span class="menu-text"> <%=m.getName() %> </span>
								<b class="arrow fa fa-angle-down"></b>
							</a>
							<b class="arrow"></b>
							<ul class="submenu" style="">
								<%
								List<Menu> childs = childMends.get(m.getId());
								for (Iterator iterator1 = childs.iterator(); iterator1.hasNext();) {
									Menu child = (Menu) iterator1.next();
									%>
									<li class="c_menu">
										<a href="<%=basePath+child.getUrl() %>">
											<i class="menu-icon fa fa-caret-right"></i>
											<%=child.getName() %>
										</a>
										<b class="arrow"></b>
									</li>
									<%
								}
								%>
							</ul>
						</li>
					<%} %>
					
					<%--<li class="">
						<a href="#" class="dropdown-toggle">
							<i class="menu-icon fa fa-pencil-square-o"></i>
							<span class="menu-text"> Forms </span>

							<b class="arrow fa fa-angle-down"></b>
						</a>

						<b class="arrow"></b>

						<ul class="submenu">
							<li class="">
								<a href="form-elements.html">
									<i class="menu-icon fa fa-caret-right"></i>
									Form Elements
								</a>

								<b class="arrow"></b>
							</li>

							<li class="">
								<a href="form-elements-2.html">
									<i class="menu-icon fa fa-caret-right"></i>
									Form Elements 2
								</a>

								<b class="arrow"></b>
							</li>
						</ul>
					</li>--%>


					<%--<li class="active open">
						<a href="#" class="dropdown-toggle">
							<i class="menu-icon fa fa-file-o"></i>

							<span class="menu-text">
								Other Pages

								<!-- #section:basics/sidebar.layout.badge -->
								<span class="badge badge-primary">5</span>

								<!-- /section:basics/sidebar.layout.badge -->
							</span>

							<b class="arrow fa fa-angle-down"></b>
						</a>

						<b class="arrow"></b>

						<ul class="submenu">
							<li class="">
								<a href="faq.html">
									<i class="menu-icon fa fa-caret-right"></i>
									FAQ
								</a>
								<b class="arrow"></b>
							</li>
							<li class="active">
								<a href="blank.html">
									<i class="menu-icon fa fa-caret-right"></i>
									Blank Page
								</a>

								<b class="arrow"></b>
							</li>
						</ul>
					</li>--%>
				</ul><!-- /.nav-list -->

				<!-- #section:basics/sidebar.layout.minimize -->
				<div class="sidebar-toggle sidebar-collapse" id="sidebar-collapse">
					<i class="ace-icon fa fa-angle-double-left" data-icon1="ace-icon fa fa-angle-double-left" data-icon2="ace-icon fa fa-angle-double-right"></i>
				</div>

				<!-- /section:basics/sidebar.layout.minimize -->
				<script type="text/javascript">
					try{ace.settings.check('sidebar' , 'collapsed')}catch(e){}
				</script>