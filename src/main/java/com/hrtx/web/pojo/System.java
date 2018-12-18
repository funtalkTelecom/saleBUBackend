package com.hrtx.web.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tb_system")
public class System extends BasePojo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@JsonSerialize(using = ToStringSerializer.class)
	private Integer id;
    private String keyId;
    private String keyValue;
    private String tempKeyValue;
    private String remark;
    private String isAudit;


    public System() {
	}

    public System(Integer id, String keyId, String keyValue, String tempKeyValue, String remark, String isAudit) {
        this.id = id;
        this.keyId = keyId;
        this.keyValue = keyValue;
        this.tempKeyValue = tempKeyValue;
        this.remark = remark;
        this.isAudit = isAudit;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    public String getTempKeyValue() {
        return tempKeyValue;
    }

    public void setTempKeyValue(String tempKeyValue) {
        this.tempKeyValue = tempKeyValue;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getIsAudit() {
        return isAudit;
    }

    public void setIsAudit(String isAudit) {
        this.isAudit = isAudit;
    }
}