package com.cat.coin.coincatmanager.mapper;

import com.cat.coin.coincatmanager.controller.vo.ResetPasswordVo;
import com.cat.coin.coincatmanager.domain.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User getUserByName(String name);
    User getUserByEmail(String email);
    int registerUser(User user);

    void resetPassword(ResetPasswordVo resetPasswordVo);
}
