package com.hrtx.web.test;


import com.hrtx.SpringbootApplication;
import com.hrtx.global.SystemParam;
import com.hrtx.web.service.ApiOrderService;
import net.sf.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=SpringbootApplication.class)
@AutoConfigureMockMvc
@WebAppConfiguration
public class test {
    private MockMvc mvc;
    @Autowired private WebApplicationContext context;
    @Autowired SystemParam systemParam;
    @Autowired
    ApiOrderService apiOrderService;

    @Before
    public void setupMockMvc() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }
    @Test
    public void insetagent () throws Exception {
        RequestBuilder  request = post("/api/save-or-update-agent").
                param("id", "1007537878516891648")
                .param("commpayName", "福建华瑞11")
                .param("person", "林林22")
                .param("phone", "189891234560")
                .param("province", "17")
                .param("city", "212")
                .param("district", "1911")
                .param("address", "五一南路121号121")
                .param("file", "CK80522150160017.jpg");
        ResultActions resultActions=mvc.perform(request);
        resultActions.andExpect(status().isOk());
        // resultActions.andDo(MockMvcResultHandlers.print());
        resultActions.andExpect(jsonPath("code").value("200"));//校验值
        MvcResult result4= resultActions.andReturn();
        String resultStr4= result4.getResponse().getContentAsString();
        System.out.println(" =================    "+resultStr4);
    }

    @Test
    public void queryAgent () throws Exception {
        RequestBuilder  request=get("/api/query-agent_by_consumerid");
        ResultActions resultActions=mvc.perform(request);
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andExpect(status().isOk());
        MvcResult result4= resultActions.andReturn();
        String resultStr4= result4.getResponse().getContentAsString();
        System.out.println("queryAgent/1006420771322462209记录信息*****返回值字符串  =================    "+resultStr4);
    }
    @Test
    public void SaveAgentLeyu () throws Exception {
        RequestBuilder  request = put("/api/save-agent-leyu").
                param("loginName", "18965902601")
                .param("pwd", "123");
        ResultActions resultActions=mvc.perform(request);
        resultActions.andExpect(status().isOk());
        // resultActions.andDo(MockMvcResultHandlers.print());
        resultActions.andExpect(jsonPath("code").value("200"));//校验值
        MvcResult result4= resultActions.andReturn();
        String resultStr4= result4.getResponse().getContentAsString();
        System.out.println(" =================    "+resultStr4);
    }
    @Test
    public void insertCu () throws Exception {
        RequestBuilder  request = put("/api/Consumer")
                .param("loginName", "")
                .param("livePhone", "18965902601")
                .param("nickName", "18965902601")
                .param("sex", "1")
                .param("img", "1212121.jsp")
                .param("province", "17")
                .param("city", "212")
                ;
        ResultActions resultActions=mvc.perform(request);
        resultActions.andExpect(status().isOk());
        // resultActions.andDo(MockMvcResultHandlers.print());
        resultActions.andExpect(jsonPath("code").value("200"));//校验值
        MvcResult result4= resultActions.andReturn();
        String resultStr4= result4.getResponse().getContentAsString();
        System.out.println(" =================    "+resultStr4);
    }
    @Test
    public void CancelOrder () throws Exception {
        systemParam.load1();
        RequestBuilder  request = post("/api/cancel-order")
                .param("orderId", "1020218534019465216")
                .param("reason", "取消订单1020218534019465216")
                ;
        ResultActions resultActions=mvc.perform(request);
        resultActions.andExpect(status().isOk());
        // resultActions.andDo(MockMvcResultHandlers.print());
        resultActions.andExpect(jsonPath("code").value("200"));//校验值
        MvcResult result4= resultActions.andReturn();
        String resultStr4= result4.getResponse().getContentAsString();
        System.out.println(" =================    "+resultStr4);
    }


    @Test
    public void fandnumber () throws Exception {
        systemParam.load1();
        RequestBuilder  request = get("/api/fand-number")
                .param("num", "1770003")
                ;
        ResultActions resultActions=mvc.perform(request);
        resultActions.andExpect(status().isOk());
        // resultActions.andDo(MockMvcResultHandlers.print());
        resultActions.andExpect(jsonPath("code").value("200"));//校验值
        MvcResult result4= resultActions.andReturn();
        String resultStr4= result4.getResponse().getContentAsString();
        System.out.println(" =================    "+resultStr4);
    }

    @Test
    public void createOrder () throws Exception {
        //测试删除用户
        RequestBuilder request =get("/api/hello-world-login");
        ResultActions resultActions=mvc.perform(request);
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("code").value("200"));//校验值
//        resultActions.andExpect(MockMvcResultMatchers.jsonPath("code").value("200"));
        MvcResult result3= resultActions.andReturn();
        String resultStr2= result3.getResponse().getContentAsString();
        JSONObject json=JSONObject.fromObject(resultStr2);
        String token=json.getString("data");

        systemParam.load1();
        for (int i = 1; i < 5000; i++) {
            Thread.sleep(1);
            System.out.println(" =================线程"+i+"=====开始============================= ");
            Map<String, Object> map = new HashMap<>();
            map.put("mvc", mvc);
            new CommonThread(map, "线程"+i) {
                @Override
                public void run() {
                    System.out.println(" =================    启动线程");
//                    MockMvc mvc1 = (MockMvc) this.getMap().get("mvc");
                    MockMvc mvc1=mvc;
                   /* RequestBuilder request = post("/api/order")
                            .param("type", "2")
                            .param("skuid", "1017248286928535552")    //普号1017248286928535552，白卡1017575741186899976 ，普靓1012283253786148864，超靓1012283275965628416
                            .param("addrid", "1017323624106819584")
                            .param("numid", "989303317700030209")
                            .param("mealid", "1012276876082675712")
                            .param("numcount", "1")*/
                            ;
                    //http://127.0.0.1:8091/api/epSaleGoodsAuciton?__sessid=9c427b7a84bdeef0650bf504d3937ba1&skuId=1014334935252598784&numId=989303317700030651&num=17700030651&gId=1014334934585704448&gName=竞彩世界杯-N1&price=1234567.56
                    RequestBuilder  request_epSaleGoodsAuciton=post("/api/epSaleGoodsAuciton?__sessid="+token).
                            param("skuId", "1014334935252598784")
                            .param("numId", "989303317700030659")
                            .param("num", "17700030659")
                            .param("gId", "1014334934585704448")
                            .param("gName", "竞彩世界杯-N1")
                            .param("price", "3.60");


                   /* ResultActions resultActions_epSaleGoodsAuciton=mvc.perform(request_epSaleGoodsAuciton);
                    MvcResult result_epSaleGoodsAuciton= resultActions_epSaleGoodsAuciton.andReturn();
                    String resultStr_epSaleGoodsAuciton= result_epSaleGoodsAuciton.getResponse().getContentAsString();
                    System.out.println("epSaleGoodsAuciton***返回值字符串  =================    "+resultStr_epSaleGoodsAuciton);*/

                    ResultActions resultActions = null;
                    try {
                        resultActions = mvc1.perform(request_epSaleGoodsAuciton);
                        resultActions.andExpect(status().isOk());
                        resultActions.andExpect(jsonPath("code").value("200"));//校验值
                        MvcResult result4 = resultActions.andReturn();
                        String resultStr4 = result4.getResponse().getContentAsString();
                        System.out.println(" =================    " + resultStr4);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }.start();
            System.out.println(" =================线程"+i+"=====结束============================= ");
        }
        while(true){
            Thread.sleep(1000);
        }
    }
}

class CommonThread extends  Thread{
    private Map map;

    public CommonThread(Map map, String name) {
        super(name);
        this.map = map;
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }



}
