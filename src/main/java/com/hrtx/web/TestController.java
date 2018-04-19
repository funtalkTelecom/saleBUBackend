package com.hrtx.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
    	userService.test();
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
//        JSONObject jobj = JSONObject.fromObject(rmap);
//        Utils.renderJson(response,jobj.toString());
//        System.out.println("page-demo  over ");
//        return pm;
    }
}
