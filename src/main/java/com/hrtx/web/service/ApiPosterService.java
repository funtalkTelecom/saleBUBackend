package com.hrtx.web.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.PowerConsts;
import com.hrtx.global.SystemParam;
import com.hrtx.web.mapper.MealMapper;
import com.hrtx.web.mapper.PosterMapper;
import com.hrtx.web.pojo.Meal;
import com.hrtx.web.pojo.Poster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


@Service
public class ApiPosterService {

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private PosterMapper posterMapper;

	/**
	 * 海报接口-根据海报位置查询有效海报
	 * @param poster
	 * @param request
	 * @return
	 */
	public Result posterList(Poster poster, HttpServletRequest request){
		PageInfo<Object> pm = null;
		try {
//			int pageNum = request.getParameter("pageNum")==null?1: Integer.parseInt(request.getParameter("pageNum"));
//			int limit = request.getParameter("limit")==null?15: Integer.parseInt(request.getParameter("limit"));
//			poster.setStart(limit*(pageNum-1));
//			poster.setLimit(limit);

			PageHelper.startPage(poster.getPageNum(),poster.getLimit());
			Page<Object> ob=this.posterMapper.queryPageListApi(poster);
			if(ob!=null && ob.size()>0){
				for (Object obj : ob) {
					Poster p = (Poster) obj;
					p.setPic(SystemParam.get("domain-full") + "/get-img/posterImages/" + p.getPic());
				}
			}
			pm = new PageInfo<Object>(ob);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			pm = new PageInfo<Object>(null);
			return new Result(Result.ERROR, pm);
		}

		return new Result(Result.OK, pm);
	}
}
