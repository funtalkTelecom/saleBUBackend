var dataList = null;
var with4s, numStatus;
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
					"dataIndex" : "with4",
            		"renderer":function(v,record){
                        return with4s[v];
					}
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
					"dataIndex" : "status",
					"renderer":function(v,record){
						return numStatus[v];
					}
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
            $(".query input[type!=checkbox],.query select").each(function(index,v2){
                var name=$(v2).attr("name");
                obj[name]=$(v2).val();
            });
            var c = $(".query input[type=checkbox]:checked").length;
            $(".query input[type=checkbox]:checked").each(function(index,v2){
                var name=$(v2).attr("name");
                obj[name] = obj[name]==undefined?"":obj[name];
                obj[name]=obj[name]+$(v2).val()+",";
                if(c<=(index+1)) obj[name] = obj[name].substring(0, obj[name].length - 1);
            });
			return obj;
		}
	});

    $.post("dict-to-map", {group: "with4"},function(data){
        with4s = data;
    },"json");

    $.post("dict-to-map", {group: "num_status"},function(data){
        numStatus = data;
    },"json");
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
        labelClass:"col-xs-2",
        param:{t:new Date().getTime()}
    };
    dictCheckBoxDefault($("#numberTags"), "num_tags", option);
    var qoption = {
        url:"",
        key:"keyId",
        value:"keyValue",
        onchange:"",
        onclick:"",
        labelClass:"col-xs-1",
        param:{t:new Date().getTime()}
    };
    dictCheckBoxDefault($("#qnumberTags"), "num_tags", qoption);

    var soption = {
        url:"",
        key:"keyId",
        value:"keyValue",
        onchange:"",
        onclick:"",
        param:{t:new Date().getTime()}
    };
    dictSelect($("#qstatus"), "num_status", soption, false);

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

    var getpos=function(str){
        //获取元素绝对位置
        var Left=0,Top=0;
        do{Left+=str.offsetLeft,Top+=str.offsetTop;}
        while(str=str.offsetParent);
        var scr=document.getElementById('scrollDiv');//减去图层滚动条

        if(scr!=null && scr!=undefined){
            Left=Left-scr.scrollLeft;
        }
        return {"Left":Left,"Top":Top};
    };
    //地市下拉框
    function showMenu(){
        //处理地市下拉位置偏移问题
        var xy = getpos($("#gSaleCityStr").get(0));
        var option = {
            "menuContentCss":'{"left":"'+(Number(xy.Left)-Number($("#sidebar").css("height")=="1px"?0:$("#sidebar").css("width").replace("px", ""))-5)+'px"}'
        };
        showCityMenu(option);
    }
    var zNodes;
    $.post("query-city-ztree", {pid: 0, isopen:false}, function (data) {
        zNodes = data;
        $.fn.zTree.init($("#cityTree"), setting, zNodes);
        $("#gSaleCityStr").off("click").on("click", showMenu);
    }, "json");
    $("#reset").click(function(){
        $.fn.zTree.init($("#cityTree"), setting, zNodes);
	});
    //地市下拉框end
});
