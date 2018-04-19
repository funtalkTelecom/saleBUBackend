package com.hrtx.web.event;

import org.springframework.context.ApplicationEvent;

public class MsgEvent extends ApplicationEvent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String msg;
	private String msg2;
	
	public MsgEvent(Object source,String msg,String msg2){
		super(source);
		this.msg=msg;
		this.msg2=msg2;
	}

	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMsg2() {
		return msg2;
	}

	public void setMsg2(String msg2) {
		this.msg2 = msg2;
	}
	
}
