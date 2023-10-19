package com.example.server.imgen.vo;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("imgen_gallery")
public class ArtWorkVo {
    private String label;
    private String path;
    private String illustrator;
}