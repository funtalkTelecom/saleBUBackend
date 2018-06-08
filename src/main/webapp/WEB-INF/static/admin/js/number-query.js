var dataList = null;
$(function() {
	/* 初始化入库单列表数据 */
	dataList = new $.DSTable({
		"url" : '/number/number-list',
		"ct" : "#result",
		"cm" : [{
					"header" : "地市名称",
					"dataIndex" : "cityName"
				},{
					"header" : "网络类型",
					"dataIndex" : "netType"
				},{
					"header" : "号码",
					"dataIndex" : "numResource"
				},{
					"header" : "靓号类型",
					"dataIndex" : "numType"
				},{
					"header" : "级别",
					"dataIndex" : "numLevel"
				},{
					"header" : "最低消费",
					"dataIndex" : "lowConsume"
				},{
					"header" : "是否带4",
					"dataIndex" : "with4"
				},{
					"header" : "特征",
					"dataIndex" : "feature"
				},{
					"header" : "号段",
					"dataIndex" : "sectionNo"
				},{
					"header" : "此号码最多的数字",
					"dataIndex" : "moreDigit"
				},{
					"header" : "供应商",
					"dataIndex" : "seller"
				},{
					"header" : "买家",
					"dataIndex" : "buyer"
				},{
					"header" : "iccid",
					"dataIndex" : "iccid"
				},{
					"header" : "状态",
					"dataIndex" : "status"
				},{
					"header" : "运营商类型",
					"dataIndex" : "teleType"
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

    var option = {
        url:"",
        key:"keyId",
        value:"keyValue",
        onchange:"",
        onclick:"",
        param:{t:new Date().getTime()}
    };
    dictCheckBoxDefault($("#numberTags"), "num_tags", option);

    //添加标签确定按钮
    $(document).on("click","#editTags .modal-footer .btn-success",function() {
        $.post("number/numRule-addTags",$("#editTags form").serialize(),function(data){
            $("#editTags .modal-footer .btn-success").attr("disabled", "disabled");
            // dataList.reload();
            $('#editTags').modal('hide');
            alert(data.data);
        },"json");
	});

    //点击设置标签,初始化
    $("button[data-target=#editTags]").bind("click", function () {
        $("#numberTags").html("");
        dictCheckBoxDefault($("#numberTags"), "num_tags", option);
	});
});
