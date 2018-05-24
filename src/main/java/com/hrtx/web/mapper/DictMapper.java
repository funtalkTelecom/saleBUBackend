package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.Dict;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DictMapper extends Mapper<Dict>,BaseMapper<Dict>{
    Dict findDictInfo(@Param("id") Long id);

    int checkDictKeyIdIsExist(Dict dict);

    void dictEdit(Dict dict);

    void dictDelete(Dict dict);

    void insertBatch(@Param("dictList") List<Dict> list);

    void dictAudit(Dict dict);

    Dict findGroupMaxInfo(@Param("keyGroup")String keyGroup);
}
