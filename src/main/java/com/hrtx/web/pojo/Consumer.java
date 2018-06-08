package com.hrtx.web.pojo;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@Table(name = "tb_consumer")
public class Consumer extends BasePojo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	private Long id;
	private String name;
	private String phone;
	private String nickName;
	private String img;
	private String province;
	private String city;
	private Integer status;
	private Date regDate;

	public Consumer() {
	}

	private Consumer(Long id, Date regDate){
		super();
		this.id = id;
		this.regDate = regDate;
	}
	private Consumer(Long id, String name, String phone, String nickName,
					 String img, String province, String city, Integer status, Date regDate) {
		super();
		this.id = id;
		this.name = name;
		this.phone = phone;
		this.name = name;
		this.nickName = nickName;
		this.img = img;
		this.province = province;
		this.city = city;
		this.status = status;
		this.regDate = regDate;
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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getRegDate() {
		return regDate;
	}

	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}
}