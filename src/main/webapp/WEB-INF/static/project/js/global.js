// 每个提交的ajax都会触发此方法
$.extend(true, jQuery.ajaxSettings, {
    data : {
        isAjax : true// 将连接请求标上ajax方式
    },
    beforeRequest : function (option){
        if(option && option.data && option.data.isMask) {
            console.log(option.data.isMask);
            $("#"+option.data.isMask).mask();
        }
    },
    beforeLoad : function(jqXHR, callbackContext, options){
        if(options && options.data && options.data.isMask) {
			$("#"+options.data.isMask).unmask();
        }
        var text = jqXHR.responseText;
        if(!text)return;
        var json;
        try{
            json = $.parseJSON(text);
        }catch(e){
            return;
        }
        var code = json.code;
        if(!(options && options.data && options.data.isTable)) {
            if(code == 400 || code == 500){
                alert(json.data);
                return false;
            }
            if(code == 300){
                alert(c_timeout);
                return false;
            }
            if(code == 250){
                alert(c_nopower);
                return false;
            }
            if(code == 999 || code == 800){
                alert(json.data);
                return false;
            }
        }
    }
});
