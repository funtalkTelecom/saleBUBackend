package com.hrtx.web.pojo;

import javax.persistence.Table;

/**
 * Permission entity. @author MyEclipse Persistence Tools
 */
@Table(name = "tb_permission")
public class Permission implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = -6136522095088295888L;
	private Long id;
	private Long pid;
	private String name;
	private String remark;
	private Integer seq;
	private String code;
	private Integer leaf;
	private Integer grade;
	private String url;
	// Constructors

	/** default constructor */
	public Permission() {
	}

	/** full constructor */
	public Permission(Long id, Long pid, String name, String remark,
			Integer seq, String code, Integer leaf, Integer grade, String url) {
		this.id = id;
		this.pid = pid;
		this.name = name;
		this.remark = remark;
		this.seq = seq;
		this.code = code;
		this.leaf = leaf;
		this.grade = grade;
		this.url = url;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getLeaf() {
		return leaf;
	}

	public void setLeaf(Integer leaf) {
		this.leaf = leaf;
	}

	public Integer getGrade() {
		return grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}