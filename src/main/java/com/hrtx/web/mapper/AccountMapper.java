package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.github.pagehelper.Page;
import com.hrtx.web.pojo.Account;
import com.hrtx.web.pojo.Meal;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface AccountMapper extends Mapper<Account>,BaseMapper<Account>{

    void accountEdit(Account account);

    void insertAccount(@Param("accountList") List<Account> list);

    Account findAccountInfo(@Param("id") Integer id);

    void accountDelete(Account account);

    List<Map> findAccountListByUserId(@Param("addUserId") Integer addUserId);

}
