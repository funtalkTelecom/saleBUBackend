package com.hrtx.web.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.dto.Result;
import com.hrtx.web.mapper.OrderItemMapper;
import com.hrtx.web.pojo.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class OrderItemService {

    @Autowired
    private OrderItemMapper orderItemMapper;

    public Result pageOrderItem(OrderItem orderItem) {
        PageHelper.startPage(orderItem.getPageNum(),orderItem.getLimit());
        Page<Object> ob=this.orderItemMapper.queryPageListDetail(orderItem);
        PageInfo<Object> pm = new PageInfo<Object>(ob);
        return new Result(Result.OK, pm);
    }
}
