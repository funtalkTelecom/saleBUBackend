var dataList = null;
var itemList = null;
var orderStatus = {
    "1":"待付款",
    "2":"已付款待推送（已付款尚未推送到仓储期）",
    "3":"待配货(仓储系统已收到)",
    "4":"待配卡",
    "5":"待签收(仓储物流已取件)",
    "6":"完成"
};
var skuGoodsTypes = {
    "1":"白卡",
    "2":"普号",
    "3":"普靓",
    "4":"超靓"
};
var signTypes = {
    "1":"用户签收",
    "2":"系统"
};

$.post("dict-to-map", {group: "orderStatus"},function(data){
    orderStatus = data;
},"json");
$(function() {
	/* 初始化入库单列表数据 */
	dataList = new $.DSTable({
		"url" : '/order/order-list',
		"ct" : "#result",
		"cm" : [{
                    "header" : "编号",
                    "dataIndex" : "orderId"
                }/*,{
                    "header" : "用户编码",
                    "dataIndex" : "consumer"
                }*/,{
                    "header" : "用户名称",
                    "dataIndex" : "consumerName"
                },{
                    "header" : "状态",
                    "dataIndex" : "status",//1待付款；2已付款待推送（已付款尚未推送到仓储期）；3待配货(仓储系统已收到)；4待签收(仓储物流已取件)；5完成
                    "renderer":function(v,record) {
                        return orderStatus[v];
                    }
                }/*,{
                    "header" : "请求的userAgent",
                    "dataIndex" : "reqUserAgent"
                },{
                    "header" : "请求的ip",
                    "dataIndex" : "reqIp"
                }*/,{
                    "header" : "添加时间",
                    "dataIndex" : "addDate"
                }/*,{
                    "header" : "1商品；2号码；3竞拍",
                    "dataIndex" : "orderType"
                },{
                    "header" : "运输方式编码",
                    "dataIndex" : "字典表	shippingMenthodId"
                },{
                    "header" : "运输方式",
                    "dataIndex" : "shippingMenthod"
                },{
                    "header" : "收货地址编码",
                    "dataIndex" : "addressId"
                }*/,{
                    "header" : "收货人",
                    "dataIndex" : "personName"
                },{
                    "header" : "收货电话",
                    "dataIndex" : "personTel"
                },{
                    "header" : "收货地址",
                    "dataIndex" : "address"
                },{
                    "header" : "通知出货时间",
                    "dataIndex" : "noticeShipmentDate"
                }/*,{
                    "header" : "支付方式编码",
                    "dataIndex" : "字典表	payMenthodId"
                }*/,{
                    "header" : "支付方式",
                    "dataIndex" : "payMenthod"
                },{
                    "header" : "付款日期",
                    "dataIndex" : "payDate"
                }/*,{
                    "header" : "快递公司",
                    "dataIndex" : "发货后  字典表	expressId"
                },{
                    "header" : "快递名称",
                    "dataIndex" : "expressName"
                },{
                    "header" : "快递单号",
                    "dataIndex" : "expressNumber"
                },{
                    "header" : "发货时间",
                    "dataIndex" : "deliverDate"
                },{
                    "header" : "仓库调用时间",
                    "dataIndex" : "pickupDate"
                },{
                    "header" : "签收方式1用户自动签收2系统",
                    "dataIndex" : "signType"
                }*/,{
                    "header" : "签收时间",
                    "dataIndex" : "signDate"
                }/*,{
                    "header" : "优惠券",
                    "dataIndex" : "折扣	commission"
                },{
                    "header" : "运输费用",
                    "dataIndex" : "shippingTotal"
                },{
                    "header" : "子项小计",
                    "dataIndex" : "subTotal"
                },{
                    "header" : "合计",
                    "dataIndex" : "total"
                },{
                    "header" : "摘要",
                    "dataIndex" : "conment"
                }*/,{
					"header" : "操作",
					"dataIndex" : "orderId",
					"renderer":function(v,record){
						var node = [];
						if(p_query) {
							node.push('<a class="btn btn-success btn-xs detail" href="javascript:void(0);">详情</a>');
                        }
						if(p_receipt && record.status=="1") {
							node.push('<a class="btn btn-success btn-xs receipt" href="javascript:void(0);">收款</a>');
                        }
						if(p_receipt && record.status=="2") {
							node.push('<a class="btn btn-success btn-xs payDeliver" href="javascript:void(0);">发货</a>');
                        }
                        //普号进入“待配卡”状态，应该不允许管理员绑定
						if(p_bindCard && record.status=="4" && record.skuGoodsType!="2") {
							node.push('<a class="btn btn-success btn-xs bindCard" href="javascript:void(0);">绑卡</a>');
                        }
                        $operate = $("<div>"+$.trim(node.join("&nbsp;"),'--')+"</div>");

						//点击详情
                        $operate.find(".detail").click(function () {
                            $("#orderInfo input").css({"border-top":"0px","border-left":"0px","border-right":"0px", "border-bottom":"1px soild", "border-style":"dashed"});
                            $("#orderInfo textarea").css({"border":"1px soild", "border-style":"dashed"});
                            $.post("order/order-info", {orderId: v}, function (data) {
                                var _data = data.data;
                                formInit($("#orderInfo form"), _data);
                                $("input[name=status]").val(orderStatus[$("input[name=status]").val()]);
                                $("input[name=signType]").val(signTypes[$("input[name=signType]").val()]);

                                itemList.load();
                                $('#orderInfo').modal('show');
                            }, "json");
                        });
                        //点击收款
                        $operate.find(".receipt").click(function () {
                            $("#receiptInfo-orderId").val(v);
                            $("#receiptInfo-orderType").val(record.orderType);
                            //如果是竞拍订单获取保证金
                            if(record.orderType=="3"){
                                var deposit;
                                $.post("order/order-deposit", {orderId: v}, function (data) {
                                    deposit = data["deposit"];
                                    $("#receivable").val(Number(record.total)-Number(deposit));
                                    $("#receipts").val(Number(record.total)-Number(deposit));
                                }, "json");
                            }
                            $('#receiptInfo').modal('show');
                        });
                        //点击发货
                        $operate.find(".payDeliver").click(function () {
                            $.post("order/order-payDeliver", {orderId: v}, function (data) {
                                dataList.reload();
                                alert(data.data);
                            }, "json");
                        });
                        //点击绑卡
                        $operate.find(".bindCard").click(function () {
                            $.post("order/order-bindCard", {orderId: v, status:record.status, skuGoodsType:record.skuGoodsType}, function (data) {
                                dataList.reload();
                                alert(data.data);
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
    itemList = new $.DSTable({
        "url" : '/order/item-list',
        "ct" : "#itemResult",
        "cm" : [{
            "header" : "商品属性",
            "dataIndex" : "skuProperty",
            "renderer":function(v,record){
                var pro = "";
                v = eval(v);
                for(p in v){
                    for(key in v[p]){
                        if(key=="keyValue"){
                            pro += v[p][key] + " ";
                        }
                    }
                }
                pro = pro.substring(0, pro.length-1);
                return pro.length==0?skuGoodsTypes[record.skuGoodsType]:skuGoodsTypes[record.skuGoodsType]+" ("+pro+")";
            }
        },{
            "header" : "手机号码",
            "dataIndex" : "num"
        },{
            "header" : "采购数量",
            "dataIndex" : "quantity"
        },{
            "header" : "单价",
            "dataIndex" : "price"
        },{
            "header" : "总价",
            "dataIndex" : "total"
        }],
        "getParam" : function(){
            return {"orderId" : $("#orderId").val()};
        }
    });
	dataList.load();

    var soption = {
        url:"",
        key:"keyId",
        value:"keyValue",
        onchange:"",
        onclick:"",
        param:{t:new Date().getTime()}
    };
    dictSelect($("#qstatus"), "orderStatus", soption, false);

	$("#query").click(function() {
		dataList.load();
	});
	
	window.reload = function(){
		dataList.reload();
	}

    $(document).on("click","#receiptInfo .modal-footer .btn-success",function() {
        $.post("order/order-receipt",$("#receiptInfo form").serialize(),function(data){
            $('#receiptInfo').modal('hide');
            dataList.reload();
            alert(data.data);
        },"json");
    });
});