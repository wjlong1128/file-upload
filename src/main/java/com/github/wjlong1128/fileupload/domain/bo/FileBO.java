package com.github.wjlong1128.fileupload.domain.bo;

import lombok.Data;

/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/8/19
 * @desc
 */
@Data
public class FileBO {
    private String url;
    private String bucket;
    private String contentType;
    private String path;
    private String serverType;
    private long size;
}
