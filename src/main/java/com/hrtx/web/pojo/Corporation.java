package com.hrtx.web.pojo;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
@Table(name = "tb_corporation")
public class Corporation extends BasePojo implements java.io.Serializable{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String principal;
    private String phone;
    private Integer district;
    private String address;
    private String email;
    private String remark;
    private Byte status;
    private Date addDate;
    private String addUser;
    private boolean isDel;
    private Integer storageId;
    private Integer companyId;
    private Integer isValidIccid;

    public Corporation() {
    }

    public Corporation(Integer id, String name, String principal, String phone, Integer district, String address, String remark, Byte status, Date addDate, String addUser, Boolean isDel) {
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

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getDistrict() {
        return district;
    }

    public void setDistrict(Integer district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
        this.addUser = addUser;
    }

    public boolean isDel() {
        return isDel;
    }

    public void setDel(boolean del) {
        isDel = del;
    }

    public Integer getStorageId() {
        return storageId;
    }

    public void setStorageId(Integer storageId) {
        this.storageId = storageId;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getIsValidIccid() {
        return isValidIccid;
    }

    public void setIsValidIccid(Integer isValidIccid) {
        this.isValidIccid = isValidIccid;
    }
}