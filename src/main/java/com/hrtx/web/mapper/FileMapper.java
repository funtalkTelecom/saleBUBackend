package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.File;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface FileMapper extends Mapper<File>,BaseMapper<File>{
    File findFileInfo(@Param("id") Long id);

    void fileDelete(File file);

    void insertBatch(@Param("fileList") List<File> list);

    void deleteFilesByRefid(@Param("refid") String refid, @Param("picSeqs") String picSeqs);

    List findFilesByRefid(@Param("refid") String refid);
}
