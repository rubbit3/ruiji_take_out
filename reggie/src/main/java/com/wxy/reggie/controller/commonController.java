package com.wxy.reggie.controller;

import com.wxy.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/common")
public class commonController {


    @Value("${reggie.path}")
    private String basePath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        //file 是一个临时文件，需要及时保存，不然会话结束文件就会消失。
        log.info("Uploading file " + file.getName());
//        file.transferTo(new File());将文件惊进行转存,指定位置，参数


//        file.getOriginalFilename(),文件原始名字，使用uuid防止文件重复覆盖
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));//文件后缀名，使用.分割出来

        System.out.println(suffix);
        String fileName = UUID.randomUUID().toString() + suffix;

//        创建文件目录
        File dir = new File(basePath);
        if (!dir.exists()) {
            //目录不存在需要创建
            dir.mkdirs();
        }
        try {
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);

    }

    /**
     * 文件下载
     *
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        //输入流对象读取文件内容
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
            //输出流对象把文件内容写回到浏览器
            ServletOutputStream outputStream = response.getOutputStream();
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }

            // close the output stream
            outputStream.close();
            //close the file input
            fileInputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
