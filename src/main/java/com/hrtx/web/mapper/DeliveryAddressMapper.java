package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.DeliveryAddress;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface DeliveryAddressMapper extends Mapper<DeliveryAddress>,BaseMapper<DeliveryAddress>{

    List<Map> findDeliveryAddressById(@Param("id") Long id);

    List<Map> findDeliveryAddressListByUserId(@Param("addUserId") Long addUserId);

    int checkDeliveryAddressKeyIdIsExist(DeliveryAddress deliveryAddress);

    void deliveryAddressEdit(DeliveryAddress deliveryAddress);

    void deliveryAddressDelete(DeliveryAddress deliveryAddress);

    void insertBatch(@Param("deliveryAddressList") List<DeliveryAddress> list);
}
