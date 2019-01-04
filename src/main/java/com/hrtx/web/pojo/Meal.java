package com.hrtx.web.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import javax.persistence.*;
import java.util.Date;

@Table(name = "tb_meal")
public class Meal extends BasePojo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonSerialize(using = ToStringSerializer.class)
	private Integer mid;
	private String mealId;
	private String mealName;
	private String mealDesc;
    private Integer saleCity;
    @Transient
    private String saleCityName;
	private String saleType;
	private Integer createBy;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	private Date createDate;
	private Integer updateBy;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	private Date updateDate;
	private String teleType;
	private Integer isDel;
    private Integer sellerId;

	public Meal() {
	}

    public Meal(Integer mid, String mealId, String mealName, String mealDesc, Integer saleCity, String saleCityName, String saleType, Integer createBy, Date createDate, Integer updateBy, Date updateDate, String teleType, Integer isDel,Integer sellerId) {
        this.mid = mid;
        this.mealId = mealId;
        this.mealName = mealName;
        this.mealDesc = mealDesc;
        this.saleCity = saleCity;
        this.saleCityName = saleCityName;
        this.saleType = saleType;
        this.createBy = createBy;
        this.createDate = createDate;
        this.updateBy = updateBy;
        this.updateDate = updateDate;
        this.teleType = teleType;
        this.isDel = isDel;
        this.sellerId = sellerId;
    }

    public Integer getMid() {
        return mid;
    }

    public void setMid(Integer mid) {
        this.mid = mid;
    }

    public String getMealId() {
        return mealId;
    }

    public void setMealId(String mealId) {
        this.mealId = mealId;
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public String getMealDesc() {
        return mealDesc;
    }

    public void setMealDesc(String mealDesc) {
        this.mealDesc = mealDesc;
    }

    public Integer getSaleCity() {
        return saleCity;
    }

    public void setSaleCity(Integer saleCity) {
        this.saleCity = saleCity;
    }

    public String getSaleCityName() {
        return saleCityName;
    }

    public void setSaleCityName(String saleCityName) {
        this.saleCityName = saleCityName;
    }

    public String getSaleType() {
        return saleType;
    }

    public void setSaleType(String saleType) {
        this.saleType = saleType;
    }

    public Integer getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Integer getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Integer updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getTeleType() {
        return teleType;
    }

    public void setTeleType(String teleType) {
        this.teleType = teleType;
    }

    public Integer getIsDel() {
        return isDel;
    }

    public void setIsDel(Integer isDel) {
        this.isDel = isDel;
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }
}