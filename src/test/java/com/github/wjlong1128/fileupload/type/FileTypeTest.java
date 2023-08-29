package com.github.wjlong1128.fileupload.type;

import com.github.wjlong1128.fileupload.utils.MimeTypeUtils;
import org.apache.commons.compress.utils.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/8/24
 * @desc
 */
public class FileTypeTest {
    public static void main(String[] args) throws IOException {
        FileInputStream bs = new FileInputStream("1.txt");
//        String s = DigestUtils.md5DigestAsHex(bs);
//        System.out.println(s);
        String mime = MimeTypeUtils.getMimeWithMagic(IOUtils.toByteArray(bs));
        System.out.println(mime);
    }
}
