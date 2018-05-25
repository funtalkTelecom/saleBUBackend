package com.hrtx.global;

import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageUtils {

    public final static void scale(String srcpath, String result, int width, int height, boolean kar) throws Exception {
        Thumbnails.of(srcpath)
//		        .scale(0.25f)           //按比例
                .size(width, height)    //指定大小
//		        .watermark(Positions.BOTTOM_RIGHT, ImageIO.read(new File("images/watermark.png")), 0.5f) //水印watermark(位置，水印图，透明度)
                .outputQuality(1f)    //输出质量
                .keepAspectRatio(kar) //默认是按照比例缩放的
//		        .rotate(90)             //(角度),正数：顺时针 负数：逆时针
//		        .outputFormat("gif")    //转换格式
//		        .sourceRegion(x, y, width_r, height_r) //指定坐标裁剪(x, y, w, h)
                .toFile(result);
    }

    public final static void scaleByRegion(String srcpath, String result, int width, int height, int x, int y, int width_r, int height_r) throws Exception {
        Thumbnails.of(srcpath)
                .size(width, height)    //指定大小
                .outputQuality(1f)    //输出质量
                .sourceRegion(x, y, width_r, height_r) //指定坐标裁剪(x, y, w, h)
                .toFile(result);
    }

    public final static boolean contains(String suffix) {
        String[] imagesSuffixs={"gif","jpg","jpeg","png","bmp"};
        for(String v:imagesSuffixs){
            if(suffix.equals(v)){
                return true;
            }
        }
        return false;
    }

    public static int getHeight(File file) {
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(file);
            return bi.getHeight();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public static int getWidth(File file) {
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(file);
            return bi.getWidth();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static double getRatio(File file) {
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(file);
            double width = bi.getWidth();
            return width/bi.getHeight();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
