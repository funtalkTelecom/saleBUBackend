var dataList = null;
var start;
$(function() {
	/* 初始化入库单列表数据 */
	dataList = new $.DSTable({
		"url" : 'lianghao/lianghao-list',
		"ct" : "#result",
		"cm" : [{
					"header" : "省份",
					"dataIndex" : "province_name"
				},{
					"header" : "地市名称",
					"dataIndex" : "city_name"
				},{
					"header" : "号码",
					"dataIndex" : "resource"
				},{
					"header" : "价格",
					"dataIndex" : "price"
				},{
					"header" : "级别",
					"dataIndex" : "numLevel"
				},{
					"header" : "最低消费",
					"dataIndex" : "lowConsume"
				},{
					"header" : "运营商",
					"dataIndex" : "net_type",
            		"renderer" : function(v, record) {
                		if(v == 1) return "电信";
                		if(v == 2) return "联通";
                		if(v == 3) return "移动";
            		}
				},{
					"header" : "匹配类型",
					"dataIndex" : "feature"
				},{
					"header" : "是否冻结",
					"dataIndex" : "is_freeze",
            		"renderer" : function(v, record) {
                        if (v == 0) return "未冻结";
                        if (v == 1) return "<span style='color: red'>已冻结</span>";
                    }
				},{
            		"header" : "操作",
            		"dataIndex" : "is_freeze",
            		"renderer":function(v,record){
            		    var node = [];
            		    if(lh_freeze&&v===0) {
            		        node.push('<a class="btn btn-danger btn-xs freeze" href="javascript:void(0);">冻结</a>');
            		    }
                        if(lh_freeze&&v===1&&userId==record.addUser) {
                            node.push('<a class="btn btn-success btn-xs unfreeze" href="javascript:void(0);">解冻</a>');
                        }
                        if(lh_add) {
                            node.push('<a class="btn btn-success btn-xs" href="lianghao/add-order?id='+record.numPriceId+'" target="_blank">下单</a>');
						}
            		    $operate = $("<div>"+$.trim(node.join("&nbsp;"),'--')+"</div>");
            		    //点击详情
                        $operate.find(".freeze").click(function (){
                            $.post("lianghao/freeze-num",{id:record.id,isFreeze:1,t:new Date().getTime()},function(data){
                                dataList.reload();
                            },"json");
                        });
						$operate.find(".unfreeze").click(function (){
                            $.post("lianghao/freeze-num",{id:record.id,isFreeze:0,t:new Date().getTime()},function(data){
                                dataList.reload();
                            },"json");
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

    $(document).on("click","#batch-add .modal-footer .btn-success",function() {
        // 准备好Options对象
        var options = {
            type : "post",
            url: "lianghao/batch-add-order",
            success : function(data) {
                dataList.load();
                $('#batch-add').modal('hide');
                alert("数据已提交，详见稍后下载的excel表格");
                if (isIE()){
                    window.open("get-file/batch_add_order/"+data.data);
				}else {
                    window.location.href="get-file/batch_add_order/"+data.data;
				}

            }
        };
        // 将options传给ajaxForm
        $('#batch-add form').ajaxSubmit(options);
    });
});

$(document).on("blur","input[name=resource]",function(){
    $(this).val($(this).val().split("-").join(""));
});

