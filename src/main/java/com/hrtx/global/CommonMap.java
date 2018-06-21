package com.hrtx.global;

import java.util.HashMap;
import java.util.Map;

public class CommonMap {

    private Map data = new HashMap<String, Object>();
    private CommonMap() {}

    public static CommonMap create() {
        return new CommonMap();
    }

    public static CommonMap create(String key, Object value) {
        return create().put(key, value);
    }

    public CommonMap put(String key, Object value) {
        data.put(key, value);
        return this;
    }

    public Map getData() {
        return data;
    }

    public void setData(Map data) {
        this.data = data;
    }
}


