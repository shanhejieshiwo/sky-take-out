package com.sky.controller.admin;


import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.sky.result.Result;
import com.sky.utils.AliOSSUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

@RequestMapping("/admin/common")
@RestController
@Tag(name = "通用接口")
@Slf4j
public class CommonController {


    @Autowired
    private AliOSSUtils aliOSSUtils;


    @PostMapping("/upload")
    @Operation(summary = "文件上传")
    public Result<URL> upload(MultipartFile file) throws IOException {
        log.info("文件上传：{}",file);
        URL url = aliOSSUtils.upload(file);
        return Result.success(url);
    }

}








