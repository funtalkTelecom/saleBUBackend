package com.hrtx.web.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.dto.Result;
import com.hrtx.global.SystemParam;
import com.hrtx.global.Utils;
import com.hrtx.web.mapper.PosterMapper;
import com.hrtx.web.pojo.Poster;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


@Service
public class PosterService {
	
	@Autowired
	private PosterMapper posterMapper;

	public Result pagePoster(Poster poster) {
		PageHelper.startPage(poster.startToPageNum(),poster.getLimit());
		Page<Object> ob=this.posterMapper.queryPageList(poster);
		PageInfo<Object> pm = new PageInfo<Object>(ob);
		return new Result(Result.OK, pm);
	}

	public Poster findPosterById(Integer id) {
		Poster poster = posterMapper.findPosterInfo(id);
		return poster;
	}

	public Result posterEdit(Poster poster, MultipartFile file, HttpServletRequest request) {
		try {
		    if(file!=null){
//				request.getServletContext().getRealPath("/WEB-INF/static/admin/posterImages/")
		        String path = SystemParam.get("posterImgPath");
                Result result = this.uploadFile(path, "jpg,png,gif", file, false, false);
                if(result.getCode()==Result.OK) poster.setPic(((Map) result.getData()).get("sourceServerFileName").toString());
                else return result;
            }
		} catch (IOException e) {
			e.printStackTrace();
			return new Result(Result.ERROR, "图片保存异常,请稍后再试");
		}

		if (poster.getId() != null && poster.getId() > 0) {
				posterMapper.posterEdit(poster);
		} else {
			poster.setId(posterMapper.getId());
			posterMapper.insert(poster);
		}
		return new Result(Result.OK, "提交成功");
	}

	private boolean PosterKeyIdIsExist(Poster poster) {
		boolean b = true;
		int num = this.posterMapper.checkPosterKeyIdIsExist(poster);
		if(num <= 0) b=false;

		return b;
	}

	public Result posterDelete(Poster poster) {
		posterMapper.posterDelete(poster);
		return new Result(Result.OK, "删除成功");
	}

	private static char mapTable[] = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','V','W','S','X','Y','Z'};
	private Result uploadFile(String projectRealPath, String file_suffix_s, MultipartFile file, boolean b, boolean b1) throws IOException {
		// ==========验证文件后缀start==========//
		if(file == null || file.isEmpty()) return new Result(Result.ERROR, "请选择上传的文件");
		String originalFilename = file.getOriginalFilename();
		String suffix_v = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
		if(!(","+file_suffix_s+",").contains(","+suffix_v+",")){
			return new Result(Result.ERROR, "请上次格式为["+file_suffix_s+"]的文件");
		}
		// ==========验证文件后缀end==========//
		Map<String, Object> map = new HashMap<>();
		String bname = this.randomNoByDateTime();
		String sourceServerFileName = bname + "."+suffix_v;
		map.put("sourceServerFileName", sourceServerFileName);  //服务器原文件名称
		map.put("sourceFileName", originalFilename);			//上传文件名
		String fullUrl = projectRealPath + sourceServerFileName;
		File outDir = new File(projectRealPath);
		if (!outDir.exists())  outDir.mkdirs();
		InputStream is = file.getInputStream();
		IOUtils.copy(is, new FileOutputStream(fullUrl));
		if(is!=null) is.close();
//		File originalFile = new File(fullUrl);
//		FileUtils.copyInputStreamToFile(file.getInputStream(), originalFile);

		return new Result(Result.OK, map);
	}


	/**
	 * C + 26个英文 中的1个(随机)+MMddHHmm(时间格式)+ 1位数字(随机)+3位自增数(超过3位归为零)
	 * @return
	 */
	public static String randomNoByDateTime(){
		StringBuffer str=new StringBuffer(15);
		str.append("C");
		str.append(mapTable[(int)(mapTable.length * Math.random())]);
		str.append(Utils.getCurrentDate("yy").substring(1, 2));
		str.append(Utils.getCurrentDate("MMddHHmm"));
		str.append(new Random().nextInt(9));
		str.append(StringUtils.leftPad(String.valueOf(countSeq()),3, "0"));
		return str.toString();
	}

	/**
	 * 3位自增数(超过3位归为归零)
	 * @return
	 */
	private static int count = 0;
	private static synchronized Integer countSeq(){
		if(count>990)count=0;
		count++;
		return count;
	}
}
