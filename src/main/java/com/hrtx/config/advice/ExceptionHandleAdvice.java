package com.hrtx.config.advice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.hrtx.config.Utils;
import com.hrtx.dto.Result;

@ControllerAdvice
public class ExceptionHandleAdvice {

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@ExceptionHandler(ServiceException.class)
	public Object serviceException(Exception e,HttpServletRequest request,HttpServletResponse response){
		long err_no=System.currentTimeMillis();
		String errorInfo = String.format(e.getMessage()+"[错误编号：%s]",err_no);
		log.error(errorInfo,e);
		if(Utils.isAjax(request)){
			Utils.returnResult(new Result(Result.ERROR, errorInfo));
			return null;
		}
		ModelAndView v=new ModelAndView("admin/error-page");
		v.addObject("errormsg",errorInfo);
		return v;
	}

	@ExceptionHandler(WarmException.class)
	public Object warmException(Exception e,HttpServletRequest request,HttpServletResponse response){
		long err_no=System.currentTimeMillis();
		String errorInfo = e.getMessage();
		log.error(errorInfo,e);
		if(Utils.isAjax(request)){
			Utils.returnResult(new Result(Result.ERROR, errorInfo));
			return null;
		}
		ModelAndView v=new ModelAndView("admin/error-page");
		v.addObject("errormsg",errorInfo);
		return v;
	}

	@ExceptionHandler(Exception.class)
//	@InitBinder
//	@ModelAttribute
//	@Conditional(WindowsCondition.class)
	public Object exception(Exception e,HttpServletRequest request,HttpServletResponse response){
		long err_no=System.currentTimeMillis();
		String errorInfo = String.format("系统出现错误[错误编号%s]",err_no);
		log.error(errorInfo,e);
		if(Utils.isAjax(request)){
			Utils.returnResult(new Result(Result.ERROR, errorInfo));
			return null;
		}
		ModelAndView v=new ModelAndView("admin/error-page");
		v.addObject("errormsg",errorInfo);
		return v;
	}
	

}
