package com.hrtx.web.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hrtx.global.Utils;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Table(name = "tb_order")
public class Order extends BasePojo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonSerialize(using = ToStringSerializer.class)
    @NotNull(message = "订单不能为空", groups = {Groups.FundOrderPayOrder.class})
    private Integer orderId;//编号
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer consumer;//用户编码
    private String consumerName;//用户名称
    private int status;//状态 1待付款；2已付款待推送（已付款尚未推送到仓储期）；3待配货(仓储系统已收到)；4待签收(仓储物流已取件)；5完成
    private String reqUserAgent;//请求的user_agent
    private String reqIp;//请求的ip
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date addDate;//添加时间
    private int orderType;//1商品；2号码；3竞拍
    private String shippingMenthodId;//运输方式编码 字典表
    private String shippingMenthod;//运输方式
    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull(message = "地址不能为空", groups = {Groups.FundOrderPayOrder.class})
    private Integer addressId;//收货地址编码
    private String personName;//收货人
    private String personTel;//收货电话
    private String address;//收货地址 地区+街道
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date noticeShipmentDate;//通知出货时间 即调用发货成功时间
    @NotBlank(message = "支付方式不能为空", groups = {Groups.FundOrderPayOrder.class})
    private String payMenthodId;//支付方式编码 字典表
    private String payMenthod;//支付方式
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date payDate;//付款日期
    private String expressId;//快递公司 发货后  字典表
    private String expressName;//快递名称
    private String expressNumber;//快递单号
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deliverDate;//发货时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date pickupDate;//仓库调用时间
    private int signType;//签收方式1用户自动签收2系统
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date signDate;//签收时间
    private double commission;//优惠券 折扣
    private double shippingTotal;//运输费用
    private double subTotal;//子项小计
    private double total;//合计
    private String conment;//摘要
    //ALTER TABLE `tb_order` ADD COLUMN `check_conment` varchar(100) NULL COMMENT '审核成功' AFTER `phone_consumer_id_num`;
    private String checkConment;//审核摘要
    private int isDel;//
    private String skuGoodsType;
    private String reason;
    private String refundReason;  //退款备注信息
    private String thirdOrder; //第三方订单号
    private String bossNum; //BOSS开户工号
    private String phoneConsumer; //客户名称
    private String phoneConsumerIdType; //客户证件类型
    private String phoneConsumerIdNum; //客户证件编码
    private Integer shareId; //下单编码
    private Integer sellerId; //卖家编码

    @Transient
    private String num;
    @Transient
    private String startTime;
    @Transient
    private String endTime;

    public Order() {
    }
    public Order(Integer consumer, String consumerName, int status, String reqUserAgent, String reqIp,int orderType, String shippingMenthodId, String shippingMenthod, Integer addressId, String personName, String personTel, String address, double commission, double shippingTotal, double subTotal,String conment, String skuGoodsType) {
        this.consumer = consumer;
        this.consumerName = consumerName;
        this.status = status;
        this.reqUserAgent = reqUserAgent;
        this.reqIp = reqIp;
        this.addDate = new Date();
        this.orderType = orderType;
        this.shippingMenthodId = shippingMenthodId;
        this.shippingMenthod = shippingMenthod;
        this.addressId = addressId;
        this.personName = personName;
        this.personTel = personTel;
        this.address = address;
        this.commission = commission;
        this.shippingTotal = shippingTotal;
        this.subTotal = subTotal;
        this.total =calculateTotal(this);
        this.conment = conment;
        this.isDel =0;
        this.skuGoodsType = skuGoodsType;
    }

    public static double calculateTotal(Order order) {
        return Utils.sub(Utils.sum(order.getSubTotal(),order.getShippingTotal()),order.getCommission());
    }
    public Order(Integer orderId, Integer consumer, String consumerName, int status, String reqUserAgent, String reqIp, Date addDate,
                 int orderType, String shippingMenthodId, String shippingMenthod, Integer addressId, String personName,
                 String personTel, String address, Date noticeShipmentDate, String payMenthodId, String payMenthod, Date payDate,
                 String expressId, String expressName, String expressNumber, Date deliverDate, Date pickupDate, int signType,
                 Date signDate, double commission, double shippingTotal, double subTotal, double total, String conment,
                 int isDel, String skuGoodsType) {
        this.orderId = orderId;
        this.consumer = consumer;
        this.consumerName = consumerName;
        this.status = status;
        this.reqUserAgent = reqUserAgent;
        this.reqIp = reqIp;
        this.addDate = addDate;
        this.orderType = orderType;
        this.shippingMenthodId = shippingMenthodId;
        this.shippingMenthod = shippingMenthod;
        this.addressId = addressId;
        this.personName = personName;
        this.personTel = personTel;
        this.address = address;
        this.noticeShipmentDate = noticeShipmentDate;
        this.payMenthodId = payMenthodId;
        this.payMenthod = payMenthod;
        this.payDate = payDate;
        this.expressId = expressId;
        this.expressName = expressName;
        this.expressNumber = expressNumber;
        this.deliverDate = deliverDate;
        this.pickupDate = pickupDate;
        this.signType = signType;
        this.signDate = signDate;
        this.commission = commission;
        this.shippingTotal = shippingTotal;
        this.subTotal = subTotal;
        this.total = total;
        this.conment = conment;
        this.isDel = isDel;
        this.skuGoodsType = skuGoodsType;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getConsumer() {
        return consumer;
    }

    public void setConsumer(Integer consumer) {
        this.consumer = consumer;
    }

    public String getConsumerName() {
        return consumerName;
    }

    public void setConsumerName(String consumerName) {
        this.consumerName = consumerName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getReqUserAgent() {
        return reqUserAgent;
    }

    public void setReqUserAgent(String reqUserAgent) {
        this.reqUserAgent = reqUserAgent;
    }

    public String getReqIp() {
        return reqIp;
    }

    public void setReqIp(String reqIp) {
        this.reqIp = reqIp;
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public String getShippingMenthodId() {
        return shippingMenthodId;
    }

    public void setShippingMenthodId(String shippingMenthodId) {
        this.shippingMenthodId = shippingMenthodId;
    }

    public String getShippingMenthod() {
        return shippingMenthod;
    }

    public void setShippingMenthod(String shippingMenthod) {
        this.shippingMenthod = shippingMenthod;
    }

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPersonTel() {
        return personTel;
    }

    public void setPersonTel(String personTel) {
        this.personTel = personTel;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getNoticeShipmentDate() {
        return noticeShipmentDate;
    }

    public void setNoticeShipmentDate(Date noticeShipmentDate) {
        this.noticeShipmentDate = noticeShipmentDate;
    }

    public String getPayMenthodId() {
        return payMenthodId;
    }

    public void setPayMenthodId(String payMenthodId) {
        this.payMenthodId = payMenthodId;
    }

    public String getPayMenthod() {
        return payMenthod;
    }

    public void setPayMenthod(String payMenthod) {
        this.payMenthod = payMenthod;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public String getExpressId() {
        return expressId;
    }

    public void setExpressId(String expressId) {
        this.expressId = expressId;
    }

    public String getExpressName() {
        return expressName;
    }

    public void setExpressName(String expressName) {
        this.expressName = expressName;
    }

    public String getExpressNumber() {
        return expressNumber;
    }

    public void setExpressNumber(String expressNumber) {
        this.expressNumber = expressNumber;
    }

    public Date getDeliverDate() {
        return deliverDate;
    }

    public void setDeliverDate(Date deliverDate) {
        this.deliverDate = deliverDate;
    }

    public Date getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(Date pickupDate) {
        this.pickupDate = pickupDate;
    }

    public int getSignType() {
        return signType;
    }

    public void setSignType(int signType) {
        this.signType = signType;
    }

    public Date getSignDate() {
        return signDate;
    }

    public void setSignDate(Date signDate) {
        this.signDate = signDate;
    }

    public double getCommission() {
        return commission;
    }

    public void setCommission(double commission) {
        this.commission = commission;
    }

    public double getShippingTotal() {
        return shippingTotal;
    }

    public void setShippingTotal(double shippingTotal) {
        this.shippingTotal = shippingTotal;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getConment() {
        return conment;
    }

    public void setConment(String conment) {
        this.conment = conment;
    }

    public int getIsDel() {
        return isDel;
    }

    public void setIsDel(int isDel) {
        this.isDel = isDel;
    }

    public String getSkuGoodsType() {
        return skuGoodsType;
    }

    public void setSkuGoodsType(String skuGoodsType) {
        this.skuGoodsType = skuGoodsType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    public String getThirdOrder() {
        return thirdOrder;
    }

    public void setThirdOrder(String thirdOrder) {
        this.thirdOrder = thirdOrder;
    }

    public String getBossNum() {
        return bossNum;
    }

    public void setBossNum(String bossNum) {
        this.bossNum = bossNum;
    }

    public String getPhoneConsumer() {
        return phoneConsumer;
    }

    public void setPhoneConsumer(String phoneConsumer) {
        this.phoneConsumer = phoneConsumer;
    }

    public String getPhoneConsumerIdType() {
        return phoneConsumerIdType;
    }

    public void setPhoneConsumerIdType(String phoneConsumerIdType) {
        this.phoneConsumerIdType = phoneConsumerIdType;
    }

    public String getPhoneConsumerIdNum() {
        return phoneConsumerIdNum;
    }

    public void setPhoneConsumerIdNum(String phoneConsumerIdNum) {
        this.phoneConsumerIdNum = phoneConsumerIdNum;
    }

    public String getCheckConment() {
        return checkConment;
    }

    public void setCheckConment(String checkConment) {
        this.checkConment = checkConment;
    }

    public Integer getShareId() {
        return shareId;
    }

    public void setShareId(Integer shareId) {
        this.shareId = shareId;
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }
}