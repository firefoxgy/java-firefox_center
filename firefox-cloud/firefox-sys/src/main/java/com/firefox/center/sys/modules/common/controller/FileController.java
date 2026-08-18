package com.firefox.center.sys.modules.common.controller;

import com.firefox.center.sys.common.Assert;
import com.firefox.center.sys.modules.common.service.FileService;
import com.firefox.center.common.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(tags = {"文件管理"})
@RestController
@RequestMapping("/sys/common/file")
public class FileController {

    @Resource
    private FileService fileService;

    /**
     * 单个文件上传
     * @param file
     * @return
     */
    @ApiOperation("单文件上传")
    @PostMapping("/upload")
    public R<?> singleUpload(@RequestParam(value = "file",required = false) MultipartFile file,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        Assert.notNull(file, "上传文件为空");
        return fileService.fileSave(file, request, response);
    }

    /**
     * 文件下载
     * @param filePath
     * @return
     */
    @ApiOperation("文件下载")
    @GetMapping("download")
    public void download(HttpServletRequest request, HttpServletResponse response, String filePath) {
        fileService.fileDownload(request, response, filePath);
    }

    /**
     * 删除文件
     * @param filePath
     * @return
     */
    @ApiOperation("文件删除")
    @GetMapping("drop")
    public R<?> drop(String filePath) {
        return fileService.dropFile(filePath);
    }

}
