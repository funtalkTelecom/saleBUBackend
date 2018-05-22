package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.Meal;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MealMapper extends Mapper<Meal>,BaseMapper<Meal>{

    Meal findMealInfo(@Param("mealid") String mealid);

    void insertBatch(@Param("mealList") List<Meal> mealList);

    Meal findMealById(@Param("mid") Long id);

    void mealEdit(Meal meal);

    int checkMealIdIsExist(Meal meal);

    void mealDelete(Meal meal);
}
