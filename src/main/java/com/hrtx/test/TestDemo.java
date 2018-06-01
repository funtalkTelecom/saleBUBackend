package com.hrtx.test;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrtx.dto.Result;
import com.hrtx.web.pojo.City;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

        //列表
        RequestBuilder request = get("/hello-world-users");
        ResultActions resultActions=mvc.perform(request);
        resultActions.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
        MvcResult result= resultActions.andReturn();
        String result1 = result.getResponse().getContentAsString();
        System.out.println("返回值字符串  =================    "+result1);
        resultActions.andExpect(jsonPath("code").value("200"));//校验值

        //插入一个数据
        request = post("/hello-world-user").param("id", "2").param("name", "HS").param("age", "24");
        resultActions=mvc.perform(request);
        resultActions.andExpect(status().isOk());
//        resultActions.andDo(MockMvcResultHandlers.print());
        resultActions.andExpect(jsonPath("code").value("200"));//校验值
        //测试put方法
        request = put("/hello-world-user/2")
                .param("name", "HGod")
                .param("age", "24");
        resultActions=mvc.perform(request);
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("code").value("200"));//校验值
        //测试获得一个用户的get方法
        request=get("/hello-world-user/2");
        resultActions=mvc.perform(request);
        resultActions.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
//        resultActions .andExpect(content().string(equalTo("{\"code\":200,\"data\":{id:2,name:\"HS\",age:24})}")));
        resultActions.andExpect(jsonPath("code").value("200"));//校验值
        resultActions.andExpect(jsonPath("data.age").value(24));//校验值
        //测试删除用户
        request=delete("/hello-world-user/2");
        resultActions=mvc.perform(request);
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("code").value("200"));//校验值

    }

}