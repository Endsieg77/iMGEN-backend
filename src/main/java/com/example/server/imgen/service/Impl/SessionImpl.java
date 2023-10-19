package com.example.server.imgen.service.Impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.server.imgen.mapper.SessionMapper;
import com.example.server.imgen.pojo.imgenSession;
import com.example.server.imgen.service.ISessionService;

@Service
public class SessionImpl extends ServiceImpl<SessionMapper, imgenSession> implements ISessionService {

}