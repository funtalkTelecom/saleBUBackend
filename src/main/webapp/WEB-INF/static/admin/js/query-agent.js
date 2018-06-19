var dataList = null;
$(function() {
    /* 初始化入库单列表数据 */
    dataList = new $.DSTable({
        "url" : '/agent/list-agent',
        "ct" : "#result",
        "cm" : [{
            "header" : "企业名称",
            "dataIndex" : "commpayName"
        },{
            "header" : "法人",
            "dataIndex" : "person"
        },{
            "header" : "电话",
            "dataIndex" : "phone"
        },{
            "header" : "省份",
            "dataIndex" : "provinceName"
        },{
            "header" : "地市",
            "dataIndex" : "cityName"
        },{
            "header" : "区县",
            "dataIndex" : "districtName"
        },{
            "header" : "添加时间",
            "dataIndex" : "addDate",
        },{
            "header" : "注册人",
            "dataIndex" : "userName",
        },{
            "header" : "状态",
            "dataIndex" : "statustext",
        },{
            "header" : "营业执照",
            "dataIndex" : "tradingImg",
            "renderer" : function (v, record) {
                return"<a href='get-img/trading_url/1000/"+record.tradingImg+"' data-lightbox='tradingImg'><img src='get-img/trading_url/300/"+record.tradingImg+"' style='max-width: 40px;max-height: 40px;'> </a>";
            }
        },{
            "header" : "操作",
            "dataIndex" : "id",
            "renderer":function(v,record){
                var node = [];
                if(record.status ==1){
                    if(p_check) {
                        node.push('<a class="btn btn-success btn-xs update" href="javascript:void(0);">审核</a>')
                    }
                    // if(p_delete) {
                    //     node.push('<a class="btn btn-success btn-xs delete" href="javascript:void(0);">删除</a>')
                    // }
                }
                $operate = $("<div>"+$.trim(node.join("&nbsp;"),'--')+"</div>");

                $operate.find(".update").click(function () {
                    $.post("/find-agent-by-id",{id:v},function(data){
                        var _data=data.data;
                        $(".scomm").html(_data.commpayName);
                        $(".person").html(_data.person);
                        $(".phone").html(_data.phone);
                        var addresss = _data.provinceName+_data.cityName+_data.districtName+_data.address;
                        $(".address").html(addresss);
                        $("#tradingImg").attr("src","get-img/trading_url/300/"+_data.tradingImg);
                        $("#adtradingImg").attr("href","get-img/trading_url/1000/"+_data.tradingImg);
                        $("#id").val(_data.id);
                        $("#addConsumerId").val(_data.addConsumerId);
                        $('#checkModal').modal('show');
                    },"json");
                })
                $operate.find(".delete").click(function () {
                    if(confirm("确认删除？")) {
                        $.post("account/account-delete",{id:v},function(data){
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


    $(document).on("click","#checkModal .modal-footer .btn-success",function() {
        var status= parseInt($("#checkModal .status").val()) || 0;
        if(status ==-1){
            alert("请选择是否通过");
            return;
        }
        if(status == 3) {
            var remark = $("#checkModal form textarea[name='checkRemark']").val();
            if($.trim(remark) == '') {
                alert("请填写审核备注");
                return;
            }
        }
        $.post("check-agent",$("#checkModal form").serialize(),function(data){
            alert(data.data);
            dataList.load();
            $('#checkModal').modal('hide');
        },"json");
    });
});
