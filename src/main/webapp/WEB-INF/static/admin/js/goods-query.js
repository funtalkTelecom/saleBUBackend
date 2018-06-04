var dataList = null;
$(function() {
	/* 初始化入库单列表数据 */
	dataList = new $.DSTable({
		"url" : '/goods/goods-list',
		"ct" : "#result",
		"cm" : [{
					"header" : "商品名称",
					"dataIndex" : "gName"
				},{
					"header" : "宣传语",
					"dataIndex" : "gAd"
				},{
                    "header" : "开始时间",
                    "dataIndex" : "gStartTime"
                },{
                    "header" : "结束时间",
                    "dataIndex" : "gEndTime"
                },{
					"header" : "操作",
					"dataIndex" : "gId",
					"renderer":function(v,record){
						var node = [];
						if(p_edit) {
							node.push('<a class="btn btn-success btn-xs update" href="javascript:void(0);">修改</a>');
                        }
						if(p_delete) {
							node.push('<a class="btn btn-success btn-xs delete" href="javascript:void(0);">删除</a>');
                        }
                        $operate = $("<div>"+$.trim(node.join("&nbsp;"),'--')+"</div>");

                        $operate.find(".update").click(function () {
                            $.post("goods/goods-info", {gId: v}, function (data) {
                                var _data = data.data;
                                $.fn.zTree.init($("#cityTree"), setting, zNodes);
                                formInit($("#goodsInfo form"), _data);
                                //给cityTree赋值
                                var zTree = $.fn.zTree.getZTreeObj("cityTree");
                                var gSaleCitys = record.gSaleCity.split(",");
                                var node=null;
                                for(var i=1; i<gSaleCitys.length-1; i++){
                                    node = zTree.getNodeByParam("id",gSaleCitys[i], null);//根据节点数据的属性(id)获取条件完全匹配的节点数据 JSON 对象集合
                                    if(node) zTree.checkNode(node,true,false,false);//根据节点数据选中指定节点,false表示单独选中，之前选中的节点会被取消选中状态，为true 表示追加选中
                                }
                                onCheck(null, "cityTree");

                                //获取sku列表
                                $.post("sku/sku-list-gid", {gId: v}, function (data) {
                                    if (data.code == "200"){
                                        var _data = data.data;
                                        $("#gProperty").hide();
                                        $("#gProperty").prev().hide();
                                        //选中属性的checkbox
                                        for(var key in _data){
                                            var d = _data[key];
                                            for(var k in d){
                                                if(!titleStrObj.hasOwnProperty(k)){
                                                    $("input[name="+k+"][value="+d[k]+"][type=checkbox]").prop("checked", "checked");
                                                }
                                            }
                                        }
                                        //调用生成列表
                                        createSkuTable(_data);

                                        var html = tablePre+titlePre+titleContent+gtitleContent+titleSuf+tbodyPre+rowContent+tbodySuf+tableSuf;
                                        // console.log(html);
                                        $("#skuResult").html(html);
                                        //再给固定的几个select赋值
                                        $("#skuResult select").each(function (i, e) {
                                            $(this).val($(this).attr("selectValue"));
                                            $(this).change();
                                        });
                                        //给文本域赋值
                                        $("#skuResult textarea").each(function (i, e) {
                                            $(this).val($(this).attr("textareaValue"));
                                        });
                                    }else{
                                        $("button.btn-success").attr("disabled", "disabled");
                                        alert("sku加载异常,请重新打开!");
                                    }
                                }, "json");
                                //获取sku列表end

                                $('#goodsInfo').modal('show');
                            }, "json");
                        });
                        $operate.find(".delete").click(function () {
                            if (confirm("确认删除？")) {
                                $.post("goods/goods-delete", {gId: v}, function (data) {
                                    dataList.reload();
                                    alert(data.data);
                                }, "json");
                            }
                        });
                        $operate.find(".addByGroup").click(function () {
                            $.post("goods/group-max-info", {keyGroup: record.keyGroup}, function (data) {
                                var _data = data.data;
                                formInit($("#goodsInfo form"), _data);
                                // $("#goodsInfo form").find("[name = 'keyId']").attr("disabled", "disabled");
                                $('#goodsInfo').modal('show');
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

	$("#query").click(function() {
		dataList.load();
	});
	
	window.reload = function(){
		dataList.reload();
	}

    $(document).on("click","#goodsInfo .modal-footer .btn-success",function() {
        var skuJson = getSkuJson();
        $("#skuJson").val(skuJson);
        // if(!validate_check($("#goodsInfo form"))) return;
        $.post("goods/goods-edit",$("#goodsInfo form").serialize(),function(data){
            $("#goodsInfo .modal-footer .btn-success").attr("disabled", "disabled");
            dataList.reload();
            $('#goodsInfo').modal('hide');
            alert(data.data);
        },"json");
    });

	function getSkuJson(){
	    var arr = new Array();
        $("#skuResult tr.sku_row").each(function (i, e) {
            var sku = {};
            $(e).find("textarea[tag^=sku_],input[tag^=sku_],select").each(function (ii, ee) {
                var input = $(ee);
                var obj = {};
                obj["value"] = input.val();
                obj["seq"] = input.next().val();
                sku[input.attr("name")] = obj;
            });
            arr.push(sku);
        });
        return JSON.stringify(arr);
    }

	//大类小类下拉框
    var gType1 = $("#gType1");
    var gType2 = $("#gType2");
    var option = {
        url:"",
        key:"keyId",
        value:"keyValue",
        onchange:gType1OnChange,
        onclick:"",
        param:{t:new Date().getTime()}
    };
    dictSelect(gType1, "goodsType1", option, false);
    function gType1OnChange(){
        $("#gProperty").html("");
    	if(gType1.val()=="-1") {
            gType2.hide();
            gType2.parent().prev().hide();
        }else {
            gType2.show();
            gType2.parent().prev().show();
        }
    	var opt = {
            url:"",
            key:"keyId",
            value:"keyValue",
            onchange:gType2OnChange,
            param:{t:new Date().getTime()}
        };
        dictSelect(gType2, gType1.val(), opt, false);
	}
	function gType2OnChange(){
    	option.onclick = checkboxClick;
    	if(gType2.val()!="-1") {
    	    dictCheckBox($("#gProperty"), gType2.val(), option);
        }
    	else $("#gProperty").html("");
	}
	function checkboxClick(){
    	setTimeout(function(){
    		setProperty();
		},100);
	}
	function setProperty(){
        var temparr = [];
        $("#gProperty").find("div.row").each(function (i, e) {
            var arr1 = new Array();
        	$(e).find("input[type=checkbox]:checked").each(function(ii, ee){
                var obj = {
                	"title":"",
                    "key":"",
                    "value":"",
                    "name":""
                };
                obj.title=$(e).prev().text();
                obj.key=$(ee).attr("name");
                obj.value=$(ee).val();
                obj.name=$(ee).next().text();
                arr1.push(obj);
            })
            if(arr1.length!=0) temparr.push(arr1);
        });
        var ret = doExchange(temparr);
        // console.log(JSON.stringify(ret));

        initTableParam();

        createTile();
        rowCount=0;
        createBody(ret, 0);

		var html = tablePre+titlePre+titleContent+gtitleContent+titleSuf+tbodyPre+rowContent+tbodySuf+tableSuf;
        // console.log(html);
		$("#skuResult").html(html);
	}



    var tablePre = '<table class="table table-striped table-bordered table-hover" border="0" cellpadding="0" cellspacing="0">';
    var titlePre = '<thead><tr align="center" height="27">';
    var titleContent = '';
    var gtitleContent = '';
    var titleSuf = '</tr></thead>';
    var tbodyPre = '<tbody>';
    var rowContent = '';
    var rowPre = '<tr align="center" height="27" class="">';
    var rowSuf = '</tr>';
    var tbodySuf = '</tbody>';
    var tableSuf = '</table>';
    function initTableParam(){
        tablePre = '<table class="table table-striped table-bordered table-hover" border="0" cellpadding="0" cellspacing="0">';
        titlePre = '<thead><tr align="center" height="27">';
        titleContent = '';
        gtitleContent = '';
        titleSuf = '</tr></thead>';
        tbodyPre = '<tbody>';
        rowContent = '';
        rowPre = '<tr align="center" height="27" class="sku_row">';
        rowSuf = '</tr>';
        tbodySuf = '</tbody>';
        tableSuf = '</table>';
    }
    function createTile(){
        $("#gProperty").find("div.row").each(function(i, e){
           if($(e).find("input[type=checkbox]:checked").length>0){
               if(titleContent=="") titleContent = '<td nowrap=""><strong>序号</strong></td>';
               titleContent += '<td nowrap="" key="'+$(e).find("input[type=checkbox]").eq(0).attr("name")+'"><strong>'+$(e).prev().text()+'</strong></td>';
           }
        });
        for(var k in titleStrObj){
            titleContent += '<td class="'+titleStrObj[k]["titleClass"]+'" nowrap="" key="'+k+'"><strong>'+titleStrObj[k].title+'</strong></td>';
        }
    }
    var rowCount=0;
    function createBody(ret, type) {
        var colIndex=0;
        for (var i = 0; i < ret.length; i++) {
            if(type==0) {
                rowCount++;
                rowContent += rowPre;
                rowContent += '<td>'+(rowCount)+'</td>';
            }
            var r = ret[i];
            if(Array.isArray(r)){
                createBody(r, 1);
            }else{
                rowContent += '<input type="hidden" tag="sku_'+(rowCount)+'" name="'+r.key+'" value="'+r.value+'">';
                rowContent += '<input type="hidden" tag="index_'+(rowCount)+'" name="seq" value="'+(colIndex+1)+'">';
                rowContent += '<td>'+r.name+'</td>';
            }


            if(type==0) {
                for(var k in titleStrObj){
                    var cObj = titleStrObj[k]["type"].replace(/skukey/g, k).replace(/skuvalue/g, "").replace(/skuindex/g, i+1);
                    cObj += '<input type="hidden" tag="index_"'+(i+1)+' name="seq" value="'+(colIndex+1)+'">';
                    rowContent += '<td>'+cObj+'</td>';
                }
                rowContent += rowSuf;
            }
            colIndex++;
        }
    }
    var titleStrObj = {
        "skuTobPrice":{
            "title":"2B价格",
            "type":'<input tag="sku_skuindex" type="text" name="skukey" value="skuvalue" class="col-xs-12">',
            "titleClass":"col-xs-1"
        },
        "skuTocPrice":{
            "title":"2C价格",
            "type":'<input tag="sku_skuindex" type="text" name="skukey" value="skuvalue" class="col-xs-12">',
            "titleClass":"col-xs-1"
        },
        "skuIsNum":{
            "title":"是否号码",
            "type":'<select onchange="skuIsNumChange(this)" tag="sku_skuindex" name="skukey" selectValue="skuvalue"><option value="1">是</option><option value="2">否</option></select>',
            "titleClass":""
        },
        "skuSaleNum":{
            "title":"所售号码",
            "type":'<textarea tag="sku_skuindex" class="col-xs-12" style="height:34px;resize: none;width:150px" type="text" onclick="selectSaleNum(this)" name="skukey" textareaValue="skuvalue" class="col-xs-12" readonly></textarea>',
            "titleClass":"col-xs-1"
        },
        "skuGoodsType":{
            "title":"商品类型",
            "type":'<select tag="sku_skuindex" name="skukey" selectValue="skuvalue"><option value="1">白卡</option><option value="2">成卡</option><option value="3">普卡</option></select>',
            "titleClass":""
        },
        "skuRepoGoods":{
            "title":"关联仓库商品",
            "type":'<select tag="sku_skuindex" name="skukey" selectValue="skuvalue"><option value="1">白卡</option><option value="2">成卡</option><option value="3">普卡</option></select>',
            "titleClass":""
        }
    };
    function createSkuTable(data){
        var index = 0;
        var colIndex = 0;
        initTableParam();
        var growContent="";
        for(var key in data){

            rowContent += rowPre;
            rowContent += '<td>'+(index+1)+'</td>';

            for(var k in data[key]){
                if(index==0){//生成标题
                    if(titleContent=="") titleContent = '<td nowrap=""><strong>序号</strong></td>';
                    var titleStr = "";
                    if(titleStrObj.hasOwnProperty(k)){
                        titleStr = titleStrObj[k]["title"];
                        gtitleContent += '<td class="'+titleStrObj[k]["titleClass"]+'" nowrap="" key="'+k+'"><strong>'+titleStr+'</strong></td>';
                    }else{
                        titleStr = $("input[name="+k+"][value="+data[key][k]+"][type=checkbox]").parents(".row").prev().html();
                        titleContent += '<td nowrap="" key="'+k+'"><strong>'+titleStr+'</strong></td>';
                    }
                }
                //生成内容
                var svalue = (data[key][k]==null||data[key][k]=="null"||data[key][k]==undefined)?'':data[key][k];
                if(titleStrObj.hasOwnProperty(k)){
                    // rowContent += '<input type="hidden" tag="sku_'+(index+1)+'" name="'+k+'" value="'+data[key][k]+'">';
                    var cObj = titleStrObj[k]["type"].replace(/skukey/g, k).replace(/skuvalue/g, svalue).replace(/skuindex/g, index+1);
                    cObj += '<input type="hidden" tag="index_"'+(index+1)+' name="seq" value="'+(colIndex+1)+'">';
                    growContent += '<td>'+cObj+'</td>';
                }else{
                    rowContent += '<input type="hidden" tag="sku_'+(index+1)+'" name="'+k+'" value="'+data[key][k]+'">';
                    rowContent += '<input type="hidden" tag="index_"'+(index+1)+' name="seq" value="'+(colIndex+1)+'">';
                    rowContent += '<td>'+$("input[name="+k+"][value="+svalue+"][type=checkbox]").next().text().trim()+'</td>';
                }
                colIndex++;
            }
            rowContent += growContent+rowSuf;
            index++;
            growContent = "";
            colIndex = 0;
        }
    }

    function doExchange(doubleArrays) {
        var len = doubleArrays.length;
        if (len >= 2) {
            var len1 = doubleArrays[0].length;
            var len2 = doubleArrays[1].length;
            var newlen = len1 * len2;
            var temp = new Array(newlen);
            var index = 0;
            for (var i = 0; i < len1; i++) {
                for (var j = 0; j < len2; j++) {
                	var arr1 = new Array();
                    arr1.push(doubleArrays[0][i]);
                    arr1.push(doubleArrays[1][j]);
                    temp[index] =  arr1;
                    index++;
                }
            }
            var newArray = new Array(len- 1);
            newArray[0] = temp;
            if (len > 2) {
                var _count = 1;
                for(var i=2;i<len;i++)
                {
                    newArray[_count] = doubleArrays[i];
                    _count ++;
                }
            }
            return doExchange(newArray);
        }
        else {
            return doubleArrays[0];
        }
    }
    //点击上架商品重置
    $("button[data-target=#goodsInfo]").bind("click", function () {
        //重置类别
        gType2.change();
        gType1.change();
        //清空sku列表
        $("#skuResult").html("");
        //初始化cityTree
        $.fn.zTree.init($("#cityTree"), setting, zNodes);
        $("#gProperty").show();
        $("#gProperty").prev().show();
    });
    //大类小类下拉框end

    //销售地市下拉框


    var zNodes;
    $.post("query-city-ztree", {pid: 0, isopen:false}, function (data) {
        zNodes = data;
    }, "json");

    $.fn.zTree.init($("#cityTree"), setting, zNodes);
    $("#gSaleCityStr").off("click").on("click", showCityMenu);
    //销售地市下拉框end

    //有效期
    $('#gStartTime').bind('focus',function() {
        WdatePicker({
            maxDate : '#F{$dp.$D(\'gEndTime\',{s:-1})}',
            dateFmt : 'yyyy-MM-dd HH:mm:ss',
            onpicked : function(item) {
                $(this).change();
            }
        });
    });
    $('#gEndTime').bind('focus',function() {
        WdatePicker({
            minDate : '#F{$dp.$D(\'gStartTime\',{s:1})}',
            dateFmt : 'yyyy-MM-dd HH:mm:ss',
            onpicked : function(item) {
                $(this).change();
            }
        });
    });
    //所售号码确定按钮
    $(document).on("click","#saleNumInfo .modal-footer .btn-success",function() {
        var saleNums = $("#saleNum").val();
        $(clickSaleNumObj).val(saleNums);
        $("#saleNum").val("");
        $('#saleNumInfo').modal('hide');
        //滚动条消失问题
        // $("#goodsInfo").css({"overflow-y": "auto"});
    });
});

var clickSaleNumObj;
function selectSaleNum(obj){
    if($("select[tag="+$(obj).attr("tag")+"][name=skuIsNum]").val()!=1) return false;
    clickSaleNumObj = obj;
    $("#saleNum").val($(obj).val());
    $('#saleNumInfo').modal('show');
    setTimeout(function () {
        $("#saleNum").focus();
    },500);
}
function skuIsNumChange(obj){
    if($(obj).val()!=1) $("textarea[tag="+$(obj).attr("tag")+"][name=skuSaleNum]").val("");
}