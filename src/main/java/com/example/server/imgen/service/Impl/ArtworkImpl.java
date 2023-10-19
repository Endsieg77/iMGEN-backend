package com.example.server.imgen.service.Impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.server.imgen.mapper.ArtworkMapper;
import com.example.server.imgen.pojo.ArtWork;
import com.example.server.imgen.service.IArtworkService;

@Service
public class ArtworkImpl extends ServiceImpl<ArtworkMapper, ArtWork> implements IArtworkService {

}