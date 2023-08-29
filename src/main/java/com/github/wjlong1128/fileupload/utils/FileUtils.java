package com.github.wjlong1128.fileupload.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/8/25
 * @desc
 */
@Slf4j
public final class FileUtils {
    private FileUtils() {
    }

    /**
     * 分片文件
     *
     * @param file       文件
     * @param chunkSize  分块后文件的大小 单位m
     * @param bufferSize 读取文件占用的缓冲区大小 byte
     * @param targetPath 分块后文件的父路径
     * @return 返回分块文件的数量
     * @throws IOException
     */
    public static int shardFile(File file, int chunkSize, int bufferSize, String targetPath) throws IOException {
        long start = System.currentTimeMillis();
        chunkSize = chunkSize * 1024 * 1024;
        targetPath = remediateFilePath(targetPath);
        if (!targetPath.endsWith(File.separator)) targetPath += File.separator;
        double sum = ((double) file.length()) / chunkSize;
        sum = Math.ceil(sum);
        log.debug("sliceNum: {}", sum);
        byte[] buf = new byte[bufferSize];
        try (RandomAccessFile r = new RandomAccessFile(file, "r");) {
            for (int i = 0; i < sum; i++) {
                String target = targetPath + i;
                int count = 0;
                int len;
                try (RandomAccessFile rw = new RandomAccessFile(target, "rw")) {
                    while ((len = r.read(buf)) != -1) {
                        rw.write(buf, 0, len);
                        count += len;
                        if (count >= chunkSize) {
                            break;
                        }
                    }
                }
            }
            log.debug("shard file end, time: {} ms", System.currentTimeMillis() - start);
        }
        return (int) sum;
    }

    /**
     * 合并分片文件
     *
     * @param sourceParentPath 被合并的文件的父目录
     * @param chunkNum         分片文件的数量
     * @param bufferSize       分配的缓冲区 byte
     * @param targetName       合并后的文件路径
     * @throws IOException
     */
    public static void mergeShardFile(String sourceParentPath, int chunkNum, int bufferSize, String targetName, boolean autoDeleteChunk) throws IOException {
        long start = System.currentTimeMillis();
        sourceParentPath = remediateFilePath(sourceParentPath);
        targetName = remediateFilePath(targetName);
        if (!sourceParentPath.endsWith(File.separator)) sourceParentPath += File.separator;
        byte[] buf = new byte[bufferSize];
        try (RandomAccessFile rw = new RandomAccessFile(targetName, "rw");) {
            for (int i = 0; i < chunkNum; i++) {
                String chunkFile = sourceParentPath + i;
                File file = new File(chunkFile);
                try (RandomAccessFile r = new RandomAccessFile(file, "r");) {
                    int len;
                    while ((len = r.read(buf)) != -1) {
                        rw.write(buf, 0, len);
                    }
                }
                if (autoDeleteChunk) {
                    file.delete();
                }
            }
        }
        log.debug("合并文件:{} 成功, 耗时:{}ms", targetName, System.currentTimeMillis() - start);
    }

    public static String remediateFilePath(String path) {
        return path.replaceAll("/", File.separator).replace("\\", File.separator);
    }

    public static String getSuffix(String fileName,boolean retainDot) {
        return fileName.substring(fileName.lastIndexOf('.'));
    }

}
