var dataList = null;
var start;
$(function() {
	/* 初始化入库单列表数据 */
	dataList = new $.DSTable({
		"url" : 'numprice/numprice-list',
		"ct" : "#result",
		"cm" : [{
					"header" : "地市名称",
					"dataIndex" : "city_name"
				},{
					"header" : "号码",
					"dataIndex" : "resource"
				},{
					"header" : "运营商",
					"dataIndex" : "net_type"
				},{
					"header" : "渠道",
					"dataIndex" : "channel"
				},{
					"header" : "价格",
            		"dataIndex" : "price"
        		},{
					"header" : "代理商",
					"dataIndex" : "commpay_name",
            		"renderer" : function(v, record) {
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

$(document).on("focus","#commpayName",function(){
    $(this).autocomplete({
        source:function(query, process){
            $.post("agent/query-agent-by-CName",{commpayName:query.term,t:new Date().getTime()},function (result) {
            	if(result.code==200){
                    return process(result.data);
				}
            });
        },
        select: function(e, ui) {
            $("#agentId").val(ui.item.id);
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
            $("#agentCommpayName").val(ui.item.value);
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
	var resource = $("#resourceT").val();
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
    var agentCommpayName = $("#agentCommpayName").val();
	if(agentCommpayName!=agentCommpayName){
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
	var resource = $("#resourceT").val();
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
