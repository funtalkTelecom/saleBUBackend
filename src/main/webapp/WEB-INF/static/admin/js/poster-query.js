var dataList = null;
$(function() {
	/* 初始化入库单列表数据 */
	dataList = new $.DSTable({
		"url" : '/poster/poster-list',
		"ct" : "#result",
		"cm" : [{
                    "header" : "标题",
                    "dataIndex" : "title"
                },{
					"header" : "位置",
					"dataIndex" : "positionName"
				},{
					"header" : "图片",
					"dataIndex" : "pic",
					"renderer": function (v, record) {
                        $operate = v;
                        if(v){
                            $operate = $("<span alt=\"点击打开原图\" onclick=\"window.open('get-img/posterImages/"+v+"','_blank')\" style=\"cursor: pointer;\" class=\"btn-sm tooltip-info\" data-toggle=\"tooltip\" data-placement=\"bottom\" title=\"<img style='width:180px' src='get-img/posterImages/"+v+"'/>\">"+v+"</span>");
                            $operate.tooltip({html : true});
                        }
                        return $operate;
					}
				},{
					"header" : "url",
					"dataIndex" : "url"
				},{
                    "header" : "开始时间",
                    "dataIndex" : "startTime"
                },{
                    "header" : "结束时间",
                    "dataIndex" : "endTime"
                },{
					"header" : "备注",
					"dataIndex" : "remark"
				},{
					"header" : "操作",
					"dataIndex" : "id",
					"renderer":function(v,record){
						var node = [];
						if(p_edit) {
							node.push('<a class="btn btn-success btn-xs update" href="javascript:void(0);">修改</a>')
                        }
						if(p_delete) {
							node.push('<a class="btn btn-success btn-xs delete" href="javascript:void(0);">删除</a>')
                        }
                        $operate = $("<div>"+$.trim(node.join("&nbsp;"),'--')+"</div>");

                        $operate.find(".update").click(function () {
                            $.post("poster/poster-info", {id: v}, function (data) {
                                var _data = data.data;
                                formInit($("#posterInfo form"), _data);
                                $('#posterInfo').modal('show');
                            }, "json");
                        });
                        $operate.find(".delete").click(function () {
                            if (confirm("确认删除？")) {
                                $.post("poster/poster-delete", {id: v}, function (data) {
                                    dataList.reload();
                                    alert(data.data);
                                }, "json");
                            }
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

    $(document).on("click","#posterInfo .modal-footer .btn-success",function() {
        // 准备好Options对象
        var options = {
            // target:  null,
            type : "post",
            url: "poster/poster-edit",
            success : function(data) {
                dataList.load();
                $('#posterInfo').modal('hide');
                alert(data.data);
            }
        };
        // 将options传给ajaxForm
        $('#posterInfo form').ajaxSubmit(options);
    });

    var option = {
        url:"",
        key:"keyId",
        value:"keyValue",
        param:{t:new Date().getTime()}
    };
    dictSelect($("#posterPosition"), "posterPosition", option, true);
    $('#startTime').bind('focus',function() {
        WdatePicker({
            maxDate : '#F{$dp.$D(\'endTime\',{s:-1})}',
            dateFmt : 'yyyy-MM-dd HH:mm:ss',
            onpicked : function(item) {
                $(this).change();
            }
        });
    });
    $('#endTime').bind('focus',function() {
        WdatePicker({
            minDate : '#F{$dp.$D(\'startTime\',{s:1})}',
            dateFmt : 'yyyy-MM-dd HH:mm:ss',
            onpicked : function(item) {
                $(this).change();
            }
        });
    });
});
