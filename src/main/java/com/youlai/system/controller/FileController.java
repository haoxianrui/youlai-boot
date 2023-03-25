package com.youlai.system.controller;

import com.youlai.system.common.result.Result;
import com.youlai.system.pojo.vo.FileInfoVO;
import com.youlai.system.service.FileService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation; 
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "07.文件接口")
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping
    @Operation(summary = "文件上传", security = {@SecurityRequirement(name = "Authorization")})
    public Result<FileInfoVO> uploadFile(
            @Parameter(description ="表单文件对象") @RequestParam(value = "file") MultipartFile file
    ) {
        FileInfoVO fileInfoVO = fileService.uploadFile(file);
        return Result.success(fileInfoVO);
    }

    @DeleteMapping
    @Operation(summary = "文件删除", security = {@SecurityRequirement(name = "Authorization")})
    @SneakyThrows
    public Result deleteFile(
            @Parameter(description ="文件路径") @RequestParam String filePath
    ) {
        boolean result = fileService.deleteFile(filePath);
        return Result.judge(result);
    }
}
