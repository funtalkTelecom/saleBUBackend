package zdh;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@WebAppConfiguration
public class TestDemo {
    //参考文章：https://www.cnblogs.com/0201zcr/p/5756642.html
    //https://www.cnblogs.com/lyy-2016/p/6122144.html

    private MockMvc mvc;
    @Autowired private WebApplicationContext context;

    @Before
    public void setupMockMvc() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void testApiSession() throws Exception{
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
        System.out.println("deliveryAddress/5000删除*****返回值字符串  =================    "+resultStr2);


        request =get("/api/hello-world-user?__sessid="+token);
        resultActions=mvc.perform(request);
        MvcResult result4= resultActions.andReturn();
        String resultStr4= result4.getResponse().getContentAsString();
        System.out.println("deliveryAddress/5000删除*****返回值字符串  =================    "+resultStr4);
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("code").value("200"));//校验值

    }

//    @Test
    public void girlList() throws Exception {
        /*mvc.perform(MockMvcRequestBuilders.get("/hello-world-info"))
                .andExpect(MockMvcResultMatchers.status().isOk())//返回状态值200
                .andExpect(MockMvcResultMatchers.);
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "{\"code\":\"200\", \"data\":\"2222\"}";
//        Result result=mapper.readValue(jsonString,Result.class);
        //.content(mapper.writeValueAsString(result))
        ResultActions resultActions= mvc.perform(get("/hello-world-info").accept(MediaType.APPLICATION_JSON));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());//返回状态值200
        resultActions.andDo(print());  //打印内容
        resultActions.andExpect(MockMvcResultMatchers.jsonPath(
                "code").value("200"));
        MvcResult mvcResult = resultActions.andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        System.out.println(content+"   ====================");
//        System.out.println(content().string());
*/
/*        deliveryAddress.setAddress("收货地此测此...");
        deliveryAddress.setPersonName("收货人林林");
        deliveryAddress.setPersonTel("18989123456");
        deliveryAddress.setAddUserId(1);
        deliveryAddress.setCity(String.valueOf(212));
        deliveryAddress.setProvince(String.valueOf(17));
        deliveryAddress.setDistrict(String.valueOf(1911));
        deliveryAddress.setCreateDate(new Date());
        deliveryAddress.setUpdateDate(new Date());
        deliveryAddress.setNote("备注");*/

        //列表deliveryAddres s
        //RequestBuilder request = get("/hello-world-users");
        RequestBuilder request = get("/api/deliveryAddresss/1");
       // .param("addUserId", "1");
        ResultActions resultActions=mvc.perform(request);
        resultActions.andDo(MockMvcResultHandlers.print()).andExpect(status().isOk());
        MvcResult result= resultActions.andReturn();
        String result1 = result.getResponse().getContentAsString();
        System.out.println("deliveryAddress-s**addUserId=1***返回值字符串  =================    "+result);
       // resultActions.andExpect(jsonPath("code").value("200"));//校验值

        //插入一个数据 deliveryAddress id=null or id=""
        request = post("/api/deliveryAddress").
                param("address", "收货地此测此...").param("personName", "收货人林林")
                .param("personTel", "18989123456") .
                        param("city", "212").param("province", "17")
                .param("district", "1911").param("note", "备注")/*.
                        param("createDate", "2018-06-01 16:22:07")
                .param("updateDate", "2018-06-01 16:22:07").param("addUserId", "1")*/;
        resultActions=mvc.perform(request);
        resultActions.andExpect(status().isOk());
        // resultActions.andDo(MockMvcResultHandlers.print());
        resultActions.andExpect(jsonPath("code").value("200"));//校验值


        //测试put方法 deliveryAddress id>0
        //request = put("/deliveryAddress/deliveryAddress").
        request = post("/api/deliveryAddress").
                param("id", "1002389671017185280").param("address", "收货地此测此...")
                .param("personName", "林林收货人林林")
                .param("personTel", "18989123456").
                        param("cityId", "212").param("provinceId", "17")
                .param("districtId", "1911").param("note", "备注")
                /*.param("createDate", "2018-06-01 17:22:07")
                .param("updateDate", "2018-06-01 17:22:07") .param("addUserId", "1")*/;
        resultActions=mvc.perform(request);
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("code").value("200"));//校验值
        MvcResult result4= resultActions.andReturn();
        String resultStr4= result4.getResponse().getContentAsString();
        System.out.println("deliveryAddress/1002389671017185280记录修改*****返回值字符串  =================    "+resultStr4);

        //测试获得一个用户的get方法  deliveryAddress/5000
        request=get("/api/deliveryAddress/1002477799878950912");
        resultActions=mvc.perform(request);
        resultActions.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
//        resultActions .andExpect(content().string(equalTo("{\"code\":200,\"data\":{id:2,name:\"HS\",age:24})}")));
        //resultActions.andExpect(jsonPath("code").value("200"));//校验值
        //resultActions.andExpect(jsonPath("data.age").value(24));//校验值
        MvcResult result2= resultActions.andReturn();
        String resultStr= result2.getResponse().getContentAsString();
        System.out.println("deliveryAddress/1002477799878950912记录信息*****返回值字符串  =================    "+resultStr);



        //测试删除用户
        request=delete("/api/deliveryAddress/5000");
        resultActions=mvc.perform(request);
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("code").value("200"));//校验值
        MvcResult result3= resultActions.andReturn();
        String resultStr2= result3.getResponse().getContentAsString();
        System.out.println("deliveryAddress/5000删除*****返回值字符串  =================    "+resultStr2);
    }

    @Test
    public void insetagent () throws Exception {
        RequestBuilder  request = put("/api/save-or-update-agent").
                param("id", "1006091350984097793")
                .param("commpayName", "福建华瑞11")
                .param("person", "林林22")
                .param("phone", "189891234560")
                .param("province", "1711")
                .param("city", "21211")
                .param("district", "1911111")
                .param("address", "五一南路111号111")
                .param("tradingImg", "222222888.jsp");
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
                .param("pwd", "1234567");
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
}