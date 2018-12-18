package com.hrtx.web.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.dto.Result;
import com.hrtx.global.ApiSessionUtil;
import com.hrtx.web.mapper.IccidMapper;
import com.hrtx.web.mapper.NumMapper;
import com.hrtx.web.mapper.OrderMapper;
import com.hrtx.web.pojo.Iccid;
import com.hrtx.web.pojo.Num;
import com.hrtx.web.pojo.Order;
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
	@Autowired
	private OrderMapper orderMapper;

	public Num findNumById(Integer Id) {
		return numMapper.selectByPrimaryKey(Id);
	}

	public Iccid findIccidByIccid(String iccidStr) {
		Iccid iccid=new Iccid();
		iccid.setIccid(iccidStr);
		return iccidMapper.selectOne(iccid);
	}

	public  void orderSign(Order order)
	{
		orderMapper.signByOrderid(order);
	}

	public void bindNum(Num num) {
		numMapper.updateBoundNum(num);
	}

	public void iccidEditStatus(Iccid iccid) {
		iccidMapper.iccidEditStatus(iccid);
	}

	/*
	  status4 未绑定 5,6,7,9已绑定
	 */
	public Result numBoundList(String status, HttpServletRequest request){
		Num num=new Num();
		PageInfo<Object> pm = null;
		Result result = null;
		String statusStr="";
		int pageNum=0;
		int limit=0;
		try {
			pageNum=request.getParameter("pageNum")==null?1: Integer.parseInt(request.getParameter("pageNum"));
			limit=request.getParameter("limit")==null?15: Integer.parseInt(request.getParameter("limit"));
            if(status.equals("0"))
			{
				statusStr="4";
			}else if(status.equals("1"))
			{
				statusStr="5,6,7,9";
			}
			//模拟登陆
//			Consumer u = new Consumer();
//			u.setId(1L);
//			u.setName("周元强");
//			u.setCity("396");
//			u.setIsAgent(2);//设置为一级代理商
//			u.setAgentCity(396L);
			//apiSessionUtil.getConsumer()==null?u.getAgentCity():

			num.setPageNum(pageNum);
			num.setLimit(limit);
			num.setStart(limit*(pageNum-1));
			PageHelper.startPage(pageNum,limit);
			if(status.equals("0"))
			{
				statusStr="4";
				Page<Object> ob=this.numMapper.queryPageNumList2(num,apiSessionUtil.getConsumer().getId(),statusStr);
				pm = new PageInfo<Object>(ob);
			}else if(status.equals("1"))
			{
				statusStr="5,6,7,9";
				Page<Object> ob=this.numMapper.queryPageNumList(num,apiSessionUtil.getConsumer().getId(),statusStr);
				pm = new PageInfo<Object>(ob);
			}
			result = new Result(Result.OK, pm);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			pm = new PageInfo<Object>(null);
			result = new Result(Result.ERROR, pm);
		}
		return result;
	}

}
