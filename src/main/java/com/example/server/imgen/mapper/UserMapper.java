package com.example.server.imgen.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.server.imgen.pojo.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
