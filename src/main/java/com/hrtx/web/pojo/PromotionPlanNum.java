package com.hrtx.web.pojo;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tb_promotion_plan_num")
public class PromotionPlanNum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer promotionPlanId;
    private String num;
//    private Integer numId;//防止号码id发生变更而导致推广无效
    private Integer isDel;
    private Date updateDate;

    public PromotionPlanNum(){
        this.isDel=0;
        this.updateDate=new Date();
    }
    public PromotionPlanNum(Integer promotionPlanId,String num){
        this();
        this.promotionPlanId=promotionPlanId;
        this.num=num;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPromotionPlanId() {
        return promotionPlanId;
    }

    public void setPromotionPlanId(Integer promotionPlanId) {
        this.promotionPlanId = promotionPlanId;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    /*public Integer getNumId() {
        return numId;
    }
    public void setNumId(Integer numId) {
        this.numId = numId;
    }*/

    public Integer getIsDel() {
        return isDel;
    }

    public void setIsDel(Integer isDel) {
        this.isDel = isDel;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}