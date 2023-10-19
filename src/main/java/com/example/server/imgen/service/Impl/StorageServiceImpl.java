package com.example.server.imgen.service.Impl;

import java.io.File;
import java.io.IOException;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import com.example.server.imgen.service.IStorageService;

@Service
public class StorageServiceImpl implements IStorageService {

    @Override
    public Boolean save(MultipartFile file, String fileName, String filePath)
    {
        String path = filePath + fileName;
        File targetFile = new File(path);

        if (!targetFile.exists()) {
            targetFile.getParentFile().mkdirs();
        }

        try {
            FileCopyUtils.copy(file.getBytes(), targetFile);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
}
