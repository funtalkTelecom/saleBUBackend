package zyq;

import com.hrtx.SpringbootApplication;
import com.hrtx.web.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes=SpringbootApplication.class)// 指定spring-boot的启动类
public class ZyqTest {
    @Autowired
    private UserService userService;


    @Test
    public void addUser()  {
        userService.paytest1();
        int a = 6;
        Assert.assertEquals("a","a");
    }
}
