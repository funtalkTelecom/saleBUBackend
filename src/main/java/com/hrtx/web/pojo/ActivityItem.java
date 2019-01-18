package com.hrtx.web.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tb_activity_item")
public class ActivityItem extends BasePojo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonSerialize(using = ToStringSerializer.class)
	private Integer id;
	private Integer activityId;   //活动ID
	private String num;       //号码
	private Double price;     //原价
	private Double downPrice;  //秒杀价格
	private Integer isDel;


	public ActivityItem() {
	}

	public ActivityItem(Integer id, Integer activityId, String num, Double price,Double downPrice, Integer isDel) {
		this.id = id;
		this.activityId = activityId;
		this.num = num;
		this.price = price;
		this.downPrice = downPrice;
		this.isDel = isDel;

	}


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getActivityId() {
		return activityId;
	}

	public void setActivityId(Integer activityId) {
		this.activityId = activityId;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getDownPrice() {
		return downPrice;
	}

	public void setDownPrice(Double downPrice) {
		this.downPrice = downPrice;
	}

	public Integer getIsDel() {
		return isDel;
	}

	public void setIsDel(Integer isDel) {
		this.isDel = isDel;
	}
}