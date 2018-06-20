package com.hrtx.web.service;

import com.hrtx.dto.Result;
import com.hrtx.global.ApiSessionUtil;
import com.hrtx.global.SessionUtil;
import com.hrtx.global.TokenGenerator;
import com.hrtx.web.mapper.ConsumerLogMapper;
import com.hrtx.web.mapper.ConsumerMapper;
import com.hrtx.web.mapper.UserMapper;
import com.hrtx.web.pojo.Consumer;
import com.hrtx.web.pojo.ConsumerLog;
import com.hrtx.web.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
		if(openid==null) return new Result(Result.ERROR, "获取openid 失败");
		String token=TokenGenerator.generateValue();
		ConsumerLog consumerLog =null;
		ConsumerLog param = new ConsumerLog();
		param.setOpenid(openid);
		consumerLog = consumerLogMapper.selectOne(param);
		if(consumerLog == null){
			//向userclient，userclientlog存数据
			Consumer userC = new Consumer();
			userC.setId(userC.getGeneralId());
			userC.setStatus(1);
			userC.setIsAgent(1);
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

			this.apiSessionUtil.saveOrUpdate(token,userC);
		}else {
			Consumer Cparam = new Consumer();
			Long id = consumerLog.getUserId();
			Cparam.setId(id);
			Consumer consumer = consumerMapper.selectOne(Cparam);
			this.apiSessionUtil.saveOrUpdate(token,consumer);
		}
		return new Result(Result.OK, token);


	}

	public Result insertConsumer(String loginName,String livePhone,String nickName,long sex,String img,String province,String city){
		//姓名,电话,昵称,性别 1男2女0未知,// 头像,//省份,//地市
		Consumer consumer= this.apiSessionUtil.getConsumer();
		long userid = consumer.getId();
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
}
