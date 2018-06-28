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
							if(record.gIsSale=="1")
							    node.push('<a class="btn btn-success btn-xs unsale" href="javascript:void(0);">下架</a>');
                        }
						if(p_delete) {
							node.push('<a class="btn btn-success btn-xs delete" href="javascript:void(0);">删除</a>');
                        }
                        $operate = $("<div>"+$.trim(node.join("&nbsp;"),'--')+"</div>");

                        $operate.find(".update").click(function () {
                            //获取仓库商品类型下拉框
                            $.ajax({
                                type: "POST",
                                async: true,
                                url: "goods/goods-repoGoods",
                                success: function(data){
                                    if(data.code=="200"){
                                        repoGoods = data.data.platresponse;
                                        var repoGoodsSelect = '<select class="chosen-select" onchange="skuRepoGoodsChange(this)" tag="sku_skuindex" name="skukey" selectValue="skuvalue">';
                                        for(var i=0; i<repoGoods.length; i++){
                                            repoGoodsSelect += '<option value="'+repoGoods[i]["companystock_id"]+'" acqu="'+repoGoods[i]["active_quantity"]+'">'+repoGoods[i]["commodity_name"]+'</option>';
                                        }
                                        repoGoodsSelect += '</select>';
                                        titleStrObj.skuRepoGoods.type=repoGoodsSelect;
                                        // active_quantity可用库存
                                        // companystock_id
                                        // commodity_name
                                        $.post("goods/goods-info", {gId: v}, function (data) {
                                            var _data = data.data;
                                            initGoodsPics(data.goodsPics);
                                            editor.html(data.kindeditor);
                                            $.fn.zTree.init($("#cityTree"), setting, zNodes);
                                            formInit($("#goodsInfo form"), _data);
                                            $("#gStartTimePicker").val(_data["gStartTime"]);
                                            $("#gEndTimePicker").val(_data["gEndTime"]);
                                            $("#gStartTime").val(_data["gStartTime"]);
                                            $("#gEndTime").val(_data["gEndTime"]);
                                            //给cityTree赋值
                                            var zTree = $.fn.zTree.getZTreeObj("cityTree");
                                            var gSaleCitys = record.gSaleCity.split(",");
                                            var node=null;
                                            for(var i=1; i<gSaleCitys.length-1; i++){
                                                node = zTree.getNodeByParam("id",gSaleCitys[i], null);//根据节点数据的属性(id)获取条件完全匹配的节点数据 JSON 对象集合
                                                if(node) zTree.checkNode(node,true,true,false);//根据节点数据选中指定节点,false表示单独选中，之前选中的节点会被取消选中状态，为true 表示追加选中
                                            }
                                            onCheck(null, "cityTree");

                                            gIsAucOnClick();

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
                                    }else{
                                        repoGoods = "";
                                    }
                                }
                            });

                        });
                        $operate.find(".delete").click(function () {
                            if (confirm("确认删除？")) {
                                $.post("goods/goods-delete", {gId: v}, function (data) {
                                    dataList.reload();
                                    alert(data.data);
                                }, "json");
                            }
                        });
                        $operate.find(".unsale").click(function () {
                            if (confirm("确认下架？")) {
                                $.post("goods/goods-unsale", {gId: v}, function (data) {
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
        $('#gStartTime').val($('#gStartTimePicker').val());
        $('#gEndTime').val($('#gEndTimePicker').val());
        //是竞拍,就出发选择活动下拉框的change事件
        if($("input[name=gIsAuc]:checked").val()=="1") {
            $("#gActive").change();
            if(!$("#gActive").val()){
                alert("请选择活动");
                return;
            }
        }
        //判断sku列表
        if($("#skuResult tr").length<2){
            alert("列表为空,请选择商品属性添加");
            return;
        }
        //判断图片
        if($("input[type=file]").eq(0).val()=="" && $("#picUpload img").eq(0).css("visibility")=="hidden"){
            alert("请上传第一张图片");
            return;
        }
        //验证填写数量和库存数量
        var isError = false;
        var msg = "";
        $("select[name=skuRepoGoods]").each(function(i, e){
            if($(this).find("option:selected").attr("acqu")==undefined){
                msg = "请选择关联仓库商品";
                isError = true;
                return;
            }
            if(parseInt($(this).find("option:selected").attr("acqu"))+parseInt($("select[name=skuRepoGoods]").parent().parent().find("input[name=skuNum]").val())<parseInt($("select[name=skuRepoGoods]").parent().parent().find("input[name=skuNum]").val())){
                msg = "第" + (i + 1) + "行数量大于库存" + $(this).find("option:selected").attr("acqu") + ",请重新填写";
                isError = true;
                return;
            }
        });
        if(isError){
            alert(msg);
            return false;
        }
        var skuJson = getSkuJson();
        $("#skuJson").val(skuJson);
        editor.sync();
        // if(!validate_check($("#goodsInfo form"))) return;
        // $.post("goods/goods-edit",$("#goodsInfo form").serialize(),function(data){
        //     $("#goodsInfo .modal-footer .btn-success").attr("disabled", "disabled");
        //     dataList.reload();
        //     $('#goodsInfo').modal('hide');
        //     alert(data.data);
        // },"json");

        var options = {
            // target:  null,
            type : "post",
            url: "goods/goods-edit",
            success : function(data) {
                dataList.load();
                $('#goodsInfo').modal('hide');
                alert("提交成功");
            }
        };
        // 将options传给ajaxForm
        $('#goodsInfo form').ajaxSubmit(options);
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
            titleContent += '<td style="'+(titleStrObj[k]==undefined?"":(titleStrObj[k]["isShow"]?"":"display:none"))+'" class="'+titleStrObj[k]["titleClass"]+'" nowrap="" key="'+k+'"><strong>'+titleStrObj[k].title+'</strong></td>';
        }
    }
    var rowCount=0;
    function createBody(ret, type) {
        if(ret == null) return;
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
                    rowContent += '<td style="'+(titleStrObj[k]["isShow"]?"":"display:none")+'">'+cObj+'</td>';
                }
                rowContent += rowSuf;
            }
            colIndex++;
        }
    }
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
                        gtitleContent += '<td style="'+(titleStrObj[k]["isShow"]?"":"display:none")+'" class="'+titleStrObj[k]["titleClass"]+'" nowrap="" key="'+k+'"><strong>'+titleStr+'</strong></td>';
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
                    growContent += '<td style="'+(titleStrObj[k]["isShow"]?"":"display:none")+'">'+cObj+'</td>';
                }else{
                    rowContent += '<input type="hidden" tag="sku_'+(index+1)+'" name="'+k+'" value="'+data[key][k]+'">';
                    rowContent += '<input type="hidden" tag="index_"'+(index+1)+' name="seq" value="'+(colIndex+1)+'">';
                    rowContent += '<td>'+$("input[name="+k+"][value="+svalue+"][type=checkbox]").next().text().trim()+'</td>';
                }
                colIndex++;
            }
            growContent += '<td>'+titleStrObj["operation"]["type"]+'</td>';

            rowContent += growContent+rowSuf;
            index++;
            growContent = "";
            colIndex = 0;
        }
            gtitleContent += '<td class="'+titleStrObj["operation"]["titleClass"]+'" nowrap="" key="operation"><strong>'+titleStrObj["operation"]["title"]+'</strong></td>';
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
        gIsAucOnClick();
        //重置类别
        gType2.change();
        gType1.change();
        //清空sku列表
        $("#skuResult").html("");
        //初始化cityTree
        $.fn.zTree.init($("#cityTree"), setting, zNodes);
        $("#gProperty").show();
        $("#gProperty").prev().show();
        editor.html('');
        initGoodsPics();
    });
    //大类小类下拉框end

    //销售地市下拉框
    var zNodes;
    $.post("query-city-ztree", {pid: 0, isopen:false}, function (data) {
        zNodes = data;
        $.fn.zTree.init($("#cityTree"), setting, zNodes);
        $("#gSaleCityStr").off("click").on("click", showCityMenu);
    }, "json");

    //销售地市下拉框end

    //所售号码确定按钮
    $(document).on("click","#saleNumInfo .modal-footer .btn-success",function() {
        var saleNums = $("#saleNum").val();
        var numcountt = $("#saleNum").val().replace(/(\r\n)|(\n)/g, "sdfsdfsdfsdf").split("sdfsdfsdfsdf").length;
        $(clickSaleNumObj).val(saleNums);
        $(clickSaleNumObj).parent().parent().find("input[name=skuNum]").val(numcountt);
        $("#saleNum").val("");
        $('#saleNumInfo').modal('hide');
        //滚动条消失问题
        // $("#goodsInfo").css({"overflow-y": "auto"});
    });
    //富文本编辑器
    var editor;
    KindEditor.ready(function(K) {
        editor = K.create('textarea[name="kindeditorContent"]', {
            resizeType : 1,
            allowPreviewEmoticons : false,
            allowImageUpload : false,
            afterBlur: function () { editor.sync(); },
            items : [
                'fontname', 'fontsize', '|', 'forecolor', 'hilitecolor', 'bold', 'italic', 'underline',
                'removeformat', '|', 'justifyleft', 'justifycenter', 'justifyright', 'insertorderedlist',
                'insertunorderedlist', '|', 'emoticons', 'link']
        });
    });
    //解决编辑器弹出层文本框不能输入的问题
    $('#goodsInfo').off('shown.bs.modal').on('shown.bs.modal', function (e) {
        $(document).off('focusin.modal');

        $('.chosen-select').chosen({allow_single_deselect:true});
    });

    //轮询时间下拉框
    option = {
        url:"",
        key:"keyId",
        value:"keyValue",
        onclick:"",
        param:{t:new Date().getTime()}
    };
    dictSelect($("#gLoopTime"), "gLoopTime", option, true);
    //有效期
    // $('#gStartTimePicker').change(function() {
    //     // WdatePicker({maxDate : '#F{$dp.$D(\'gEndTime\',{s:-1})}',dateFmt : 'yyyy-MM-dd HH:mm:ss'});
    //     $('#gStartTime').val($('#gStartTimePicker').val());
    // });
    // $('#gEndTimePicker').change(function() {
    //     // WdatePicker({minDate : '#F{$dp.$D(\'gStartTime\',{s:1})}',dateFmt : 'yyyy-MM-dd HH:mm:ss'});
    //     $('#gEndTime').val($('#gEndTimePicker').val());
    // });
    $("input[name=gIsAuc]").off("click").on("click", gIsAucOnClick);
    function gIsAucOnClick(){
        var isAucContent = $("#isAucContent");
        var gActive = $("#gActive");
        if($("input[name=gIsAuc]:checked").val()=="1"){
            isAucContent.show();
            gActive.show();
            $('#gStartTimePicker').prop("disabled", true);
            $('#gEndTimePicker').prop("disabled", true);
        }else{
            isAucContent.hide();
            gActive.hide();
            $('#gStartTimePicker').prop("disabled", false);
            $('#gEndTimePicker').prop("disabled", false);
            // $('#gStartTime').bind('click',function() {
            //     WdatePicker({
            //         maxDate : '#F{$dp.$D(\'gEndTime\',{s:-1})}',
            //         dateFmt : 'yyyy-MM-dd HH:mm:ss',
            //         onpicked : function(item) {
            //             $(this).change();
            //         }
            //     });
            // });
            // $('#gEndTime').bind('click',function() {
            //     WdatePicker({
            //         minDate : '#F{$dp.$D(\'gStartTime\',{s:1})}',
            //         dateFmt : 'yyyy-MM-dd HH:mm:ss',
            //         onpicked : function(item) {
            //             $(this).change();
            //         }
            //     });
            // });
        }
    }
    function initGoodsPics(picList){
        var html = '<span style="color:red; font-size: 12px;">注:重新上传将删除之前的图片</span>' +
            '<input type="hidden" id="delPicSeqs" name="delPicSeqs">' +
            '<input type="hidden" id="picSeqs" name="picSeqs">';
        var style = 'visibility: hidden;';
        var refid,filename;
        var pcount=0;
        for(var i=0;i<6;i++){
            if(pcount==0) html += '<div class="form-group" style="padding-bottom: 10px; padding-top:10px;">';
            html += '<div class="col-xs-4">';

            for(var j=0; picList!=null && j<picList.length; j++){
                if(picList[j] && picList[j].seq==(i+1)+"") {
                    style = '';
                    refid = picList[j].refId;
                    filename = picList[j].fileName;
                    break;
                }
            }
            html+='<input style="float:left" type="file" name="file" seq="'+(i+1)+'" onchange="fileChange('+(i+1)+')">';
            html+='<div class="rating inline" onclick="deletePic(this)" style="cursor: pointer;"><i title="删除图片" class="raty-cancel cancel-off-png" data-alt="x"></i></div>';
            html+='<img style="width:150px;'+style+'" src="'+basePath+'get-img/goodsPics/'+refid+'/'+filename+'">';

            html+='</div>';

            pcount++;
            if(pcount==3 || i==5) {
                html+='</div>';
                pcount=0;
            }
            style = 'visibility: hidden;';
            refid = '';
            filename = '';
        }
        $("#picUpload").html(html);
    }
    getRepoGodds();
    getActive();
});
var activeSelectOptions;
function getActive(){
    $.ajax({
        type: "GET",
        async: true,
        url: "epSales",
        success: function(data){
            if(data.code=="200"){
                activeSelectOptions = data.data;
                var option = '';
                for(var i=0; i<activeSelectOptions.length; i++){
                    option += '<option value="'+activeSelectOptions[i]["id"]+'" startTime="'+activeSelectOptions[i]["startTime"]+'" endTime="'+activeSelectOptions[i]["endTime"]+'">'+activeSelectOptions[i]["title"]+'</option>';
                }
                activeSelectOptions=option;
                $("#gActive").html(activeSelectOptions);
            }else{
                activeSelectOptions = "";
            }
        }
    });
}
function activeChange(obj){
    // console.log($(obj).find("option:selected").attr("startTime"));
    var gStartTime = $("#gStartTime");
    var gStartTimePicker = $("#gStartTimePicker");
    var gEndTime = $("#gEndTime");
    var gEndTimePicker = $("#gEndTimePicker");
    gStartTime.val(dateFtt("yyyy-MM-dd hh:mm:ss", new Date(Number($(obj).find("option:selected").attr("startTime")))));
    gStartTimePicker.val(dateFtt("yyyy-MM-dd hh:mm:ss", new Date(Number($(obj).find("option:selected").attr("startTime")))));
    gEndTime.val(dateFtt("yyyy-MM-dd hh:mm:ss", new Date(Number($(obj).find("option:selected").attr("endTime")))));
    gEndTimePicker.val(dateFtt("yyyy-MM-dd hh:mm:ss", new Date(Number($(obj).find("option:selected").attr("endTime")))));
}
var titleStrObj = {
    "skuId":{
        "isShow":false,
        "title":"skuId",
        "type":'<input tag="sku_skuindex" type="text" name="skukey" value="skuvalue" class="col-xs-12">',
        "titleClass":"col-xs-1"
    },
    "skuTobPrice":{
        "isShow":true,
        "title":"2B价格",
        "type":'<input tag="sku_skuindex" type="text" name="skukey" value="skuvalue" class="col-xs-12">',
        "titleClass":"col-xs-1"
    },
    "skuTocPrice":{
        "isShow":false,
        "title":"2C价格",
        "type":'<input tag="sku_skuindex" type="text" name="skukey" value="skuvalue" class="col-xs-12">',
        "titleClass":"col-xs-1"
    },
    // "skuIsNum":{
    //     "isShow":true,
    //     "title":"是否号码",
    //     "type":'<select onchange="skuIsNumChange(this)" tag="sku_skuindex" name="skukey" selectValue="skuvalue"><option value="1">是</option><option value="2">否</option></select>',
    //     "titleClass":""
    // },
    "skuSaleNum":{
        "isShow":true,
        "title":"所售号码",
        "type":'<textarea tag="sku_skuindex" class="col-xs-12" style="height:34px;resize: none;width:150px" type="text" onclick="selectSaleNum(this)" name="skukey" textareaValue="skuvalue" class="col-xs-12" readonly></textarea>',
        "titleClass":"col-xs-1"
    },
    "skuNum":{
        "isShow":true,
        "title":"数量",
        "type":'<input tag="sku_skuindex" type="text" name="skukey" value="skuvalue" class="col-xs-12">',
        "titleClass":"col-xs-1"
    },
    "skuGoodsType":{
        "isShow":true,
        "title":"商品类型",
        "type":'<select onchange="skuIsNumChange(this)" tag="sku_skuindex" name="skukey" selectValue="skuvalue"><option value="1">白卡</option><option value="2">普号</option><option value="3">普靓</option><option value="4">超靓</option></select>',
        "titleClass":""
    },
    "skuRepoGoodsName":{
        "isShow":false,
        "title":"关联仓库商品",
        "type":'<input tag="sku_skuindex" type="hidden" name="skukey" value="skuvalue" class="col-xs-12">',
        "titleClass":""
    },
    "skuRepoGoods":{
        "isShow":true,
        "title":"关联仓库商品",
        "type":'<select class="chosen-select" tag="sku_skuindex" name="skukey" selectValue="skuvalue"><option value="1">白卡</option><option value="2">成卡</option><option value="3">普卡</option></select>',
        "titleClass":""
    },
    "operation":{
        "isShow":true,
        "title":"操作",
        "type":'<a class="btn btn-danger btn-xs delete" href="javascript:void(0);" onclick="deleteSkuRow(this)">删除</a>',
        "titleClass":""
    }
};
var repoGoods;
function getRepoGodds(){
    //获取仓库商品类型下拉框
    $.ajax({
        type: "POST",
        async: true,
        url: "goods/goods-repoGoods",
        success: function(data){
            if(data.code=="200"){
                repoGoods = data.data.platresponse;
                var repoGoodsSelect = '<select class="chosen-select" onchange="skuRepoGoodsChange(this)" tag="sku_skuindex" name="skukey" selectValue="skuvalue">';
                for(var i=0; i<repoGoods.length; i++){
                    repoGoodsSelect += '<option value="'+repoGoods[i]["companystock_id"]+'" acqu="'+repoGoods[i]["active_quantity"]+'">'+repoGoods[i]["commodity_name"]+'</option>';
                }
                repoGoodsSelect += '</select>';
                titleStrObj.skuRepoGoods.type=repoGoodsSelect;
                // active_quantity可用库存
                // companystock_id
                // commodity_name
                return true;
            }else{
                repoGoods = "";
                return false;
            }
        }
    });
}
function skuRepoGoodsChange(obj){
    $(obj).parent().parent().find("input[name=skuRepoGoodsName]").eq(0).val($(obj).find("option:selected").text());
}
function deletePic(obj){
    if($("#delPicSeqs").val().indexOf($(obj).prev().attr("seq"))==-1) $("#delPicSeqs").val($("#delPicSeqs").val()+'"'+$(obj).prev().attr("seq")+'",');
    $(obj).parent().find("img").eq(0).css("visibility","hidden");
}
var allowFileType = ".png,.jpg,.gif";
function fileChange(i){
    var picSeqs = "";
    var fileType;
    $("input[type=file]").each(function(){
        if($(this).val()!="") {
            fileType = $(this).val().substring($(this).val().lastIndexOf("."));
            if(allowFileType.indexOf(fileType)==-1){
                $(this).val('');
                alert("请上传"+allowFileType+"格式的图片");
            }else{
                picSeqs+='"'+$(this).attr("seq")+'",';
            }
        }
    });

    $("#picSeqs").val(picSeqs);
}
var clickSaleNumObj;
function selectSaleNum(obj){
    if($("select[tag="+$(obj).attr("tag")+"][name=skuGoodsType]").val()==1) {
        $("#saleNum").val($(obj).val());
        return false;
    }
    clickSaleNumObj = obj;
    $("#saleNum").val($(obj).val());
    $('#saleNumInfo').modal('show');
    setTimeout(function () {
        $("#saleNum").focus();
    },500);
}
function skuIsNumChange(obj){
    if($(obj).val()==1) $("textarea[tag="+$(obj).attr("tag")+"][name=skuSaleNum]").val("");
}
//sku列表删除行
function deleteSkuRow(obj){
    if($("#skuResult tr.sku_row").length<=1){
        alert("至少保留一条记录");
        return false;
    }
    $("#delSkus").val($("#delSkus").val()+$(obj).parent().parent().find("input[tag^=sku_][name=skuId]").eq(0).val()+",");
    $(obj).parent().parent().remove();
}
// 用法dateFtt("yyyy-MM-dd hh:mm:ss",new Date(1528271207000));
function dateFtt(fmt,date) {
    try {
        var o = {
            "M+": date.getMonth() + 1,                 //月份
            "d+": date.getDate(),                    //日
            "h+": date.getHours(),                   //小时
            "m+": date.getMinutes(),                 //分
            "s+": date.getSeconds(),                 //秒
            "q+": Math.floor((date.getMonth() + 3) / 3), //季度
            "S": date.getMilliseconds()             //毫秒
        };
        if (/(y+)/.test(fmt))
            fmt = fmt.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));
        for (var k in o)
            if (new RegExp("(" + k + ")").test(fmt))
                fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    } catch (e) {
        return "";
    }
    return fmt;
} ;