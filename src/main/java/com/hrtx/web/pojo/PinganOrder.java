package com.hrtx.web.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_pingan_order")
public class PinganOrder extends BasePojo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@JsonSerialize(using = ToStringSerializer.class)
	private Integer id;
	private String open_id;
	private String timestamp;
	private String req_sign;
	private String out_no;
	private String pmt_tag;
	private String pmt_name;
	private String ord_name;
	private String original_amount;
	private String discount_amount;
	private String ignore_amount;
	private String trade_amount;
	private String trade_account;
	private String trade_no;
	private String remark;
	private String tag;
	private String notify_url;
	private String auth_code;
	private String jump_url;
	private String wx_appid;
	private String goods_tag;
	private String limit_pay;
	private String goods_detail;
	private String extend_params;

	public PinganOrder() {
	}

    public PinganOrder(Long id, String open_id, String timestamp, String req_sign, String out_no, String pmt_tag, String pmt_name, String ord_name, String original_amount, String discount_amount, String ignore_amount, String trade_amount, String trade_account, String trade_no, String remark, String tag, String notify_url, String auth_code, String jump_url, String wx_appid, String goods_tag, String limit_pay, String goods_detail, String extend_params) {
        this.id = id;
        this.open_id = open_id;
        this.timestamp = timestamp;
        this.req_sign = req_sign;
        this.out_no = out_no;
        this.pmt_tag = pmt_tag;
        this.pmt_name = pmt_name;
        this.ord_name = ord_name;
        this.original_amount = original_amount;
        this.discount_amount = discount_amount;
        this.ignore_amount = ignore_amount;
        this.trade_amount = trade_amount;
        this.trade_account = trade_account;
        this.trade_no = trade_no;
        this.remark = remark;
        this.tag = tag;
        this.notify_url = notify_url;
        this.auth_code = auth_code;
        this.jump_url = jump_url;
        this.wx_appid = wx_appid;
        this.goods_tag = goods_tag;
        this.limit_pay = limit_pay;
        this.goods_detail = goods_detail;
        this.extend_params = extend_params;
    }

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
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

    public String getReq_sign() {
        return req_sign;
    }

    public void setReq_sign(String req_sign) {
        this.req_sign = req_sign;
    }

    public String getOut_no() {
        return out_no;
    }

    public void setOut_no(String out_no) {
        this.out_no = out_no;
    }

    public String getPmt_tag() {
        return pmt_tag;
    }

    public void setPmt_tag(String pmt_tag) {
        this.pmt_tag = pmt_tag;
    }

    public String getPmt_name() {
        return pmt_name;
    }

    public void setPmt_name(String pmt_name) {
        this.pmt_name = pmt_name;
    }

    public String getOrd_name() {
        return ord_name;
    }

    public void setOrd_name(String ord_name) {
        this.ord_name = ord_name;
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

    public String getTrade_amount() {
        return trade_amount;
    }

    public void setTrade_amount(String trade_amount) {
        this.trade_amount = trade_amount;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public void setNotify_url(String notify_url) {
        this.notify_url = notify_url;
    }

    public String getAuth_code() {
        return auth_code;
    }

    public void setAuth_code(String auth_code) {
        this.auth_code = auth_code;
    }

    public String getJump_url() {
        return jump_url;
    }

    public void setJump_url(String jump_url) {
        this.jump_url = jump_url;
    }

    public String getWx_appid() {
        return wx_appid;
    }

    public void setWx_appid(String wx_appid) {
        this.wx_appid = wx_appid;
    }

    public String getGoods_tag() {
        return goods_tag;
    }

    public void setGoods_tag(String goods_tag) {
        this.goods_tag = goods_tag;
    }

    public String getLimit_pay() {
        return limit_pay;
    }

    public void setLimit_pay(String limit_pay) {
        this.limit_pay = limit_pay;
    }

    public String getGoods_detail() {
        return goods_detail;
    }

    public void setGoods_detail(String goods_detail) {
        this.goods_detail = goods_detail;
    }

    public String getExtend_params() {
        return extend_params;
    }

    public void setExtend_params(String extend_params) {
        this.extend_params = extend_params;
    }
}