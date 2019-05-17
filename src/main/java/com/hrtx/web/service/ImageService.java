package com.hrtx.web.service;

import com.hrtx.common.weixin.api.AccessTokenApi;
import com.hrtx.common.weixin.api.WxConfig;
import com.hrtx.dto.Result;
import com.hrtx.global.Constants;
import com.hrtx.global.HttpUtil;
import com.hrtx.global.SystemParam;
import com.hrtx.global.Utils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.*;

@Service
public class ImageService {
	public static Logger log = LoggerFactory.getLogger(ImageService.class);

	public static File shareCardDemoFile=null;
	public static File shareLinkDemoFile=null;

	public static void main(String[] args) throws Exception{
//		new ImageService().createShareCardFile(null,"189-6590-2602","福建-福州","电信","乐语","189");
	}
	public static Result downloadXcxQr(String web_site,String path) throws Exception{
		WxConfig.init(SystemParam.get("AppID"),SystemParam.get("AppSecret"),web_site);
		String ACCESS_TOKEN=AccessTokenApi.getAccessToken().getAccessToken();
		if(StringUtils.isEmpty(ACCESS_TOKEN))return  new Result(Result.ERROR,"小程序码创建失败");
		JSONObject json=new JSONObject();
		json.put("path",path);
		json.put("width",300);
		json.put("is_hyaline",true);
		String root_path=SystemParam.get("upload_root_path")+"/temp/";
		String suffix="png";
		Result result=HttpUtil.doHttpPost2Download("https://api.weixin.qq.com/wxa/getwxacode?access_token="+ACCESS_TOKEN,json.toString(),"application/json","utf-8",512,root_path,suffix);
		if(result.getCode()==Result.OK)return  result;
		else return  new Result(Result.ERROR,"小程序码创建失败");
	}

