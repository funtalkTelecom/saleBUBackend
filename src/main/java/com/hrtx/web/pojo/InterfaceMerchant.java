package com.hrtx.web.pojo;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_interface_merchant")
public class InterfaceMerchant {
    @Id
    private Integer id;

    private String name;

    private String code;

    private String ip;

    private Integer corpId;

    private Integer chanel;

    private Integer status;

    private String remark;

    private String secretKey;

    public InterfaceMerchant(Integer id, String name, String code, String ip, Integer corpId, Integer status, String remark, String secretKey) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.ip = ip;
        this.corpId = corpId;
        this.status = status;
        this.remark = remark;
        this.secretKey = secretKey;
    }

    public InterfaceMerchant() {
        super();
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
        this.name = name == null ? null : name.trim();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip == null ? null : ip.trim();
    }

    public Integer getCorpId() {
        return corpId;
    }

    public void setCorpId(Integer corpId) {
        this.corpId = corpId;
    }

    public Integer getChanel() {
        return chanel;
    }

    public void setChanel(Integer chanel) {
        this.chanel = chanel;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey == null ? null : secretKey.trim();
    }
}