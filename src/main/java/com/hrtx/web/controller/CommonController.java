package com.hrtx.web.controller;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hrtx.dto.Result;
import com.hrtx.global.Arith;
import com.hrtx.global.SystemParam;
import com.hrtx.web.service.CityService;
import com.hrtx.web.service.DictService;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.hrtx.config.annotation.Powers;
import com.hrtx.global.PowerConsts;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hrtx.global.ImageZipUtil.zipWidthHeightImageFile;

@Controller
public class CommonController extends BaseReturn{
	@Resource
	private CityService cityService;
	@Resource
	private DictService dictService;

    @RequestMapping("query-city")
    @Powers( { PowerConsts.NOLOGINPOWER })
    @ResponseBody
    public Object queryCity(HttpServletRequest request) {
        String pid_=request.getParameter("pid");
        Object list=cityService.queryByPidList(NumberUtils.toInt(pid_,0));
        return list;
    }

    @RequestMapping("query-city-ztree")
    @Powers( { PowerConsts.NOLOGINPOWER })
    @ResponseBody
    public Object queryCityZtree(HttpServletRequest request) {
        String pid_=request.getParameter("pid");
        String isopen=request.getParameter("isopen");
        Object list=cityService.queryByPidListForZtree(NumberUtils.toInt(pid_,0), isopen);
        return list;
    }

    @GetMapping("/api/citys")
    @Powers( { PowerConsts.NOLOGINPOWER })
    @ResponseBody
    public Object ApiCitys(HttpServletRequest request) {
        Object list= null;
        try {
            list = cityService.queryCitys();
        } catch (Exception e) {
            log.error("获取地市异常",e);
            return new Result(Result.ERROR,"暂时无法获取地市");
        }
        return new Result(Result.OK,list);
    }

    @RequestMapping("query-third-city")
    @Powers( { PowerConsts.NOLOGINPOWER })
    @ResponseBody
    public Object queryThirdCity(HttpServletRequest request) {
        String third=request.getParameter("third");
        Object list=cityService.queryByThird(third);
        return list;
    }

    @RequestMapping("dict-query-group")
    @Powers( { PowerConsts.NOLOGINPOWER })
    @ResponseBody
    public Object dictQueryGroup(HttpServletRequest request) {
        String group=request.getParameter("group");
        Object list = dictService.findDictByGroup(group);
        return list;
    }

    @RequestMapping("dict-to-map")
    @Powers( { PowerConsts.NOLOGINPOWER })
    @ResponseBody
    public Object dictToMap(HttpServletRequest request) {
        String group=request.getParameter("group");
        List list = dictService.findDictByGroup(group);
        Map map = new HashMap<>();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Map m = (Map) list.get(i);
                map.put(m.get("keyId"), m.get("keyValue"));
            }
        }
        return map;
    }

    @RequestMapping("type-group-dict")
    @Powers( { PowerConsts.NOLOGINPOWER })
    @ResponseBody
    public Object typeGroupDict(HttpServletRequest request) {
        String group=request.getParameter("group");
        Object list = dictService.findDictByTypeGroup(group);
        return list;
    }

	@RequestMapping("/{str}")
    @Powers({PowerConsts.NOLOGINPOWER})
    public String pageModel(@PathVariable String str, HttpServletRequest request) {
        return "admin/"+str;
    }
	
	@RequestMapping("/{str1}/{str2}")
	@Powers({PowerConsts.NOLOGINPOWER})
	public String pageModel(@PathVariable String str1, @PathVariable String str2, HttpServletRequest request) {
		return "admin/"+str1+"/"+str2;
	}

    @RequestMapping("/get-file/{str}/{str1:.+}")
    @Powers({PowerConsts.NOLOGINPOWER})
    public void getImg(@PathVariable String str,  @PathVariable String str1, HttpServletResponse response) {
        this.downLoadFile(str+"/"+str1, response);
    }

    @RequestMapping("/get-img/{str:.+}")
    @Powers({PowerConsts.NOLOGINPOWER})
    public void getImg(@PathVariable String str, HttpServletResponse response) {
        this.downLoadImg(str, response);
    }

    @RequestMapping("/get-img/{str}/{str1:.+}")
    @Powers({PowerConsts.NOLOGINPOWER})
    public void getImg1(@PathVariable String str, @PathVariable String str1, HttpServletResponse response) {
        this.downLoadImg(str+"/"+str1, response);
    }

    @RequestMapping("/get-img/{str}/{str1}/{str2:.+}")
    @Powers({PowerConsts.NOLOGINPOWER})
    public void getImg2(@PathVariable String str, @PathVariable String str1,  @PathVariable String str2, HttpServletResponse response) {
        this.downLoadImg(str+"/"+str1+"/"+str2, response);
    }
    @RequestMapping("/get-img/{str}/{str1}/{str2}/{str3:.+}")
    @Powers({PowerConsts.NOLOGINPOWER})
    public void getImg3(@PathVariable String str, @PathVariable String str1,  @PathVariable String str2,@PathVariable String str3, HttpServletResponse response) {
        this.downLoadImg(str+"/"+str1+"/"+str2+"/"+str3, response);
    }

    @RequestMapping("/kindeditorUploadFile")
    @Powers({PowerConsts.NOLOGINPOWER})
    @ResponseBody
    public Map kindeditorUploadFile(@RequestParam(name = "imgFile",required = false)MultipartFile file, HttpServletResponse response) {
        Map resultMap = new HashMap();
        try {
            Result result = uploadFile(SystemParam.get("kindeditorPicDir"), "jpg,png,gif", file, false, false);
            if(result.getCode()!=200){
                resultMap.put("error", 1);
                resultMap.put("message", "上传失败"+result.getData());
            }else{
                String sourceServerFileName = ((Map) result.getData()).get("sourceServerFileName").toString();
                BufferedImage image = ImageIO.read(file.getInputStream());
                //图片宽度大于1000进行等比例压缩
                if(image.getWidth()>1000) {
                    double xs = 1000D/image.getWidth();
                    String rootPath = SystemParam.get("upload_root_path") + SystemParam.get("kindeditorPicDir");
                    int width, height;
                    width = ((Double)Arith.add(image.getWidth()*xs, 0D)).intValue();
                    height = ((Double)Arith.add(image.getHeight()*xs, 0D)).intValue();
                    sourceServerFileName = zipWidthHeightImageFile(new File(rootPath + sourceServerFileName), new File(rootPath + "zip" + sourceServerFileName), width, height, 0.7f);
                }
                resultMap.put("error", 0);
                resultMap.put("url", SystemParam.get("domain-full") + "/get-img" + SystemParam.get("kindeditorPicDir") + sourceServerFileName);
                resultMap.put("message", "上传成功");

            }
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("error", 1);
            resultMap.put("message", "上传失败");
        }
        return resultMap;
    }
}
