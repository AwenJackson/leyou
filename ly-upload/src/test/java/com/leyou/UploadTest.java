package com.leyou;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.domain.ThumbImageConfig;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UploadTest {
    @Resource
    private FastFileStorageClient fastFileStorageClient;

    @Resource
    private ThumbImageConfig thumbImageConfig;

    @Test
    public void test() throws FileNotFoundException {
        File file = new File("C:\\Users\\Shmily_0219\\Desktop\\IMG_20210808_180525.jpg");
        FileInputStream fileInputStream = new FileInputStream(file);
        String name = file.getName();
        String ext = StringUtils.substringAfterLast(name,".");
        StorePath storePath = fastFileStorageClient.uploadFile(fileInputStream, file.length(), ext, null);
        System.out.println("storePath.getFullPath() = http://image.leyou.com/" + storePath.getFullPath());
    }

    @Test
    public void test_crt() throws FileNotFoundException {
        File file = new File("C:\\Users\\Shmily_0219\\Desktop\\简历\\简历照片\\1.jpg");
        FileInputStream fileInputStream = new FileInputStream(file);
        String name = file.getName();
        String ext = StringUtils.substringAfterLast(name,".");
        StorePath storePath = fastFileStorageClient.uploadImageAndCrtThumbImage(fileInputStream, file.length(), ext, null);
        System.out.println("storePath.getFullPath() = http://image.leyou.com/" + storePath.getFullPath());
        //上传缩略图
        String thumbImagePath = thumbImageConfig.getThumbImagePath(storePath.getFullPath());
        System.out.println("thumbImagePath = http://image.leyou.com/" + thumbImagePath);
    }
}
