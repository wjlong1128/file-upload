package com.github.wjlong1128.fileupload.utils;

import org.apache.tika.Tika;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/8/20
 * @desc
 */
public final class MimeTypeUtils {

    private MimeTypeUtils() {
    }

    public static String getMimeWithSuffix(String fileName) {
        Optional<MediaType> mediaType = MediaTypeFactory.getMediaType(fileName);
        return mediaType.orElse(MediaType.APPLICATION_OCTET_STREAM).toString();
    }

    public static String getMimeWithMagic(byte[] bs) {
        return new Tika().detect(bs);
    }

    public static String getMimeWithMagic(File file) throws IOException {
        return new Tika().detect(file);
    }
}
