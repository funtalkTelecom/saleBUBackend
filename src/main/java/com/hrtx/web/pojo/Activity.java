package com.hrtx.web.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import javax.persistence.*;
import java.util.Date;

@Table(name = "tb_activity")
public class Activity extends BasePojo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonSerialize(using = ToStringSerializer.class)
	private Integer id;
	private String title;
	private Integer type;   //活动类型  秒杀
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	private Date beginDate;  //开始时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	private Date endDate;   //结束时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	private Date addDate;   //添加时间
	private Integer addSellerId;  //添加人公司ID
	private Integer addUserId;    //添加人ID
	private Integer isDel;
	private Integer agentId;    //代理商
	private Integer status;    //状态
	private String startH;
	private String endH;

	@Transient
	private String saleNum;
	@Transient
	private String beginDates;
	@Transient
	private String endDates;
	@Transient
	private String strjson;
	@Transient
	private String statusText;

	public Activity() {
	}

	public Activity(Integer id,String title, Integer type,Date beginDate,Date endDate,Date addDate,Integer addSellerId,Integer addUserId,Integer isDel,Integer agentId,Integer status) {
		this.id = id;
		this.title = title;
		this.type = type;
		this.beginDate = beginDate;
		this.endDate = endDate;
		this.addDate = addDate;
		this.addSellerId = addSellerId;
		this.addUserId = addUserId;
		this.isDel = isDel;
		this.agentId = agentId;
		this.status = status;

	}


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getAddDate() {
		return addDate;
	}

	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}

	public Integer getAddSellerId() {
		return addSellerId;
	}

	public void setAddSellerId(Integer addSellerId) {
		this.addSellerId = addSellerId;
	}

	public Integer getAddUserId() {
		return addUserId;
	}

	public void setAddUserId(Integer addUserId) {
		this.addUserId = addUserId;
	}

	public Integer getIsDel() {
		return isDel;
	}

	public void setIsDel(Integer isDel) {
		this.isDel = isDel;
	}

	public String getSaleNum() {
		return saleNum;
	}

	public void setSaleNum(String saleNum) {
		this.saleNum = saleNum;
	}

	public Integer getAgentId() {
		return agentId;
	}

	public void setAgentId(Integer agentId) {
		this.agentId = agentId;
	}

	public String getBeginDates() {
		return beginDates;
	}

	public void setBeginDates(String beginDates) {
		this.beginDates = beginDates;
	}

	public String getEndDates() {
		return endDates;
	}

	public void setEndDates(String endDates) {
		this.endDates = endDates;
	}

	public String getStrjson() {
		return strjson;
	}

	public void setStrjson(String strjson) {
		this.strjson = strjson;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getStatusText() {
		return statusText;
	}

	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}

	public String getStartH() {
		return startH;
	}

	public void setStartH(String startH) {
		this.startH = startH;
	}

	public String getEndH() {
		return endH;
	}

	public void setEndH(String endH) {
		this.endH = endH;
	}

}