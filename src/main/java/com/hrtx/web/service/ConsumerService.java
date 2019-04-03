package com.hrtx.web.service;

import com.github.abel533.entity.Example;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.common.weixin.api.AccessTokenApi;
import com.hrtx.common.weixin.api.WxConfig;
import com.hrtx.dto.Result;
import com.hrtx.global.*;
import com.hrtx.web.mapper.ConsumerLogMapper;
import com.hrtx.web.mapper.ConsumerMapper;
import com.hrtx.web.mapper.UserMapper;
import com.hrtx.web.pojo.Consumer;
import com.hrtx.web.pojo.ConsumerLog;
import com.hrtx.web.pojo.PromotionPlan;
import com.hrtx.web.pojo.User;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ConsumerService extends BaseService {
	
	@Autowired SessionUtil sessionUtil;
	@Autowired private UserMapper userMapper;
	@Autowired private ConsumerLogMapper consumerLogMapper;
	@Autowired private ConsumerMapper consumerMapper;
	@Autowired private ApiSessionUtil apiSessionUtil;

	public void test() {
		List<User> list=this.userMapper.select(null);
		for (User user : list) {
			System.out.println(user.getName());
		}
	}

	public Result getOpenId(String code) {
		String appid=SystemParam.get("AppID");
		String appsecret=SystemParam.get("AppSecret");
		String grant_type = "authorization_code";//授权（必填）
		String requestUrl = "https://api.weixin.qq.com/sns/jscode2session";
		String params = "appid=" + appid + "&secret=" + appsecret + "&js_code=" + code + "&grant_type=" + grant_type;//请求参数
		String Openid = null;//发送请求
		try {
			String data = HttpUtil.get(requestUrl, params);
			if(StringUtils.isEmpty(data))return new Result(Result.ERROR,"请求错误");
			JSONObject json = JSONObject.fromObject(data);
			if(!json.containsKey("openid"))return new Result(Result.ERROR,"微信返回结果错误");
			Openid=String.valueOf(json.get("openid"));//用户的唯一标识（openid）
			return new Result(Result.OK,Openid);
		} catch (Exception e) {
			log.error("获取用户Openid失败",e);
		}
		return new Result(Result.ERROR,"无法获取Openid");
	}
	
	public Result isOpenid(String openid) {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if(openid==null) return new Result(Result.ERROR, "获取openid 失败");
		String token=TokenGenerator.generateValue();
		ConsumerLog consumerLog =null;
		ConsumerLog param = new ConsumerLog();
		param.setOpenid(openid);
		consumerLog = consumerLogMapper.selectOne(param);
		Map<String,String> _map=new HashMap<>();
		_map.put("__sessid",token);
		if(consumerLog == null){
			//向userclient，userclientlog存数据
			Consumer userC = new Consumer();
//			userC.setId(userC.getGeneralId());
			userC.setStatus(1);
			userC.setIsAgent(1);
			userC.setRegDate(new Date());
			userC.setIsPartner(0);  //是否合伙人 1是，2否
			userC.setPartnerCheck(0); //已确认的合伙人 1是0否；是方可提现
			consumerMapper.insert(userC);
			Integer userid = userC.getId();

			ConsumerLog log = new ConsumerLog();
			log.setUserId(userid);
			log.setOpenid(openid);
			log.setStatus(1);
			log.setLoginType(2);
			log.setSubTime(sdf.format(date));
			log.setAddDate(new Date());
			consumerLogMapper.insert(log);

			this.apiSessionUtil.saveOrUpdate(token,userC);
			_map.put("consumer_id",String.valueOf(userC.getId()));
			_map.put("isPartner",String.valueOf(userC.getIsPartner()));//是否合伙人 1是，2否
			_map.put("partnerCheck",String.valueOf(userC.getPartnerCheck())); //已确认的合伙人 1是0否；是方可提现
		}else {
			Consumer Cparam = new Consumer();
			Integer id = consumerLog.getUserId();
			Cparam.setId(id);
			Consumer consumer = consumerMapper.selectOne(Cparam);
			this.apiSessionUtil.saveOrUpdate(token,consumer);
			_map.put("consumer_id",String.valueOf(Cparam.getId()));
			_map.put("isPartner",String.valueOf(consumer.getIsPartner()));//是否合伙人 1是，2否
			_map.put("partnerCheck",String.valueOf(Cparam.getPartnerCheck()));//已确认的合伙人 1是0否；是方可提现
		}
		return new Result(Result.OK, _map);


	}

	public Result insertConsumer(String loginName,String livePhone,String nickName,long sex,String img,String province,String city){
		//姓名,电话,昵称,性别 1男2女0未知,// 头像,//省份,//地市
		Consumer consumer= this.apiSessionUtil.getConsumer();
		Integer userid = consumer.getId();
		ConsumerLog param = new ConsumerLog();
		param.setUserId(userid);
		ConsumerLog log = consumerLogMapper.selectOne(param);
		if(log!=null){
			consumerLogMapper.insertConsumerLog(userid,loginName,livePhone,nickName,sex);
		}
		Consumer userC=new Consumer();
		userC.setId(userid);
		Consumer user = consumerMapper.selectOne(userC);
		if(user!=null){
			//更新userClient
			consumerMapper.insertConsumer(userid,loginName,livePhone,nickName,img,province,city);
		}
		return new Result(Result.OK, "注册成功");
	}

	public  Consumer  getConsumerById(Consumer consumer)
	{
		consumer = consumerMapper.selectOne(consumer);
		return  consumer;
	}
    public Result getAccess_token() {
		WxConfig.init(SystemParam.get("AppID"),SystemParam.get("AppSecret"),"");
		String ACCESS_TOKEN=AccessTokenApi.getAccessToken().getAccessToken();
		return new Result(Result.OK, ACCESS_TOKEN);
        /*String tokenUrl = "https://api.weixin.qq.com/cgi-bin/token";
        String tokenParams = "grant_type=client_credential&appid=" + appid + "&secret=" + appsecret;
        String access_token = null;//发送请求
        try {
            String data = HttpUtil.get(tokenUrl, tokenParams);
            if (StringUtils.isEmpty(data)) return new Result(Result.ERROR, "请求错误");
            JSONObject json = JSONObject.fromObject(data);
            if (!json.containsKey("access_token")) return new Result(Result.ERROR, "微信返回结果错误");
            access_token = String.valueOf(json.get("access_token"));//用户的唯一标识（openid）
            return new Result(Result.OK, access_token);
        } catch (Exception e) {
            log.error("获取access_token失败", e);
        }
        return new Result(Result.ERROR, "无法获取access_token");*/
    }

    public Result getQrcode(String access_token, String scene, String page,String width,String auto_color,String line_color,String is_hyaline,HttpServletResponse response) {
		String requestUrl = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + access_token;
        JSONObject json = new JSONObject();
        if(!StringUtils.isBlank(scene)){
            json.put("scene", scene);
        }
        if(!StringUtils.isBlank(page)){
            json.put("page", page);
        }
        if(!StringUtils.isBlank(width)){
            json.put("width", width);
        }
        if(!StringUtils.isBlank(line_color)){
            json.put("line_color", JSONObject.fromObject(line_color));
        }
        if(!StringUtils.isBlank(auto_color)){
            json.put("auto_color", new Boolean(auto_color));
        }
        if(!StringUtils.isBlank(is_hyaline)){
            json.put("is_hyaline", new Boolean(is_hyaline));
        }
        try {
            HttpUtil.getQrcode(requestUrl, json.toString(), null,response);
            return new Result(Result.OK,"success");
        }catch (Exception e){
            return new Result(Result.ERROR,"二维码生成失败");
        }
	}

	public ConsumerLog getConsumerLog(int consumer_id,int loginType){
		ConsumerLog consumerLog = new ConsumerLog();
		consumerLog.setUserId(consumer_id);
		consumerLog.setStatus(1);
		consumerLog.setLoginType(loginType);
		consumerLog = consumerLogMapper.selectOne(consumerLog);
		return consumerLog;
	}

	/**
	 *
	 * @param status
	 * @param num
	 * @param start
	 * @param limit
	 * @return
	 */
	public PageInfo partnerPage(int start,int limit,String name,String phone) {
		Consumer pp=new Consumer();
		pp.setStart(start);
		pp.setLimit(limit);
		pp.setIsPartner(1);
		Example example=new Example(Consumer.class);
		Example.Criteria criteria=example.createCriteria();
		criteria.andEqualTo("isPartner",1);
		if(StringUtils.isNotEmpty(name))criteria.andLike("name",name);
		if(StringUtils.isNotEmpty(phone))criteria.andLike("phone",phone);
		example.setOrderByClause("id desc");
		PageHelper.startPage(pp.startToPageNum(),pp.getLimit());
		List<Consumer> _list = consumerMapper.selectByExample(example);
		PageInfo<Object> pm = new PageInfo(_list);
		List<Object> mapList=new ArrayList<>();
		for (Consumer consumer:_list) {
			Map map=new HashMap();
			map.put("id",consumer.getId());
			map.put("name",consumer.getName());
			if(consumer.getPartnerCheck()==null||consumer.getPartnerCheck().equals(0)){
				map.put("phone",consumer.getPhone());
				map.put("idcard",consumer.getIdcard());
			}else{
				map.put("phone",Utils.sensitive(consumer.getPhone(),3,4));
				map.put("idcard",Utils.sensitive(consumer.getIdcard(),4,4));
			}
			map.put("idcard_back",consumer.getIdcardBack());
			map.put("idcard_face",consumer.getIdcardFace());
			map.put("nick_name",consumer.getNickName());
			map.put("partner_check",consumer.getPartnerCheck());
			mapList.add(map);
		}
		pm.getList().clear();
		pm.getList().addAll(mapList);
		return pm;
	}

	public Result partnerCheck(int consumer_id,String check_status,String check_remark) {
		Consumer consumer=this.consumerMapper.selectByPrimaryKey(consumer_id);
		if(consumer==null)return new Result(Result.ERROR,"数据错误");
		if(StringUtils.equals(check_status,"1")){
			consumer.setPartnerCheck(1);
		}else{
			consumer.setPartnerCheck(2);
			Messager.send(consumer.getPhone(),"抱歉，您申请的合伙人资料由于【"+check_remark+"】,请到小程序中重新提交。");
		}
		this.consumerMapper.updateByPrimaryKey(consumer);
		return new Result(Result.OK,"审核已受理");
	}
}
