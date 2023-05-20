package com.example.mapper;

import com.example.entity.Account;
import com.example.entity.AccountUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * separate-project.com.example.mapper
 *
 * @description 用户Mapper
 */
@Mapper
public interface UserMapper {
    /**
     * 根据用户名或邮箱查询用户信息
     *
     * @param userOrEmail 用户名或邮箱
     * @return 用户信息
     */
    @Select("select * from account where username = #{userOrEmail} or email = #{userOrEmail}")
    Account findAccountByUserOrEmail(String userOrEmail);

    /**
     * 根据用户名或邮箱查询用户详细信息
     *
     * @param userOrEmail 用户名或邮箱
     * @return 用户信息
     */
    @Select("select * from account where username = #{userOrEmail} or email = #{userOrEmail}")
    AccountUser findAccountUserByUserOrEmail(String userOrEmail);

    /**
     * 注册
     */
    @Insert("insert into account (id,email, username, password) values (id=2,#{email}, #{username}, #{password})")
    int createAccount(String email, String username, String password);

    /**
     * 重置密码
     */
    @Update("update account set password = #{password} where email = #{email}")
    int resetPasswordByEmail(String password, String email);
}