	public Result createShareCardFile(String head_img_file,String nick_name,String promotion_tip,String share_page,String num,String city,String net_type,String tele_type,String low_c) throws Exception{
		long _start=System.currentTimeMillis();
		Result result=downloadXcxQr("",share_page);
		if(result.getCode()!=Result.OK)return result;
		String xcx_qr_path=String.valueOf(result.getData());
		String root_path = SystemParam.get("upload_root_path");
		String shareLinkDemo=SystemParam.get("card-demo-file");
		String suffix_v = shareLinkDemo.substring(shareLinkDemo.lastIndexOf(".") + 1).toLowerCase();//获得原文件的后缀
		String new_file_name ="card_"+Utils.randomNoByDateTime();//获得图片文件随机文件名
		String shareLink=new_file_name + "."+suffix_v;//获得新图片名称
		if(shareCardDemoFile==null)shareCardDemoFile=new File(root_path+"/"+Constants.UPLOAD_PATH_SHARE.getStringKey()+"/"+shareLinkDemo);
		String new_file=root_path+"/"+Constants.UPLOAD_PATH_SHARE.getStringKey()+"/"+shareLink;
		Image image = ImageIO.read(shareCardDemoFile);
		int width = image.getWidth(null);
		int height = image.getHeight(null);
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bufferedImage.createGraphics();
		g.drawImage(image,0,0, width, height, null);
		int padding_left=200;//左侧偏移
		//写入头像
		createIcon(g,head_img_file,80,96,80);
		//写入昵称
		g.setFont(new Font("宋体",Font.PLAIN,24));
		g.setColor(new Color(67,67,67));//颜色
		g.drawString(nick_name,padding_left,110);
		//写入推广语
		g.setFont(new Font("宋体",Font.PLAIN,20));
		g.setColor(new Color(100,100,100));//颜色
		g.drawString(promotion_tip,padding_left,140);
		//写入号码信息
		g.setFont(new Font("宋体",Font.BOLD,32));
		g.setColor(new Color(4,4,4));//颜色
		g.drawString(num,padding_left+30,240);
		g.drawString(city,padding_left+30,290);
		g.drawString(tele_type,padding_left+30,350);
		g.drawString(net_type,padding_left+30,400);
		//写入小程序码
		createIcon(g,xcx_qr_path,230,200,880);
		g.dispose();
		ImageIO.write(bufferedImage, "JPEG",new File(new_file));
		log.info(String.format("创建卡片分享耗时[%s]ms",(System.currentTimeMillis()-_start)));
		return new Result(Result.OK, shareLink);
	}
	public Result createShareCardFile(String head_img_file,String nick_name,String promotion_tip,String share_page) throws Exception{
		long _start=System.currentTimeMillis();
		Result result=downloadXcxQr("",share_page);
		if(result.getCode()!=Result.OK)return result;
		String xcx_qr_path=String.valueOf(result.getData());
		String root_path = SystemParam.get("upload_root_path");
		String shareLinkDemo=SystemParam.get("alone-demo-file");
		String suffix_v = shareLinkDemo.substring(shareLinkDemo.lastIndexOf(".") + 1).toLowerCase();//获得原文件的后缀
		String new_file_name ="card_"+Utils.randomNoByDateTime();//获得图片文件随机文件名
		String shareLink=new_file_name + "."+suffix_v;//获得新图片名称
		File shareCardDemoFile=new File(root_path+"/"+Constants.UPLOAD_PATH_SHARE.getStringKey()+"/"+shareLinkDemo);
		String new_file=root_path+"/"+Constants.UPLOAD_PATH_SHARE.getStringKey()+"/"+shareLink;
		Image image = ImageIO.read(shareCardDemoFile);
		int width = image.getWidth(null);
		int height = image.getHeight(null);
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bufferedImage.createGraphics();
		g.drawImage(image,0,0, width, height, null);
		int padding_left=200;//左侧偏移
		//写入头像
		createIcon(g,head_img_file,80,96,80);
		//写入昵称
		g.setFont(new Font("宋体",Font.PLAIN,24));
		g.setColor(new Color(67,67,67));//颜色
		g.drawString(nick_name,padding_left,110);
		//写入推广语
		g.setFont(new Font("宋体",Font.PLAIN,20));
		g.setColor(new Color(100,100,100));//颜色
		g.drawString(promotion_tip,padding_left,140);
		//写入小程序码
		createIcon(g,xcx_qr_path,230,255,750);
		g.dispose();
		ImageIO.write(bufferedImage, "JPEG",new File(new_file));
		log.info(String.format("创建专享分享卡耗时[%s]ms",(System.currentTimeMillis()-_start)));
		return new Result(Result.OK, shareLink);
	}
	public static Graphics2D createIcon(Graphics2D g,String icon_path,int incon_width,int icon_x,int icon_y) throws IOException{
		ImageIcon imgIcon=new ImageIcon(icon_path);
		Image img=imgIcon.getImage();
		int border = 1;//图片是一个圆型
		Ellipse2D.Double shape = new Ellipse2D.Double(border, border, incon_width - border * 2, incon_width - border * 2);
		BufferedImage formatAvatarImage = new BufferedImage(incon_width, incon_width, BufferedImage.TYPE_4BYTE_ABGR);//需要保留的区域
		Graphics2D graphics = formatAvatarImage.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setClip(shape);
		graphics.drawImage(img, border, border, incon_width - border * 2, incon_width - border * 2, null);
		float alpha=1f;
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,alpha));
		g.drawImage(formatAvatarImage,icon_x,icon_y,null);
		return g;
	}

	public Result createShareLinkFile(String num,String city,String net_type,String tele_type,String low_c) throws IOException{
		long _start=System.currentTimeMillis();
//		String root_path ="D:/test/";
//		String shareLinkDemo="shareLinkDemoFile.jpg";
		String root_path = SystemParam.get("upload_root_path");
		String shareLinkDemo=SystemParam.get("link-demo-file");
		String suffix_v = shareLinkDemo.substring(shareLinkDemo.lastIndexOf(".") + 1).toLowerCase();//获得原文件的后缀
		String new_file_name ="link_"+Utils.randomNoByDateTime();//获得图片文件随机文件名
		String shareLink=new_file_name + "."+suffix_v;//获得新图片名称
		if(shareLinkDemoFile==null)shareLinkDemoFile=new File(root_path+"/"+Constants.UPLOAD_PATH_SHARE.getStringKey()+"/"+shareLinkDemo);
		String new_file=root_path+"/"+Constants.UPLOAD_PATH_SHARE.getStringKey()+"/"+shareLink;
		int fontSize=20;
		Image image = ImageIO.read(shareLinkDemoFile);
		int width = image.getWidth(null);
		int height = image.getHeight(null);
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bufferedImage.createGraphics();
		g.drawImage(image,0,0, width, height, null);
		g.setFont(new Font("宋体",Font.BOLD,fontSize));
		g.setColor(new Color(251,251,251));//颜色
		int padding_left=160;//左侧偏移
		g.drawString(num,padding_left,228);
		g.drawString(city,padding_left,265);
		g.drawString(tele_type,padding_left,305);
		g.drawString(net_type,padding_left,340);

		g.dispose();
		ImageIO.write(bufferedImage, "JPEG",new File(new_file));
		log.info(String.format("创建链接图片分享耗时[%s]ms",(System.currentTimeMillis()-_start)));
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
