package com.hrtx.web.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import javax.persistence.*;
import java.util.Date;

@Table(name = "tb_agent")
public class Agent extends BasePojo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonSerialize(using = ToStringSerializer.class)
	private Integer id;
	private String commpayName;
	private String person;
	private String phone;
	private Long province;
	private Long city;
	private Long district;
	private String address;
	private String tradingImg;
	private Integer status;
	@JsonSerialize(using = ToStringSerializer.class)
	private Integer addConsumerId;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	private Date addDate;
	private Integer isDel;
	private String checkRemark;  //备注信息
	private String loginName;
	private String pwd;
	private Integer type;
	private Integer channelId;  //代理商ID

	@Transient
	private String  provinceName;// 所属省份
	@Transient
	private String  cityName;   //所属地市
	@Transient
	private String  districtName;//所属区县
	@Transient
	private String  statustext;//
	@Transient
	private String  userName;//

	public Agent() {
	}

	public Agent(String commpayName, String person,String phone,Long province,Long city,Long district
				 , String address,String tradingImg, Integer status, Integer addConsumerId, Date addDate, Integer isDel,Integer type,Integer channelId ) {
		this.commpayName = commpayName;
		this.person = person;
		this.phone =phone;
		this.province = province;
		this.city = city;
		this.district = district;
		this.address = address;
		this.tradingImg =tradingImg;
		this.status = status;
		this.addConsumerId = addConsumerId;
		this.addDate = addDate;
		this.isDel = isDel;
		this.type = type;
		this.channelId = channelId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
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

	public Integer getAddConsumerId() {
		return addConsumerId;
	}
	public void setAddConsumerId(Integer addConsumerId) {
		this.addConsumerId = addConsumerId;
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

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	public String getStatustext() {
		return statustext;
	}

	public void setStatustext(String statustext) {
		this.statustext = statustext;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCheckRemark() {
		return checkRemark;
	}

	public void setCheckRemark(String checkRemark) {
		this.checkRemark = checkRemark;
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

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getChannelId() {
		return channelId;
	}
	public void setChannelId(Integer channelId) {
		this.channelId = channelId;
	}
}