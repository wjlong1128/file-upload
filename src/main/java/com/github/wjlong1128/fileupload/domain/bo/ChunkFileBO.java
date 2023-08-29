package com.github.wjlong1128.fileupload.domain.bo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/8/20
 * @desc
 */
@Data
@AllArgsConstructor
public class ChunkFileBO {
    private String bucket;
    private String chunkFilePath;
}
