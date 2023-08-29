package com.github.wjlong1128.fileupload.task.delay;

import com.github.wjlong1128.fileupload.domain.dto.ChunkCheckDTO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/8/29
 * @desc desc.
 */
@Slf4j
@Data
public class ChunkCheckDelay implements Delayed {

    // 存储到队列中的数据
    private ChunkCheckDTO chunkCheckDTO;

    // 当前队列任务执行的时间(单位:纳秒)
    private long deadlineNanos;

    public ChunkCheckDelay(ChunkCheckDTO chunkCheckDTO, int delayTime, TimeUnit unit) {
        log.debug("延迟任务: 创建 {}", chunkCheckDTO);
        this.deadlineNanos = System.nanoTime() + TimeUnit.NANOSECONDS.convert(delayTime, unit);
        this.chunkCheckDTO = chunkCheckDTO;
    }

    // 剩余多长时间执行
    @Override
    public long getDelay(TimeUnit unit) {
        long l = Math.max(0, deadlineNanos - System.nanoTime());
        return unit.convert(l, TimeUnit.NANOSECONDS);
    }

    // 比较器
    @Override
    public int compareTo(Delayed o) {
        long l = this.getDelay(TimeUnit.NANOSECONDS) - o.getDelay(TimeUnit.NANOSECONDS);
        if (l > 0) {
            return 1;
        } else if (l < 0) {
            return -1;
        } else {
            return 0;
        }
    }

}
