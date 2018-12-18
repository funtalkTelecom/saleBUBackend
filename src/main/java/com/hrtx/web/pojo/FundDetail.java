package com.hrtx.web.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tb_fund_detail")
public class FundDetail extends BasePojo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
    public static String ORDER_ACT_TYPE_ADD = "ADD";
    public static String ORDER_ACT_TYPE_CANCEL = "CANCEL";
    public static String ORDER_ACT_TYPE_REFUND = "REFUND";

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonSerialize(using = ToStringSerializer.class)
	private Integer id;
    private Integer fundOrderId;
    private String serial;
    private String reqIp;
    private Integer reqUser;
    private Date addDate;
    private String actType;
    private Integer status;
    private Integer resCode;
    private String resDesc;

    public FundDetail() {
	}

    public FundDetail(Integer fundOrderId, String serial, String reqIp, Integer reqUser, Date addDate, String actType, Integer status) {
        this.fundOrderId = fundOrderId;
        this.serial = serial;
        this.reqIp = reqIp;
        this.reqUser = reqUser;
        this.addDate = addDate;
        this.actType = actType;
        this.status = status;
    }

    public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

    public Integer getFundOrderId() {
        return fundOrderId;
    }

    public void setFundOrderId(Integer fundOrderId) {
        this.fundOrderId = fundOrderId;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getReqIp() {
        return reqIp;
    }

    public void setReqIp(String reqIp) {
        this.reqIp = reqIp;
    }

    public Integer getReqUser() {
        return reqUser;
    }

    public void setReqUser(Integer reqUser) {
        this.reqUser = reqUser;
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    public String getActType() {
        return actType;
    }

    public void setActType(String actType) {
        this.actType = actType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getResCode() {
        return resCode;
    }

    public void setResCode(Integer resCode) {
        this.resCode = resCode;
    }

    public String getResDesc() {
        return resDesc;
    }

    public void setResDesc(String resDesc) {
        this.resDesc = resDesc;
    }
}