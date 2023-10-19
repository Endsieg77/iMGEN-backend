package com.example.server.imgen.pojo;

import java.sql.Timestamp;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("imgen_user")
public class User {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String name;
    private String email;
    private byte[] salt;
    private String hash;
    private String token;
    
    // @TableField(fill = FieldFill.INSERT_UPDATE)
    private Timestamp lastLogin;
}
