package com.hrtx.web.TimeTask;

import com.hrtx.web.service.EPSaleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskJob {
    @Autowired
    private EPSaleService epSaleService;
   // private Logger logger = LoggerFactory.getLogger(TaskJob.class);
   public final Logger logger = LoggerFactory.getLogger(this.getClass());
    //用户密码恢复：每日上午7点
    public void job1() {
        logger.info("用户登录次数清零；任务进行中。。。");
        epSaleService.checkEPsaleNum();

    }
}
