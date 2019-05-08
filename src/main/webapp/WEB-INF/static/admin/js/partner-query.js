var dataList = null;
$(function() {
	/* 初始化入库单列表数据 */
	dataList = new $.DSTable({
		"url" : 'partner/partner-list',
		"ct" : "#result",
		"cm" : [{
					"header" : "编码",
					"dataIndex" : "id"
				},{
					"header" : "姓名",
					"dataIndex" : "name"
				},{
					"header" : "电话号码",
					"dataIndex" : "phone"
				},{
                    "header" : "证件号码",
                    "dataIndex" : "idcard"
                },{
                    "header" : "证件图",
                    "dataIndex" : "keyValue",
                    "renderer":function(v,record){
                        var _html="<div><a target='_blank' href='get-img/idcard/1000/"+record.idcard_face+"'><img style='max-height:80px' src='get-img/idcard/1000/"+record.idcard_face+"'/></a>";
                        _html+="&nbsp;&nbsp;<a target='_blank' href='get-img/idcard/1000/"+record.idcard_back+"'><img style='max-height:80px' src='get-img/idcard/1000/"+record.idcard_back+"'/></a></div>";
                        return _html;
                    }
                },{
					"header" : "微信昵称",
					"dataIndex" : "nick_name"
				},{
                    "header" : "状态",
                    "dataIndex" : "partner_check",
                    "renderer":function(v,record){
                        if(v==1)return "已通过";
                        if(v==2)return "已拒绝";
                        else return "待审核";
                    }
                },{
					"header" : "操作",
					"dataIndex" : "id",
					"renderer":function(v,record){
						var node = [];
                        if(record.partner_check==0 && audit){
                            node.push('<div><a class="btn btn-success btn-xs audit" href="javascript:void(0);">审核</a></div>');
						}
                        $operate = $("<div>"+$.trim(node.join("&nbsp;"),'--')+"</div>");
                        $operate.find(".audit").click(function () {
                            $("#partnerInfo input[name='id']").val(v);
                            $("#partnerInfo").modal('show');
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

    var soption = {
        url:"",
        key:"keyId",
        value:"keyValue",
        onchange:"",
        onclick:"",
        param:{t:new Date().getTime()}
    };
    dictSelect($("#qstatus"), "partner-status", soption, false);

	$(".check_audit").click(function(){
	    var _id=$("#partnerInfo input[name='id']").val();
	    var check_status=$(this).attr("data");
	    var check_remark=$("#partnerInfo input[name='content']").val();
	    if(check_remark==''){
	        alert("请填写审核意见");
	        return;
        }
        $.post("partner/partner-check", {id:_id,"check_status":check_status,"check_remark":check_remark}, function (data) {
            dataList.reload();
            alert(data.data);
            $("#partnerInfo").modal('hide');
        }, "json");
    });


});
