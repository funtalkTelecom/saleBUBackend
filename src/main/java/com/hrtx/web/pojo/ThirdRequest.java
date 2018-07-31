package com.hrtx.web.pojo;

import javax.persistence.Id;
import java.util.Date;

public class ThirdRequest {
    @Id
    private Integer id;

    private String contentType;

    private String interfaceName;

    private String thirdType;

    private String requestSerial;

    private String requestOrder;

    private Date addDate;

    public ThirdRequest(String contentType, String interfaceName, String thirdType, String requestSerial, String requestOrder, Date addDate) {
        this.contentType = contentType;
        this.interfaceName = interfaceName;
        this.thirdType = thirdType;
        this.requestSerial = requestSerial;
        this.requestOrder = requestOrder;
        this.addDate = addDate;
    }

    public ThirdRequest() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType == null ? null : contentType.trim();
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName == null ? null : interfaceName.trim();
    }

    public String getThirdType() {
        return thirdType;
    }

    public void setThirdType(String thirdType) {
        this.thirdType = thirdType == null ? null : thirdType.trim();
    }

    public String getRequestSerial() {
        return requestSerial;
    }

    public void setRequestSerial(String requestSerial) {
        this.requestSerial = requestSerial == null ? null : requestSerial.trim();
    }

    public String getRequestOrder() {
        return requestOrder;
    }

    public void setRequestOrder(String requestOrder) {
        this.requestOrder = requestOrder == null ? null : requestOrder.trim();
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }
}