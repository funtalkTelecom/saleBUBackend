package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.github.pagehelper.Page;
import com.hrtx.web.pojo.Meal;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public interface MealMapper extends Mapper<Meal>,BaseMapper<Meal>{

    Meal findMealInfo(@Param("mealid") String mealid);

    void insertBatch(@Param("mealList") List<Meal> mealList);

    Meal findMealById(@Param("mid") Integer id);

    void mealEdit(Meal meal);

    int checkMealIdIsExist(Meal meal);

    void mealDelete(Meal meal);

    Page<Object> queryPageListApi(Meal meal);

    List getMealListByNum(@Param("id") String id);

    List<Integer> mealListByNum(@Param("phone") String phone);

    List getMeal(@Param("id") String id);

}
