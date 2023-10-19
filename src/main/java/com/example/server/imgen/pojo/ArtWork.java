package com.example.server.imgen.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("imgen_gallery")
public class ArtWork {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String email;
    private int category;
    private String name;
    private String illustrator;
}
