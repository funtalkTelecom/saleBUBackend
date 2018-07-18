var dataList = null;
$(function() {
	/* 初始化入库单列表数据 */
	dataList = new $.DSTable({
		"url" : 'system/system-list',
		"ct" : "#result",
		"cm" : [{
					"header" : "ID",
					"dataIndex" : "id"
				},{
					"header" : "key_id",
					"dataIndex" : "keyId"
				},{
					"header" : "key_value",
					"dataIndex" : "keyValue"
				},{
					"header" : "temp_key_value",
					"dataIndex" : "tempKeyValue",
					"renderer":function(v,record){
                        $operate = v;
						if(v){
							$operate = $("<span class=\"btn-sm tooltip-info\" data-toggle=\"tooltip\" data-placement=\"bottom\" title=\""+v+"\">"+$.trim(v,'--')+"</span>");
							$operate.tooltip();
						}
                        return $operate;
					}
				},{
					"header" : "remark",
					"dataIndex" : "remark"
				},{
					"header" : "操作",
					"dataIndex" : "id",
					"renderer":function(v,record){
						var node = [];
						if(p_edit) {
							node.push('<a class="btn btn-success btn-xs update" href="javascript:void(0);">修改</a>')
							// node.push('<a class="btn btn-success btn-xs delete" href="javascript:void(0);">删除</a>')
                        }
                        if(record.isAudit!="1" && p_audit){
                            node.push('<a class="btn btn-success btn-xs audit" href="javascript:void(0);">审核</a>')
						}
                        $operate = $("<div>"+$.trim(node.join("&nbsp;"),'--')+"</div>");

                        $operate.find(".update").click(function () {
                            $.post("system/system-info", {id: v}, function (data) {
                                var _data = data.data;
                                formInit($("#systemInfo form"), _data);
                                $('#systemInfo').modal('show');
                            }, "json");
                        });
                        $operate.find(".delete").click(function () {
                            if (confirm("确认删除？")) {
                                $.post("system/system-delete", {id: v}, function (data) {
                                    dataList.reload();
                                    alert(data.data);
                                }, "json");
                            }
                        });

                        $operate.find(".audit").on(ace.click_event, function() {
                            bootbox.dialog({
                                boxCss:{"width":"400px"},
                                title: "<span class='bigger-110'>确认审核</span>",
                                message: "将key_value修改为:"+record.tempKeyValue+",原值:"+record.keyValue,
                                buttons:
                                    {
                                        "success" :
                                            {
                                                "label" : "<i class='ace-icon fa fa-check'></i> 审核通过",
                                                "className" : "btn-sm btn-success",
                                                "callback": function() {
                                                    $.post("system/system-audit",{id:v, isAudit:'1'},function(data){
                                                        dataList.reload();
                                                        alert(data.data);
                                                    },"json");
                                                }
                                            },
                                        "danger" :
                                            {
                                                "label" : "审核不通过",
                                                "className" : "btn-sm btn-danger",
                                                "callback": function() {
                                                    $.post("system/system-audit",{id:v, isAudit:'0'},function(data){
                                                        dataList.reload();
                                                        alert(data.data);
                                                    },"json");
                                                }
                                            }
                                    }
                            });
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

    $(document).on("click","#systemInfo .modal-footer .btn-success",function() {
        // if(!validate_check($("#systemInfo form"))) return;
        $.post("system/system-edit",$("#systemInfo form").serialize(),function(data){
            dataList.reload();
            $('#systemInfo').modal('hide');
            alert(data.data);
        },"json");
    });

});
