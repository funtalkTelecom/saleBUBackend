var dataList = null;
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
                }/*,{
                    "header" : "状态",
                    "dataIndex" : "1待付款；2已付款待推送（已付款尚未推送到仓储期）；3待配货(仓储系统已收到)；4待签收(仓储物流已取件)；5完成	status"
                },{
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
                    "dataIndex" : "地区+街道	address"
                },{
                    "header" : "通知出货时间",
                    "dataIndex" : "即调用发货成功时间	noticeShipmentDate"
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
                        $operate = $("<div>"+$.trim(node.join("&nbsp;"),'--')+"</div>");

                        $operate.find(".detail").click(function () {
                            //获取仓库商品类型下拉框
                            $.post("order/order-info", {orderId: v}, function (data) {
                                var _data = data.data;
                                formInit($("#orderInfo form"), _data);

                                $('#orderInfo').modal('show');
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

});
var titleStrObj = {
    "skuId":{
        "isShow":false,
        "title":"skuId",
        "type":'<input tag="sku_skuindex" type="text" name="skukey" value="skuvalue" class="col-xs-12">',
        "titleClass":"col-xs-1"
    },
    "skuTobPrice":{
        "isShow":true,
        "title":"2B价格",
        "type":'<input tag="sku_skuindex" type="text" name="skukey" value="skuvalue" class="col-xs-12">',
        "titleClass":"col-xs-1"
    },
    "skuTocPrice":{
        "isShow":true,
        "title":"2C价格",
        "type":'<input tag="sku_skuindex" type="text" name="skukey" value="skuvalue" class="col-xs-12">',
        "titleClass":"col-xs-1"
    },
    // "skuIsNum":{
    //     "isShow":true,
    //     "title":"是否号码",
    //     "type":'<select onchange="skuIsNumChange(this)" tag="sku_skuindex" name="skukey" selectValue="skuvalue"><option value="1">是</option><option value="2">否</option></select>',
    //     "titleClass":""
    // },
    "skuSaleNum":{
        "isShow":true,
        "title":"所售号码",
        "type":'<textarea tag="sku_skuindex" class="col-xs-12" style="height:34px;resize: none;width:150px" type="text" onclick="selectSaleNum(this)" name="skukey" textareaValue="skuvalue" class="col-xs-12" readonly></textarea>',
        "titleClass":"col-xs-1"
    },
    "skuNum":{
        "isShow":true,
        "title":"数量",
        "type":'<input tag="sku_skuindex" type="text" name="skukey" value="skuvalue" class="col-xs-12">',
        "titleClass":"col-xs-1"
    },
    "skuOrderType":{
        "isShow":true,
        "title":"商品类型",
        "type":'<select onchange="skuIsNumChange(this)" tag="sku_skuindex" name="skukey" selectValue="skuvalue"><option value="1">白卡</option><option value="2">普号</option><option value="3">普靓</option><option value="4">超靓</option></select>',
        "titleClass":""
    },
    "skuRepoOrderName":{
        "isShow":false,
        "title":"关联仓库商品",
        "type":'<input tag="sku_skuindex" type="hidden" name="skukey" value="skuvalue" class="col-xs-12">',
        "titleClass":""
    },
    "skuRepoOrder":{
        "isShow":true,
        "title":"关联仓库商品",
        "type":'<select tag="sku_skuindex" name="skukey" selectValue="skuvalue"><option value="1">白卡</option><option value="2">成卡</option><option value="3">普卡</option></select>',
        "titleClass":""
    },
    "operation":{
        "isShow":true,
        "title":"操作",
        "type":'<a class="btn btn-danger btn-xs delete" href="javascript:void(0);" onclick="deleteSkuRow(this)">删除</a>',
        "titleClass":""
    }
};