package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.Dict;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface DictMapper extends Mapper<Dict>,BaseMapper<Dict>{
    Dict findDictInfo(@Param("id") Integer id);

    int checkDictKeyIdIsExist(Dict dict);

    void dictEdit(Dict dict);

    void dictDelete(Dict dict);

    void insertBatch(@Param("dictList") List<Dict> list);

    Dict findGroupMaxInfo(@Param("keyGroup")String keyGroup);

    List findDictByGroup(@Param("keyGroup")String group);

    List findDictByGroupAndCorpId(@Param("keyGroup")String group, @Param("corpId") Integer corpId);

    List findDictByTypeGroup(@Param("keyGroup")String group);

    Map maxSeqAndKeyId(@Param("keyGroup") String keyGroup);

    List findDictByValue(@Param("keyGroup") String keyGroup,@Param("keyValue") String keyValue);
}
