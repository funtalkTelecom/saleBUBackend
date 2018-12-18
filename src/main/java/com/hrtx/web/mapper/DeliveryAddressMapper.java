package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.DeliveryAddress;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
@Component
public interface DeliveryAddressMapper extends Mapper<DeliveryAddress>,BaseMapper<DeliveryAddress>{

    List<Map> findDeliveryAddressById(@Param("id") Integer id);

    List<Map> findDeliveryAddressListByUserId(@Param("addUserId") Integer addUserId);

    List<Map> findDeliveryAddressDefaultByUserId(@Param("addUserId") Integer addUserId);

    int checkDeliveryAddressKeyIdIsExist(DeliveryAddress deliveryAddress);

    void deliveryAddressEdit(DeliveryAddress deliveryAddress);

    void deliveryAddressDefault(DeliveryAddress deliveryAddress);

    void deliveryAddressDelete(DeliveryAddress deliveryAddress);

    void insertBatch(@Param("deliveryAddressList") List<DeliveryAddress> list);

    DeliveryAddress findDeliveryAddressByIdForOrder(@Param("id") Integer id);
}
