package com.hrtx.web.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_pingan_cancel_res")
public class PinganCancelRes extends BasePojo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
    private String errcode;
    private String msg ;
    private String data;
    private String sign;
    private String order_no;
    private String ord_status;

    public PinganCancelRes() {
	}

    public PinganCancelRes(Long id, String errcode, String msg, String data, String sign, String order_no, String ord_status) {
        this.id = id;
        this.errcode = errcode;
        this.msg = msg;
        this.data = data;
        this.sign = sign;
        this.order_no = order_no;
        this.ord_status = ord_status;
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getOrd_status() {
        return ord_status;
    }

    public void setOrd_status(String ord_status) {
        this.ord_status = ord_status;
    }
}