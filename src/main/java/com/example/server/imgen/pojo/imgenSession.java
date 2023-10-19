package com.example.server.imgen.pojo;

import java.sql.Timestamp;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("imgen_session")
public class imgenSession {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String session;
    private Timestamp createTime;
    private String name;
    private String email;
    private byte[] salt;
    private String hash;
    private String typeof;
}
