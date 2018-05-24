var dataList = null;
$(function() {
	/* 初始化入库单列表数据 */
	dataList = new $.DSTable({
		"url" : '/dict/dict-list',
		"ct" : "#result",
		"cm" : [{
					"header" : "key_id",
					"dataIndex" : "keyId"
				},{
					"header" : "key_value",
					"dataIndex" : "keyValue"
				},{
					"header" : "key_group",
					"dataIndex" : "keyGroup"
				},{
					"header" : "note",
					"dataIndex" : "note"
				},{
                    "header" : "seq",
                    "dataIndex" : "seq"
                },{
					"header" : "操作",
					"dataIndex" : "id",
					"renderer":function(v,record){
						var node = [];
						if(p_edit) {
							node.push('<a class="btn btn-success btn-xs update" href="javascript:void(0);">修改</a>');
							node.push('<a class="btn btn-success btn-xs delete" href="javascript:void(0);">删除</a>');
							node.push('<a class="btn btn-success btn-xs addByGroup" href="javascript:void(0);">添加同级</a>');
                        }
                        $operate = $("<div>"+$.trim(node.join("&nbsp;"),'--')+"</div>");

                        $operate.find(".update").click(function () {
                            $.post("dict/dict-info", {id: v}, function (data) {
                                var _data = data.data;
                                formInit($("#dictInfo form"), _data);
                                // $("#dictInfo form").find("[name = 'keyId']").attr("disabled", "disabled");
                                $('#dictInfo').modal('show');
                            }, "json");
                        });
                        $operate.find(".delete").click(function () {
                            if (confirm("确认删除？")) {
                                $.post("dict/dict-delete", {id: v}, function (data) {
                                    dataList.reload();
                                    alert(data.data);
                                }, "json");
                            }
                        });
                        $operate.find(".addByGroup").click(function () {
                            $.post("dict/group-max-info", {keyGroup: record.keyGroup}, function (data) {
                                var _data = data.data;
                                formInit($("#dictInfo form"), _data);
                                // $("#dictInfo form").find("[name = 'keyId']").attr("disabled", "disabled");
                                $('#dictInfo').modal('show');
                            }, "json");
                        });
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

    $(document).on("click","#dictInfo .modal-footer .btn-success",function() {
        // if(!validate_check($("#dictInfo form"))) return;
        $.post("dict/dict-edit",$("#dictInfo form").serialize(),function(data){
            dataList.reload();
            $('#dictInfo').modal('hide');
            alert(data.data);
        },"json");
    });

});
