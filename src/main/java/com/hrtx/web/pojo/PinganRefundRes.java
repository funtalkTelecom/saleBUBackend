package com.hrtx.web.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_pingan_refund_res")
public class PinganRefundRes extends BasePojo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
    private String errcode;
    private String msg ;
    private String ord_no;
    private String ord_shop_id;
    private String ord_mct_id;
    private String trade_amount;
    private String trade_no;
    private String trade_result;
    private String original_ord_no;
    private String status;
    private String ord_currency;
    private String currency_sign;
    private String out_no;
    private String trade_time;


    public PinganRefundRes() {
	}

    public PinganRefundRes(Long id, String errcode, String msg, String ord_no, String ord_shop_id, String ord_mct_id, String trade_amount, String trade_no, String trade_result, String original_ord_no, String status, String ord_currency, String currency_sign, String out_no, String trade_time) {
        this.id = id;
        this.errcode = errcode;
        this.msg = msg;
        this.ord_no = ord_no;
        this.ord_shop_id = ord_shop_id;
        this.ord_mct_id = ord_mct_id;
        this.trade_amount = trade_amount;
        this.trade_no = trade_no;
        this.trade_result = trade_result;
        this.original_ord_no = original_ord_no;
        this.status = status;
        this.ord_currency = ord_currency;
        this.currency_sign = currency_sign;
        this.out_no = out_no;
        this.trade_time = trade_time;
    }

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getOrd_no() {
        return ord_no;
    }

    public void setOrd_no(String ord_no) {
        this.ord_no = ord_no;
    }

    public String getOrd_shop_id() {
        return ord_shop_id;
    }

    public void setOrd_shop_id(String ord_shop_id) {
        this.ord_shop_id = ord_shop_id;
    }

    public String getOrd_mct_id() {
        return ord_mct_id;
    }

    public void setOrd_mct_id(String ord_mct_id) {
        this.ord_mct_id = ord_mct_id;
    }

    public String getTrade_amount() {
        return trade_amount;
    }

    public void setTrade_amount(String trade_amount) {
        this.trade_amount = trade_amount;
    }

    public String getTrade_no() {
        return trade_no;
    }

    public void setTrade_no(String trade_no) {
        this.trade_no = trade_no;
    }

    public String getTrade_result() {
        return trade_result;
    }

    public void setTrade_result(String trade_result) {
        this.trade_result = trade_result;
    }

    public String getOriginal_ord_no() {
        return original_ord_no;
    }

    public void setOriginal_ord_no(String original_ord_no) {
        this.original_ord_no = original_ord_no;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrd_currency() {
        return ord_currency;
    }

    public void setOrd_currency(String ord_currency) {
        this.ord_currency = ord_currency;
    }

    public String getCurrency_sign() {
        return currency_sign;
    }

    public void setCurrency_sign(String currency_sign) {
        this.currency_sign = currency_sign;
    }

    public String getOut_no() {
        return out_no;
    }

    public void setOut_no(String out_no) {
        this.out_no = out_no;
    }

    public String getTrade_time() {
        return trade_time;
    }

    public void setTrade_time(String trade_time) {
        this.trade_time = trade_time;
    }
}