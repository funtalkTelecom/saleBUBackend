package com.hrtx.web.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import javax.persistence.*;
import java.util.Date;

@Table(name = "tb_consumer")
public class Consumer extends BasePojo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonSerialize(using = ToStringSerializer.class)
	private Integer id;
	private String name;
	private String phone;
	private String nickName;
	private String img;
	private String province;
	private String city;
	private Integer status;
	private Date regDate;
	private Integer isAgent;
	private String idcard;//身份证号
	private String idcardFace;//身份证正面  文件名
	private String idcardBack;//身份证背面
	private Integer isPartner;//是否合伙人 1是0否
	private Integer partnerCheck;//已确认的合伙人 1是0否 是则可提现
	private Integer upConsumer;//首次通过那个用户打开的
	private String commpayName;
	private Long agentProvince;
	private Long agentCity;
	private Long agentDistrict;
	private String agentAddress;
	private String tradingImg;

	public Consumer() {
	}

	private Consumer(Integer id, Date regDate){
		super();
		this.id = id;
		this.regDate = regDate;
	}
	private Consumer(Integer id, String name, String phone, String nickName,
					 String img, String province, String city, Integer status, Date regDate,Integer isAgent) {
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
		this.isAgent = isAgent;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
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

	public Integer getIsAgent() {
		return isAgent;
	}

	public void setIsAgent(Integer isAgent) {
		this.isAgent = isAgent;
	}

	public String getCommpayName() {
		return commpayName;
	}

	public void setCommpayName(String commpayName) {
		this.commpayName = commpayName;
	}

	public Long getAgentProvince() {
		return agentProvince;
	}

	public void setAgentProvince(Long agentProvince) {
		this.agentProvince = agentProvince;
	}

	public Long getAgentCity() {
		return agentCity;
	}

	public void setAgentCity(Long agentCity) {
		this.agentCity = agentCity;
	}

	public Long getAgentDistrict() {
		return agentDistrict;
	}

	public void setAgentDistrict(Long agentDistrict) {
		this.agentDistrict = agentDistrict;
	}

	public String getAgentAddress() {
		return agentAddress;
	}

	public void setAgentAddress(String agentAddress) {
		this.agentAddress = agentAddress;
	}

	public String getTradingImg() {
		return tradingImg;
	}

	public void setTradingImg(String tradingImg) {
		this.tradingImg = tradingImg;
	}

	public String getIdcard() {
		return idcard;
	}

	public void setIdcard(String idcard) {
		this.idcard = idcard;
	}

	public String getIdcardFace() {
		return idcardFace;
	}

	public void setIdcardFace(String idcardFace) {
		this.idcardFace = idcardFace;
	}

	public String getIdcardBack() {
		return idcardBack;
	}

	public void setIdcardBack(String idcardBack) {
		this.idcardBack = idcardBack;
	}

	public Integer getIsPartner() {
		return isPartner;
	}

	public void setIsPartner(Integer isPartner) {
		this.isPartner = isPartner;
	}

	public Integer getPartnerCheck() {
		return partnerCheck;
	}

	public void setPartnerCheck(Integer partnerCheck) {
		this.partnerCheck = partnerCheck;
	}

	public Integer getUpConsumer() {
		return upConsumer;
	}

	public void setUpConsumer(Integer upConsumer) {
		this.upConsumer = upConsumer;
	}
}