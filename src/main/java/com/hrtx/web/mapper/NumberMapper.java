package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.github.pagehelper.Page;
import com.hrtx.web.pojo.Number;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
@Component
public interface NumberMapper extends Mapper<Number>,BaseMapper<Number>{
    void updateStatus(@Param("number") Number number, @Param("isClearSkuid") boolean isClearSkuid);

    int checkNumberIsOk(Number number);

    int checkNumberIsOkStatus(Number number);

    Page<Object> queryPageListApi(@Param("tags") String tags);

    Map getNumInfoById(@Param("id") String id);

    Map getNumInfoByGId(@Param("gId") String gId);

    Map getNumInfoByGId2(@Param("gId") String gId);

    void freezeNum(@Param("id") String numid, @Param("status") String status,@Param("isUpdateSukid") boolean isUpdateSukid);

    int freezeNumbyStatus(@Param("id") String numid, @Param("status") String status,@Param("newStatus") String newStatus);

    List<Number> getListBySkuid(@Param("skuid")String skuid);

    void freezeNumByIds(@Param("numberList") List<Number> nlist, @Param("status") String status);

    List<Number> getListBySkuidAndStatus(@Param("skuid") String skuid, @Param("status") String status, @Param("numcount") int numcount);

    Page<Object> queryPageListApiForNumber3(@Param("skuGoodsType") String skuGoodsType,@Param("agentCity") Long agentCity,@Param("isAgent") Integer isAgent);

    Map getNumInfoByNum(@Param("num") String num);

    Page<Object> queryPageList(Number number, @Param("param") Map param);

    void updateDelStatus(@Param("delSku") Long delSku);

    Page<Object> queryPageByNumList(@Param("num") String num);

    /**
     * 批量竞拍，根据goodid 查找对应的号码信息，状态=2
     * @param goodsid
     * @return
     */
    List queryGoodsNumberList(@Param("goodsid")  String goodsid);

    void updateStatusByNumber(@Param("StrNums") String StrNums, @Param("skuId") Integer skuId,@Param("status") int status,
                              @Param("StartTime") Date StartTime, @Param("EndTime") Date EndTime);

    int updateNumStatusWithData(@Param("org_status")int org_status,@Param("new_status")int new_status,@Param("num_id")int num_id);

    Map getNumSkuGoodsTypeById(@Param("id") String id);


    /**
     * 活动验证上架的号码
     * @param
     * @return
     */
    int activitycheckNumberIsOkStatus(@Param("sellerId") Integer sellerId,@Param("numResource") String numResource,@Param("angentId") Integer angentId);

}
