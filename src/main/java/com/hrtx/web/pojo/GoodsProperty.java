package com.hrtx.web.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_goods_property")
public class GoodsProperty extends BasePojo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@JsonSerialize(using = ToStringSerializer.class)
    private Long gpId;
    private Long gId;
    private String gpKey;
    private String gpValue;

    public GoodsProperty() {
	}

    public GoodsProperty(Long gpId, Long gId, String gpKey, String gpValue) {
        this.gpId = gpId;
        this.gId = gId;
        this.gpKey = gpKey;
        this.gpValue = gpValue;
    }

    public Long getGpId() {
        return gpId;
    }

    public void setGpId(Long gpId) {
        this.gpId = gpId;
    }

    public Long getgId() {
        return gId;
    }

    public void setgId(Long gId) {
        this.gId = gId;
    }

    public String getGpKey() {
        return gpKey;
    }

    public void setGpKey(String gpKey) {
        this.gpKey = gpKey;
    }

    public String getGpValue() {
        return gpValue;
    }

    public void setGpValue(String gpValue) {
        this.gpValue = gpValue;
    }
}
