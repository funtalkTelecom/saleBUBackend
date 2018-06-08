package com.hrtx.web.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.dto.Menu;
import com.hrtx.dto.Result;
import com.hrtx.global.ApiSessionUtil;
import com.hrtx.global.SessionUtil;
import com.hrtx.global.TokenGenerator;
import com.hrtx.global.Utils;
import com.hrtx.web.mapper.ConsumerLogMapper;
import com.hrtx.web.mapper.ConsumerMapper;
import com.hrtx.web.mapper.UserMapper;
import com.hrtx.web.pojo.Consumer;
import com.hrtx.web.pojo.ConsumerLog;
import com.hrtx.web.pojo.User;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ConsumerService {
	
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
	
	public Result isOpenid(String openid) {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String token=TokenGenerator.generateValue();
		ConsumerLog userlog =null;
		ConsumerLog param = new ConsumerLog();
		param.setOpenid(openid);
		userlog = consumerLogMapper.selectOne(param);
		if(userlog == null){
			//向userclient，userclientlog存数据
			Consumer userC = new Consumer();
			userC.setId(userC.getGeneralId());
			userC.setStatus(1);
			userC.setRegDate(new Date());
			consumerMapper.insert(userC);
			Long userid = userC.getId();

			ConsumerLog log = new ConsumerLog();
			log.setId(log.getGeneralId());
			log.setUserId(userid);
			log.setOpenid(openid);
			log.setStatus(1);
			log.setLoginType(2);
			log.setSubTime(sdf.format(date));
			log.setAddDate(new Date());
			consumerLogMapper.insert(log);

			this.apiSessionUtil.saveOrUpdate(token,log);
		}else {
			this.apiSessionUtil.saveOrUpdate(token,userlog);
		}
		return new Result(Result.OK, token);


	}

	public Result insertConsumer(String loginName,String livePhone,String nickName,long sex,String img,String province,String city){
		//姓名,电话,昵称,性别 1男2女0未知,// 头像,//省份,//地市
		ConsumerLog userlog= this.apiSessionUtil.getUserClient();
		long userid = userlog.getUserId();
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
}
