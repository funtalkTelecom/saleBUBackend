var dataList = null;
$(function() {
	/* 初始化入库单列表数据 */
	dataList = new $.DSTable({
		"url" : 'list-user',
		"ct" : "#result",
		"cm" : [{
					"header" : "编号",
					"dataIndex" : "id"
				},{
					"header" : "登录帐号",
					"dataIndex" : "loginName"
				},{
					"header" : "用户名",
					"dataIndex" : "name"
				},{
					"header" : "电话",
					"dataIndex" : "phone"
				},{
					"header" : "所属公司",
					"dataIndex" : "COMPANYNAME"
				},{
					"header" : "状态",
					"dataIndex" : "status",
					"renderer" : function(v, record) {
						if(v == 0) return "待审核";
						if(v == 1) return "正常";
						if(v == 2) return "冻结";
						if(v == 3) return "未通过";
					}
				},{
					"header" : "操作",
					"dataIndex" : "operate",
					"renderer":function(v,record){
						var node = [];
						if(record.STATUS != 3) {
//							if(add_p) node.push('<a class="btn btn-success btn-xs" href="JavaScript:_update('+record.ID+')">修改</a>')
//							if(add_p && record.STATUS == 1) node.push('<a class="btn btn-danger btn-xs" href="JavaScript:_freeze('+record.ID+', 2)">冻结</a>')
//							if(add_p && record.STATUS == 2) node.push('<a class="btn btn-success btn-xs" href="JavaScript:_freeze('+record.ID+', 1)">解冻</a>')
//							if(examine_p && record.STATUS == 0) node.push('<a class="btn btn-success btn-xs" href="JavaScript:_examine('+record.ID+')">审核</a>')
						}
						
						$operate = $("<div>"+$.trim(node.join("&nbsp;"),'--')+"</div>");
						return $operate;
					}
				}],
		"pm" : {
			"limit" : 15,
			"start" : 0
		},
		"getParam" : function() {
			var obj={};
			$(".query input,.query select").each(function(index,v2){
				var name=$(v2).attr("name");
				obj[name]=$(v2).val();
			});
			return obj;
		}
	});
	dataList.load();

	$("#query").click(function() {
		dataList.load();
	});
	
	window.reload = function(){
		dataList.reload();
	}
	
});
