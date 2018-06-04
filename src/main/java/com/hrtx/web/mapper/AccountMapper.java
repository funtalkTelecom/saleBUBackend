package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.Account;
import com.hrtx.web.pojo.Meal;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AccountMapper extends Mapper<Account>,BaseMapper<Account>{

    void accountEdit(Account account);

    void insertAccount(@Param("accountList") List<Account> list);

    Account findAccountInfo(@Param("id") Long id);

    void accountDelete(Account account);

}
