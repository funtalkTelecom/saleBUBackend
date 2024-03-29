package com.hrtx.web.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Table(name = "tb_fund_order")
public class FundOrder extends BasePojo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
    public static String THIRD_PAY_PINGANAPP = "PINGANAPP";
    public static String THIRD_PAY_YZFFQ = "YZFFQ";
    public static String THIRD_PAY_OFFLINE = "OFFLINE";

    public static String BUSI_TYPE_PAYORDER = "PAYORDER";
    public static String BUSI_TYPE_PAYDEPOSIT = "PAYDEPOSIT";
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonSerialize(using = ToStringSerializer.class)
	private Integer id;
    private String busi;
	@NotNull(message = "付款金额不能为空", groups = {Groups.FundOrderPayOrder.class})
	@Min(value = 1, message = "付款金额必须大于0", groups = {Groups.FundOrderPayOrder.class})
    private Integer amt;
    private String payee;
    private String payer;
    private Integer status;

    @NotBlank(message = "付款描述不能为空", groups = {Groups.FundOrderPayOrder.class})
    private String orderName;
    private String contractno;
    private String third;
    private Integer actualAmt;
    private String remark;
    @NotBlank(message = "来源不能为空", groups = {Groups.FundOrderPayOrder.class})
    private String sourceId;

    public FundOrder() {
	}

    public FundOrder(String busi, Integer amt, String payee, String payer, Integer status, String orderName, String contractno, String third, Integer actual_amt, String remark, String sourceId) {
        this.busi = busi;
        this.amt = amt;
        this.payee = payee;
        this.payer = payer;
        this.status = status;
        this.orderName = orderName;
        this.contractno = contractno;
        this.third = third;
        this.actualAmt = actual_amt;
        this.remark = remark;
        this.sourceId = sourceId;
    }

    public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
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

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
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

    public Integer getActualAmt() {
        return actualAmt;
    }

    public void setActualAmt(Integer actualAmt) {
        this.actualAmt = actualAmt;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }
}