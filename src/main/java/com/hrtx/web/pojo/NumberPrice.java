package com.hrtx.web.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "tb_num_price")
public class NumberPrice extends BasePojo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private Long skuId;
    private Long numId;
    private int provinceCode;
    private String provinceName;
    private int cityCode;
    private String cityName;

    private String resource;   //号码
    private double basePrice;   //基础价格
    private String netType;
    private String feature;
    private double lowConsume;

    private Long corpId;    //供销商
    private int channel;   //渠道名称
    private double ratioPrice;    //价格系数
    private double price ;       //销售价
    private long agentId;      //代理商id
    private String agent;
    private Integer isDel;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date addDate;


    public NumberPrice() {
    }

    public NumberPrice(Long id, Long skuId, Long numId, int provinceCode, String provinceName, int cityCode, String cityName,
                        String resource,double basePrice, String netType, String feature, double lowConsume,
                       Long corpId, int channel, double ratioPrice,double price, long agentId, String agent,  Integer isDel, Date addDate) {
        this.id = id;
        this.skuId = skuId;
        this.numId = numId;
        this.provinceCode = provinceCode;
        this.provinceName = provinceName;
        this.cityCode = cityCode;
        this.cityName = cityName;

        this.resource = resource;
        this.basePrice = basePrice;
        this.netType = netType;
        this.feature = feature;
        this.lowConsume = lowConsume;

        this.corpId = corpId;
        this.channel = channel;
        this.ratioPrice = ratioPrice;
        this.price = price;
        this.agentId = agentId;
        this.agent = agent;
        this.isDel = isDel;
        this.addDate = addDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Long getNumId() {
        return numId;
    }

    public void setNumId(Long numId) {
        this.numId = numId;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public String getNetType() {
        return netType;
    }

    public void setNetType(String netType) {
        this.netType = netType;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public double getLowConsume() {
        return lowConsume;
    }

    public void setLowConsume(double lowConsume) {
        this.lowConsume = lowConsume;
    }

    public Long getCorpId() {
        return corpId;
    }

    public void setCorpId(Long corpId) {
        this.corpId = corpId;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public double getRatioPrice() {
        return ratioPrice;
    }

    public void setRatioPrice(double ratioPrice) {
        this.ratioPrice = ratioPrice;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public long getAgentId() {
        return agentId;
    }

    public void setAgentId(long agentId) {
        this.agentId = agentId;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public Integer getIsDel() {
        return isDel;
    }

    public void setIsDel(Integer isDel) {
        this.isDel = isDel;
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }
}