package com.hrtx.web.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.config.annotation.Powers;
import com.hrtx.config.utils.RedisUtil;
import com.hrtx.dto.Result;
import com.hrtx.dto.StorageInterfaceRequest;
import com.hrtx.global.*;
import com.hrtx.web.dto.StorageInterfaceResponse;
import com.hrtx.web.mapper.*;
import com.hrtx.web.pojo.*;
import com.hrtx.web.pojo.Number;
import com.hrtx.web.service.ApiOrderService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.System;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ApiOrderController extends BaseReturn{

	public final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
    ApiOrderService apiOrderService;


    @PostMapping("/order")
    @Powers(PowerConsts.NOPOWER)
//    @Powers(PowerConsts.NOLOGINPOWER)
    @ResponseBody
    public Result createOrder(HttpServletRequest request){
        return apiOrderService.createOrder(request, null);
    }

    @GetMapping("/order")
//    @Powers(PowerConsts.NOPOWER)
    @Powers(PowerConsts.NOLOGINPOWER)
    @ResponseBody
    public Result getOrderByConsumer(HttpServletRequest request){
        return apiOrderService.getOrderByConsumer(request);
    }
}
