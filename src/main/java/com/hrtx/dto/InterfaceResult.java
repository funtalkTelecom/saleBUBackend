package com.hrtx.dto;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InterfaceResult implements java.io.Serializable {
    protected static Logger log = LoggerFactory.getLogger(InterfaceResult.class);
    private static final long serialVersionUID = 1L;
    private String code;
    private String desc;
    private String platData = "";
    private Object map;

    public InterfaceResult() {
        super();
    }
    public InterfaceResult(String code, String desc) {
        super();
        this.code = code;
        this.desc = desc;
        log.info("---------------------------interfaceResult{code:"+code+",desc:"+desc+"}");
    }
    public InterfaceResult(String code, String desc, String platData) {
        super();
        this.code = code;
        this.desc = desc;
        this.platData = platData;
        log.info("---------------------------interfaceResult{code:"+code+",desc:"+desc+",platData:"+platData+"}");
    }
    public InterfaceResult(String code, Object map) {
        super();
        this.code = code;
        this.map = map;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public Object getMap() {
        return map;
    }
    public void setMap(Object map) {
        this.map = map;
    }
    public String getPlatData() {
        return platData;
    }
    public void setPlatData(String platData) {
        this.platData = platData;
    }

}
