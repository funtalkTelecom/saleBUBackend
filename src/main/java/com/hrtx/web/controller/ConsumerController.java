package com.hrtx.web.controller;

import com.github.pagehelper.PageInfo;
import com.hrtx.config.annotation.Powers;
import com.hrtx.config.utils.RedisUtil;
import com.hrtx.dto.Result;
import com.hrtx.global.*;
import com.hrtx.web.service.ConsumerService;
import com.hrtx.web.service.ShareService;
import com.hrtx.web.service.UserService;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@RestController
//@RequestMapping("/api")
public class ConsumerController extends BaseReturn{

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired private ShareService shareService;
	@Autowired private ConsumerService consumerService;
	@Autowired private ApiSessionUtil apiSessionUtil;
	@Autowired private RedisUtil redisUtils;

    @PutMapping(value = "/api/Consumer")
    @Powers({PowerConsts.NOPOWER})
	public Result InsertConsumer(@RequestParam(value="loginName",required=false) String loginName,
								   @RequestParam(value="livePhone",required=false) String livePhone,
								   @RequestParam(value="nickName",required=false) String nickName,
								   @RequestParam(value="sex",required=false) Long sex,
								   @RequestParam(value="img",required=false) String img,
								   @RequestParam(value="province",required=false) String province,
								   @RequestParam(value="city",required=false) String city) {
		if(loginName==null || livePhone==null  || nickName==null || sex==null || img==null || province==null  || city==null )
			return new Result(Result.ERROR, "请确认参数是否正确。");
		return consumerService.insertConsumer(loginName,livePhone,nickName,sex,img,province,city);
	}
	/**
	 * 添加/修改合伙人信息
	 */
	@PostMapping("/api/partner/user-info")
	@Powers({PowerConsts.NOPOWER})
	public Result addInfo(HttpServletRequest request){
		String idcard_face=request.getParameter("idcard_face_file_name");
		String idcard_back=request.getParameter("idcard_back_file_name");
		String name=request.getParameter("name");
		String idcard=request.getParameter("idcard");
		String phone=request.getParameter("phone");
//		String sub_path=request.getParameter("sub_path");
		String check_code=request.getParameter("check_code");
//		String name_ = "^[\\u4E00-\\u9FA5\\uf900-\\ufa2d·s]{2,20}$";//验证姓名正则
		if(StringUtils.isEmpty(name))return new Result(Result.ERROR,"请填写姓名");
		if(StringUtils.isEmpty(idcard))return new Result(Result.ERROR,"请填写身份证号码");
		if(!RegexConsts.matches(idcard,RegexConsts.REGEX_IC_CARD))return new Result(Result.ERROR,"身份证不符合规则");
		if(!RegexConsts.matches(phone,RegexConsts.REGEX_MOBILE_COMMON))return new Result(Result.ERROR,"手机号码不符合规则");
		if(StringUtils.isEmpty(phone))return new Result(Result.ERROR,"请填写手机号码");
        String key=this.apiSessionUtil.getTokenStr()+":sms-ack";
        Object object=this.apiSessionUtil.getObject(key);
        if(object==null)return new Result(Result.ERROR,"短信验证码错误");
        Map<String,String> _map=(Map<String,String>)object;
        String session_rand=_map.get("rand");
		String session_phone=_map.get("phone");
		if(!(StringUtils.equals(session_rand,check_code)&&StringUtils.equals(phone,session_phone)))return new Result(Result.ERROR,"短信验证码错误");
		return this.shareService.addInfo(name,phone,idcard,idcard_face,idcard_back);
	}

	/**
	 * 上传文件信息
	 */
	@PostMapping("/api/upload/image")
	@Powers({PowerConsts.NOPOWER})
	public Result uploadImage(HttpServletRequest request){
		String upload_file_name=null;
		String sub_path=request.getParameter("sub_path");
		CommonsMultipartResolver multipartResolver=new CommonsMultipartResolver(request.getSession().getServletContext());//将当前上下文初始化给  CommonsMutipartResolver （多部分解析器）
		if(multipartResolver.isMultipart(request)){
			MultipartHttpServletRequest multiRequest=(MultipartHttpServletRequest)request;
			Iterator iter=multiRequest.getFileNames();//获取multiRequest中所有的文件名
			while(iter.hasNext()){//一次遍历所有文件
				String key=iter.next().toString();
				MultipartFile file=multiRequest.getFile(key);
				if(file==null)continue;
				Result result = null;
				try {
					result = this.uploadFile(/*Constants.UPLOAD_PATH_IDCARD.getStringKey()*/sub_path,"jpg,png,gif,jpeg", file, true, true);
					if(result.getCode()!=Result.OK)return result;
				} catch (Exception e) {
					log.error("文件上传失败",e);
				}
				Map<String, Object> map=(Map<String, Object>)result.getData();
				upload_file_name=String.valueOf(map.get("thumbnailServerFileName"));
			}
		}
		if(StringUtils.isEmpty(upload_file_name))return new Result(Result.ERROR,"文件上传失败");
		Map<String,String> _map=new HashMap<>();
		_map.put("file_name",upload_file_name);
		_map.put("sub_path",sub_path);
		return new Result(Result.OK,_map);
	}
	/**
	 * 发送短信验证码
	 */
	@PostMapping("/api/sms/ack")
	@Powers({PowerConsts.NOPOWER})
	public Result sendSmsMessage(HttpServletRequest request){
		String phone=request.getParameter("phone");
		String rand=Utils.randomNoByDateTime(6);
		String key=this.apiSessionUtil.getTokenStr()+":sms-ack";
		Map<String,String> _map=new HashMap<>();
		_map.put("rand",rand);
		_map.put("phone",phone);
		this.apiSessionUtil.saveObject(key,_map,60*10l);
		Messager.send(phone,String.format("【%s】您的短信验证码[%s],10分钟内有效，请勿转发给他人","靓号优选",rand));
		return new Result(Result.OK,"验证码发送成功");
	}
	//

	/**
	 * 合伙人管理
	 */
	@GetMapping("/partner/partner-index")
	@Powers({PowerConsts.PARTNER_MOUULE})
	public ModelAndView partnerIndex(){
		return new ModelAndView("admin/consumer/partner-query");
	}
	/**
	 * 合伙人列表
	 */
	@PostMapping("/partner/partner-list")
	@Powers({PowerConsts.PARTNER_MOUULE_QUERY})
	public Result partnerList(HttpServletRequest request){
		int start=NumberUtils.toInt(request.getParameter("start"),1);
		int limit=NumberUtils.toInt(request.getParameter("limit"),15);
		String name=request.getParameter("name");
		String phone=request.getParameter("phone");
		PageInfo pageInfo= consumerService.partnerPage(start,limit,name,phone);
		return new Result(Result.OK, pageInfo);
	}
	/**
	 * 合伙人审核
	 */
	@PostMapping("/partner/partner-check")
	@Powers({PowerConsts.PARTNER_MOUULE_EDIT})
	public Result partnerCheck(HttpServletRequest request){
		int consumer_id=NumberUtils.toInt(request.getParameter("id"));
		String check_status=request.getParameter("check_status");
		String check_remark=request.getParameter("check_remark");
		Result r=this.consumerService.partnerCheck(consumer_id,check_status,check_remark);
		return r;
	}
}
