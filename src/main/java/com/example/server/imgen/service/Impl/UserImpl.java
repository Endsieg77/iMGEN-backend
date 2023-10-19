package com.example.server.imgen.service.Impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.server.imgen.mapper.UserMapper;
import com.example.server.imgen.pojo.User;
import com.example.server.imgen.service.IUserService;

@Service
public class UserImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}