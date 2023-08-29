package com.github.wjlong1128.fileupload.domain.vo;

import lombok.Data;

/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/8/29
 * @desc desc.
 */
@Data
public class FileVO {

    /**
     * 文件md5值
     */
    private String id;

    /**
     * 文件名称
     */
    private String fileName;


    /**
     * 文件大小
     */
    private String size;


    /**
     * 文件访问地址
     */
    private String url;


}
