package com.hrtx.web.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_pingan_cancel")
public class PinganCancel extends BasePojo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
    private String open_id;
    private String timestamp;
    private String sign;
    private String sign_type;
    private String ord_no;
    private String out_no;


    public PinganCancel() {
	}

    public PinganCancel(Long id, String open_id, String timestamp, String sign, String sign_type, String ord_no, String out_no) {
        this.id = id;
        this.open_id = open_id;
        this.timestamp = timestamp;
        this.sign = sign;
        this.sign_type = sign_type;
        this.ord_no = ord_no;
        this.out_no = out_no;
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

    public String getOrd_no() {
        return ord_no;
    }

    public void setOrd_no(String ord_no) {
        this.ord_no = ord_no;
    }

    public String getOut_no() {
        return out_no;
    }

    public void setOut_no(String out_no) {
        this.out_no = out_no;
    }
}