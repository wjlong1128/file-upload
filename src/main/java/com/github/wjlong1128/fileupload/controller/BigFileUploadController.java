package com.github.wjlong1128.fileupload.controller;

import com.github.wjlong1128.fileupload.domain.result.RestResp;
import com.github.wjlong1128.fileupload.service.BigFileUploadService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/8/26
 * @desc
 */
@RequestMapping("big")
@RestController
@CrossOrigin
public class BigFileUploadController {

    @Resource
    private BigFileUploadService bigFileUploadService;

    @GetMapping("{md5}")
    public RestResp isExists(@PathVariable("md5") String hex) {
        boolean isExists = this.bigFileUploadService.isExistsFile(hex);
        return RestResp.success().data(isExists);
    }


    @GetMapping("chunk/{md5}/{chunkNo}")
    public RestResp isExists(@PathVariable("md5") String hex, @PathVariable("chunkNo") Integer chunkNo) {
        boolean isExists = this.bigFileUploadService.isExistsChunk(hex, chunkNo);
        HashMap<Object, Object> data = new HashMap<>();
        data.put("chunkNo", chunkNo);
        data.put("isExists", isExists);
        return RestResp.success().data(data);
    }


    @PostMapping("upload")
    public RestResp testUpload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("md5") String md5,
            @RequestParam("chunkNo") Integer chunkNo
    ) throws IOException {
        boolean success = this.bigFileUploadService.uploadChunk(md5, chunkNo, file);
        HashMap<String, Object> data = new HashMap<>();
        data.put("chunkNo", chunkNo);
        data.put("success", success);
        return RestResp.success().data(data);
    }


    @GetMapping("merge")
    public RestResp mergeChunk(String fileName, String md5, Integer chunkNum) {
        bigFileUploadService.mergeChunk(fileName, md5, chunkNum);
        return RestResp.success().data("上传成功");
    }
}
