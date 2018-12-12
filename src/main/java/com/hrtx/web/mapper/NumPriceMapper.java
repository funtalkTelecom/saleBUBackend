package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.NumPrice;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface NumPriceMapper extends BaseMapper<NumPrice>,Mapper<NumPrice> {
    long countList(@Param("param") NumPrice numPrice);

    long matchNumPrice();
}