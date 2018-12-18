;$(function(){
	/* 初始化角色列表数据 */
	var roleList = new $.DSTable({
		"url" : 'list-role',
		"ct" : "#result",
		"cm" : [{
			"header" : "角色名",
			"dataIndex" : "name",
			"width" : 200
		}],
		"onSelected" : function(row,record){
			$("#roleId").val(record.id);
			$("#property").val("1");
			$("#tlabel").html("角色："+record.name);
			loadStorageAllocate(record.id+'','号卡平台');
		},
        "getParam" : function(){
         	return {"byName" : $("#byName").val()};
         }
	});
	roleList.load();
	var userList = new $.DSTable({
		"url" : 'list-user',
		"ct" : "#userresult",
		"cm" : [{
			"header" : "登录帐号",
			"dataIndex" : "login_name"
		},{
			"header" : "用户名",
			"dataIndex" : "name"
		}],
		"onSelected" : function(row,record){
			$("#roleId").val(record.id);
			$("#property").val("2");
			$("#tlabel").html("用户："+record.name+"-"+record.login_name);
			loadStorageAllocate(record.id+'','号卡平台');
		},
		
        "getParam" : function(){
         	return {limit:999999,"name" : $("#username").val()};
         }
	});
	userList.load();
	
	$("#query").click(function(){
		userList.load();
	});
	/*  加载公司库位分配信息 */
	function loadStorageAllocate(id,name){
		$("#tree").treeview({
		"showcheck" : true,
		"url" : "permission-tree-node",
		"cbiconpath" : "project/css/checktree/images/icons/",
		"theme": "bbit-tree-no-lines",
		"data" : [{
                "id" : id,
                "text" : name,
                "value" : $("#property").val(),
                "complete" : false,
                "checkstate" : 0,
                "hasChildren" : true
            }],
         "oncheckboxclick" : function(node, state){
         	$("#tree").attr("hasChange",true);
         },
         "onload" : function(){
         	var tree = $("#tree");
         	var oldCount = tree.getCheckedNodes(function(item){
				if(!item.hasChildren){
					return item.value;
				}
			}).length;
         	tree.attr("checkedCount",oldCount);
         }
		});
	}
	
	
	/* 注册“提交分配”按钮点击事件 */
	$("#submit").click(function(){
			//获取选中的隔层结点的集合
			var selected = $("#tree").getCheckedNodes();
			$("#tree").attr("hasChange",false);//将是否改变设为未改变
			$("#sendTip").show();
//			$(this).attr("disabled",true);
			var param = {
				"isMask" : "isMask",
				"roleId" : $("#roleId").val(),
				"property" : $("#property").val(),
				"permissionIds" : selected.join(",")
			};
			$.post("update-permission",param,function(data){
				$("#submit").attr("disabled",false);
				$("#sendTip").hide();
				alert("授权成功");
			});
	});
	
	
	$("#name").focus(function(){
		$("#save").attr("disabled",false);
	})
	
	$("#save").click(function(){
		if($.trim($("#name").val()) == '') {
			alert("请输入角色名");
			return;
		}
		$(this).attr("disabled",true);
		$.ajax({
			url : "add-role",
			data : {
				"t" : new Date().getTime(),
				"name":$.trim($("#name").val())
			},
			dataType : "json",
			type: "post",
			success : function(data) {
				if(data.code == 200) roleList.reload();
				else alert(data.data);
			}
		})
	})
});