package com.github.wjlong1128.fileupload.task.delay;

import com.github.wjlong1128.fileupload.domain.dto.ChunkCheckDTO;
import com.github.wjlong1128.fileupload.domain.exception.FileServerException;
import com.github.wjlong1128.fileupload.server.MultipartUploadFileServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.DelayQueue;

/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/8/29
 * @desc desc.
 */
@Slf4j
@Component
public class ChunkProcessDelayTaskExecutor {

    // 延迟队列(集合)
    private final DelayQueue<ChunkCheckDelay> delayQueue = new DelayQueue<>();

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private MultipartUploadFileServer fileServer;

    // 处理延迟队列中的任务
    @PostConstruct
    public void handleDelayQueue() {
        new Thread(() -> {
            while (true) {
                // 检查，如果分块的时间过期，那么就删除该分块
                try {
                    ChunkCheckDelay take = delayQueue.take();
                    ChunkCheckDTO dto = take.getChunkCheckDTO();
                    String key = dto.getKey();
                    Boolean member = this.stringRedisTemplate.opsForSet().isMember(key, dto.getChunkNo().toString());
                    if (Boolean.FALSE.equals(member)) {
                        continue;
                    }
                    this.fileServer.deleteObject(dto.getBucket(), dto.getFilePath());
                } catch (InterruptedException e) {
                    log.error("处理延迟任务报错", e);
                } catch (FileServerException e) {
                    log.error("延迟删除文件失败", e);
                }
            }
        }).start();
    }

    public void add(ChunkCheckDelay checkDelay) {
        this.delayQueue.add(checkDelay);
    }

}
