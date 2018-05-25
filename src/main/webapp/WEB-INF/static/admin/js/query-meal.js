var dataList = null;
$(function() {
	/* 初始化入库单列表数据 */
	dataList = new $.DSTable({
		"url" : '/meal/list-meal',
		"ct" : "#result",
		"cm" : [{
					"header" : "套餐ID",
					"dataIndex" : "mealId"
				},{
					"header" : "套餐名称",
					"dataIndex" : "mealName",
					"renderer":function(v,record){
                        $operate = v;
                        if(v) {
                            $operate = $("<span class=\"btn-sm tooltip-info\" data-toggle=\"tooltip\" data-placement=\"bottom\" data-original-title=\"" + record.mealDesc + "\">" + $.trim(v, '--') + "</span>");
                            $operate.tooltip();
                        }
						return $operate;
					}
				},{
					"header" : "销售地市",
					"dataIndex" : "saleCityName"
				},{
					"header" : "创建时间",
					"dataIndex" : "createDate",
				},{
					"header" : "操作",
					"dataIndex" : "mid",
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
                            $.post("meal/meal-info",{mid:v},function(data){
                                var _data=data.data;
                                formInit($("#mealInfo form"), _data);
                                $('#mealInfo').modal('show');
                            },"json");
                        })
                        $operate.find(".delete").click(function () {
                            if(confirm("确认删除？")) {
                                $.post("meal/meal-delete",{mid:v},function(data){
                                    dataList.reload();
                                    alert("删除成功");
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

    $(document).on("click","#importModal .modal-footer .btn-success",function() {
        // 准备好Options对象
        var options = {
            // target:  null,
            type : "post",
            url: "meal/import-meal",
            success : function(data) {
                dataList.load();
                $('#importModal').modal('hide');
				alert("导入成功");
            }
        };
        // 将options传给ajaxForm
        $('#importModal form').ajaxSubmit(options);
    });

    // citySelect($("#fcity"),17);
    var option = {
            url:"",
            key:"cityId",
            value:"cityName",
            param:{t:new Date().getTime()}
        };
    thirdCitySelect($("#fcity"),"ly",option);
    $(document).on("click","#mealInfo .modal-footer .btn-success",function() {
        // if(!validate_check($("#mealInfo form"))) return;
        $.post("meal/meal-edit",$("#mealInfo form").serialize(),function(data){
            dataList.reload();
            $('#mealInfo').modal('hide');
            alert(data.data);
        },"json");
    });
});
