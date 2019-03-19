package com.hrtx.web.service;

import com.hrtx.dto.Result;
import com.hrtx.global.Constants;
import com.hrtx.global.SystemParam;
import com.hrtx.global.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

@Service
public class ImageService {
	public final Logger log = LoggerFactory.getLogger(this.getClass());

	public static File shareCardDemoFile=null;
	public static File shareLinkDemoFile=null;

	public static void main(String[] args) throws IOException{
//		createShareLinkFile("189-1234-1234","福建-福州","电信","乐语","189元");
	}

	public static void createShareCardFile(LinkedHashMap<String,String> map){
		if(shareCardDemoFile==null)shareCardDemoFile=new File("D:\\test\\idcard\\1000\\CK9031409044002.jpg");
	}

	public Result createShareLinkFile(String num,String city,String net_type,String tele_type,String low_c) throws IOException{
		long _start=System.currentTimeMillis();
		String root_path = SystemParam.get("upload_root_path");
		String shareLinkDemo=SystemParam.get("link-demo-file");
		String suffix_v = shareLinkDemo.substring(shareLinkDemo.lastIndexOf(".") + 1).toLowerCase();//获得原文件的后缀
		String new_file_name = Utils.randomNoByDateTime();//获得图片文件随机文件名
		String shareLink=new_file_name + "."+suffix_v;//获得新图片名称
		if(shareLinkDemoFile==null)shareLinkDemoFile=new File(root_path+"/"+Constants.UPLOAD_PATH_SHARE.getStringKey()+"/"+shareLinkDemo);
		String new_file=root_path+"/"+Constants.UPLOAD_PATH_SHARE.getStringKey()+"/"+shareLink;
		int padding_top=150;//行间距
		int font_height=40;//字体高度
		int fontSize=24;
		Image image = ImageIO.read(shareLinkDemoFile);
		int width = image.getWidth(null);
		int height = image.getHeight(null);
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bufferedImage.createGraphics();
		g.drawImage(image,0,0, width, height, null);
		g.setFont(new Font("黑体",Font.PLAIN,fontSize));
		g.setColor(new Color(0,0,0));//颜色

		Graphics2D gccc = bufferedImage.createGraphics();
		gccc.drawImage(image,0,0, width, height, null);
		gccc.setFont(new Font("黑体",Font.PLAIN,fontSize));
		gccc.setColor(new Color(204,0,0));//颜色
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,1));
//		g.drawString(num,padding_left,padding_top+(font_height*1));//水印文件
		/*
		 *1.将图上分1/4、2/4和1/4  3块
		 *2.第一块(1/4)写号码;第二块(2/4)写号码信息;第三块留白
		 *3.中间是2/4进行画横线和竖线
		 *4.
		 *
		 */
		g.drawLine(width/2, height/4, width/2, height*3/4);//竖线
		g.drawLine(60, height/2, width-60, height/2);//横线

		g.setFont(new Font("黑体",Font.PLAIN,48));
		g.drawString(num,(width-48*getTextLength(num))/2,60+(48/2));//写中图片中间
		g.setFont(new Font("黑体",Font.PLAIN,fontSize));
		int padding_left=width/2-(getTextLength(net_type)*fontSize)-15;//左侧偏移
		int padding_city_left=width/2+15;//左侧偏移
		g.drawString(net_type,padding_left,height/4+height/4-(48/2)-15);
		gccc.drawString("制式",padding_left,height/4+height/4-15);
		g.drawString(city,padding_city_left,height/4+height/4-(48/2)-15);
		gccc.drawString("地市",padding_city_left,height/4+height/4-15);


		g.drawString(tele_type,padding_left,height/4+height/4+(60)-15);
		gccc.drawString("运营商",padding_left,height/4+height/4+80-15);
		g.drawString(low_c,padding_city_left,height/4+height/4+(60)-15);
		gccc.drawString("保底消费",padding_city_left,height/4+height/4+80-15);
		g.dispose();
		ImageIO.write(bufferedImage, "JPEG",new File(new_file));
		System.out.println(System.currentTimeMillis()-_start);
		return new Result(Result.OK, shareLink);
	}

	/**
	 * 计算文字像素长度
	 * @param text
	 * @return
	 */
	private static int getTextLength(String text){
		int textLength = text.length();
		int length = textLength;
		for (int i = 0; i < textLength; i++) {
			int wordLength = String.valueOf(text.charAt(i)).getBytes().length;
			if(wordLength > 1){
				length+=(wordLength-1);
			}
		}
		return length%2==0 ? length/2:length/2+1;
	}
}
