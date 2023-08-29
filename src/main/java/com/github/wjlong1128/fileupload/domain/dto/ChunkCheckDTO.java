package com.github.wjlong1128.fileupload.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/8/29
 * @desc desc.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChunkCheckDTO {

    private String key;
    private Integer chunkNo;
    private String bucket;
    private String filePath;
}
