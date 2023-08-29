package com.github.wjlong1128.fileupload.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.wjlong1128.fileupload.domain.entity.FileRecord;
import com.github.wjlong1128.fileupload.domain.exception.BusinessException;
import com.github.wjlong1128.fileupload.domain.exception.FileServerException;
import com.github.wjlong1128.fileupload.domain.result.UploadMessage;
import com.github.wjlong1128.fileupload.server.FileServer;
import com.github.wjlong1128.fileupload.service.FileRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/8/24
 * @desc
 */
@Slf4j
@Component
public class FileTypeHandler {

    /**
     * content-type: file.suffix
     */
    private Map<String, String> map = new HashMap<>();

    {
        map.put("image/png", "png");
    }

    @Resource
    private FileRecordService fileRecordService;

    @Resource
    private FileServer fileServer;


    @Scheduled(cron = "0/60 * * * * ?")
    public void fileSuffixProcessTask() {
        // 查出content和mime不一致的文件
        LambdaQueryWrapper<FileRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.last("where content_type <> mime_type limit 5");
        List<FileRecord> list = fileRecordService.list(wrapper);
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        for (FileRecord record : list) {
            String bucket = record.getBucket();
            String path = record.getPath();
            String mimeType = record.getMimeType();
            String suffix = this.map.get(mimeType);
            if (suffix == null) {
                return;
            }
            log.info("转换文件任务开始");
            // 下载
            byte[] bs = null;
            try {
                bs = fileServer.getObject(bucket, path);
            } catch (FileServerException e) {
                throw new BusinessException(UploadMessage.Normal.UNABLE_GET_FILE, e);
            }
            String newPath = newFilePath(path, suffix);
            String newFileName = newFilePath(record.getFileName(), suffix);
            // 上传
            try {
                fileServer.uploadObject(bucket, newPath, mimeType, bs);
            } catch (FileServerException e) {
                throw new BusinessException(UploadMessage.Normal.UNABLE_UPLOAD_FILE, e);
            }
            this.fileRecordService.lambdaUpdate()
                    .eq(FileRecord::getId, record.getId())
                    .set(FileRecord::getFileName, newFileName)
                    .set(FileRecord::getPath, newPath)
                    .set(FileRecord::getContentType, mimeType)
                    .update();
            log.info("更新{} 完成 -> {}", path, newPath);
            boolean deleted = false;
            try {
                deleted = fileServer.deleteObject(bucket, path);
            } catch (FileServerException e) {
                throw new BusinessException("删除" + path + "文件失败", e);
            }
            if (deleted) {
                log.info("删除{}", path);
            }
        }

    }

    public static String newFilePath(String fileName, String suffix) {
        String sub = fileName.substring(0, fileName.lastIndexOf('.'));
        return sub + '.' + suffix;
    }

}
