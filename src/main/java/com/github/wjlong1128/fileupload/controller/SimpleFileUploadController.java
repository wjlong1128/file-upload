package com.github.wjlong1128.fileupload.controller;

import com.github.wjlong1128.fileupload.domain.bo.DownloadBO;
import com.github.wjlong1128.fileupload.domain.result.Result;
import com.github.wjlong1128.fileupload.domain.vo.FileVO;
import com.github.wjlong1128.fileupload.service.FileUploadService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/8/19
 * @desc
 */
@RequestMapping("simple")
@Controller
@CrossOrigin
public class SimpleFileUploadController {

    @Resource
    private FileUploadService fileUploadService;

    @ResponseBody
    @PostMapping("upload")
    public Result<FileVO> upload(@RequestPart("file") MultipartFile file) throws IOException {
        FileVO result = this.fileUploadService.upload(file.getOriginalFilename(), file.getContentType(), file.getBytes());
        return Result.success("上传成功", result);
    }

    @GetMapping(value = "{fileName}")
    public ResponseEntity<byte[]> downLoad(@PathVariable String fileName) throws UnsupportedEncodingException {
        DownloadBO downloadBO = fileUploadService.downLoad(fileName);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", URLEncoder.encode(downloadBO.getFileName(), "UTF-8")));
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(downloadBO.getSize())
                .contentType(MediaType.valueOf(downloadBO.getContextType()))
                .body(downloadBO.getContent());
    }

    @PostMapping(value = "{id}")
    public Result deleteFile(@PathVariable String id) {
        boolean success = this.fileUploadService.deleteFile(id);
        return success ? Result.success("删除成功") : Result.fail("删除失败");
    }

}
