var dataList = null;
$(function() {
    /* 初始化入库单列表数据 */
    dataList = new $.DSTable({
        "url" : 'corp/income/list',
        "ct" : "#result",
        "cm" : [{
            "header" : "订单号",
            "dataIndex" : "contractno"
        },{
            "header" : "类型",
            "dataIndex" : "act_type_str"
        },{
            "header" : "备注",
            "dataIndex" : "order_name"
        },{
            "header" : "金额",
            "dataIndex" : "amt"
        },{
            "header" : "支付后余额",
            "dataIndex" : "after_amt"
        },{
            "header" : "创建时间",
            "dataIndex" : "add_date",
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

