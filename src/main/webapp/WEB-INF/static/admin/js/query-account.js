var dataList = null;
$(function() {
	/* 初始化入库单列表数据 */
	dataList = new $.DSTable({
		"url" : '/account/list-account',
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
					"dataIndex" : "cardAccount"
				},{
					"header" : "创建时间",
					"dataIndex" : "addDate",
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
                        $operate = $("<div>"+$.trim(node.join("&nbsp;"),'--')+"</div>");

                        $operate.find(".update").click(function () {
                            $.post("account/account-info",{id:v},function(data){
                                var _data=data.data;
                                formInit($("#accountInfo form"), _data);
                                $('#accountInfo').modal('show');
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

    // citySelect($("#fcity"),17);
    var option = {
        url:"",
        key:"keyId",
        value:"keyValue",
        param:{t:new Date().getTime()}
    };
    // thirdCitySelect($("#fcity"),"ly",option);
	//银行list
    dictSelect($("#cardBankP"), "brank", option, true);


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
});
