// 每个提交的ajax都会触发此方法
function getRequest(str) {
    var theRequest = new Object();
    var strs = str.split("&");
    for(var i = 0; i < strs.length; i ++) {
        theRequest[strs[i].split("=")[0]] = decodeURI(strs[i].split("=")[1]);
    }
    return theRequest;
}
var ajaxLock = {};
$.extend(true, jQuery.ajaxSettings, {
    data : {
        isAjax : true// 将连接请求标上ajax方式
    },
    beforeRequest : function (option){
        var param =  option.data;
        if(typeof(param) == 'string') param = getRequest(option.data);
        var key = option.url;
        if(param && (param.noRepeat == 1 || param.mask) && ajaxLock[key] && ajaxLock[key] == 1){
            return false;
        }
        ajaxLock[key] = 1;
        if(param &&　param.mask) {
            if(param.maskId){
                $("#"+param.maskId).mask(param.mask);
            }else {
                $("body").mask(param.mask);
            }
        }
        return true;
    },
    beforeLoad : function(jqXHR, callbackContext, option){
        var param =  option.data;
        if(typeof(param) == 'string') param = getRequest(option.data);
        if(param && param.mask) {
            if(param.maskId){
                $("#"+param.maskId).unmask();
            }else {
                $("body").unmask();
            }
        }
        ajaxLock[option.url] = 0;
        var text = jqXHR.responseText;
        if(!text)return;
        var json;
        try{
            json = $.parseJSON(text);
        }catch(e){
            return;
        }
        var code = json.code;
        if(!(option && option.data && option.data.isTable)) {
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
