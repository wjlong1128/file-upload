package com.github.wjlong1128.fileupload.domain.vo;

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
public class ChunkVO {
    private Integer chunkNo;
    private Boolean isExists;
}
