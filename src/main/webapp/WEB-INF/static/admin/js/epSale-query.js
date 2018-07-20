var dataList = null;
$(function() {
	/* 初始化竟拍活动管理列表数据 */
	dataList = new $.DSTable({
		"url" : '/epSale/epSale-list',
		"ct" : "#result",
		"cm" : [{
            "header" : "编号",
            "dataIndex" : "id"
        },{
            "header" : "标题",
            "dataIndex" : "title"
        }/*,{
            "header" : "竟拍规则",
            "dataIndex" : "epRule"
        }*/,{
            "header" : "开始时间",
            "dataIndex" : "startTime"
        },{
            "header" : "结束时间",
            "dataIndex" : "endTime"
        },{
            "header" : "最迟付款时间",
            "dataIndex" : "lastPayTime"
        }/*,{
            "header" : "显示条件",
            "dataIndex" : "isShow",
            "renderer" : function(v, record) {
                if(v == 1) return "是"
                    else  return "否";
            }
        }*/,{
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
                $operate = $("<div>"+trim(node.join("&nbsp;"),'--')+"</div>");
                $operate.find(".update").click(function () {
                    $.post("epSale/epSale-info", {id: v}, function (data) {
                        var _data = data.data;
                        var isSale=data.isSale=="1"?true:false;//是否有上架的商品
                        $("#startTimePicker").val(_data.startTime);
                        $("#startTimePicker").attr("disabled",isSale);
                        $("#endTimePicker").val(_data.endTime);
                        $("#endTimePicker").attr("disabled",isSale);
                        $("#startTime").val(_data.startTime);
                        $("#endTime").val(_data.endTime);
                        $("#lastPayTime").val(_data.lastPayTime);
                        $("#lastPayTimePicker").val(_data.lastPayTime);
                        $("#lastPayTimePicker").attr("disabled",isSale);
                        initEPSalePics(data.epSalePics);
                        if(data.epSalePics.length>0)
                        {
                            $("#delImgI").attr("class","raty-cancel cancel-off-png");
                        }else
                        {
                            $("#delImgI").attr("class","");
                        }
                        editor.html(_data.epRule);
                        formInit($("#epSaleInfo form"), _data);
                        $('#epSaleInfo').modal('show');
                    }, "json");
                });
                $operate.find(".delete").click(function () {
                    if (confirm("确认删除？")) {
                        $.post("epSale/epSale-delete", {id: v}, function (data) {
                            dataList.reload();
                            alert(data.data);
                        }, "json");
                    }
                });
                return $operate;
            }}
        ],
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

    //点击弹出添加/修改竟拍活动界面
    $("button[data-target=#epSaleInfo]").bind("click", function () {
        editor.html('');
        initEPSalePics();

    });



    $(document).on("click","#epSaleInfo .modal-footer .btn-success",function() {
        $('#startTime').val($('#startTimePicker').val());
        $('#endTime').val($('#endTimePicker').val());
        $('#lastPayTime').val($('#lastPayTimePicker').val());
        var title=$("#title").val();
        var startTime=$("#startTime").val();
        var endTime=$("#endTime").val();
        var lastPayTime=$("#lastPayTime").val();
        var epRule=$("textarea[name='epRule']").val();
        var file=$("#file").val();
        var imgSrc=$("#epSaleImg")[0].src;
       if(title=="")
       {
           alert("标题不能为空!")
           return false;
       }
        if(startTime=="")
        {
            alert("开始时间不能为空!")
            return false;
        }
        if(endTime=="")
        {
            alert("结束时间能为空!")
            return false;
        }
        if(lastPayTime=="")
        {
            alert("最迟付款时间不能为空!")
            return false;
        }
        if(epRule=="")
        {
            alert("竟拍规则不能为空!")
            return false;
        }
        var delImgClass=$("#delImgI").attr("class");
        if(file=="")
        {
           // if(imgSrc==""||imgSrc.indexOf("undefined")>0)
            if(delImgClass=="")
            {
                alert("图片不能为空!")
                return false;
            }
        }

        //editor.sync();
        // 准备好Options对象
        var options = {
            // target:  null,
            type : "post",
            url: "epSale/epSale-edit",
            success : function(data) {
                dataList.load();
                $('#epSaleInfo').modal('hide');
                alert(data.data);
            }
        };
        // 将options传给ajaxForm
        $('#epSaleInfo form').ajaxSubmit(options);
        editor.html('');
    });

    //富文本编辑器
    var editor;
    KindEditor.ready(function(K) {
        editor = K.create('textarea[name="epRule"]', {
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
    $('#epSaleInfo').off('shown.bs.modal').on('shown.bs.modal', function (e) {
        $(document).off('focusin.modal');
    });

    var option = {
        url:"",
        key:"keyId",
        value:"keyValue",
        param:{t:new Date().getTime()}
    };
    citySelect($("#fprovince"),0);

    $(document).on("change","#fprovince",function(){
        citySelect($("#fcity"),$(this).val());
    })

    $(document).on("change","#fcity",function(){
        citySelect($("#fdistrict"),$(this).val());
    });


    /**
     * 图片上传
     * */
    function initEPSalePics(picList){
        var html = '<span style="color:red; font-size: 12px;">注:重新上传将删除之前的图片</span>' +
            '<input type="hidden" id="delPicSeqs" name="delPicSeqs">' +
            '<input type="hidden" id="picSeqs" name="picSeqs">';
        var style = 'visibility: hidden;';
        var refid,filename;
        var pcount=0;
        var imgClass="";
        for(var i=0;i<1;i++){
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
            if((typeof picList !='undefined')&&(filename!=''))
            {
                imgClass="raty-cancel cancel-off-png";
            }
            html+='<input style="float:left" type="file" name="file" id="file" seq="'+(i+1)+'" onchange="fileChange('+(i+1)+')">';
            html+='<div class="rating inline" onclick="deletePic(this)" style="cursor: pointer;"><i  id="delImgI" title="删除图片" class="'+imgClass+'" data-alt="x"></i></div>';
            html+='<img id="epSaleImg" style="width:150px;'+style+'" src="'+basePath+'get-img/epSalePics/'+refid+'/'+filename+'">';

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
});

function deletePic(obj){
    if($("#delPicSeqs").val().indexOf($(obj).prev().attr("seq"))==-1) $("#delPicSeqs").val($("#delPicSeqs").val()+'"'+$(obj).prev().attr("seq")+'",');
    $(obj).parent().find("img").eq(0).css("visibility","hidden");
    $("#delImgI").attr("class","");
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