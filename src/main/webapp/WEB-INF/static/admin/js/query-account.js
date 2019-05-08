var dataList = null;
$(function() {
    /* 初始化入库单列表数据 */
    dataList = new $.DSTable({
        "url" : 'account/list-account',
        "ct" : "#result",
        "cm" : [{
            "header" : "户名",
            "dataIndex" : "bankAccount"
        },{
            "header" : "开户行",
            "dataIndex" : "cardBankName"
        },{
            "header" : "开户支行",
            "dataIndex" : "subbranchBank"
        },{
            "header" : "账号",
            "dataIndex" : "cardAccountHidden"
        },{
            "header" : "创建时间",
            "dataIndex" : "addDate",
        },{
            "header" : "操作",
            "dataIndex" : "id",
            "renderer":function(v,record){
                var node = [];
                if(p_delete) {
                    node.push('<a class="btn btn-success btn-xs delete" href="javascript:void(0);">删除</a>')
                }
                $operate = $("<div>"+$.trim(node.join("&nbsp;"),'--')+"</div>");
                $operate.find(".delete").click(function () {
                    if(confirm("确认删除？")) {
                        $.get("account/account-delete/"+v,null,function(data){
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

    // citySelect($("#fcity"),17);
    var option = {
        url:"",
        key:"keyId",
        value:"keyValue",
        param:{t:new Date().getTime()}
    };
    // thirdCitySelect($("#fcity"),"ly",option);
    //银行list
    dictSelect($("#cardBankP"), "brank", option, false);


    $(document).on("click","#accountInfo .modal-footer .btn-success",function() {
        // 准备好Options对象
        var options = {
            // target:  null,
            type : "post",
            url: "account/account-edit",
            success : function(data) {
                dataList.load();
                $('#accountInfo').modal('hide');
                alert(data.data);
            }
        };
        // 将options传给ajaxForm
        $('#accountInfo form').ajaxSubmit(options);
    });

    $("select[name='accountType']").change(function () {
        var accountType=$(this).val();
        $(".wx-info").hide();
        $(".bank-info").hide();
        if(accountType==2)$(".wx-info").show();
        if(accountType==1)$(".bank-info").show();
    });

    $("select[name='cardBank']").change(function () {
        var cardBankName=$(this).find("option:selected").text();
        $("input[name='cardBankName']").val(cardBankName);
    });

    $(".send-sms").click(function () {
        $.post("sms/ack-corp",{},function(data){
            alert(data.data);
        },"json");
    });



    $(".nick-query").click(function () {
        var s_nick=$(".search-nick").val();
        $.post("consumer/list-nick",{nick:s_nick},function(data){
            if(data.code==200){
                $(".wx-info ul.ace-thumbnails").empty();
                $("input[name='consumer_id']").val(0);
                for(var i=0;i<data.data.length;i++){
                    var li_demo=$("li.li_demo").clone();
                    li_demo.removeClass("hidden").removeClass("li_demo");
                    li_demo.find(".wx-nick").html(data.data[i].nickName);
                    li_demo.find("img").attr("src",data.data[i].img);
                    li_demo.attr("data",data.data[i].id);
                    $(".wx-info ul.ace-thumbnails").append(li_demo);
                }
                listenThumbnails();
            }else{
                alert(data.data);
            }
        },"json");
    });
});

function listenThumbnails() {
    $(".wx-info .ace-thumbnails li").click(function () {
        $(".wx-info .ace-thumbnails .text").removeClass("opacity1");
        $(this).find(".text").addClass("opacity1");

        $("input[name='consumer_id']").val($(this).attr("data"));
    });
}
