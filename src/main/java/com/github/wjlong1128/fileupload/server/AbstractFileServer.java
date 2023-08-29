package com.github.wjlong1128.fileupload.server;

import cn.hutool.core.io.FileUtil;
import com.github.wjlong1128.fileupload.domain.exception.FileServerException;
import com.github.wjlong1128.fileupload.utils.MimeTypeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/8/24
 * @desc fileServer的抽象接口，负责默认的bucket自动创建处理逻辑
 */
@Slf4j
public abstract class AbstractFileServer implements FileServer {

    /**
     * key: file.suffix/contextType
     * value: bucketName
     */
    protected final ConcurrentHashMap<String, String> buckets = new ConcurrentHashMap<>();

    @Resource
    protected FileServerProperties fileServerProperties;

    @PostConstruct
    public void init() throws FileServerException {
        if (!fileServerProperties.getAutoCreateBuckets()) {
            return;
        }
        String unknownBucket = this.fileServerProperties.getUnknownBucket();
        this.ifNotExistsCreateBucket(unknownBucket);
        log.info("{} 自动创建unknownBucket: {}", this.getClass().getSimpleName(), unknownBucket);
        List<FileServerProperties.Bucket> initBuckets = this.fileServerProperties.getDefaultTypeBuckets();
        if (initBuckets == null) {
            return;
        }
        for (FileServerProperties.Bucket bucket : initBuckets) {
            for (String storeType : bucket.getStoreTypes()) {
                this.buckets.put(storeType, bucket.getName());
            }
        }
        for (String bucket : new HashSet<>(this.buckets.values())) {
            log.info("{} 自动创建bucket: {}", this.getClass().getSimpleName(), bucket);
            this.ifNotExistsCreateBucket(bucket);
        }
    }


    @Override
    public final String getBucket(String key) throws FileServerException {
        if (!StringUtils.hasText(key)) {
            throw new FileServerException("获取bucket的key不能为空");
        }
        String bucket = this.buckets.get(key);
        if (StringUtils.hasText(bucket)) {
            return bucket;
        }
        // 获取后缀
        String suffix = FileUtil.getSuffix(key);
        bucket = this.buckets.get(suffix);
        if (StringUtils.hasText(bucket)) {
            return bucket;
        }
        // 获取mimetype
        String mimeType = MimeTypeUtils.getMimeWithSuffix("." + suffix);
        bucket = this.buckets.get(mimeType);
        if (StringUtils.hasText(bucket)) {
            return bucket;
        }
        bucket = this.fileServerProperties.getUnknownBucket();
        if (!StringUtils.hasText(bucket)) {
            throw new FileServerException("未能找到与" + key + "对应的bucket");
        }
        return bucket;
    }


    public abstract void ifNotExistsCreateBucket(String bucketName) throws FileServerException;
}
