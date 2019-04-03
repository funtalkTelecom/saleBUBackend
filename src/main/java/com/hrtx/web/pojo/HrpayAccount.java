package com.hrtx.web.pojo;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_hrpay_account")
public class HrpayAccount  extends BasePojo {
    public static String acctoun_type_user="user";
    public static String acctoun_type_corp="corp";
    public static String acctoun_type_consumer="consumer";
    public static String acctoun_type_sys="sys";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String type;
    private Integer sourceId;
    private Boolean status;

    public HrpayAccount(Integer id, String type, Integer sourceId, Boolean status) {
        this.id = id;
        this.type = type;
        this.sourceId = sourceId;
        this.status = status;
    }

    public HrpayAccount() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}