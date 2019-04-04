var dataList = null;
var orderStatus = {
    "0":"删除",
    "1":"草稿",
    "2":"有效",
    "3":"失效",
    "4":"过期"
};
$(function() {
	dataList = new $.DSTable({
		"url" : 'partner/promotion-plan-list',
		"ct" : "#result",
		"cm" : [{
                    "header" : "推广编号",
                    "dataIndex" : "id"
                },/*{
                    "header" : "费用类型",
                    "dataIndex" : "consumerName"
                },*/{
                    "header" : "参与方式",
                    "dataIndex" : "promotion",
                    "renderer":function(v,record) {
                        //0全部号码1根据号码销售价格段2指定号码
                        if(v=='0')return '<span class="green">全部号码</span>';
                        if(v=='1')return '<span class="blue">指定价格段</span>';
                        if(v=='2')return '<span class="green">指定号码</span>';
                        return '未知';
                    }
                },{
                    "header" : "奖励方案",
                    "dataIndex" : "awardWay",
                    "renderer":function(v,record) {//1固定金额2订单比例
                        if(v=='1')return '<span class="green">固定金额</span>';
                        if(v=='2')return '<span class="blue">销售价比例</span>';
                        return '未知';
                    }
                },{
                    "header" : "奖励金额",
                    "dataIndex" : "award",
                    "renderer":function(v,record) {
                        if(record.awardWay=='1')return '<span class="green">'+v+'元</span>';
                        if(record.awardWay=='2')return '<span class="blue">销售价的'+v+'%</span>';
                        return '未知';
                    }
                },{
                    "header" : "奖励上限",
                    "dataIndex" : "isLimit",
                    "renderer":function(v,record) {//0无1有
                        if(v=='0')return '<span class="green">无上限</span>';
                        if(v=='1')return '<span class="blue">最高奖励'+record.limitAward+'元</span>';
                        return '未知';
                    }
                },{
                    "header" : "起效价/止效价",
                    "dataIndex" : "beginPrice",
                    "renderer":function(v,record) {
                        return '<div class="green">起：'+v+'元</div><div class="blue">止：'+record.endPrice+'元</div>';
                    }
                },{
                    "header" : "推广号码",
                    "dataIndex" : "num"
                },{
                    "header" : "起效时间/止效时间",
                    "dataIndex" : "beginDate",//endDate
                    "renderer":function(v,record) {
                        var beginDate = new Date(v).format("yyyy-MM-dd hh:mm:ss");
                        var endDate = new Date(record.endDate).format("yyyy-MM-dd hh:mm:ss");
                        return '<div class="green">起：'+beginDate+'</div><div class="blue">止：'+endDate+'</div>';
                    }
                },{
                    "header" : "状态",
                    "dataIndex" : "status",
                    "renderer":function(v,record) {
                        //0删除1草稿2有效3无效4过期
                        if(v=='1')return '<span class="label label-info arrowed">草稿</span>';
                        if(v=='2')return '<span class="label label-danger arrowed">有效</span>';
                        if(v=='3')return '<span class="label arrowed">失效</span>';
                        if(v=='4')return '<span class="label arrowed">过期</span>';
                        if(v=='99')return '<span class="label arrowed">删除</span>';
                        return '未知';
                    }
                },{
					"header" : "操作",
					"dataIndex" : "status",
					"renderer":function(v,record){
						var node = [];
						if(v=='1' && pp_edit)node.push('<a class="btn btn-xs btn-info edit" href="javascript:void(0);">修改</a>');
                        if(v=='2' && pp_edit)node.push('<a class="btn btn-danger btn-xs cancel" href="javascript:void(0);">取消推广</a>');
                        node.push('<a class="btn btn-success btn-xs detail" href="javascript:void(0);">详情</a>');
                        $operate = $("<div>"+$.trim(node.join("&nbsp;"),'--')+"</div>");
                        $operate.find(".detail").click(function () {
                            findPromotionPlanInfo(record.id,"detail");
                        });
                        $operate.find(".edit").click(function () {//编辑
                            findPromotionPlanInfo(record.id,"edit");
                        });
                        $operate.find(".cancel").click(function () {
                            var bool=confirm("确认取消此推广计划?");
                            if(!bool)return;
                            $.post("partner/promotion-plan-expire", {id:record.id}, function (data) {
                                alert(data.data);
                                dataList.reload();
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

    var soption = {
        url:"",
        key:"keyId",
        value:"keyValue",
        onchange:"",
        onclick:"",
        param:{t:new Date().getTime()}
    };
    //dictSelect($("#qstatus"), "orderStatus", soption, false);

	$("#query").click(function() {
		dataList.load();
	});
	
	window.reload = function(){
		dataList.reload();
	}
    $("#promotion-id").change(function(){
        var val=$(this).val();
        promotionModel(val);
    })
    $("#awardWay-id").change(function(){
        var val=$(this).val();
        awardWayModel(val)
    })
    function promotionModel(val){
        $(".plan-price").hide();
        $(".plan-nums").hide();
        if(val=='1') $(".plan-price").show();
        if(val=='2') $(".plan-nums").show();
    }
    function awardWayModel(val){
	    $("#limitAward-id").removeAttr("readonly");
        $("#isLimit-id").removeAttr("readonly");
        if(val=='1'){
            $(".award-sign").html("元");
            $("#limitAward-id").attr("readonly","readonly");
            $("#isLimit-id").attr("readonly","readonly");
        }
        if(val=='2')$(".award-sign").html("%");
    }
    $(".draft-promotion-plan").click(function(){
        promotionPlanSubmit(1);
    });
    $(".publish-promotion-plan").click(function(){
        var bool=confirm("确认发布此推广计划?");
        if(!bool)return;
        promotionPlanSubmit(2);
    });
    $("#isLimit-id").change(function(){
        var isl=$(this).val();
        if(isl==0)$("#limitAward-id").val(0);
    });
    function promotionPlanSubmit(operation){
        $("#operation-id").val(operation);
        var post_data=$("#promotion-plan-form").serializeArray();
        $.ajax({
            url : 'partner/promotion-plan',
            data : post_data,
            type : "post",
            dataType : "json",
            success : function(result) {
                alert(result.data);
                $("#check_form").hide();
                $("#query_table").show();
                window.location.reload();
            }
        });
    }


    function findPromotionPlanInfo(ppid,opt){
        $.get("partner/promotion-plan", {id:ppid}, function (data) {
            if(data.code!='200'){
                alert(data.data);
                return;
            }
            var _tag=$('#add-promotion-plan');
            updateFormInit(_tag,data.data,false);
            _tag.find("[name=beginDate]").val(new Date(data.data.beginDate).format("yyyy-MM-dd hh:mm:ss"));
            _tag.find("[name=endDate]").val(new Date(data.data.endDate).format("yyyy-MM-dd hh:mm:ss"));
            awardWayModel(data.data.awardWay)
            promotionModel(data.data.promotion);
            var nums="";
            for(var i=0;i<data.data.list.length;i++){
                var _obj=data.data.list[i];
                nums+=_obj.num+"\r\n";
            }
            _tag.find("[name=nums]").val(nums);
            _tag.find("[name=addDate]").val(new Date(data.data.addDate).format("yyyy-MM-dd hh:mm:ss"));
            _tag.find("[name=updateDate]").val(new Date(data.data.updateDate).format("yyyy-MM-dd hh:mm:ss"));
            if(opt=='detail'){
                _tag.find("input,select,textarea").attr("disabled","disabled");
                _tag.find(".detail-info").show();
                _tag.find(".modal-footer").hide();
            }
            if(opt=='edit'){
                _tag.find("input,select,textarea").removeAttr("disabled");
                _tag.find(".detail-info").hide();
                _tag.find(".modal-footer").show();
            }
            $('#add-promotion-plan').modal('show');
        }, "json");

    }

    $('#add-promotion-plan').on('hide.bs.modal', function () {

        $(this).find("input,select,textarea").removeAttr("disabled");
        $(this).find(".detail-info").hide();
        $(this).find(".modal-footer").show();

    })

});