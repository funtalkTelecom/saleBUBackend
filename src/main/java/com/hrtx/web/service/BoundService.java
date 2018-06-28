package com.hrtx.web.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.dto.Result;
import com.hrtx.global.ApiSessionUtil;
import com.hrtx.web.mapper.IccidMapper;
import com.hrtx.web.mapper.NumMapper;
import com.hrtx.web.pojo.Iccid;
import com.hrtx.web.pojo.Num;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class BoundService {

	@Autowired private ApiSessionUtil apiSessionUtil;
	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private NumMapper numMapper;
	@Autowired
	private IccidMapper iccidMapper;

	public Num findNumById(Long Id) {
		return numMapper.selectByPrimaryKey(Id);
	}

	public Iccid findIccidById(Long Id) {
		return iccidMapper.selectByPrimaryKey(Id);
	}

	public void bindNum(Num num) {
		numMapper.updateBoundNum(num);
	}

	public void iccidEditStatus(Iccid iccid) {
		iccidMapper.iccidEditStatus(iccid);
	}

	/*
	  status4 未绑定
	 */
	public Result numUnBoundList(Num num, HttpServletRequest request){
		PageInfo<Object> pm = null;
		Result result = null;
		try {
			num.setPageNum(request.getParameter("pageNum")==null?1: Integer.parseInt(request.getParameter("pageNum")));
			num.setLimit(request.getParameter("limit")==null?15: Integer.parseInt(request.getParameter("limit")));

			//模拟登陆
//			Consumer u = new Consumer();
//			u.setId(1L);
//			u.setName("周元强");
//			u.setCity("396");
//			u.setIsAgent(2);//设置为一级代理商
//			u.setAgentCity(396L);
			//apiSessionUtil.getConsumer()==null?u.getAgentCity():

			PageHelper.startPage(num.getPageNum(),num.getLimit());
			Page<Object> ob=this.numMapper.queryPageNumList(num,apiSessionUtil.getConsumer().getId(),"4");
			pm = new PageInfo<Object>(ob);
			result = new Result(Result.OK, pm);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			pm = new PageInfo<Object>(null);
			result = new Result(Result.ERROR, pm);
		}
		return result;
	}

	/*
	  status  5,6,7 已绑定
	 */
	public Result numEndBoundList(Num num, HttpServletRequest request){
		PageInfo<Object> pm = null;
		Result result = null;
		try {
			num.setPageNum(request.getParameter("pageNum")==null?1: Integer.parseInt(request.getParameter("pageNum")));
			num.setLimit(request.getParameter("limit")==null?15: Integer.parseInt(request.getParameter("limit")));

			//模拟登陆
//			Consumer u = new Consumer();
//			u.setId(1L);
//			u.setName("周元强");
//			u.setCity("396");
//			u.setIsAgent(2);//设置为一级代理商
//			u.setAgentCity(396L);
			//apiSessionUtil.getConsumer()==null?u.getAgentCity():

			PageHelper.startPage(num.getPageNum(),num.getLimit());
			Page<Object> ob=this.numMapper.queryPageNumList(num,apiSessionUtil.getConsumer().getId(),"5,6,7");
			pm = new PageInfo<Object>(ob);
			result = new Result(Result.OK, pm);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			pm = new PageInfo<Object>(null);
			result = new Result(Result.ERROR, pm);
		}
		return result;
	}
}
