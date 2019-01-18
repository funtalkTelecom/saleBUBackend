var dataList = null;
$(function() {
    dataList = new $.DSTable({
        "url" : 'activity/activity-list',
        "ct" : "#result",
        "cm" : [{
            "header" : "编号",
            "dataIndex" : "id"
        },{
            "header" : "标题",
            "dataIndex" : "title"
        }/*,{
            "header" : "竞拍规则",
            "dataIndex" : "epRule"
        }*/,{
            "header" : "开始时间",
            "dataIndex" : "beginDate"
        },{
            "header" : "结束时间",
            "dataIndex" : "endDate"
        },{
            "header" : "状态",
            "dataIndex" : "statusText"
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
                    $.post("epSale/epSale-info", {id: v}, function (data) {
                        var _data = data.data;
                        var isSale=data.isSale=="1"?true:false;//是否有上架的商品
                        $("#startTimePicker").val(_data.startTime);
                        $("#startTimePicker").attr("disabled",isSale);
                        $("#endTimePicker").val(_data.endTime);
                        $("#endTimePicker").attr("disabled",isSale);
                        $("#startTime").val(_data.startTime);
                        $("#endTime").val(_data.endTime);
                        $("#lastPayTime").val(_data.lastPayTime);
                        $("#lastPayTimePicker").val(_data.lastPayTime);
                        $("#lastPayTimePicker").attr("disabled",isSale);
                        initEPSalePics(data.epSalePics);
                        if(data.epSalePics.length>0)
                        {
                            $("#delImgI").attr("class","raty-cancel cancel-off-png");
                        }else
                        {
                            $("#delImgI").attr("class","");
                        }
                        editor.html(_data.epRule);
                        formInit($("#epSaleInfo form"), _data);
                        $('#epSaleInfo').modal('show');
                    }, "json");
                });
                $operate.find(".delete").click(function () {
                    if (confirm("确认删除？")) {
                        $.post("epSale/epSale-delete", {id: v}, function (data) {
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



    $(document).on("click","#NumsDiv .del",function(){
        $(this).parents("tr").remove();
    })
    
    $("#checkAdd").click(function () {
        // $("#NumsDiv tbody").remove();
        var agentId =  $("#agentId").val();
        var saleNum =  $("#saleNum").val();
        if(agentId==0){
            alert("请选择代理商");
            return false;
        }
        if(saleNum==""){
            alert("请添加号码");
            return false;
        }
        var saleNums = $("#saleNum").val();
        $.ajax({
            type : "POST",
            // async : "false",
            url : "activity/activity-check",
            data : ({
                "agentId" : agentId,
                "saleNums" : saleNums,
                "t"		: new Date().getTime()
            }),
            success :function (data) {
               if(data.code==888){
                   alert(data.data);
               }else{
                   var obj =data.data.numLists;
                   console.info(obj);
                   for (var i=0;i<obj.length;i++) {
                       $tr=$('<tr align="center" >' +
                           '<td >'+obj[i].num_resource+'</td>' +
                           '<td >'+obj[i].tele_type+'</td>' +
                           '<td >'+obj[i].price+'</td>' +
                           '<td ><input class="downPrice" type="text"/></td>' +
                           '<td><a class="btn btn-danger btn-xs del">移除</a></td></tr>');
                       $("#NumsDiv tbody").append($tr.append('<input type="hidden" class="num_resource" value="'+obj[i].num_resource+'" >' +
                           '<input type="hidden" class="price" value="'+obj[i].price+'"  >'));
                   }
                   $("#saleNum").val("");
               }
                
            }
        })
    })



    $(document).on("click", "#epSaleInfo .modal-footer .btn-success", function () {

        var title = $("#title").val();
        var agentId = $("#agentId").val();
        var type = $("#type").val();
        var gStartTime = $("#gStartTime").val();
        var startH = $("#startH").val();
        var gEndTime = $("#gEndTime").val();
        var endH = $("#endH").val();
        var sH = $("#startH").find("option:selected").attr("keyValue");
        var eH = $("#endH").find("option:selected").attr("keyValue");
        var priceCount = $("#NumsDiv tbody tr").length;
        var ss = gStartTime+" "+sH+":00:00";
        var ee = gEndTime+" "+eH+":00:00";

        $("#beginDates").val(ss);
        $("#endDates").val(ee);


        // if (title == "") {
        //     alert("标题不能为空!")
        //     return false;
        // }
        // if (agentId ==0) {
        //     alert("代理商不能为空!")
        //     return false;
        // }
        // if (type ==0) {
        //     alert("活动类型不能为空!")
        //     return false;
        // }
        // if (gStartTime == "" || startH ==-1 ||gEndTime == "" || endH ==-1) {
        //     alert("活动时间不能为空!")
        //     return false;
        // }
        if(priceCount == 0){
            alert("请添加号码！");
            return false;
        }
        $("#NumsDiv tbody tr").each(function(i, obj){
           var downPrice = $(obj).find(".downPrice").val();
           if(downPrice==""){
               alert("请填写活动价格！");
               return false;
           }
        })
        var json = [];
        $("#NumsDiv tbody tr").each(function(i, obj){
            json.push('{"num_resource":"'+$(obj).find(".num_resource").val()+'"' +
                ',"downPrice":"'+$(obj).find(".downPrice").val()+'"' +
                ',"price":"'+$(obj).find(".price").val()+'"}');
        })
        $("#strjson").val("["+json.join(",")+"]");
        var options = {
            type: "post",
            url: "activity/activity-edit",
            success: function (data) {
                dataList.load();
                $('#epSaleInfo').modal('hide');
                alert(data.data);
            }
        };
        // 将options传给ajaxForm
        $('#epSaleInfo form').ajaxSubmit(options);
    });

})

function selectSaleNum(obj){
    $('#saleNumInfo').modal('show');

}
