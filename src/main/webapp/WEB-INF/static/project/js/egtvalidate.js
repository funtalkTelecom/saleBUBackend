
/**
 * 平台验证使用说明
 * 一、目前支持的验证类型有
 * 1、en		英文
 * 2、ch		中文
 * 3、email	邮箱
 * 4、date	时间 格式yyyy-mm-dd
 * 5、float	浮点	 格式 1.29
 * 6、str	字符串	需要特别注意  只支持中文、英文、数字 .-
 * 7、num	整数
 * 8、idc	身份证
 * 9、post	邮编
 * 10、phone	手机
 * 11、tel	固话
 * 12、ip	ip
 * 13、url	网址
 * 14、select	下拉宽  只要是-1既没选择
 * 
 * 二、表单的名字规则：参数名|验证类型(如上)_是否必填[0非必填|1必填]_字符串长度(整数);
 * 实例1、bean.name1|en_0_30	参数名为bean.name  验证方式是英文，非必填，最长30位
 * 实例2、name2|ch_1_20	参数名为name2  验证方式是中文，必填，最长20位
 * 
 * 三、注意事项
 * 提交的参数名不能重复，否则会进行覆盖
 * 如同时提交name2|ch_1_20=2&name2=1这种都含name2的参数，则实际的name2=1会被name2|ch_1_20=2覆盖，最后取到name2的结果将是2不是1
 * 遇到如上情况，需要将name2参数名进行调整
 * 
 * 四、使用方案
 * 1、页面引用当前js   加载页面并执行validate_init(from jquery对象);
 * 2、表单提交前调用validate_check(from对象)，返回true则提交后台，否则当前提交对象非法
 * <code>
 * 	validate_check(from jquery对象);
 * </code>
 * 3、提交到后台后,后台调用如下方法进行后台验证
 * <code>
 * Map<String, String> _map=Egtvalidate.validate(this.getRequest());
 * if(!_map.isEmpty()) return returnJson(800,_map);
 * </code>
 * 4、后台返回处理结果，前端处理
 * <code>
 * 	validate_show_error(from jquery对象,后台返回的Json对象);
 * </code>
 * 
 * 五、本验证方式基于Bootstrap的popover进行提示，其他平台需要修改showTipBase和clearTipBase方法
 * 
 */

//bean.name|en_0_30	验证类型_是否必填_字符串长度
//var sub_name=/^[A-Za-z0-9]+_[0-9]+_[0-9]+[_\s\S]*$/;
var _sub_name=/^[A-Za-z0-9._]+(\|){1}(en|ch|email|date|float|str|num|idc|post|phone|tel|ip|url|select){1}_(0|1){1}_[0-9]*$/;
/*console.log(_sub_name.test("bean.name|en_1_2"));
console.log(_sub_name.test("name1|en_1_2"));
console.log(_sub_name.test("bean.name|en_1_12"));
console.log(_sub_name.test("bean._name|en_1_100"));
console.log(_sub_name.test("bean.nameN|en_1_"));
console.log(_sub_name.test("en_1_2"));*/

/**
 * form 表单初始化
 * @param {} element
 */
function validate_init(element){//整个表单
	element.find("input,select").each(function(index){
		alert(111);
		if(validate_check_rule($(this))){
			//TODO 监听鼠标离开后，获取值并进行验证
			$(this).focusout(function(){
			    var res=egt_validate($(this));
				if(res.code=='300'){
					showTipBase($(this),res.msg);
				}
			});
		}
	})
}
/**
 * 验证组件是否符合规则
 * @param {} msg
 * @param {} element
 */
function validate_check_rule(element){
	var name=element.attr("name");
	if(!(_sub_name.test(name))) return false;//不符合规则	
	return true;
};
/**
 * 后台验证后返回结果处理
 * @param {} msg
 * @param {} element
 */
function validate_show_error(element,_result){
	clearTipBase(element);
	if(_result.code!='800'&&_result.code!='999')return;
	var _data=_result.data;
	for(var key in _data){
		var _value=_data[key];
		var _select="[name='"+key+"']";
		var _target=element.find(_select);
		showTipBase(_target,_value);
	};
};

/**
 * form 验证
 * @param {} element
 */
