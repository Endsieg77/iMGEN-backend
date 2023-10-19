package com.example.server.imgen.vo;

import lombok.Data;

@Data
public class ImageInfo {

    public ImageInfo(String path, String name) {
        this.path = path;
        this.name = name;
    }
    
    private String path;
    private String name;
    
}
