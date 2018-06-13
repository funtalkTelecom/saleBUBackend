package com.hrtx.web.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_fund_detail")
public class FundDetail extends BasePojo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
    private Long fund_order_id;
    private String serial;
    private String req_ip;
    private Long req_user;
    private String add_date;
    private String busi;
    private Long busi_num;
    private String act_type;
    private Long status;

    public FundDetail() {
	}

    public FundDetail(Long id, Long fund_order_id, String serial, String req_ip, Long req_user, String add_date, String busi, Long busi_num, String act_type, Long status) {
        this.id = id;
        this.fund_order_id = fund_order_id;
        this.serial = serial;
        this.req_ip = req_ip;
        this.req_user = req_user;
        this.add_date = add_date;
        this.busi = busi;
        this.busi_num = busi_num;
        this.act_type = act_type;
        this.status = status;
    }

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

    public Long getFund_order_id() {
        return fund_order_id;
    }

    public void setFund_order_id(Long fund_order_id) {
        this.fund_order_id = fund_order_id;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getReq_ip() {
        return req_ip;
    }

    public void setReq_ip(String req_ip) {
        this.req_ip = req_ip;
    }

    public Long getReq_user() {
        return req_user;
    }

    public void setReq_user(Long req_user) {
        this.req_user = req_user;
    }

    public String getAdd_date() {
        return add_date;
    }

    public void setAdd_date(String add_date) {
        this.add_date = add_date;
    }

    public String getBusi() {
        return busi;
    }

    public void setBusi(String busi) {
        this.busi = busi;
    }

    public Long getBusi_num() {
        return busi_num;
    }

    public void setBusi_num(Long busi_num) {
        this.busi_num = busi_num;
    }

    public String getAct_type() {
        return act_type;
    }

    public void setAct_type(String act_type) {
        this.act_type = act_type;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }
}