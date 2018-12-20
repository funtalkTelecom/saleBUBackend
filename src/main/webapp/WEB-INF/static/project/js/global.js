// 每个提交的ajax都会触发此方法
function formDataToMap(formdata) {
    var data = {};
    for (var key of formdata.keys()) {
        data[key] = formdata.getAll(key);
    }
    return data;
}
var ajaxLock = {};
$.extend(true, jQuery.ajaxSettings, {
    data : {
        isAjax : true// 将连接请求标上ajax方式
    },
    beforeRequest : function (option){
        if(option.data instanceof FormData) option.data = formDataToMap(option.data);
        var key = option.url+JSON.stringify(option.data);
        if(ajaxLock[key] && ajaxLock[key] == 1){
            return false;
        }
        ajaxLock[key] = 1;
        // if(option && option.data && option.data.mask) $.showLoading(option.data.mask);
        return true;
    },
    beforeLoad : function(jqXHR, callbackContext, options){
        if(!options && options.data instanceof FormData) options.data = formDataToMap(options.data);
        var key = options.url+JSON.stringify(options.data);
        // if(options && options.data && options.data.mask) $.hideLoading();
        ajaxLock[key] = 0;
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
                alert(json.data);
                return false;
            }
            if(code == 250){
                alert(json.data);
                return false;
            }
            if(code == 999 || code == 800){
                alert(json.data);
                return false;
            }
        }
    }
});
