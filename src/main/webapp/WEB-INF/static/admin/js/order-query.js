var dataList = null;
var itemList = null;
var orderStatus = {
    "1":"待付款",
    "2":"已付款待推送（已付款尚未推送到仓储期）",
    "3":"待配货(仓储系统已收到)",
    "4":"待配卡",
    "5":"待签收(仓储物流已取件)",
    "6":"完成",
    "7":"已取消",
    "11":"待仓库撤销",
    "12":"退款中",
    "13":"退款失败",
    "14":"待财务退款",
    "20":"已创建待冻结",
    "21":"待审核",
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
		"url" : 'order/order-list',
		"ct" : "#result",
		"cm" : [{
                    "header" : "<input type='checkbox' id='all' />",
                    "dataIndex" : "orderId",
                    renderer : function(v,record) {
                        if(p_check &&　record.status == 21) {//待审核
                            return "<input type='checkbox' class='single' value='"+record.orderId+"' />";
                        }
                    }
                },{
                    "header" : "编号",
                    "dataIndex" : "orderId"
                }/*,{
                    "header" : "用户编码",
                    "dataIndex" : "consumer"
                }*/,{
                    "header" : "用户名称",
                    "dataIndex" : "consumerName"
                },{
                    "header" : "类型",
                    "dataIndex" : "skuGoodsType",
                    "renderer":function(v) {
                        return skuGoodsTypes[v];
                    }
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
                },{
                    "header" : "合计",
                    "dataIndex" : "total"
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
                        if(p_adjust && record.status=="1") {
                            node.push('<a class="btn btn-success btn-xs adjust" href="javascript:void(0);">调价</a>');
                        }
                        if(record.status=="20") {
                            node.push('<a class="btn btn-success btn-xs order-push-storage" href="javascript:void(0);">推送</a>');
                        }
                        if(p_cancel &&( (record.status=="1"||record.status=="2"||record.status=="21"||record.status=="3") && record.orderType!=3)) {
							node.push('<a class="btn btn-danger btn-xs cancel" href="javascript:void(0);">取消订单</a>');
                        }
                        if(p_cancel_out &&( (record.status=="4" ||record.status=="5" ||record.status=="6") && record.orderType!=3)) {
							node.push('<a class="btn btn-danger btn-xs cancel" href="javascript:void(0);">取消订单</a>');
                        }
						if(p_refund && record.status=="14") {
							node.push('<a class="btn btn-success btn-xs refund" href="javascript:void(0);">退款</a>');
                        }
						if(p_refund_live && record.status=="13") {
							node.push('<a class="btn btn-success btn-xs refund_live" href="javascript:void(0);">重新退款</a>');
                        }
						if(p_receipt && record.status=="2") {
							node.push('<a class="btn btn-success btn-xs payDeliver" href="javascript:void(0);">发货</a>');
                        }
                        if(p_again && (record.status=="4" || record.status=="5" || record.status=="6") && record.orderType != 5) {
                            node.push('<a class="btn btn-success btn-xs again" href="javascript:void(0);">补发</a>');
                        }
                        //普号进入“待配卡”状态，应该不允许管理员绑定
                        var bk_gtypes = ['1','2','4'];
						if(p_bindCard && record.status=="4" && $.inArray(record.skuGoodsType,bk_gtypes) != -1) {
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
                                var aamt;
                                $.post("order/order-deposit", {orderId: v}, function (data) {
                                    deposit = data["deposit"];
                                    var bb =Number(record.total)-Number(deposit);
                                    $("#receivable").val(bb.toFixed(2));
                                    $("#receipts").val(bb.toFixed(2));
                                }, "json");
                            }else {
                                $.post("order/order-yPayAmt", {orderId: v}, function (data) {
                                    aamt = data.data;
                                    var aa =Number(record.total)-Number(aamt);
                                    $("#receivable").val(aa.toFixed(2));
                                    $("#receipts").val(aa.toFixed(2));
                                }, "json");
                            }
                            $('#receiptInfo').modal('show');
                        });

                        //点击调价
                        $operate.find(".adjust").click(function () {
                            $("#adjustInfo input[name='orderId']").val(record.orderId);
                            $("#adjustInfo .msg").html("");
                            $("#adjustInfo input[name='orderId1']").val(record.orderId);
                            $("#adjustInfo input[name='total']").val(record.total);
                            $("#adjustInfo input[name='maskId']").val("adjustInfo");
                            $("#adjustInfo input[name='mask']").val("提交中...");
                            $('#adjustInfo').modal('show');
                        });
                        //点击补发
                        $operate.find(".again").click(function () {
                            $("#adjustInfo input[name='orderId']").val(record.orderId);
                            if(confirm("确定对订单["+record.orderId+"]机型补发操作？")) {
                                $.post("order/again-order", {orderId: v, mask:"提交中..."}, function (data) {
                                    dataList.reload();
                                    alert(data.data);
                                }, "json");
                            }
                        });
                        //点击线下退款
                        $operate.find(".refund").click(function () {
                            $("#refund-orderId").val(v);
                            $('#refundInfo').modal('show');
                        });

                        $operate.find(".order-push-storage").click(function () {
                            if(confirm("是否确认推送？")){
                                $.post("order/order-push-storage", {orderId: v}, function (data) {
                                    dataList.reload();
                                    alert(data.data);
                                }, "json");
                            }
                        });

                        // 退款失败，重新退款
                        $operate.find(".refund_live").click(function () {
                            if(confirm("是否确认再次退款？")){
                                $.post("order/order-refund-live", {orderId: v}, function (data) {
                                    dataList.reload();
                                    alert(data.data);
                                }, "json");
                            }
                        });
                        //取消订单
                        $operate.find(".cancel").click(function () {
                            $("#cancelOrderId").val(v);
                            $('#cancelOrder').modal('show');
                        });
                        //点击发货
                        $operate.find(".payDeliver").click(function () {
                            $.post("order/order-payDeliver", {orderId: v, noRepeat : 1}, function (data) {
                                dataList.reload();
                                alert(data.data);
                            }, "json");
                        });
                        //点击绑卡
                        $operate.find(".bindCard").click(function () {
                            $.post("order/order-bindCard", {orderId: v, status:record.status, skuGoodsType:record.skuGoodsType, noRepeat : 1}, function (data) {
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
        "url" : 'order/item-list',
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

    $(document).on("click","#refundInfo .modal-footer .btn-success",function() {
        if(confirm("是否确认退款？")){
            $.post("order/order-refund",$("#refundInfo form").serialize(),function(data){
                $('#refundInfo').modal('hide');
                dataList.reload();
                alert(data.data);
            },"json");
        }

    });

    $('#startTime').bind('focus',function() {
        WdatePicker({
            maxDate : '#F{$dp.$D(\'endTime\',{s:-1})}',
            dateFmt : 'yyyy-MM-dd',
            onpicked : function(item) {
                $(this).change();
            }
        });
    });
    $('#endTime').bind('focus',function() {
        WdatePicker({
            minDate : '#F{$dp.$D(\'startTime\',{s:1})}',
            dateFmt : 'yyyy-MM-dd',
            onpicked : function(item) {
                $(this).change();
            }
        });
    });

    $("#receipts").blur(function(){
        var $this = $(this);
        var receipts = window.parseFloat($this.val()) || 0;
        receipts = receipts.toFixed(2);
        if(receipts <= 0) {
            alert("交通费总金额需大于0");
            $this.val("");
            return;
        }
        $this.val(receipts);
    })

    $("#all").click(function() {
        var checked = $("#all")[0].checked;
        if(checked){
            $(".single").each(function (i, obj){
                $(obj)[0].checked=true;
            })
        }else{
            $(".single").each(function (i, obj){
                $(obj)[0].checked=false;
            })

        }
    })
    $("#batch-check").click(function() {
        var ischeckde = $(".single:checked").val();
        if(ischeckde == undefined){
            alert("请勾选需要审核的订单")
            return false;
        }
        var check_val = [];
        $(".single:checked").each(function (i, obj){
            var id =$(obj).attr("value");
            check_val.push(id);
        })
        $("#check-modal form input[name=temp]").val(check_val.join(","))
        $('#check-modal').modal('show');
    })


    $(document).on("click","#check-modal .modal-footer .btn-success",function() {
        // if(!validate_check($("#checkModal form"))) return;
        $.post("order/order-check",$("#check-modal form").serialize(),function(result){
            if(result.data.length == 0) {
                alert("审核成功")
            }else{
                alert(result.data.join("\n"))
            }
            $('#check-modal').modal('hide');
            dataList.reload();
        },"json");
    });
    $(document).on("change","#cancelOrder input[name=reason][type=radio]",function() {
        var val = $(this).val();
        if(val==="5"){
            $("#cancelOrder textarea").show();
        }else {
            $("#cancelOrder textarea").hide();
            $("#reason").val("")
        }
    });
    $(document).on("click","#cancelOrder .modal-footer .btn-success",function() {
        var val =$("#cancelOrder input:radio:checked").val();
        if(!val){
            alert("请选择取消订单的原因");
            return
        }
        var reason=$("#cancelOrder input:radio:checked").attr("reason");
        if(val==="5"){
            reason=$("#reason").val();
            if(!reason) {
                alert("请输入其他原因");
                return
            }
        }
        $.post("order/order-cancel",{orderId:$("#cancelOrderId").val(),reason:reason},function(result){
            if(result.code==200){
                alert(result.data)
            }
            $('#cancelOrder').modal('hide');
            dataList.reload();
        },"json");


    });

    $("#adjustInfo form input[name='adjustPrice']").blur(function () {
        var total = parseFloat($("#adjustInfo form input[name='total']").val()) || 0;
        var adjustPrice = parseFloat($(this).val()) || 0;
        if(adjustPrice <=0 || adjustPrice >= total) {
            $(this).siblings(".msg").html("调价金额需大于0小于总价");
        }else {
            $(this).siblings(".msg").html("还需支付:"+(total-adjustPrice).toFixed(2)+"元");
        }
    })

    $(document).on("click","#adjustInfo .modal-footer .btn-success",function() {
        $.post("order/adjust-order",$("#adjustInfo form").serialize(),function(data){
            alert(data.data);
            dataList.reload();
            $('#adjustInfo').modal('hide');
        },"json");
    });
});