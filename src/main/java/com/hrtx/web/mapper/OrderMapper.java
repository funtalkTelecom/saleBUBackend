package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.github.pagehelper.Page;
import com.hrtx.web.pojo.Order;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@Component
public interface OrderMapper extends Mapper<Order>,BaseMapper<Order>{
    void insertBatch(@Param("orderList") List<Order> orderList);

    void deleteByOrderid(@Param("orderid") Long orderId);

    void signByOrderid(Order order);

    List<Map> findOrderSignList();

    List<Map> findOrderListByNumIdAndConsumerId(@Param("numId") Long numId,@Param("consumerId") Long consumerId);

    Order findOrderInfo(@Param("orderid") Long id);

    Page<Object> getOrderByConsumer(@Param("order") Order order, @Param("st") String st);

    Page<Object> queryPageList(@Param("param") Order order, @Param("sellerId") Long sellerId);


    /**----------------benging---取消订单操作涉及的方法--------------------------**/

    Order getOrderByid(@Param("orderId") Long orderId);
    /***
     * 取消订单
     * @param orderId
     * @param status
     * @param reason
     */
    void CancelOrderStatus(@Param("orderId") Long orderId, @Param("status") int status, @Param("reason") String reason);

    List getOrderItmeList(@Param("orderId") Long orderId,@Param("isShipment") int isShipment);

    List getOrderItmeCount(@Param("orderId") Long orderId,@Param("isShipment") int isShipment);

    /**
     *查询下单时间后两个小时未付款的订单
     * @return
     */
    List getTwoHoursOrderList();

    /**----------------end---取消订单操作涉及的方法--------------------------**/

}
