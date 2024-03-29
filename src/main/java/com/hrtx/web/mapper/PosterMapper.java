package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.github.pagehelper.Page;
import com.hrtx.web.pojo.Poster;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public interface PosterMapper extends Mapper<Poster>,BaseMapper<Poster>{
    Poster findPosterInfo(@Param("id") Integer id);

    int checkPosterKeyIdIsExist(Poster poster);

    void posterEdit(Poster poster);

    void posterDelete(Poster poster);

    void insertBatch(@Param("posterList") List<Poster> list);

    Page<Object> queryPageListApi(@Param("param") Poster poster);
}
