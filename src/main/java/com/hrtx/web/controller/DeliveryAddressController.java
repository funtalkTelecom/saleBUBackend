package com.hrtx.web.controller;

import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.ApiSessionUtil;
import com.hrtx.global.PowerConsts;
import com.hrtx.web.pojo.DeliveryAddress;
import com.hrtx.web.service.DeliveryAddressService;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
//@RequestMapping("/api")
public class DeliveryAddressController extends BaseReturn{

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private DeliveryAddressService deliveryAddressService;
	@Autowired
	private ApiSessionUtil apiSessionUtil;

	@RequestMapping("/deliveryAddress/deliveryAddress-query")
	@Powers({PowerConsts.NOPOWER})
	public ModelAndView deliveryAddressQuery(DeliveryAddress deliveryAddress){
		return new ModelAndView("admin/deliveryAddress/deliveryAddress-query");
	}

	@RequestMapping("/deliveryAddress/deliveryAddress-list")
	@Powers({PowerConsts.NOPOWER})
	public Result listDeliveryAddress(DeliveryAddress deliveryAddress){
		return deliveryAddressService.pageDeliveryAddress(deliveryAddress);
	}

	@GetMapping("/api/deliveryAddresss")
	@Powers({PowerConsts.NOPOWER})
	@ResponseBody
	public Result listDeliveryAddress(){
		return deliveryAddressService.findDeliveryAddressList();
	}

	@GetMapping("/api/deliveryAddressDefault")
	@Powers({PowerConsts.NOPOWER})
	@ResponseBody
	public Result DeliveryAddressDefault(){
		Integer cunsumerId=0;
		cunsumerId=this.apiSessionUtil.getConsumer().getId();
		return deliveryAddressService.findDeliveryAddressDefault(cunsumerId);
	}

	@GetMapping("/api/deliveryAddress/{id}")
	@ResponseBody
	@Powers({PowerConsts.NOPOWER})
	public Map deliveryAddressInfo(DeliveryAddress deliveryAddress, HttpServletRequest request,@PathVariable("id") String id){
		Map<String, Object> map = new HashMap<String, Object>();
		//Object list=cityService.queryByPidList(0);
/*		deliveryAddress.setId(new Long(5000));*/
		List<Map> list=deliveryAddressService.findDeliveryAddressById(NumberUtils.toInt(id));
		map.put("code", Result.OK);
		map.put("data", list);
		request.setAttribute("bean", list);
		//request.setAttribute("province", list);
		return map;
	}

	@PostMapping("/api/deliveryAddress")
	@Powers({PowerConsts.NOPOWER})
	@ResponseBody
	public void deliveryAddressEdit(DeliveryAddress deliveryAddress, HttpServletRequest request) {
		returnResult(deliveryAddressService.deliveryAddressEdit(deliveryAddress, request));
	}

	@GetMapping("/api/deliveryAddressDefault/{id}")
	@Powers({PowerConsts.NOPOWER})
	@ResponseBody
	public void deliveryAddressDefault(DeliveryAddress deliveryAddress, HttpServletRequest request,@PathVariable("id") String id) {
		deliveryAddress.setId(NumberUtils.toInt(id));
		returnResult(deliveryAddressService.deliveryAddressDefault(deliveryAddress, request));
	}

	@DeleteMapping("/api/deliveryAddress/{id}")
	@Powers({PowerConsts.NOPOWER})
	public void deliveryAddressDelete(DeliveryAddress deliveryAddress, HttpServletRequest request,@PathVariable("id") String id){
		deliveryAddress.setId(NumberUtils.toInt(id));
		returnResult(deliveryAddressService.deliveryAddressDelete(deliveryAddress));
	}

}
