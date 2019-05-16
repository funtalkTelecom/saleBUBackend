package com.hrtx.web.pojo;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tb_iccid")
public class Iccid {
    @Id
    private Integer id;

    private Integer cityId;

    private String cityName;

    private String sections;

    private String netType;

    private String iccid;

    private Integer dealStatus;

    private Integer orderId;

    private Integer consumerId;

    private Integer stockStatus;

    private Date inStockDate;

    private Date outStockDate;

    private Integer sellerId;

    public Iccid(Integer id, Integer cityId, String cityName, String sections, String netType, String iccid, Integer dealStatus, Integer orderId, Integer consumerId, Integer stockStatus, Date inStockDate, Date outStockDate) {
        this.id = id;
        this.cityId = cityId;
        this.cityName = cityName;
        this.sections = sections;
        this.netType = netType;
        this.iccid = iccid;
        this.dealStatus = dealStatus;
        this.orderId = orderId;
        this.consumerId = consumerId;
        this.stockStatus = stockStatus;
        this.inStockDate = inStockDate;
        this.outStockDate = outStockDate;
    }

    public Iccid() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName == null ? null : cityName.trim();
    }

    public String getSections() {
        return sections;
    }

    public void setSections(String sections) {
        this.sections = sections == null ? null : sections.trim();
    }

    public String getNetType() {
        return netType;
    }

    public void setNetType(String netType) {
        this.netType = netType == null ? null : netType.trim();
    }

    public String getIccid() {
        return iccid;
    }

    public void setIccid(String iccid) {
        this.iccid = iccid == null ? null : iccid.trim();
    }

    public Integer getDealStatus() {
        return dealStatus;
    }

    public void setDealStatus(Integer dealStatus) {
        this.dealStatus = dealStatus;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(Integer consumerId) {
        this.consumerId = consumerId;
    }

    public Integer getStockStatus() {
        return stockStatus;
    }

    public void setStockStatus(Integer stockStatus) {
        this.stockStatus = stockStatus;
    }

    public Date getInStockDate() {
        return inStockDate;
    }

    public void setInStockDate(Date inStockDate) {
        this.inStockDate = inStockDate;
    }

    public Date getOutStockDate() {
        return outStockDate;
    }

    public void setOutStockDate(Date outStockDate) {
        this.outStockDate = outStockDate;
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }
}