/** *********直供平台专用表格插件************************* */
;
(function($) {
    $.DSTip = function(option){
        var target = option.target || document.body;
        target = $(target);
        var msg = option.msg || '正在获取数据，请稍候...';
        var overlay = $("<div class='overlay' style='position: relative; top: -9999px; left: -9999px'></div>").appendTo(document.body);
        //overlay.css("opacity","0.2");
        var p_margin_left=33;
        var margin_left=100;
        var width=target.width()|| 0;
        if(width<500){
            p_margin_left=0;
            margin_left=0;
        }
        var box = $("<span class='tipbox' style='height: 76px; line-height: 76px;margin-left:"+p_margin_left+"%; background-color: rgb(204, 204, 204); font-size: 18px; border: 1px solid; padding: 20px "+margin_left+"px;'>"+msg+"</span>").appendTo(overlay);
        this.show = function(){
            overlay.width(target.width());
            overlay.height(target.height());
            overlay.offset(target.offset()).show();
        };
        this.hide = function(){
            overlay.offset({top:-9999,left:-9999}).hide();
        }
    }
    $.DSTable = function(option) {
        var self = this;
        $.extend(true,this, {
            loadComplete : null,//当数据都加载完后会触发此方法
            onSelected : null,//当被选中时触发此方法
            tableClass : "table table-striped table-bordered table-hover",
            dataList : null,
            reader : {
                url : null,
                param : null,
                list : 'list',
                total : 'total',
                load : function(param,callback){
                    param = param || {isTable:true};
                    self.loadMask.show();
                    var url = this.url = param.url || this.url || self.url;
                    this.param = param;
                    this.callback = callback;
                    param.date = new Date().getTime();//防止缓存
                    param.isTable = true;
                    $.post(url, param, function(data,textstatus){
                        self.loadMask.hide();
                        if($.trim(textstatus) === 'success'){
                            self.render(data);
                            self.dataList = data.data;
                            callback && $.isFunction(callback) && callback();
                        }else{
                            alert("系统出错");
                        }
                    },"json");
                },
                reload : function(){
                    this.load(this.param, this.callback);
                }
            },
            render : function(data){
                if(!data){
                    return;
                }
                if($.isArray(data)&&data.length==1){
                    data = data[0];
                }
                if(data.code == 200){
                    data = data.data;
                }else {
                    this.tbody.empty().append($("<tr align='center' height='27'><td colspan='"+this.cm.length+"' style='color:red'>加载数据异常！("+data.data+")</td></tr>"));
                    return;
                }
                var reader = this.reader;
                //渲染tbody的内容
                var list = reader.list==""?data:data[reader.list];
                if(!$.isArray(list))list = [list];
                this.tbody.html("");
                if(list.length == 0) $("<tr align='center' height='27'><td colspan='"+this.cm.length+"'>没有记录！</td></tr>").appendTo(this.tbody);
                for(var i = 0, len = list.length; i < len; i++){
                    var record = list[i];
                    var tr = $("<tr align='center' height='27'></tr>").appendTo(this.tbody).data("row",i);
                    for(var j = 0, cmLen = this.cm.length; j < cmLen; j ++){
                        var column = this.cm[j];
                        var value = record[column['dataIndex']];
                        if(column.renderer && $.isFunction(column.renderer))
                            value = column.renderer(value,record);

                        if(value != "" && value == 0){
                            $("<td "+(column.nowrap ? "nowrap":"")+"></td>").append("0").appendTo(tr);
                        }else {
                            $("<td "+(column.nowrap ? "nowrap":"")+"></td>").append(value&&value!="null"?value:"--").appendTo(tr);
                        }
                    }
                }

                //统计属性
                if(this.calcBar){
                    var calcMsg = this.calcBar.replace(/\{\w+\}/g,function(key){
                        key = key.substring(1,key.length-1);
                        return data[key]
                    });
                    this.pageBar.find(".calcBar").html(calcMsg);
                }
                //更改页面信息,未指定pm表示不需要分页栏
                if(this.pm){
                    var total,pages,pageNum;
                    var pageBar = this.pageBar;
                    var start = this.pm.start;
                    var limit = this.pm.limit;
                    this.pm.total = total = data[reader.total];
                    this.pm.pageNum = pageNum = Math.ceil((start+1)/limit);
                    this.pm.pages = pages = Math.ceil(total/limit);
                    pageBar.find(".allRow").text(total).end()
                        .find(".pageCount").text(pages).end()
                        .find(".currentPage").text(pageNum).end();
                    //第一页
                    if(pageNum <= 1) pageBar.find(".firstPage").addClass("disabled").unbind("click");
                    else pageBar.find(".firstPage").removeClass("disabled").unbind("click").click($.proxy(this.doFirstPage,this));
                    //上一页
                    if(pageNum <= 1)pageBar.find(".prevPage").addClass("disabled").unbind("click");
                    else pageBar.find(".prevPage").removeClass("disabled").unbind("click").click($.proxy(this.doPrevPage,this));
                    //下一页
                    if(pageNum >= pages) pageBar.find(".nextPage").addClass("disabled").unbind("click");
                    else pageBar.find(".nextPage").removeClass("disabled").unbind("click").click($.proxy(this.doNextPage,this));
                    //最后一页
                    if(pageNum >= pages) pageBar.find(".lastPage").addClass("disabled").unbind("click");
                    else pageBar.find(".lastPage").removeClass("disabled").unbind("click").click($.proxy(this.doLastPage,this));
                }
                var tr = $(this.tbody).find("tr").hover(function(){//添加鼠标移入移出事件
                    $(this).addClass("over");
                    if(self.onSelected && $.isFunction(self.onSelected )){
                        $(this).addClass("point");
                    }
                },function(){
                    $(this).removeClass("over");
                }).click(function(){//添加点击事件
                    var row = $(this).data("row");
                    self.onSelected && $.isFunction(self.onSelected ) && self.onSelected(row,list[row]);
                    if(!$(this).hasClass("selected")){
                        tr.removeClass("selected");
                        $(this).addClass("selected")
                    }
                });
                this.loadComplete && $.isFunction(this.loadComplete) && this.loadComplete(this.tbody, this.table, this);

            },
            getSelectedRowNum : function(){
                return this.tbody.find(".selected").data("row");
            },
            getCell : function(rowIndex,colIndex){
                return this.tbody.find("tr:eq("+rowIndex+")").find("td:eq("+colIndex+")");
            },
            getParam : function(){
                return {};
            },
            load : function(param, callback, reInit){
                if(reInit) this.init();
                if(this.pm){
                    this.pm.start = 0;
                    param = $.extend(true,param||{},{start:0,limit:this.pm.limit});
                }
                var option_param = {};
                if($.isArray(this.getParam())){
                    $(this.getParam()).each(function(i, obj){
                        option_param[obj.name]=obj.value;
                    })
                }else{
                    option_param = this.getParam();
                }
                this.param = $.extend(option_param,param||{});
                this.reader.load(this.param, callback);
            },

            reload : function(){
                this.reader.reload();
            },

            doFirstPage : function(){
                if(this.pm.pageNum == 1)return;
                this.pm.start = 0;
                this.reader.load($.extend(this.param,{start:this.pm.start,limit:this.pm.limit}));
            },
            doLastPage : function(){
                if(this.pm.pageNum == this.pm.pages)return;
                this.pm.start = (this.pm.pages-1)*this.pm.limit;
                this.reader.load($.extend(this.param,{start:this.pm.start,limit:this.pm.limit}));
            },
            doNextPage : function(){
                if(this.pm.pageNum >= this.pm.pages)return;
                this.pm.start += this.pm.limit;
                this.reader.load($.extend(this.param,{start:this.pm.start,limit:this.pm.limit}));
            },
            doPrevPage : function(){
                if(this.pm.pageNum <= 1)return;
                this.pm.start -= this.pm.limit;
                this.reader.load($.extend(this.param,{start:this.pm.start,limit:this.pm.limit}));
            },
            init : function() {
                var cm = this.cm, // columnModel,{dataIndex:'',header:'',width:'',renderer}
                    pm = this.pm,// pageModel
                    ct = this.ct,//container
                    table,thead,tbody,title,tfoot,pageBar//对应表格的thead,tbody

                //初始化参数校验
                if (!ct) {//container
                    throw new Error("需为表格指定一个父结点ct");
                }
                if(!cm){
                    throw new Error("需为表格指定一个列模型cm");
                }
                if (!$.isArray(cm)) {
                    this.cm = cm = [cm];
                }

                //初始化表格主体并清除之前的数据
                this.ct = ct = $(ct).empty();
                this.table = table = $("<table class='"+this.tableClass+"' border='0' cellpadding='0' cellspacing='0'></table>")
                    .appendTo(ct);

                //初始化表格标题栏
                thead = $("<thead></thead>").appendTo(table);
                title = $("<tr align='center' height='27'></tr>").appendTo(thead);
                for (var i = 0, len = cm.length; i < len; i++) {
                    var column = cm[i];
                    var tdtitle = $("<td "+(column.width ? "width='"+column.width+"'":"")+" nowrap></td>");
                    tdtitle.append("<strong>"+column.header+"</strong>");
                    title.append(tdtitle);
                }
                this.tbody = tbody = $("<tbody></tbody>").appendTo(table);

                //初始化页面信息栏,未指定pm表示不需要分页栏
                if(this.pm){
                    this.pm = $.extend({
                        total : 0,
                        pages : 0,
                        pageNum : 0,
                        limit :15,
                        start : 0
                    },this.pm);
                    tfoot = $("<tfoot></tfoot>").appendTo(table);
                    this.pageBar = pageBar = $("<td colspan='"+cm.length+"'></td>");
                    $("<tr align='right'></tr>").append(pageBar).appendTo(tfoot);
                    var pageInfo = [];

                    //统计属性
                    if(this.calcBar){
                        pageInfo.push("<div class='calcBar'>");
                        pageInfo.push("</div>");
                    }

                    //页面属性
                    pageInfo.push("<div>");
                    pageInfo.push("共<span class='allRow'>"+this.pm.total+"</span>条记录");
//					pageInfo.push("每页显示");
//					pageInfo.push("<select class='pageSize' style='width:45px;'>");
//					pageInfo.push("<option value="+this.pm.limit+">"+this.pm.limit+"</option>");
//					pageInfo.push("<option value=15>15</option>");
//					pageInfo.push("<option value=20>20</option>");
//					pageInfo.push("<option value=50>50</option>");
//					pageInfo.push("<option value=100>100</option>");
//					pageInfo.push("</select>");
//					pageInfo.push("条记录");
                    pageInfo.push("共<span class='pageCount'>"+this.pm.pages+"</span>页");
                    pageInfo.push("当前第<span class='currentPage'>"+this.pm.pageNum+"</span>页");
                    pageInfo.push("</div>");

                    //翻页
                    pageInfo.push("<div>");
                    pageInfo.push("<a class='refreshPage' href='javascript:void(0)'>刷新</a>");
                    pageInfo.push("<a class='firstPage disabled' href='javascript:void(0)'>第一页</a>");
                    pageInfo.push("<a class='prevPage disabled' href='javascript:void(0)'>上一页</a>");
                    pageInfo.push("<a class='nextPage disabled' href='javascript:void(0)'>下一页</a>");
                    pageInfo.push("<a class='lastPage disabled' href='javascript:void(0)'>最后一页</a>");
                    pageInfo.push("</div>");

                    pageBar.append(pageInfo.join(" "));

                    pageBar.find(".refreshPage").click(function(){
                        self.reload();
                    });
                    //每页显示记录数下拉框事件
                    var pageSize = pageBar.find(".pageSize");
                    pageSize.val(this.pm.limit);
                    pageSize.change(function(){
                        var limit = parseInt($(this).val());
                        $.extend(self.pm,{
                            total : 0,
                            pages : 0,
                            pageNum : 0,
                            limit :limit,
                            start : 0
                        });
                        self.load();
                    });
                }

                //初始化遮罩层
                this.loadMask = new $.DSTip({target:table});
            }

        }, option);
        this.init();
    }
})(jQuery)