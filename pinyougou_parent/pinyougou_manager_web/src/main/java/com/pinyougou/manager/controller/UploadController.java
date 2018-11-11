package com.pinyougou.manager.controller;

import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import util.FastDFSClient;

@RestController
@RequestMapping("/upload")
public class UploadController {

    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;

    @RequestMapping("/uploadFile")
    public Result uploadFile(MultipartFile file){
    
        try {
            //获取源文件名
            String originalFilename = file.getOriginalFilename();
            String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            //基于fastDFS工具类实现上传功能
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");
            //文件上传
            String path = fastDFSClient.uploadFile(file.getBytes(), extName);

            //图片访问地址
            String url =FILE_SERVER_URL+path;

            return new Result(true,url);

        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }

    }


}
