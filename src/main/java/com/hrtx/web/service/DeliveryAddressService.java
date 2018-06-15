package com.hrtx.web.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.dto.Result;
import com.hrtx.global.ApiSessionUtil;
import com.hrtx.global.SystemParam;
import com.hrtx.global.Utils;
import com.hrtx.web.mapper.DeliveryAddressMapper;
import com.hrtx.web.pojo.City;
import com.hrtx.web.pojo.DeliveryAddress;
import com.hrtx.web.pojo.Poster;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


@Service
public class DeliveryAddressService {
	
	@Autowired
	private DeliveryAddressMapper deliveryAddressMapper;
	@Autowired
	private ApiSessionUtil apiSessionUtil;
	public Result pageDeliveryAddress(DeliveryAddress deliveryAddress) {
		PageHelper.startPage(deliveryAddress.getPageNum(),deliveryAddress.getLimit());
		Page<Object> ob=this.deliveryAddressMapper.queryPageList(deliveryAddress);
		PageInfo<Object> pm = new PageInfo<Object>(ob);
		return new Result(Result.OK, pm);
	}

	public Result findDeliveryAddressListByUserId(Long userId) {
		return new Result(Result.OK,  deliveryAddressMapper.findDeliveryAddressListByUserId( userId));
	}

	public Result findDeliveryAddressList() {
		return new Result(Result.OK,  deliveryAddressMapper.findDeliveryAddressListByUserId(this.apiSessionUtil.getConsumer().getId()));
	}

	public Result findDeliveryAddressDefault() {
		return new Result(Result.OK,  deliveryAddressMapper.findDeliveryAddressDefaultByUserId(this.apiSessionUtil.getConsumer().getId()));
	}

	public  List<Map> findDeliveryAddressById(Long id) {
		return  deliveryAddressMapper.findDeliveryAddressById(id);
	}

	public Result deliveryAddressEdit(DeliveryAddress deliveryAddress, HttpServletRequest request) {
		if (deliveryAddress.getId() != null && deliveryAddress.getId() > 0) {
			deliveryAddress.setUpdateDate(new Date());
			deliveryAddressMapper.deliveryAddressEdit(deliveryAddress);
		} else {
			Long kk=this.apiSessionUtil.getConsumer().getId();
			//List<Map> defaultMap= deliveryAddressMapper.findDeliveryAddressDefaultByUserId(this.apiSessionUtil.getConsumer().getId());
			List<Map> listDeliveryAddress=deliveryAddressMapper.findDeliveryAddressListByUserId(this.apiSessionUtil.getConsumer().getId());
			if(listDeliveryAddress.size()>0)
			{
				deliveryAddress.setIsDefaultl(0);
			}else
			{
				deliveryAddress.setIsDefaultl(1);
			}
			List<DeliveryAddress> list = new ArrayList<DeliveryAddress>();
			deliveryAddress.setAddUserId(apiSessionUtil.getConsumer().getId());
			deliveryAddress.setId(deliveryAddress.getGeneralId());
			deliveryAddress.setCreateDate(new Date());
			deliveryAddress.setUpdateDate(new Date());
			list.add(deliveryAddress);
			deliveryAddressMapper.insertBatch(list);
		}
		return new Result(Result.OK, "提交成功");
	}

    public Result deliveryAddressDelete(DeliveryAddress deliveryAddress) {
        deliveryAddressMapper.deliveryAddressDelete(deliveryAddress);
        return new Result(Result.OK, "删除成功");
    }
}
