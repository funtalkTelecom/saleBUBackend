package com.hrtx.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hrtx.dto.Result;
import com.hrtx.global.ApiSessionUtil;
import com.hrtx.global.TokenGenerator;
import com.hrtx.web.pojo.Consumer;
import com.hrtx.web.pojo.User;
import com.hrtx.web.service.ConsumerService;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.hrtx.config.Utils;
import com.hrtx.config.annotation.Powers;
import com.hrtx.global.PowerConsts;
import com.hrtx.web.service.EventService;
import com.hrtx.web.service.UserService;

/**
 * 一、session管理
 * 		共享
 * 		公共类
 * 二、权限
 * 		原始或spring security
 * 		是否有权限
 * 三、异常处理
 * 四、分页问题
 * 五、消息--事件  异步
 * 六、日志-log4j
 * 七、模板 Thymeleaf
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

@Controller
//@RestController
public class TestController {

    @Autowired private UserService userService;
    @Autowired private EventService eventService;
    @Autowired private ApiSessionUtil apiSessionUtil;
    @Autowired private ConsumerService consumerService;

    @RequestMapping("/")
    @Powers({PowerConsts.NOLOGINPOWER})
    public String helloworld1(HttpServletRequest request) {
        eventService.testEvent("==========================","99999");
        System.out.println("我主线程完成了");
        return "=======";
    }


    @RequestMapping("/helloworld")
    @Powers({PowerConsts.NOLOGINPOWER})
    public String helloworld(HttpServletRequest request) {
        String key="abcdeeedddddd";
        request.getSession().setAttribute("userinfo12",key);
        System.out.println("添加了一个session");
//        userService.test();
//    	if("1".equals("1"))throw new RuntimeException("this test!");
        return key;
    }

    @GetMapping("/helloworld2")
    @ResponseBody
    @Powers({PowerConsts.NOLOGINPOWER})
    public String helloworld2(HttpServletRequest request) {
        String userinfo=String.valueOf(request.getSession().getAttribute("userinfo12"));
        System.out.println("添加了一个session："+userinfo);
//    	if("1".equals("1"))throw new RuntimeException("this test!");
        return "index";
    }


    @GetMapping("/error")
    @Powers({PowerConsts.NOLOGINPOWER})
    public String error(HttpServletRequest request) {
        return "index";
    }
//    @GetMapping("/loin-index")
//    @Powers({PowerConsts.NOLOGINPOWER})
//    public String loginIndex(HttpServletRequest request) {
//        return "index";
//    }

    @GetMapping("/helloworld3")
    @Powers({PowerConsts.NOLOGINPOWER})
    public String helloworld3(HttpServletRequest request) {
        String p=request.getParameter("p");
        return "admin/"+p;
    }

    @RequestMapping("/page-demo")
    @Powers({PowerConsts.NOLOGINPOWER})
    @ResponseBody
    public Object helloworld4(HttpServletRequest request,HttpServletResponse response) {
        List<Map<String,String>> list=new ArrayList<Map<String,String>>();
        Map<String,String> map=new HashMap<String, String>();
        map.put("id", "1");
        map.put("created", "2");
        map.put("anothername", "3");
        map.put("anothe1", "4");
        map.put("anothe2", "5");
        map.put("anothe3", "5");
        list.add(map);
        Map<String,Object> rmap=new HashMap<String, Object>();
        rmap.put("iTotalRecords","10");
        rmap.put("iTotalDisplayRecords","50");
//        request.getParameter("draw") 第draw页
        rmap.put("sEcho",request.getParameter("draw"));
        rmap.put("aaData",list);
        System.out.println("page-demo  over ");
        return rmap;
    }

    @GetMapping("/api/hello-world-info")
    @Powers({PowerConsts.NOLOGINPOWER})
    public Result helloworld4(HttpServletRequest request) {
        Result result=new Result(Result.OK,"SS");
        return result;
    }

    @PostMapping("/hello-world-user")
    @Powers({PowerConsts.NOLOGINPOWER})
    @ResponseBody
    public Result helloworld5(HttpServletRequest request) {
        String id=request.getParameter("id");
        String name=request.getParameter("name");
        String age=request.getParameter("age");
        System.out.println(String.format("新增用户信息 收到一个请求id[%s],name[%s],age[%s]",id,name,age));
        Result result=new Result(Result.OK,"添加成功");
        return result;
    }
    @PutMapping("/api/hello-world-user/{id}")
    @Powers({PowerConsts.NOLOGINPOWER})
    @ResponseBody
    public Result helloworld6(HttpServletRequest request,@PathVariable("id") String id) {
        String name=request.getParameter("name");
        String age=request.getParameter("age");
        System.out.println(String.format("修改用户信息，收到一个请求id[%s],name[%s],age[%s]",id,name,age));
        Result result=new Result(Result.OK,"修改成功");
        return result;
    }
    @DeleteMapping("/hello-world-user/{id}")
    @Powers({PowerConsts.NOLOGINPOWER})
    @ResponseBody
    public Result helloworld7(HttpServletRequest request,@PathVariable("id") String id) {
        System.out.println(String.format("删除用户信息，收到一个请求id[%s]",id));
        Result result=new Result(Result.OK,"删除成功");
        return result;
    }
    @GetMapping("/hello-world-user/{id}")
    @Powers({PowerConsts.NOLOGINPOWER})
    @ResponseBody
    public Result helloworld8(HttpServletRequest request,@PathVariable("id") String id) {
        System.out.println(String.format("获取用户信息，收到一个请求id[%s]",id));
        Map<String,Object> _map=new HashMap<>();
        _map.put("id",2);
        _map.put("name","HS");
        _map.put("age",24);
        Result result=new Result(Result.OK,_map);
        return result;
    }
    @GetMapping("/hello-world-users")
    @Powers({PowerConsts.NOLOGINPOWER})
    @ResponseBody
    public Result helloworld9(HttpServletRequest request) {
        System.out.println("获取用户列表！");
        Result result=new Result(Result.OK,"[]");
        return result;
//        return "[]";
    }

    @GetMapping("/api/hello-world-login")
    @Powers({PowerConsts.NOLOGINPOWER})
    @ResponseBody
    public Result helloworld10(HttpServletRequest request) {
        Consumer user= this.apiSessionUtil.getConsumer();
        System.out.println("获取用户  "+user);
        user=new Consumer();
        user.setId(1009758228466106368l);
        user=consumerService.getConsumerById(user);
        String token=TokenGenerator.generateValue();
        this.apiSessionUtil.saveOrUpdate(token,user);
        System.out.println("method{login}  "+user.getName());
        Result result=new Result(Result.OK,token);
        return result;
//        return "[]";
    }

    @GetMapping("/api/hello-world-user")
    @Powers({PowerConsts.NOPOWER})
    @ResponseBody
    public Result helloworld11(HttpServletRequest request) {
        User user= this.apiSessionUtil.getUser();
        System.out.println("method{user}  "+user.getLoginName());
        Result result=new Result(Result.OK,"[]");
        return result;
//        return "[]";
    }

}
