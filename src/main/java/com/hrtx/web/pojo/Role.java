package com.hrtx.web.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Role entity. @author MyEclipse Persistence Tools
 */
@Table(name = "tb_role")
public class Role extends BasePojo implements java.io.Serializable {

	// Fields
	@Id
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
	private String name;
	private Integer status;
	protected Integer isDel;
	// Constructors

	/** default constructor */
	public Role() {
	}

	/** full constructor */
	public Role(String name, Integer status, Integer isDel) {
		this.name = name;
		this.status = status;
		this.isDel = isDel;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

}