package com.hrtx.web.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_pingan_refund")
public class PinganRefund extends BasePojo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@JsonSerialize(using = ToStringSerializer.class)
	private Integer id;
    private String open_id;
    private String timestamp;
    private String sign;
    private String sign_type;
    private String out_no;
    private String refund_out_no;
    private String refund_ord_name;
    private String refund_amount;
    private String trade_account;
    private String trade_no;
    private String trade_result;
    private String tml_token;
    private String remark;
    private String shop_pass;


    public PinganRefund() {
	}

    public PinganRefund(Integer id, String open_id, String timestamp, String sign, String sign_type, String out_no, String refund_out_no, String refund_ord_name, String refund_amount, String trade_account, String trade_no, String trade_result, String tml_token, String remark, String shop_pass) {
        this.id = id;
        this.open_id = open_id;
        this.timestamp = timestamp;
        this.sign = sign;
        this.sign_type = sign_type;
        this.out_no = out_no;
        this.refund_out_no = refund_out_no;
        this.refund_ord_name = refund_ord_name;
        this.refund_amount = refund_amount;
        this.trade_account = trade_account;
        this.trade_no = trade_no;
        this.trade_result = trade_result;
        this.tml_token = tml_token;
        this.remark = remark;
        this.shop_pass = shop_pass;
    }

    public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

    public String getOpen_id() {
        return open_id;
    }

    public void setOpen_id(String open_id) {
        this.open_id = open_id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }

    public String getOut_no() {
        return out_no;
    }

    public void setOut_no(String out_no) {
        this.out_no = out_no;
    }

    public String getRefund_out_no() {
        return refund_out_no;
    }

    public void setRefund_out_no(String refund_out_no) {
        this.refund_out_no = refund_out_no;
    }

    public String getRefund_ord_name() {
        return refund_ord_name;
    }

    public void setRefund_ord_name(String refund_ord_name) {
        this.refund_ord_name = refund_ord_name;
    }

    public String getRefund_amount() {
        return refund_amount;
    }

    public void setRefund_amount(String refund_amount) {
        this.refund_amount = refund_amount;
    }

    public String getTrade_account() {
        return trade_account;
    }

    public void setTrade_account(String trade_account) {
        this.trade_account = trade_account;
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

    public String getTml_token() {
        return tml_token;
    }

    public void setTml_token(String tml_token) {
        this.tml_token = tml_token;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getShop_pass() {
        return shop_pass;
    }

    public void setShop_pass(String shop_pass) {
        this.shop_pass = shop_pass;
    }
}