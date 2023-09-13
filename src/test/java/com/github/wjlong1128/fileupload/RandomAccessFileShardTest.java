package com.github.wjlong1128.fileupload;

import com.github.wjlong1128.fileupload.utils.FileUtils;
import com.github.wjlong1128.fileupload.utils.MimeTypeUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/8/21
 * @desc
 */
@Slf4j
public class RandomAccessFileShardTest {

    public static final String SOURCE = "D:\\file\\video\\4k.mkv";
    public static final String TARGET = "C:\\Users\\wangj\\Desktop\\chunk\\";

    // 6MB
    public static final int CHUNK_SIZE = 1024 * 1024 * 6;


    @Test
    void sliceChunk() throws IOException {
        int sum = FileUtils.shardFile(new File(SOURCE), 6, 1024, "C:\\Users\\wangj\\Desktop\\chunk");
        System.out.println(sum);
    }

    @Test
    void mergeChunk() throws IOException {
        String targetName = "C:\\Users\\wangj\\Desktop\\丛林.mkv";
        FileUtils.mergeShardFile(
                "C:\\Users\\wangj\\Desktop\\chunk",
                74,
                1024,
                targetName,
                false
        );
        System.out.println(MimeTypeUtils.getMimeWithMagic(new File(targetName)));
        System.out.println(MimeTypeUtils.getMimeWithSuffix(targetName));
    }

    @Test
    void getChunkMime() throws IOException {
        String chunk = "C:\\Users\\wangj\\Desktop\\chunk\\0";
        String mime = MimeTypeUtils.getMimeWithMagic(new File(chunk));
        System.out.println(mime);
    }

    @Test
    void hash() throws Exception{
        // 0bd9d82fa4ece14109362f914cc3de51
        File file = new File("C:\\Users\\wangj\\Desktop\\chunk\\0");
        System.out.println(DigestUtils.md5DigestAsHex(new FileInputStream(file)));
    }
}
