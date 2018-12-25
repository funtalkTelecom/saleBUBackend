// 每个提交的ajax都会触发此方法
function getRequest(str) {
    var theRequest = new Object();
    var strs = str.split("&");
    console.log(strs);
    for(var i = 0; i < strs.length; i ++) {
        theRequest[strs[i].split("=")[0]] = unescape(strs[i].split("=")[1]);
    }
    return theRequest;
}
var ajaxLock = {};
$.extend(true, jQuery.ajaxSettings, {
    data : {
        isAjax : true// 将连接请求标上ajax方式
    },
    beforeRequest : function (option){
        var key = option.url;
        if(ajaxLock[key] && ajaxLock[key] == 1){
            return false;
        }
        ajaxLock[key] = 1;
        var param =  option.data;
        if(typeof(param) == 'string') param = getRequest(option.data);
        if(param &&　param.mask) {
            $("body").mask(param.mask);
        }
        return true;
    },
    beforeLoad : function(jqXHR, callbackContext, options){
        $("body").unmask();
        var key = options.url;
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
