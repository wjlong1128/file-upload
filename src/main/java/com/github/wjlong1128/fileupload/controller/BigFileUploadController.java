package com.github.wjlong1128.fileupload.controller;

import com.github.wjlong1128.fileupload.domain.result.Result;
import com.github.wjlong1128.fileupload.domain.vo.ChunkVO;
import com.github.wjlong1128.fileupload.domain.vo.FileVO;
import com.github.wjlong1128.fileupload.domain.vo.UploadChunkVO;
import com.github.wjlong1128.fileupload.service.BigFileUploadService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

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
    public Result<Boolean> isExists(@PathVariable("md5") String md5) {
        boolean isExists = this.bigFileUploadService.isExistsFile(md5);
        return Result.success(isExists);
    }


    @GetMapping("chunk/{md5}/{chunkNo}")
    public Result<ChunkVO> isExists(@PathVariable("md5") String md5, @PathVariable("chunkNo") Integer chunkNo) {
        boolean isExists = this.bigFileUploadService.isExistsChunk(md5, chunkNo);
        return Result.success(new ChunkVO(chunkNo, isExists));
    }


    @PostMapping("upload")
    public Result<UploadChunkVO> uploadChunk(
            @RequestParam("file") MultipartFile file,
            @RequestParam("md5") String md5,
            @RequestParam("chunkNo") Integer chunkNo
    ) {
        boolean success = this.bigFileUploadService.uploadChunk(md5, chunkNo, file);
        UploadChunkVO uploadChunkVO = new UploadChunkVO();
        uploadChunkVO.setSuccess(success);
        uploadChunkVO.setChunkNo(chunkNo);
        return Result.success(uploadChunkVO);
    }


    @GetMapping("merge")
    public Result<FileVO> mergeChunk(String fileName, String md5, Integer chunkNum) {
        FileVO fileVO = bigFileUploadService.mergeChunk(fileName, md5, chunkNum);
        return Result.success("文件上传完成！", fileVO);
    }
}
