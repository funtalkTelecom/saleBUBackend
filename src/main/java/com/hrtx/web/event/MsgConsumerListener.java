package com.hrtx.web.event;

import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


@Component
public class MsgConsumerListener implements ApplicationListener<MsgEvent>{
	
	@Async
	public void onApplicationEvent(MsgEvent event) {
		System.out.println(event.getMsg()+"  2222222  "+"  ==="+event.getMsg2());
		try {
			Thread.sleep(1000);
			System.out.println("2222222 休息好了");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
