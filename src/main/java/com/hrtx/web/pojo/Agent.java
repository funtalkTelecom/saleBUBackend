package com.hrtx.web.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tb_agent")
public class Agent extends BasePojo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
	private String commpayName;
	private String person;
	private String phone;
	private Long province;
	private Long city;
	private Long district;
	private String address;
	private String tradingImg;
	private Integer status;
	private Long addUserId;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	private Date addDate;
	private Integer isDel;

	public Agent() {
	}

	public Agent(Long id, String commpayName, String person,String phone,Long province,Long city,Long district
				 , String address,String tradingImg, Integer status, Long addUserId, Date addDate, Integer isDel) {
		this.id = id;
		this.commpayName = commpayName;
		this.person = person;
		this.phone =phone;
		this.province = province;
		this.city = city;
		this.district = district;
		this.address = address;
		this.tradingImg =tradingImg;
		this.status = status;
		this.addUserId = addUserId;
		this.addDate = addDate;
		this.isDel = isDel;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCommpayName() {
		return commpayName;
	}

	public void setCommpayName(String commpayName) {
		this.commpayName = commpayName;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Long getProvince() {
		return province;
	}

	public void setProvince(Long province) {
		this.province = province;
	}

	public Long getCity() {
		return city;
	}

	public void setCity(Long city) {
		this.city = city;
	}

	public Long getDistrict() {
		return district;
	}

	public void setDistrict(Long district) {
		this.district = district;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}


	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Long getAddUserId() {
		return addUserId;
	}

	public void setAddUserId(Long addUserId) {
		this.addUserId = addUserId;
	}

	public Date getAddDate() {
		return addDate;
	}

	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}

	public Integer getIsDel() {
		return isDel;
	}

	public void setIsDel(Integer isDel) {
		this.isDel = isDel;
	}

	public String getTradingImg() {
		return tradingImg;
	}

	public void setTradingImg(String tradingImg) {
		this.tradingImg = tradingImg;
	}
}