package com.hrtx.web.pojo;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tb_consumer_log")
public class ConsumerLog extends BasePojo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	private Long id;
	private Integer loginType;
	private Long userId;
	private String loginName;
	private String livePhone;
	private String openid;
	private Integer subscribe;
	private String nickName;
	private Integer sex;
	private String  subTime;
	private Integer status;
	protected Date addDate;

	public ConsumerLog() {
	}

	private ConsumerLog(Long id, Integer loginType, Long userId, String loginName, String livePhone, String openid,
                        Integer subscribe, String nickName,
                        Integer sex, String  subTime, Integer status, Date addDate) {
		super();
		this.id = id;
		this.loginType = loginType;
		this.userId = userId;
		this.loginName = loginName;
		this.nickName = nickName;
		this.livePhone = livePhone;
		this.openid = openid;
		this.subscribe = subscribe;
		this.nickName = nickName;
		this.sex = sex;
		this.subTime = subTime;
		this.status = status;
		this.addDate = addDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getLoginType() {
		return loginType;
	}

	public void setLoginType(Integer loginType) {
		this.loginType = loginType;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getLivePhone() {
		return livePhone;
	}

	public void setLivePhone(String livePhone) {
		this.livePhone = livePhone;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public Integer getSubscribe() {
		return subscribe;
	}

	public void setSubscribe(Integer subscribe) {
		this.subscribe = subscribe;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public String getSubTime() {
		return subTime;
	}

	public void setSubTime(String subTime) {
		this.subTime = subTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getAddDate() {
		return addDate;
	}

	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}
}