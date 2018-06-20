package com.hrtx.web.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.dto.Result;
import com.hrtx.web.mapper.OrderMapper;
import com.hrtx.web.pojo.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class OrderService {
	
	@Autowired
	private OrderMapper orderMapper;

	public Result pageOrder(Order order) {
		PageHelper.startPage(order.getPageNum(),order.getLimit());
		Page<Object> ob=this.orderMapper.queryPageList(order);
		PageInfo<Object> pm = new PageInfo<Object>(ob);
		return new Result(Result.OK, pm);
	}

	public Order findOrderById(Long id) {
		Order order = orderMapper.findOrderInfo(id);
		return order;
	}
}
