var dataList = null;
var start;
$(function() {
	/* 初始化入库单列表数据 */
	dataList = new $.DSTable({
		"url" : 'numprice/numprice-list',
		"ct" : "#result",
		"cm" : [{
					"header" : "地市名称",
					"dataIndex" : "cityName"
				},{
					"header" : "号码",
					"dataIndex" : "resource"
				},{
					"header" : "网络制式",
					"dataIndex" : "netType"
				},{
					"header" : "渠道",
					"dataIndex" : "channel"
				},{
					"header" : "价格",
            		"dataIndex" : "price"
        		},{
                    "header" : "级别",
                    "dataIndex" : "numLevel"
                },{
                    "header" : "最低消费",
                    "dataIndex" : "lowConsume"
                },{
					"header" : "代理商",
					"dataIndex" : "commpayName",
            		"renderer" : function(v, record) {
                        start = dataList.pm.start;
            		    if (!v) return "默认";
            		    else  return v
            		}
				},{
					"header" : "添加时间",
					"dataIndex" : "addDate"
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

// $(document).on("focus","#commpayName",function(){
//     $(this).autocomplete({
//         source:function(query, process){
//             $.post("agent/query-agent-by-CName",{commpayName:query.term,t:new Date().getTime()},function (result) {
//             	if(result.code==200){
//                     return process(result.data);
// 				}
//             });
//         },
//         select: function(e, ui) {
//             $("#agentId").val(ui.item.id);
//             $("#agentCommpayName").val(ui.item.value);
//         },
//         disabled:false,
//         autoFocus:true,
//         delay:500,
//         isSelect:true,
//         minLength:2
//     });
// });
$(document).on("focus","#commpayNameB",function(){
    $(this).autocomplete({
        source:function(query, process){
            $.post("agent/query-agent-by-CName",{commpayName:query.term,t:new Date().getTime()},function (result) {
            	if(result.code==200){
                    return process(result.data);
				}
            });
        },
        select: function(e, ui) {
            $("#agentIdB").val(ui.item.id);
            $("#agentCommpayNameB").val(ui.item.value);
        },
        disabled:false,
        autoFocus:true,
        delay:500,
        isSelect:true,
        minLength:2
    });
});
$(document).on("focus","#commpayNameT",function(){
    $(this).autocomplete({
        source:function(query, process){
            $.post("agent/query-agent-by-CName",{commpayName:query.term,t:new Date().getTime()},function (result) {
                if(result.code==200){
                    return process(result.data);
                }
            });
        },
        select: function(e, ui) {
            $("#agentIdT").val(ui.item.id);
            $("#agentCommpayNameT").val(ui.item.value);
        },
        // open: function( event, ui ) {  //自己的想法是给前两个加change事件，直接清空价格
        //     console.log(1)
        // },
        disabled:false,
        autoFocus:true,
        delay:500,
        isSelect:true,
        minLength:2
    });
});
$(document).on("blur","#resourceT",function(){
    blurQueryAgentNumprice();
});
$(document).on("change","#resourceT",function(){
    clearPrice();
});
$(document).on("blur","#commpayNameT",function(){
    blurQueryAgentNumprice();
});
$(document).on("change","#commpayNameT",function(){
    clearPrice();
});
$(document).on("blur","input[name=resource]",function(){
    $(this).val($(this).val().split("-").join(""));
});
// $(document).on("change","#commpayName",function(){
//     var commpayName=$("#commpayName").val();
//     var agentCommpayName=$("#agentCommpayName").val();
//     console.log(agentCommpayName)
//     console.log(commpayName)
//     if(commpayName==""){
//         $("#channel").parent().show();
//     }else if (agentCommpayName&&agentCommpayName!=commpayName){
//         $("#channel").parent().show();
//     } else {
//         $("#channel").parent().hide();
//         $("#channel").val("-1");
//     }
// });
// $(document).on("change","#channel",function(){
//     var channel = $("#channel").val();
//     if (channel==-1){
//         $("#commpayName").parent().show();
//     }else {
//         $("#commpayName").parent().hide();
//         $("#commpayName").val("");
//         $("#agentId").val("");
//         $("#agentCommpayName").val("");
//     }
// });
function clearPrice() {
    $("#price").val("");
    $("#agentPrice").val("");
}
function blurQueryAgentNumprice() {
    var resource = $("#resourceT").val();
    if(!resource){
        return ;
    }
    var commpayNameT = $("#commpayNameT").val();
    var agentIdT = $("#agentIdT").val();
    if(!commpayNameT){
        return ;
    }
    if(!agentIdT){
        return ;
    }
    queryAgentNumprice();
}
function queryAgentNumprice() {
	var resource = $("#resourceT").val().trim();
	if(resource.length<11){
        alert("请输入完整的号码");
        return ;
	}
    if(resource.match(/^1[345789]\d{9}$/) == null){
        alert("手机号格式有误");
        return ;
    }
    var commpayNameT = $("#commpayNameT").val();
    var agentIdT = $("#agentIdT").val();
    if(!commpayNameT){
        alert("请输入代理商");
        return ;
	}
	if(!agentIdT){
        alert("请选择代理商");
        return ;
	}
    var agentCommpayName = $("#agentCommpayNameT").val();
	if(agentCommpayName!=commpayNameT){
        return ;
    }
    $.post("numprice/query-agent-numprice",{resource:resource,agentId:agentIdT,commpayName:commpayNameT},function(data){
        if(data.code==200){
            if(data.data){
                $("#price").val(data.data.price)
                $("#agentPrice").val(data.data.agentPrice)
            }else {
                $("#price").val("")
                $("#agentPrice").val("")
                alert("无数据")
            }
        }else {
            alert(data.data)
        }

    },"json");
};
$(document).on("click","#goBtn",function() {
	var resource = $("#resourceT").val().trim();
	if(resource.length<11){
        alert("请输入完整的号码");
        return ;
	}
    if(resource.match(/^1[345789]\d{9}$/) == null){
        alert("手机号格式有误");
        return ;
    }
    var commpayNameT = $("#commpayNameT").val();
    var agentIdT = $("#agentIdT").val();
    if(!commpayNameT){
        alert("请输入代理商");
        return ;
	}
	if(!agentIdT){
        alert("请选择代理商");
        return ;
	}
	var price = $("#agentPrice").val();
	var pprice = $("#price").val();
    var re = /^\d+(?=\.{0,1}\d+$|$)/
    if(!price){
        alert("请填写价格");
        return;
    }
    if(!re.test(price)){
        alert('请输入正确的价格');
        return false
    }
    price=parseFloat(price)
    pprice=parseFloat(pprice)
    if(!pprice){
        alert("请先查询一下价格");
        return false
    }
    if(price<=0){
        alert('请输入大于0的数');
        return false
    }
    if(pprice==price){
        alert('价格相同无需修改');
        return false
    }
    $.post("numprice/save-agent-numprice",{resource:resource,agentId:agentIdT,commpayName:commpayNameT,price:price},function(data){
        dataList.reload();
        $('#myModal').modal('hide');
    },"json");
});
$(document).on("click","#saleNumInfo .modal-footer .btn-success",function() {
    var commpayNameT = $("#commpayNameB").val();
    var agentIdT = $("#agentIdB").val();
    if(!commpayNameT){
        alert("请输入代理商");
        return ;
    }
    if(!agentIdT){
        alert("请选择代理商");
        return ;
    }
    var price = $("#priceB").val();
    var re = /^\d+(?=\.{0,1}\d+$|$)/
    if(!price){
        alert("请填写价格");
        return;
    }
    if(!re.test(price)){
        alert('请输入正确的价格');
        return false
    }
    var agentCommpayName = $("#agentCommpayNameB").val();
    if(agentCommpayName!=commpayNameT){
        return ;
    }
    var resource = $("#saleNum").val();
    if(!resource){
        alert('请输入号码');
        return false
    }
    $.post("numprice/save-agent-numprices",{resource:resource,agentId:agentIdT,commpayName:commpayNameT,price:price},
        function(data){
            dataList.reload();
            $('#saleNumInfo').modal('hide');
             if(data.code==888){
                $('#failnumMod').modal('show');
                $("#failnum").val(data.data)
            }
            alert("成功");
    },"json");
});

$(document).on("click","#export",function() {
    var obj={};
    $(".query input[type!=checkbox],.query select").each(function(index,v2){
        var name=$(v2).attr("name");
        obj[name]=$(v2).val();
    });

    bootbox.dialog({
        boxCss:{"width":"400px"},
        title: "<span class='bigger-110'>确认框</span>",
        message: "请选择导出哪些数据?",
        buttons:
            {
                "success" :
                    {
                        "label" : "<i class='ace-icon fa fa-check'></i> 当前页",
                        "className" : "btn-sm btn-success",
                        "callback": function() {
                            $("#export").prop("disabled", true);
                            obj["isCurrentPage"] = "1";
                            var param = "start="+start+"&";
                            for (key in obj) {
                                param += key+"="+obj[key]+"&";
                            }
                            downloadFile("numprice/numprice-export", param, '号码价格列表.xlsx','$("#export").prop("disabled", false)');
                        }
                    },
                "danger" :
                    {
                        "label" : "全部",
                        "className" : "btn-sm btn-danger",
                        "callback": function() {
                            $("#export").prop("disabled", true);
                            obj["isCurrentPage"] = "0";
                            var param = "";
                            for (key in obj) {
                                param += key+"="+obj[key]+"&";
                            }
                            downloadFile("numprice/numprice-export", param, '号码价格列表.xlsx','$("#export").prop("disabled", false)');
                        }
                    }
            }
    });
});
function downloadFile(url, param, fileName, functionStr){
    // url = 'number/number-export';
    console.log(url)
    var xhr = new XMLHttpRequest();
    xhr.open('POST', url+"?"+param, true);        // 也可以使用POST方式，根据接口
    xhr.responseType = "blob";    // 返回类型blob
    // 定义请求完成的处理函数，请求前也可以增加加载框/禁用下载按钮逻辑
    xhr.onload = function () {
        // 请求完成
        if (this.status === 200) {
            // 返回200
            var blob = this.response;
            var reader = new FileReader();
            reader.readAsDataURL(blob);    // 转换为base64，可以直接放入a表情href
            reader.onload = function (e) {
                // 转换完成，创建一个a标签用于下载
                var a = document.createElement('a');
                a.download = fileName;
                a.href = e.target.result;
                $("body").append(a);    // 修复firefox中无法触发click
                a.click();
                $(a).remove();
                eval(functionStr);
            }
        }
    };
    xhr.send(JSON.stringify(param));
}
