package com.hrtx.web.pojo;

import javax.persistence.Table;
import java.util.Date;
@Table(name = "tb_third_request")
public class ThirdRequestWithBLOBs extends ThirdRequest {
    private String requestContent;

    private String responseContent;

    public ThirdRequestWithBLOBs(String contentType, String interfaceName, String thirdType, String requestSerial, String requestOrder, Date addDate, String requestContent, String responseContent) {
        super(contentType, interfaceName, thirdType, requestSerial, requestOrder, addDate);
        this.requestContent = requestContent;
        this.responseContent = responseContent;
    }

    public ThirdRequestWithBLOBs() {
        super();
    }

    public String getRequestContent() {
        return requestContent;
    }

    public void setRequestContent(String requestContent) {
        this.requestContent = requestContent == null ? null : requestContent.trim();
    }

    public String getResponseContent() {
        return responseContent;
    }

    public void setResponseContent(String responseContent) {
        this.responseContent = responseContent == null ? null : responseContent.trim();
    }
}