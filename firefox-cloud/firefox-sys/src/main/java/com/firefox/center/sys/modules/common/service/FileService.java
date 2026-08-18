package com.firefox.center.sys.modules.common.service;

import com.firefox.center.sys.common.constant.CommonConstant;
import com.firefox.center.sys.common.system.api.ISysBaseAPI;
import com.firefox.center.sys.common.util.*;
import com.firefox.center.sys.config.property.FirefoxProperty;
import com.firefox.center.common.R;
import com.firefox.center.sys.modules.common.property.FileProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Service
public class FileService {

    private final ISysBaseAPI sysBaseAPI;
    private final FirefoxProperty firefoxProperty;
    private final FileProperty fileProperty;

    public FileService(ISysBaseAPI sysBaseAPI,
                       FirefoxProperty firefoxProperty,
                       FileProperty fileProperty) {
        this.sysBaseAPI = sysBaseAPI;
        this.firefoxProperty = firefoxProperty;
        this.fileProperty = fileProperty;
    }

    public R<?> fileSave(MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        R<?> result = new R<>();
        String savePath = "";
        String bizPath = request.getParameter("biz");
        //扩展名格式
        String extName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String name,filePath="";
        //图片类型文件
        if(this.inArray(fileProperty.getImageType(),extName)){
            filePath = fileProperty.getImageFolder();
        }
        //视频类型文件
        else if(this.inArray(fileProperty.getVideoType(),extName)){
            filePath = fileProperty.getVideoFolder();
        }
        //文档类型文件
        else if(this.inArray(fileProperty.getDocumentType(),extName)){
            filePath = fileProperty.getDocumentFolder();
        }
        //音频类型文件
        else if(this.inArray(fileProperty.getMusicType(),extName)){
            filePath = fileProperty.getMusicFolder();
        }else {
            return R.error("上传类型暂不支持");
        }
        //阿里云上传
        if(CommonConstant.UPLOAD_TYPE_OSS.equals(firefoxProperty.getUploadType())){
            //未指定目录，则用阿里云默认目录 upload
            //result.setMsg("使用阿里云文件上传时，必须添加目录！");
            //result.setSuccess(false);
            //return result;
        }
        //本地上传
        else if(CommonConstant.UPLOAD_TYPE_LOCAL.equals(firefoxProperty.getUploadType())){
            //针对jeditor编辑器如何使 lcaol模式，采用 base64格式存储
            String jeditor = request.getParameter("jeditor");
            if(oConvertUtils.isNotEmpty(jeditor)){
                result.setMsg(CommonConstant.UPLOAD_TYPE_LOCAL);
                result.setSuccess(true);
                return result;
            }else{
                savePath = fileUp(filePath,file);
            }
        }else{
            savePath = sysBaseAPI.upload(file,bizPath,firefoxProperty.getUploadType());
        }
        if (savePath.contains("\\")) {
            savePath = savePath.replace("\\", "/");
        }
        if(oConvertUtils.isNotEmpty(savePath)){
            result.setMsg(savePath);
            result.setSuccess(true);
        }else {
            result.setMsg("上传失败！");
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 下载文件
     * @param filePath 包含文件路径的文件名
     * @return
     */
    public void fileDownload(HttpServletRequest request, HttpServletResponse response, String filePath) {
        OutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            String path=getFileServerpath(filePath);
            inputStream = new FileInputStream(path);
            String filename = filePath.substring(filePath.lastIndexOf("/") + 1);
            response.setHeader("Content-Disposition", "attachment;filename=" + EncodingUtil.convertToFileName(request, filename));
            // 获取输出流
            outputStream = response.getOutputStream();
            IOUtils.copy(inputStream, outputStream);
        } catch (IOException e) {
            log.error("文件下载出错", e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除文件
     * @param filePath 包含文件路径的文件名
     * @return
     */
    public R<?> dropFile(String filePath) {
        try {
            FileUtil.delFile(fileProperty.getPath()+filePath);
            return R.ok();
        } catch (Exception e) {
            return R.error();
        }
    }

    /**
     * 判断数组中是否包含某个元素
     * @param array 类型的数组
     * @param element 被检查的类型
     * @return
     */
    private boolean inArray(String[] array,String element) {
        boolean flag = false;
        for(String type : array) {
            if(element.equals(type)){
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * 默认上传文件到文件夹
     * @param folder 默认文件夹
     * @param file 上传的文件
     * @return
     */
    private String fileUp(String folder, MultipartFile file) {
        String saveName = new SimpleDateFormat("/yyyy/MM/dd/").format(new Date());
        String returnName= FileUpload.fileUp(file,fileProperty.getPath()+"/"+folder+saveName, ToolUtil.getId());
        saveName = "/static"+ folder+saveName +returnName;
        return saveName;
    }

    public String getFileServerpath(String filePath) {
        return fileProperty.getPath()+filePath.replace("/static", "");
    }

}
