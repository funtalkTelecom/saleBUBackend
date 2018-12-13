var dataList = null;
var channlList=null;
var start;
$(function() {
	/* 初始化入库单列表数据 */
    channlList = new $.DSTable({
		"url" : 'channel/channel-list',
		"ct" : "#channel",
		"cm" : [{
					"header" : "代理",
					"dataIndex" : "channel"
				},{
					"header" : "比例系数",
					"dataIndex" : "ratioPrice",
            		"renderer" : function (v, record) {
            		    $seq = $("<input readonly type='text' value='"+record.ratioPrice+"' >");
            		    return $seq;
            		}
        		},{
        		    "header" : "操作",
        		    "dataIndex" : "ratioPrice",
        		    "renderer":function(v,record){
        		        var node = [];

        		        node.push('<a class="btn btn-danger btn-xs edit" href="javascript:void(0);">修改</a>');
        		        node.push('<a class="btn btn-success btn-xs save" href="javascript:void(0);" style="display: none">保存</a>');


        		        $operate = $("<div>"+$.trim(node.join("&nbsp;"),'--')+"</div>");
        		        //点击详情
        		        $operate.find(".edit").click(function (){
        		           $(this).hide().parent().parent().prev().find("input").attr("readonly",false);
							$(this).next().show();
        		        });
                        $operate.find(".save").click(function (){
                            var re = /^\d+(?=\.{0,1}\d+$|$)/
                            var ratioPrice=$(this).parent().parent().prev().find("input").val();
                            if(!ratioPrice) {
                                alert("请填写系数")
								return false
							}
                            if(!re.test(ratioPrice)){
                                alert('请输入正确的系数');
                                return false
                            }
                            ratioPrice=parseFloat(ratioPrice)
							if(ratioPrice<1){
                                alert('请输入大于1的数');
                                return false
							}
                            if (confirm("提交后将按最新的价格规则同步更新当前在售的号码价格，请确认是否提交？")) {
                                $.post("channel/channel-edit", {
                                    id: record.id,
                                    ratioPrice: ratioPrice
                                }, function (data) {
                                    if (data.code != 200) {
                                        alert(data.data)
                                    }
                                    channlList.reload();
                                }, "json");
                            }
                        })

        		        return $operate;
        		    }
        		}],
		"getParam" : function() {
            var obj={};
            $(".query input,.query select").each(function(index,v2){
                var name=$(v2).attr("name");
                obj[name]=$(v2).val();
            });
            return obj;
		}
	});
    channlList.load();

	window.reload = function(){
        channlList.reload();
	}

    dataList = new $.DSTable({
        "url" : 'channel/feather-list',
        "ct" : "#feather",
        "cm" : [{
            "header" : "规则",
            "dataIndex" : "keyValue"
        },{
            "header" : "不带4价",
            "dataIndex" : "ext1",
            "renderer" : function (v, record) {
                $seq = $("<input readonly type='text' value='"+record.ext1+"' >");
                return $seq;
            }
        },{
        	"header" : "带4价",
            "dataIndex" : "ext2",
            "renderer" : function (v, record) {
                $seq = $("<input readonly type='text' value='"+record.ext2+"' >");
                return $seq;
            }
        },{
            "header" : "操作",
            "dataIndex" : "ratioPrice",
            "renderer":function(v,record){
                var node = [];

                node.push('<a class="btn btn-danger btn-xs edit" href="javascript:void(0);">修改</a>');
                node.push('<a class="btn btn-success btn-xs save" href="javascript:void(0);" style="display: none">保存</a>');


                $operate = $("<div>"+$.trim(node.join("&nbsp;"),'--')+"</div>");
                //点击详情
                $operate.find(".edit").click(function (){
                    $(this).hide().parent().parent().parent().find("input[type=text]").attr("readonly",false);
                    $(this).next().show();
                });
                $operate.find(".save").click(function (){
                    var re = /^\d+(?=\.{0,1}\d+$|$)/
                    var ext=$(this).parent().parent().parent().find("input[type=text]");
                   	var ext1 = ext.eq(0).val();
                    var ext2 =  ext.eq(1).val()
                    if(!ext1||!ext2) {
                        alert("请填写价格")
                        return false
                    }
                    if(!re.test(ext1)||!re.test(ext2)){
                        alert('请输入正确的价格');
                        return false
                    }
                    ext1=parseFloat(ext1)
                    ext2=parseFloat(ext2)
                    if(ext1<=0||ext2<=0){
                        alert('请输入大于0的数');
                        return false
                    }
                    if (confirm("提交后将按最新的价格规则同步更新当前在售的号码价格，请确认是否提交？")) {
                        $.post("channel/feather-edit", {id: record.id, ext1: ext1, ext2: ext2}, function (data) {
                            if (data.code != 200) {
                                alert(data.data)
                            }
                            dataList.reload();
                        }, "json");
                    }
                })

                return $operate;
            }
        }],
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

    window.reload = function(){
        dataList.reload();
    }
});

