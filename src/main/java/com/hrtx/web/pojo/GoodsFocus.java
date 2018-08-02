package com.hrtx.web.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tb_goods_focus")
public class GoodsFocus extends BasePojo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
    @Id
	@JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long skuId;//	tb_sku.id  二级属性关联到一级商品保证金;号码三级
    @JsonSerialize(using = ToStringSerializer.class)
    private Long numId;//	号码编码
    private String num;//	号码  来自 tb_num.num_resource
    private String gName;//商品名称
    @JsonSerialize(using = ToStringSerializer.class)
    private Long gId;//商品编码tb_goods.id
    private double price;//	当前价格
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date addDate;//	关注时间 添加
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;//	关注时间  修改
    private Long  consumerId;//	用户id  来自tb_consumer.id
    private String addIp;//	用户ip
    private String  remark;//备注
    private boolean isDel;



    public GoodsFocus() {
    }

    public GoodsFocus(Long id, Long skuId, Long numId, String num, String gName, Long gId, double price, Date addDate, Date updateDate, Long consumerId,
                      String addIp, String remark, Boolean isDel) {
        this.id = id;
        this.skuId = skuId;
        this.numId = numId;
        this.num = num;
        this.gId=gId;
        this.price = price;
        this.addDate = addDate;
        this.updateDate=updateDate;
        this.consumerId = consumerId;
        this.addIp = addIp;
        this.consumerId = consumerId;
        this.addIp = addIp;
        this.remark = remark;
        this.isDel = isDel;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Long getNumId() {
        return numId;
    }

    public void setNumId(Long numId) {
        this.numId = numId;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getgName() {
        return gName;
    }

    public void setgName(String gName) {
        this.gName = gName;
    }

    public Long getgId() {
        return gId;
    }

    public void setgId(Long gId) {
        this.gId = gId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    public Date getUpdateDate() { return updateDate; }

    public void setUpdateDate(Date updateDate) { this.updateDate = updateDate; }

    public Long getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(Long consumerId) {
        this.consumerId = consumerId;
    }

    public String getAddIp() {
        return addIp;
    }

    public void setAddIp(String addIp) {
        this.addIp = addIp;
    }

    public boolean isDel() {
        return isDel;
    }

    public void setDel(boolean del) {
        isDel = del;
    }

}