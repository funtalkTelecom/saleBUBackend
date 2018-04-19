package com.hrtx.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.hrtx.web.event.MsgEvent;


@Component
public class EventService {
	
	@Autowired ApplicationContext applicationContext;
	
	public void testEvent(String msg1,String msg2){
		applicationContext.publishEvent(new MsgEvent(this,msg1,msg2));
	}
	
	
}
