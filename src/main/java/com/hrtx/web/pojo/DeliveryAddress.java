package com.hrtx.web.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tb_delivery_address")
public class DeliveryAddress extends BasePojo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
    private String  personName ;// 收货人
    private String  personTel; //收货人电话
    private String  provinceName;// 所属省份
    private String  cityName;   //所属地市
    private String  districtName;//所属区县
    private int  provinceId;// 所属省份Id
    private int  cityId;   //所属地市Id
    private int  districtId;//所属区县Id
    private String  address;//地址
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date  createDate;//'创建日期
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date  updateDate;//修改日期
    private Long  addUserId;//添加人ID
    private int  isDel;//是否删除
    private int   isDefaultl;//是否默认
    private String  note;//备注



    public DeliveryAddress() {
	}

    public DeliveryAddress(Long id, String personName,String personTel,String provinceName,String cityName,String districtName,String address, Date createDate, Date updateDate
            ,Long addUserId ,int isDel ,int isDefaultl,String note) {
        this.id = id;
        this.personName = personName;
        this.personTel = personTel;
        this.provinceName = provinceName;
        this.cityName = cityName;
        this.districtName = districtName;
        this.address = address;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.addUserId = addUserId;
        this.isDel = isDel;
        this.isDefaultl = isDefaultl;
        this.note = note;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPersonTel() {
        return personTel;
    }

    public void setPersonTel(String personTel) {
        this.personTel = personTel;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Long getAddUserId() {
        return addUserId;
    }

    public void setAddUserId(Long addUserId) {
        this.addUserId = addUserId;
    }

    public int getIsDel() {
        return isDel;
    }

    public void setIsDel(int isDel) {
        this.isDel = isDel;
    }

    public int getIsDefaultl() {
        return isDefaultl;
    }

    public void setIsDefaultl(int isDefaultl) {
        this.isDefaultl = isDefaultl;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}