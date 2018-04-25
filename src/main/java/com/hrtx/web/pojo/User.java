package com.hrtx.web.pojo;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Table(name = "tb_user")
public class User extends BasePojo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	private Long id;
	private String loginName;
	private String pwd;
	private String name;
	private Long corpId;
	private String phone;
	private Long addUser;
	private Date addDate;
	private Integer status;
	protected Integer isDel;
	@Transient
	private String roles;

	public User() {
	}

	private User(Long id, String loginName, String pwd, String name,
			Long corpId, String phone, Long addUserId, Date addDate, Integer status) {
		super();
		this.id = id;
		this.loginName = loginName;
		this.pwd = pwd;
		this.name = name;
		this.corpId = corpId;
		this.phone = phone;
		this.addUser = addUserId;
		this.addDate = addDate;
		this.status = status;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getCorpId() {
		return corpId;
	}

	public void setCorpId(Long corpId) {
		this.corpId = corpId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Long getAddUser() {
		return addUser;
	}

	public void setAddUser(Long addUser) {
		this.addUser = addUser;
	}

	public Date getAddDate() {
		return addDate;
	}

	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getIsDel() {
		return isDel;
	}

	public void setIsDel(Integer isDel) {
		this.isDel = isDel;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	

}