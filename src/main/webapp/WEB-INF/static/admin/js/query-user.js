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
					"dataIndex" : "login_name"
				},{
					"header" : "用户名",
					"dataIndex" : "name"
				},{
					"header" : "电话",
					"dataIndex" : "phone"
				},{
					"header" : "所属公司",
					"dataIndex" : "company"
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
							if(add_p) node.push('<a class="btn btn-success btn-xs update" href="JavaScript:void(0);">修改</a>')
							if(add_p && record.status == 1) node.push('<a class="btn btn-danger btn-xs freeze" href="JavaScript:void(0);">冻结</a>')
							if(add_p && record.status == 2) node.push('<a class="btn btn-success btn-xs unfreeze" href="JavaScript:void(0);">解冻</a>')
							if(add_p) node.push('<a class="btn btn-success btn-xs reset-pwd" href="JavaScript:void(0);">重置密码</a>')
						}
						
						$operate = $("<div>"+$.trim(node.join("&nbsp;"),'--')+"</div>");
                        $operate.find(".update").click(function (){
                            $.post("edit-user-index",{id:record.id},function(data){
                                var _data=data.data;
                                $("#myModal .loginName").attr("disabled",true);
                                updateFormInit($("#myModal form"), _data);
                                $("#myModal input[name='roles']").each(function () {
                                    $(this)[0].checked = false;
                                })
                                var arr=(_data.roles).split(",");
                                for (var index = 0; index < arr.length; index++) {
                                    if(arr[index]=='')continue;
                                    // $("#myModal input.pmid"+arr[index]).prop("checked",true);
                                    $("#myModal input.pmid"+arr[index])[0].checked = true;
                                }
                                $('#myModal').modal('show');
                            },"json");
                        })
                        $operate.find(".freeze").click(function (){
                            $.post("freeze-user",{id:record.id, status:2, t:new Date().getTime()},function(data){
                                dataList.reload();
                            },"json");
                        })
                        $operate.find(".unfreeze").click(function (){
                            $.post("freeze-user",{id:record.id, status:1, t:new Date().getTime()},function(data){
                                dataList.reload();
                            },"json");
                        })
                        $operate.find(".reset-pwd").click(function (){
                            if(confirm("确认重置密码？")) {
                                $.post("reset-pwd",{id:record.id, t:new Date().getTime()},function(data){
                                    if(data.code == 200) {
                                        alert("重置成功");
                                    }else {
                                        alert(data.data);
                                    }
                                },"json");
                            }
                        })
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

    $(document).on("click","#myModal .modal-footer .btn-primary",function() {
        // if(!validate_check($("#myModal form"))) return;
        $.post("add-user",$("#myModal form").serialize(),function(data){
            alert(data.data);
            dataList.reload();
            $('#myModal').modal('hide');
        },"json");
    });

    function init(){
        $("#myModal").data("html",$("#myModal form").html());
        $('#myModal').on('hide.bs.modal', function () {
            $("#myModal form").html($("#myModal").data("html"));
        });
    }
    init();
});

