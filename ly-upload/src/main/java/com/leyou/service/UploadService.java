package com.leyou.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.props.UploadProperties;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Service
@EnableConfigurationProperties(value = UploadProperties.class)
public class UploadService {

//    private final List<String> allowImage =
//            Arrays.asList("image/jpeg","image/png","image/bmp");

    @Resource
    private FastFileStorageClient fastFileStorageClient;

    @Resource
    private UploadProperties uploadProperties;

    public String uploadImage(MultipartFile file) {
        try {
            String contentType = file.getContentType();
            //1.判断图片格式是否正确
            if (!uploadProperties.getAllowTypes().contains(contentType)) {
                throw new LyException(ExceptionEnum.UPLOAD_IMAGE_TYPE_ERROR);
            }
            //2.判断图片是否有内容
            if (ImageIO.read(file.getInputStream())==null){
                throw new LyException(ExceptionEnum.UPLOAD_IMAGE_CTX_ERROR);
            }
            //3.实现上传  存放位置 FastDFS
            String ext = StringUtils.substringAfterLast(file.getOriginalFilename(),".");        //获取图片格式
            StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), ext, null);
            return uploadProperties.getBaseUrl()+storePath.getFullPath();
        } catch (IOException e) {
            throw new LyException(ExceptionEnum.UPLOAD_IMAGE_ERROR);
        }
    }
}
