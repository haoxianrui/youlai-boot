package com.youlai.boot.shared.file.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.youlai.boot.shared.file.model.FileInfo;
import com.youlai.boot.shared.file.service.FileService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDateTime;

/**
 * 本地存储服务类
 *
 * @author Theo
 * @since 2024-12-09 17:11
 */
@Component
@ConditionalOnProperty(value = "oss.type", havingValue = "local")
@ConfigurationProperties(prefix = "oss.local")
@RequiredArgsConstructor
@Data
public class LocalFileService implements FileService {

    @Value("${oss.local.storage-path}")
    private String storagePath;

    @Override
    public FileInfo uploadFile(MultipartFile file) {
        // 生成文件名(日期文件夹)
        String suffix = FileUtil.getSuffix(file.getOriginalFilename());
        String uuid = IdUtil.simpleUUID();
        String folder = DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + File.separator;
        String fileName =  uuid + "." + suffix;
        String filePrefix = storagePath.endsWith(File.separator) ? storagePath : storagePath + File.separator;
        //  try-with-resource 语法糖自动释放流
        try (InputStream inputStream = file.getInputStream()) {
            // 上传文件
            FileUtil.writeFromStream(inputStream, filePrefix +folder+ fileName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("文件上传失败");
        }
        // 获取文件访问路径，因为这里是本地存储，所以直接返回文件的相对路径，需要前端自行处理访问前缀
        String fileUrl = File.separator +folder+File.separator + fileName;
        FileInfo fileInfo = new FileInfo();
        fileInfo.setName(fileName);
        fileInfo.setUrl(fileUrl);
        return fileInfo;
    }

    @Override
    public boolean deleteFile(String filePath) {
        //判断文件是否为空
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        // 判断filepath是否为文件夹
        if (FileUtil.isDirectory(storagePath + filePath)) {
            // 禁止删除文件夹
            return false;
        }
        // 删除文件
        return FileUtil.del(storagePath + filePath);
    }
}
