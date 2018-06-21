package com.hrtx.web.controller;

import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.ApiSessionUtil;
import com.hrtx.global.PowerConsts;
import com.hrtx.web.pojo.Account;
import com.hrtx.web.pojo.DeliveryAddress;
import com.hrtx.web.service.AccountService;
import com.hrtx.web.service.CityService;
import com.hrtx.web.service.DeliveryAddressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
//@RequestMapping("/api")
public class DeliveryAddressController extends BaseReturn{

	@Resource
	private CityService cityService;
	@Autowired
	AccountService accountService;
	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private DeliveryAddressService deliveryAddressService;
	@Autowired
	private ApiSessionUtil apiSessionUtil;

	@RequestMapping("/deliveryAddress/deliveryAddress-query")
	@Powers({PowerConsts.DELIVERYADDRESSMOUDULE})
	public ModelAndView deliveryAddressQuery(DeliveryAddress deliveryAddress){
		return new ModelAndView("admin/deliveryAddress/deliveryAddress-query");
	}

	@RequestMapping("/deliveryAddress/deliveryAddress-list")
	@Powers({PowerConsts.DELIVERYADDRESSMOUDULE_COMMON_QUEYR})
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
		Long cunsumerId=0L;
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
		List<Map> list=deliveryAddressService.findDeliveryAddressById(Long.valueOf(id));
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
		deliveryAddress.setId(Long.valueOf(id));
		returnResult(deliveryAddressService.deliveryAddressDefault(deliveryAddress, request));
	}

	@DeleteMapping("/api/deliveryAddress/{id}")
	@Powers({PowerConsts.NOPOWER})
	public void deliveryAddressDelete(DeliveryAddress deliveryAddress, HttpServletRequest request,@PathVariable("id") String id){
		deliveryAddress.setId(Long.valueOf(id));
		returnResult(deliveryAddressService.deliveryAddressDelete(deliveryAddress));
	}

	@GetMapping("/account-list/{addUserId}")
	@Powers({PowerConsts.NOPOWER})
	@ResponseBody
	public Result listDeliveryAddress(Account account, @PathVariable("addUserId") String addUserId){
		return accountService.findAccountListByUserId(Long.valueOf(addUserId));
	}
}
