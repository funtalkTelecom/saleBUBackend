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
                if(record.status==1){
                    if(p_cancel) {
                        node.push('<a class="btn btn-success btn-xs delete" href="javascript:void(0);">失效</a>')
                    }
                }
                if(p_edit) {
                    node.push('<a class="btn btn-success btn-xs xq" href="javascript:void(0);">详情</a>')
                }
                $operate = $("<div>"+trim(node.join("&nbsp;"),'--')+"</div>");
                $operate.find(".xq").click(function () {
                    $.post("activity/find-activity-by-id", {id: v}, function (data) {
                        var _data = data.data;
                        $("#numDivs").attr("style","display:none;");
                        $("#epSaleInfo .modal-footer .btn-success").attr("disabled","disabled");
                        formInit($("#epSaleInfo form"), _data.at);
                        var activityItemList = _data.activityItemList;
                        for (var i=0;i<activityItemList.length;i++) {
                            $tr=$('<tr align="center" >' +
                                '<td >'+activityItemList[i].num+'</td>' +
                                '<td >'+activityItemList[i].price+'</td>' +
                                '<td >'+activityItemList[i].down_price+'</td>' +
                                '<td></td></tr>');
                            $("#NumsDiv tbody").append($tr);
                        }

                        $('#epSaleInfo').modal('show');
                    }, "json");
                });
                $operate.find(".delete").click(function () {
                    if (confirm("确认失效吗？")) {
                        $.post("activity/activity-cancel", {id: v}, function (data) {
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

        var istrue =true;
        $("#NumsDiv tbody tr").each(function(i, obj){
           var num = $(obj).find(".num_resource").val();
            if(saleNum.indexOf(num) != -1){
                istrue=false;
                alert(num+"已经存在列表中");
                return false;
            }

        })
        if(!istrue){
            return false;
        }
        $.ajax({
            type : "POST",
            // async : "false",
            url : "activity/activity-check",
            data : ({
                "agentId" : agentId,
                "saleNums" : saleNum,
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
                           '<td >'+obj[i].resource+'</td>' +
                           '<td >'+obj[i].price+'</td>' +
                           '<td ><input class="downPrice" type="text" onkeyup="value=value.replace(/[^\\d.]/g,\'\')"  /></td>' +
                           '<td><a class="btn btn-danger btn-xs del">移除</a></td></tr>');
                       $("#NumsDiv tbody").append($tr.append('<input type="hidden" class="num_resource" value="'+obj[i].resource+'" >' +
                           '<input type="hidden" class="price" value="'+obj[i].price+'"  >' +
                           '<input type="hidden" class="agentid" value="'+obj[i].agent_id+'"  > '));
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


        if (title == "") {
            alert("标题不能为空!")
            return false;
        }
        if (agentId ==0) {
            alert("代理商不能为空!")
            return false;
        }
        if (type ==0) {
            alert("活动类型不能为空!")
            return false;
        }
        if (gStartTime == "" || startH ==-1 ||gEndTime == "" || endH ==-1) {
            alert("活动时间不能为空!")
            return false;
        }
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
                ',"agentid":"'+$(obj).find(".agentid").val()+'"' +
                ',"price":"'+$(obj).find(".price").val()+'"}');
        })
        $("#strjson").val("["+json.join(",")+"]");
        var options = {
            type: "post",
            url: "activity/activity-edit",
            success: function (data) {
                dataList.load();
                $("#NumsDiv tbody tr ").remove();
                $('#epSaleInfo').modal('hide');
                alert(data.data);
            }
        };
        // 将options传给ajaxForm
        $('#epSaleInfo form').ajaxSubmit(options);
    });

    $("#close").click(function(){
        $("#numDivs").attr("style","display:block;");
        $("#epSaleInfo .modal-footer .btn-success").attr("disabled",true);
        $("#NumsDiv tbody tr ").remove();
        $('#epSaleInfo').modal('hide');
    });

    $("#modColse").click(function(){
        $("#numDivs").attr("style","display:block;");
        $("#NumsDiv tbody tr ").remove();
        $("#epSaleInfo .modal-footer .btn-success").attr("disabled",true);
        $('#epSaleInfo').modal('hide');

    });

    $('#agentId').on('change',function(){
        $("#NumsDiv tbody tr ").remove();
    })


})

