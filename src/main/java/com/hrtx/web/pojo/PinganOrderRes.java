package com.hrtx.web.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_pingan_order_res")
public class PinganOrderRes extends BasePojo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
    private String errcode;
    private String msg;
//    private String data;
    private String sign;
    private String pmt_name;
    private String pmt_tag ;
    private String ord_mct_id;
    private String ord_shop_id;
    private String ord_no;
    private String ord_type;
    private String original_amount;
    private String discount_amount;
    private String ignore_amount;
    private String trade_account;
    private String trade_no;
    private String trade_amount;
    private String trade_qrcode;
    private String trade_pay_time;
    private String status;
    private String trade_result;
    private String out_no;
    private String jsapi_pay_url;
    private String wxapp_partnerid;
    private String wxapp_prepayid;
    private String wxapp_package;
    private String wxapp_noncestr;
    private String swxapp_timestamp;
    private String wxapp_sign;


    public PinganOrderRes() {
	}

    public PinganOrderRes(Long id, String errcode, String msg, String sign, String pmt_name, String pmt_tag, String ord_mct_id, String ord_shop_id, String ord_no, String ord_type, String original_amount, String discount_amount, String ignore_amount, String trade_account, String trade_no, String trade_amount, String trade_qrcode, String trade_pay_time, String status, String trade_result, String out_no, String jsapi_pay_url, String wxapp_partnerid, String wxapp_prepayid, String wxapp_package, String wxapp_noncestr, String swxapp_timestamp, String wxapp_sign) {
        this.id = id;
        this.errcode = errcode;
        this.msg = msg;
//        this.data = data;
        this.sign = sign;
        this.pmt_name = pmt_name;
        this.pmt_tag = pmt_tag;
        this.ord_mct_id = ord_mct_id;
        this.ord_shop_id = ord_shop_id;
        this.ord_no = ord_no;
        this.ord_type = ord_type;
        this.original_amount = original_amount;
        this.discount_amount = discount_amount;
        this.ignore_amount = ignore_amount;
        this.trade_account = trade_account;
        this.trade_no = trade_no;
        this.trade_amount = trade_amount;
        this.trade_qrcode = trade_qrcode;
        this.trade_pay_time = trade_pay_time;
        this.status = status;
        this.trade_result = trade_result;
        this.out_no = out_no;
        this.jsapi_pay_url = jsapi_pay_url;
        this.wxapp_partnerid = wxapp_partnerid;
        this.wxapp_prepayid = wxapp_prepayid;
        this.wxapp_package = wxapp_package;
        this.wxapp_noncestr = wxapp_noncestr;
        this.swxapp_timestamp = swxapp_timestamp;
        this.wxapp_sign = wxapp_sign;
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

    /*public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }*/

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getPmt_name() {
        return pmt_name;
    }

    public void setPmt_name(String pmt_name) {
        this.pmt_name = pmt_name;
    }

    public String getPmt_tag() {
        return pmt_tag;
    }

    public void setPmt_tag(String pmt_tag) {
        this.pmt_tag = pmt_tag;
    }

    public String getOrd_mct_id() {
        return ord_mct_id;
    }

    public void setOrd_mct_id(String ord_mct_id) {
        this.ord_mct_id = ord_mct_id;
    }

    public String getOrd_shop_id() {
        return ord_shop_id;
    }

    public void setOrd_shop_id(String ord_shop_id) {
        this.ord_shop_id = ord_shop_id;
    }

    public String getOrd_no() {
        return ord_no;
    }

    public void setOrd_no(String ord_no) {
        this.ord_no = ord_no;
    }

    public String getOrd_type() {
        return ord_type;
    }

    public void setOrd_type(String ord_type) {
        this.ord_type = ord_type;
    }

    public String getOriginal_amount() {
        return original_amount;
    }

    public void setOriginal_amount(String original_amount) {
        this.original_amount = original_amount;
    }

    public String getDiscount_amount() {
        return discount_amount;
    }

    public void setDiscount_amount(String discount_amount) {
        this.discount_amount = discount_amount;
    }

    public String getIgnore_amount() {
        return ignore_amount;
    }

    public void setIgnore_amount(String ignore_amount) {
        this.ignore_amount = ignore_amount;
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

    public String getTrade_amount() {
        return trade_amount;
    }

    public void setTrade_amount(String trade_amount) {
        this.trade_amount = trade_amount;
    }

    public String getTrade_qrcode() {
        return trade_qrcode;
    }

    public void setTrade_qrcode(String trade_qrcode) {
        this.trade_qrcode = trade_qrcode;
    }

    public String getTrade_pay_time() {
        return trade_pay_time;
    }

    public void setTrade_pay_time(String trade_pay_time) {
        this.trade_pay_time = trade_pay_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTrade_result() {
        return trade_result;
    }

    public void setTrade_result(String trade_result) {
        this.trade_result = trade_result;
    }

    public String getOut_no() {
        return out_no;
    }

    public void setOut_no(String out_no) {
        this.out_no = out_no;
    }

    public String getJsapi_pay_url() {
        return jsapi_pay_url;
    }

    public void setJsapi_pay_url(String jsapi_pay_url) {
        this.jsapi_pay_url = jsapi_pay_url;
    }

    public String getWxapp_partnerid() {
        return wxapp_partnerid;
    }

    public void setWxapp_partnerid(String wxapp_partnerid) {
        this.wxapp_partnerid = wxapp_partnerid;
    }

    public String getWxapp_prepayid() {
        return wxapp_prepayid;
    }

    public void setWxapp_prepayid(String wxapp_prepayid) {
        this.wxapp_prepayid = wxapp_prepayid;
    }

    public String getWxapp_package() {
        return wxapp_package;
    }

    public void setWxapp_package(String wxapp_package) {
        this.wxapp_package = wxapp_package;
    }

    public String getWxapp_noncestr() {
        return wxapp_noncestr;
    }

    public void setWxapp_noncestr(String wxapp_noncestr) {
        this.wxapp_noncestr = wxapp_noncestr;
    }

    public String getSwxapp_timestamp() {
        return swxapp_timestamp;
    }

    public void setSwxapp_timestamp(String swxapp_timestamp) {
        this.swxapp_timestamp = swxapp_timestamp;
    }

    public String getWxapp_sign() {
        return wxapp_sign;
    }

    public void setWxapp_sign(String wxapp_sign) {
        this.wxapp_sign = wxapp_sign;
    }
}