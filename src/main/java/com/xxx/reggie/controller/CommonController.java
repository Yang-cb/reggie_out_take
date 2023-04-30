package com.xxx.reggie.controller;

import com.xxx.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * reggie_take_out.com.xxx.reggie.controller
 *
 * @author yang_
 * @description 通用的 controller ，主要用于文件的上传和下载
 * @date 2023/4/20 21:59
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.img-path}")
    private String imgPath;


    /**
     * 上传文件
     *
     * @param file 参数名必须要与前端表单 name 一致（name = file）
     * @return 将最终生成的文件名返回，文件名需要保存到数据库中
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        // file是一个临时文件，需要转存到一个位置。否则当本次请求结束时，file会被销毁。
        log.info("file => {}", file.toString());
        //1，获取file的原始名字（上传时的名字）
        String originalFilename = file.getOriginalFilename();
        //2，获取文件后缀
        String lastName = null;
        if (originalFilename != null) {
            lastName = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        //3，判断指定转存目录是否存在
        File dir = new File(imgPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        //4，随机获取一个文件名，防止冲突覆盖
        UUID randomuuid = UUID.randomUUID();

        //5，拼接文件名
        String fileName = randomuuid + lastName;

        //6，拼接转存目录
        String transferPath = imgPath + fileName;

        try {
            //转存
            file.transferTo(new File(transferPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //将最终生成的文件名返回
        return R.success(fileName);
    }

    //imageUrl = `/common/download?name=${response.data}`

    /**
     * 下载图片到页面
     *
     * @param response 将文件数据写到页面
     * @param name     文件名
     */
    @GetMapping("/download")
    public void download(HttpServletResponse response, String name) {
        try {
            //字节输入流读取数据
            FileInputStream is = new FileInputStream(imgPath + name);
            //字节输出流写到页面数据
            ServletOutputStream os = response.getOutputStream();

            //设置响应类型为图片
            response.setContentType("image/jpeg");
            //图片对拷
            int len;
            byte[] bytes = new byte[1024];
            while ((len = is.read(bytes)) != -1) {
                os.write(bytes, 0, len);
                os.flush();
            }
            is.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