function validate_check(element){//整个表单
	clearTipBase(element);
	var error=[];
	element.find("input:enabled[name],select:enabled[name]").each(function(index){
		if(validate_check_rule($(this))){
			var res=egt_validate($(this));
			if(res.code=='300'){
				error.push($(this).attr("name"));
				showTipBase($(this),res.msg);
			}
		}
	});
	//console.log(error)
	return error.length==0;
}


/**
 * 提示方式
 * @param {} msg
 * @param {} element
 */
function egt_tip(msg,element){
	showTip(element, msg,-1);
};

function showTipBase(element,msg){
	element.attr("data-toggle","popover").attr("data-placement","bottom").attr("data-content",msg)
	element.popover('show');
	element.click(function (event) {
		element.popover('destroy');
	});
}
/**
 * 清除表单的验证信息
 * @param {} element
 */
function clearTipBase(element){
	element.find("[data-toggle='popover']").popover('destroy');//方案一
	/*element.find("input").each(function(index){//方案二
		$(this).popover('destroy');
	});*/
}

function showTip(target){
	var result = egt_validate(target);
	if(result.code=='300'){
		showTipBase(target,result.msg);
	}
}

/*			
function showTip(target){
	target.poshytip('destroy');
	target.poshytip({
		className: 'tip-yellowsimple',
		showOn : "focus",
//		content: msg,
		content:function(updateCallback) {
			var result = egt_validate($(this));
			if(result.code=='300'){
				return result.msg
			}
		},
		alignTo: 'target',
		alignX: 'right',
		offsetY: -25,
		offsetX: 10,
		showTimeout: 100
	});
}*/
/**
 * 
 * @param {} code
 * 100 不验证
 * 200 成功
 * 300 值不符合规则
 * 301 不验证
 * @param {} msg
 */
function validate_result(code,msg){
	 var info={"code":code,"msg":msg};
	 return info;
};

function egt_validate(element){
	var name=element.attr("name");
	if(!(_sub_name.test(name)))return new validate_result('100',"不符合规则，无需校验");//不符合规则
	var _fz=name.split("|");
	var file=_fz[1].split("_");
//	console.log(element.val().length+"     ==   "+file[1]+"   "+name+"  = "+element.val())
	if((element.val().length==0)&&(file[1]=='0'))return new validate_result('301',"非必填项同时字段为空");//非必填且值为空
	if(file[0]=='select'&&file[1]=='0'&&element.val()=='-1')return new validate_result('301',"非必填项同时字段为空");//非必填且值为空
	var value=element.val();
	if(value.length>file[2]){
		//if(!(/^[\u0391-\uFFE5]+$/.test(value)))
		return new validate_result('300',"长度不能超过"+file[2]+"个");
	}
	if(file[1]=='1'&&value.length==0){
		return new validate_result('300',"此项为必填项");
	}
	var type=file[0];
	if(type=='email'){
		if(!(/^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/.test(value))) 
		return new validate_result('300',"请输入正确的邮箱");
	}
	if(type=='url'){
		    //^(((http|https)://)|(www\\.))+(([a-zA-Z0-9\\._!-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}[\\:[0-9]+]*))(/[\u4e00-\u9fa5\\w-./?%&=!]*)?$
//		if(!(/^(((http|https):\/\/)|(www.))+(([a-zA-Z0-9._!-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}[:[0-9]+]*))([\u4e00-\u9fa5\w-.?%&=!]*)?$/.test(value))) 
		if(!isURL(value))return new validate_result('300',"请输入正确的网址");
	}
	if(type=='date'){
		if(!(/^\d{4}[\/-]\d{1,2}[\/-]\d{1,2}$/.test(value)))return new validate_result('300',"请填写正确的时间格式");//eg.2017-07-05
	}
	if(type=='ch'){
		if(!(/^[\u0391-\uFFE5]+$/.test(value)))
		return new validate_result('300',"只能包含中文字符");
	}
	if(type=='en'){
		if(!(/^[A-Za-z]+$/.test(value)))
		return new validate_result('300',"只能包含英文字符");
	}
	if(type=='float'){
		if(!(/^[-\+]?\d+(\.\d+)?$/.test(value)))
		return new validate_result('300',"只能是数字");
	}
	if(type=='str'){
		if(!(/^[\u0391-\uFFE5\w\/(\/)\/.\/:\/-\s]+$/.test(value))) 
		return new validate_result('300',"只能包含中文、英文、数字、下划线、点、冒号等字符");
	}
	if(type=='num'){
		if(!(/^\d+$/.test(value))) 
		return new validate_result('300',"只能是数字");
	}
	if(type=='idc'){
		if(!(/^(\d{6})()?(\d{4})(\d{2})(\d{2})(\d{3})(\w)$/.test(value))) 
		return new validate_result('300',"请输入正确的身份证号码");
	}
	if(type=='post'){
		if(!(/^[0-9]{6}$/.test(value))) 
		return new validate_result('300',"请正确填写您的邮政编码");
	}
	if(type=='phone'){
		if(!(/^1\d{10}$/.test(value))) 
		return new validate_result('300',"请正确填写您的手机号码");	
	}
	if(type=='tel'){
		var mobile = /^1\d{10}$/;   
	    var tel = /^\d{3,4}-?\d{7,9}(|([-\u8f6c]{1}\d{1,5}))$/;
	    //var tel =/^((\d{3,4}\-)|)\d{7,8}(|([-\u8f6c]{1}\d{1,5}))$/;
		if(!((tel.test(value) || mobile.test(value))))
		return new validate_result('300',"请正确填写您的联系电话");
	}
	if(type=='ip'){
		if(!(/^(([1-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-5])))\.)(([1-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-5])))\.){2}([1-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-5])))$/.test(value)))
		return new validate_result('300',"请正确填写您的IP地址");
	}
	if(type=='select'){
		if(value=='-1')return new validate_result('300',"请选择");
		if(!(/^[A-Za-z0-9_\-\.\|\u0391-\uFFE5]+$/.test(value)))return new validate_result('300',"选择的值非法");
	}
	return new validate_result('200',"验证成功");
};

