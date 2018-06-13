package com.hrtx.web.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_fund_order")
public class FundOrder extends BasePojo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
    private String busi;
    private Long busi_num;
    private Integer amt;
    private String payee;
    private String payer;
    private Integer status;
    private String order_name;
    private String contractno;
    private String third;
    private Integer actual_amt;
    private String remark;

    public FundOrder() {
	}

    public FundOrder(Long id, String busi, Long busi_num, Integer amt, String payee, String payer, Integer status, String order_name, String contractno, String third, Integer actual_amt, String remark) {
        this.id = id;
        this.busi = busi;
        this.busi_num = busi_num;
        this.amt = amt;
        this.payee = payee;
        this.payer = payer;
        this.status = status;
        this.order_name = order_name;
        this.contractno = contractno;
        this.third = third;
        this.actual_amt = actual_amt;
        this.remark = remark;
    }

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

    public static long getSerialVersionUID() {
        return serialVersionUID;
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

    public Integer getAmt() {
        return amt;
    }

    public void setAmt(Integer amt) {
        this.amt = amt;
    }

    public String getPayee() {
        return payee;
    }

    public void setPayee(String payee) {
        this.payee = payee;
    }

    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getOrder_name() {
        return order_name;
    }

    public void setOrder_name(String order_name) {
        this.order_name = order_name;
    }

    public String getContractno() {
        return contractno;
    }

    public void setContractno(String contractno) {
        this.contractno = contractno;
    }

    public String getThird() {
        return third;
    }

    public void setThird(String third) {
        this.third = third;
    }

    public Integer getActual_amt() {
        return actual_amt;
    }

    public void setActual_amt(Integer actual_amt) {
        this.actual_amt = actual_amt;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}