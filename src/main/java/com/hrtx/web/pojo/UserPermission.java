package com.hrtx.web.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import static javax.persistence.GenerationType.SEQUENCE;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * RolePermission entity. @author MyEclipse Persistence Tools
 */
@Table(name = "tb_user_permission")
public class UserPermission extends BasePojo implements java.io.Serializable {

	// Fields
	@Id
	private Long id;
	private Long userId;
	private Long permission;

	// Constructors

	/** default constructor */
	public UserPermission() {
	}

	/** full constructor */
	public UserPermission(Long userId, Long permission) {
		this.userId = userId;
		this.permission = permission;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getPermission() {
		return permission;
	}

	public void setPermission(Long permission) {
		this.permission = permission;
	}

	

}