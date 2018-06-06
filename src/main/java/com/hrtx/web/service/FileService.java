package com.hrtx.web.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.dto.Result;
import com.hrtx.web.mapper.FileMapper;
import com.hrtx.web.pojo.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class FileService {

	@Autowired
	private FileMapper fileMapper;

	public Result pageFile(File file) {
		PageHelper.startPage(file.getPageNum(),file.getLimit());
		Page<Object> ob=this.fileMapper.queryPageList(file);
		PageInfo<Object> pm = new PageInfo<Object>(ob);
		return new Result(Result.OK, pm);
	}

	public File findFileById(Long id) {
		File file = fileMapper.findFileInfo(id);
		return file;
	}

	public Result fileDelete(File file) {
		fileMapper.fileDelete(file);
		return new Result(Result.OK, "删除成功");
	}

	public Result deleteFilesByRefid(String refid, String picSeqs) {
		fileMapper.deleteFilesByRefid(refid, picSeqs);
		return new Result(Result.OK, "删除成功");
	}

    public List findFilesByRefid(String refid) {
		return fileMapper.findFilesByRefid(refid);
	}
}