// 身份证号码的验证规则
function isIdCardNo(num) {
	// if (isNaN(num)) {alert("输入的不是数字！"); return false;}
	var len = num.length, re;
	if (len == 15)
		re = new RegExp(/^(\d{6})()?(\d{2})(\d{2})(\d{2})(\d{2})(\w)$/);
	else if (len == 18)
		re = new RegExp(/^(\d{6})()?(\d{4})(\d{2})(\d{2})(\d{3})(\w)$/);
	else {
		//alert("输入的数字位数不对。");
		return false;
	}
	var a = num.match(re);
	if (a != null) {
		if (len == 15) {
			var D = new Date("19" + a[3] + "/" + a[4] + "/" + a[5]);
			var B = D.getYear() == a[3] && (D.getMonth() + 1) == a[4]
					&& D.getDate() == a[5];
		} else {
			var D = new Date(a[3] + "/" + a[4] + "/" + a[5]);
			var B = D.getFullYear() == a[3] && (D.getMonth() + 1) == a[4]
					&& D.getDate() == a[5];
		}
		if (!B) {
			//alert("输入的身份证号 " + a[0] + " 里出生日期不对");
			return false;
		}
	}
	if (!re.test(num)) {
		//alert("身份证最后一位只能是数字和字母X");
		return false;
	}
	return true;
}

function isURL(str_url) { 
	var strRegex = '^((https|http|ftp|rtsp|mms)?://)'
	+ '?(([0-9a-z_!~*\'().&=+$%-]+: )?[0-9a-z_!~*\'().&=+$%-]+@)?' //ftp的user@ 
	+ '(([0-9]{1,3}.){3}[0-9]{1,3}' // IP形式的URL- 199.194.52.184 
	+ '|' // 允许IP和DOMAIN（域名） 
	+ '([0-9a-z_!~*\'()-]+.)*' // 域名- www. 
	+ '([0-9a-z][0-9a-z-]{0,61})?[0-9a-z].' // 二级域名 
	+ '[a-z]{2,6})' // first level domain- .com or .museum 
	+ '(:[0-9]{1,4})?' // 端口- :80 
	+ '((/?)|' // a slash isn't required if there is no file name 
	+ '(/[0-9a-z_!~*\'().;?:@&=+$,%#-]+)+/?)$'; 
	var re=new RegExp(strRegex); 
	return re.test(str_url);
}
