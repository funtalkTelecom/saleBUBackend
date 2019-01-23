package com.hrtx.web.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "tb_num_price")
public class NumPrice extends BasePojo implements java.io.Serializable {
    @Id
    private Integer id;

    private Integer skuId;

    private Integer numId;

    private Integer provinceCode;

    private String provinceName;

    private Integer cityCode;

    private String cityName;

    private String resource;

    private BigDecimal basePrice;

    private String netType;

    private String feature;

    private BigDecimal lowConsume;

    private Integer corpId;

    private Integer channel;

    private BigDecimal ratioPrice;

    private BigDecimal price;

    private String agent;

    private Integer agentId;

    private Integer isDel;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date addDate;

    @Transient
    private String tag;
    @Transient
    private String beginDate;  //秒杀开始时间
    @Transient
    private String newDate;   //秒杀当前时间
    @Transient
    private String endDate;   //秒杀结束时间
    @Transient
    private int falg;         //1 当前正在进行；2即将开始

    @Transient
    private BigDecimal priceS;

    @Transient
    private BigDecimal priceE;
    @Transient
    private String numTags;

    public NumPrice(Integer id, Integer skuId, Integer numId, Integer provinceCode, String provinceName, Integer cityCode, String cityName, String resource, BigDecimal basePrice, String netType, String feature, BigDecimal lowConsume, Integer corpId, Integer channel, BigDecimal ratioPrice, BigDecimal price, String agent, Integer agentId, Integer isDel, Date addDate) {
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
        this.agent = agent;
        this.agentId = agentId;
        this.isDel = isDel;
        this.addDate = addDate;
    }

    public NumPrice() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSkuId() {
        return skuId;
    }

    public void setSkuId(Integer skuId) {
        this.skuId = skuId;
    }

    public Integer getNumId() {
        return numId;
    }

    public void setNumId(Integer numId) {
        this.numId = numId;
    }

    public Integer getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(Integer provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName == null ? null : provinceName.trim();
    }

    public Integer getCityCode() {
        return cityCode;
    }

    public void setCityCode(Integer cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName == null ? null : cityName.trim();
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource == null ? null : resource.trim();
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public String getNetType() {
        return netType;
    }

    public void setNetType(String netType) {
        this.netType = netType == null ? null : netType.trim();
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature == null ? null : feature.trim();
    }

    public BigDecimal getLowConsume() {
        return lowConsume;
    }

    public void setLowConsume(BigDecimal lowConsume) {
        this.lowConsume = lowConsume;
    }

    public Integer getCorpId() {
        return corpId;
    }

    public void setCorpId(Integer corpId) {
        this.corpId = corpId;
    }

    public Integer getChannel() {
        return channel;
    }

    public void setChannel(Integer channel) {
        this.channel = channel;
    }

    public BigDecimal getRatioPrice() {
        return ratioPrice;
    }

    public void setRatioPrice(BigDecimal ratioPrice) {
        this.ratioPrice = ratioPrice;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent == null ? null : agent.trim();
    }

    public Integer getAgentId() {
        return agentId;
    }

    public void setAgentId(Integer agentId) {
        this.agentId = agentId;
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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getFalg() {
        return falg;
    }

    public void setFalg(int falg) {
        this.falg = falg;
    }

    public String getNewDate() {
        return newDate;
    }

    public void setNewDate(String newDate) {
        this.newDate = newDate;
    }

    public BigDecimal getPriceS() {
        return priceS;
    }

    public void setPriceS(BigDecimal priceS) {
        this.priceS = priceS;
    }

    public BigDecimal getPriceE() {
        return priceE;
    }

    public void setPriceE(BigDecimal priceE) {
        this.priceE = priceE;
    }

    public String getNumTags() {
        return numTags;
    }

    public void setNumTags(String numTags) {
        this.numTags = numTags;
    }
}