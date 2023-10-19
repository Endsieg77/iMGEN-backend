package com.example.server.imgen.controller;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import com.example.server.imgen.service.IStorageService;
import com.example.server.imgen.vo.ImageInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/")
@Slf4j
public class commonController {
    @Autowired
    private IStorageService storage;

    private final String uploadDir = "e://web_app/image/";

    @PostMapping("/upload")
    public ResponseEntity upload(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        final var userMail = request.getParameter("userMail");
        final var image    = multipartRequest.getFile("image");
        final var fileName = image.getOriginalFilename();
        // log.info(userName);

        if (storage.save(image, fileName, uploadDir + userMail + '/'))
        {
            ImageInfo imgInfo = new ImageInfo(uploadDir, fileName);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(imgInfo);
        } else {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(null);
        }
    }
}
