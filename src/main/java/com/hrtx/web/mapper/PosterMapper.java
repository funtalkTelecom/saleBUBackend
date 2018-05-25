package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.Poster;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PosterMapper extends Mapper<Poster>,BaseMapper<Poster>{
    Poster findPosterInfo(@Param("id") Long id);

    int checkPosterKeyIdIsExist(Poster poster);

    void posterEdit(Poster poster);

    void posterDelete(Poster poster);

    void insertBatch(@Param("posterList") List<Poster> list);
}
