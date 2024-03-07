package com.leyou.upload.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.upload.config.UploadProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Service
@Slf4j
@EnableConfigurationProperties(UploadProperties.class)  //使用属性配置类
public class UploadService {

    @Autowired
    private FastFileStorageClient storageClient;

    //注入常量类
    @Autowired
    private UploadProperties prop;

//    private static final List<String> ALLOW_TYPES = Arrays.asList("image/png", "image/jpeg");

    /**
     * 图片文件上传
     * @param file
     * @return
     */
    public String uploadImage(MultipartFile file) {
        try {

            //校验文件类型(后缀)
            String contentType = file.getContentType();
            if (!prop.getAllowTypes().contains(contentType)) {
                throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
            }
            //校验文件内容
            BufferedImage read = ImageIO.read(file.getInputStream());
            if (read == null) {
                throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
            }

            //保存文件到数据库
//            File path = new File("D://upload");
//            if (!path.exists()) {
//                path.mkdirs();
//            }
//            file.transferTo(new File(path, file.getOriginalFilename()));
//            return prop.getBaseUrl()+ file.getOriginalFilename();

//            上传至fastdfs
            String extension = StringUtils.substringAfterLast(file.getOriginalFilename(),".");
            StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), extension, null);
            return prop.getBaseUrl() + storePath.getFullPath();

        } catch (IOException e) {
            log.error("[文件上传] 文件上传失败", e);
            throw new LyException(ExceptionEnum.UPLOAD_FILE_ERROR);
        }
    }
}
