package com.hrtx.global;

import com.hrtx.web.controller.UserController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class AuthCodeUtil {
    protected static Logger log = LoggerFactory.getLogger(AuthCodeUtil.class);
    Color getRandColor(int fc, int bc) {//给定范围获得随机颜色
        Random random = new Random();
        if (fc > 255)
            fc = 255;
        if (bc > 255)
            bc = 255;
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    public void downLoadAuthCode(HttpServletRequest request, HttpServletResponse response){
        //设置页面不缓存
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        // 在内存中创建图象
        try {
            int width = 90, height = 50;
            BufferedImage image = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);

            // 获取图形上下文
            Graphics g = image.getGraphics();

            //生成随机类
            Random random = new Random();

            // 设定背景色
            g.setColor(getRandColor(200, 250));
            g.fillRect(0, 0, width, height);

            //设定字体
            g.setFont(new Font("Times New Roman", Font.PLAIN, 18));

            //画边框
            //g.setColor(new Color());
            //g.drawRect(0,0,width-1,height-1);

            // 随机产生155条干扰线，使图象中的认证码不易被其它程序探测到
            g.setColor(getRandColor(160, 200));
            for (int i = 0; i < 155; i++) {
                int x = random.nextInt(width);
                int y = random.nextInt(height);
                int xl = random.nextInt(12);
                int yl = random.nextInt(12);
                g.drawLine(x, y, x + xl, y + yl);
            }

            // 取随机产生的认证码(6位数字 4位蓝色 2位红色)  蓝色为有效验证码
            String sRand = "";
            Color[] colors = new Color[]{new Color(27,191,117), new Color(247,76,49)};
            int count0 = 0, count1 = 0;
            for (int i = 0; i < 6; i++) {
                String rand = String.valueOf(random.nextInt(10));
                //sRand += rand;
                // 将认证码显示到图象中
                //g.setColor(new Color(20 + random.nextInt(110), 20 + random
                //.nextInt(110), 20 + random.nextInt(110)));// 调用函数出来的颜色相同，可能是因为种子太接近，所以只能直接生成
                int rd=Math.random()>0.5?1:0;
                while(true) {
                    if((rd == 0 && count0 >= 4) || (rd == 1 && count1>=2)) {
                        rd = Math.random()>0.5?1:0;
                    }else {
                        break;
                    }
                }
                if(rd == 0) {
                    sRand += rand;
                    count0++;
                }else {
                    count1++;
                }
                g.setColor(colors[rd]);
                g.drawString(rand, 13 * i + 6, 25);
            }
            Font f = new Font("微软雅黑",Font.CENTER_BASELINE,14);
            g.setFont(f);
            g.setColor(colors[0]);
            g.drawString("*取绿色数字", 6, 42);
            //g.drawLine(x, y, x + xl, y + yl);
            // 将认证码存入SESSION
            request.getSession().setAttribute(UserController.RANK_AUTH_CODE, sRand);
            // 图象生效
            g.dispose();
            // 输出图象到页面
            ImageIO.write(image, "PNG", response.getOutputStream());
            response.setContentType("image/jpeg");
        }catch (Exception e){
            log.error("获取验证码异常", e);
        }
    }
}
