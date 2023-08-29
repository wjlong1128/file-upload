package com.github.wjlong1128.fileupload.domain.bo;

import lombok.Builder;
import lombok.Getter;

/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/8/24
 * @desc
 */
@Getter
@Builder
public class DownloadBO {
    private byte[] content;
    private int size;
    private String fileName;
    private String contextType;
}
