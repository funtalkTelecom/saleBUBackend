package com.hrtx.web.pojo;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
@Table(name = "tb_corporation")
public class Corporation extends BasePojo{
    @Id
    private Long id;

    private String name;

    private String principal;

    private String phone;

    private Long district;

    private String address;

    private String remark;

    private Byte status;

    private Date addDate;

    private String addUser;

    private Boolean isDel;

    public Corporation(Long id, String name, String principal, String phone, Long district, String address, String remark, Byte status, Date addDate, String addUser, Boolean isDel) {
        this.id = id;
        this.name = name;
        this.principal = principal;
        this.phone = phone;
        this.district = district;
        this.address = address;
        this.remark = remark;
        this.status = status;
        this.addDate = addDate;
        this.addUser = addUser;
        this.isDel = isDel;
    }

    public Corporation() {
        super();
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
        this.name = name == null ? null : name.trim();
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal == null ? null : principal.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
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
        this.address = address == null ? null : address.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    public String getAddUser() {
        return addUser;
    }

    public void setAddUser(String addUser) {
        this.addUser = addUser == null ? null : addUser.trim();
    }

    public Boolean getIsDel() {
        return isDel;
    }

    public void setIsDel(Boolean isDel) {
        this.isDel = isDel;
    }
}