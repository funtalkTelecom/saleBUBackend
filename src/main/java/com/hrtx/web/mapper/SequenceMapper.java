package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.Sequence;
import org.springframework.stereotype.Repository;

@Repository
public interface SequenceMapper extends Mapper<Sequence> {
    int nextVal(String seqName);
}