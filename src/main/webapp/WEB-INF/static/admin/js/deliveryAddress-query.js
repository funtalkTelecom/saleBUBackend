var dataList = null;
$(function() {
	/* 初始化入库单列表数据 */
	dataList = new $.DSTable({
		"url" : '/deliveryAddress/deliveryAddress-list',
		"ct" : "#result",
		"cm" : [{
            "header" : "编号",
            "dataIndex" : "id"
        },{
            "header" : "收货人名称",
            "dataIndex" : "personName"
        },{
            "header" : "收货人电话",
            "dataIndex" : "personTel"
        },{
            "header" : "省份",
            "dataIndex" : "province"
        },{
            "header" : "地市",
            "dataIndex" : "city"
        },{
            "header" : "区县",
            "dataIndex" : "district"
        },{
            "header" : "地址",
            "dataIndex" : "address",
            "renderer":function(v, record){

                return  "<p class='text-left' >"+record.address+"</p>";

            }
        },{
            "header" : "是否默认",
            "dataIndex" : " isDefaultl",
//					"renderer":function(v,record){
//						var node =v=='1'?"是":v=='0'?"否":"";
//						return node;
//
//					}
            "renderer":function(v, record){
                var node =record.isDefaultl=='1'?"是":record.isDefault=='0'?"否":"";
                if(record.isDefaultl=='0'){
                    return "<a href='JavaScript:_isDefaultl("+record.id+","+record.addUserId+")'>"+node+"</a>";
                }else {
                    return node;
                }

            }
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
                $operate = $("<div>"+trim(node.join("&nbsp;"),'--')+"</div>");
                $operate.find(".update").click(function () {
                    $.post("deliveryAddress/deliveryAddress-info", {id: v}, function (data) {
                        var _data = data.data;
                        formInit($("#deliveryAddressInfo form"), _data);
                        $('#deliveryAddressInfo').modal('show');
                    }, "json");
                });
                $operate.find(".delete").click(function () {
                    if (confirm("确认删除？")) {
                        $.post("deliveryAddress/deliveryAddress-delete", {id: v}, function (data) {
                            dataList.reload();
                            alert(data.data);
                        }, "json");
                    }
                });
                return $operate;
            }}
        ],
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


    $(document).on("click","#deliveryAddressInfo .modal-footer .btn-success",function() {
        // 准备好Options对象
        var options = {
            // target:  null,
            type : "post",
            url: "deliveryAddress/deliveryAddress-edit",
            success : function(data) {
                dataList.load();
                $('#deliveryAddressInfo').modal('hide');
                alert(data.data);
            }
        };
        // 将options传给ajaxForm
        $('#deliveryAddressInfo form').ajaxSubmit(options);
    });

    var option = {
        url:"",
        key:"keyId",
        value:"keyValue",
        param:{t:new Date().getTime()}
    };
    citySelect($("#fprovince"),0);

    $(document).on("change","#fprovince",function(){
        citySelect($("#fcity"),$(this).val());
    })

    $(document).on("change","#fcity",function(){
        citySelect($("#fdistrict"),$(this).val());
    })
});
