package com.example.server.imgen.vo;

import lombok.Data;

@Data
public class ResetPwd {
    private String session;
    private String pwd;
}
