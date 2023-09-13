package com.github.wjlong1128.fileupload;

import com.github.wjlong1128.fileupload.domain.result.Result;
import org.junit.jupiter.api.Test;

/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/9/13
 * @desc desc.
 */
public class VoidTest {
    @Test
    void testJson() {
        Result<Void> result = Result.success();
        Void data = result.getData();
        System.out.println(data);
    }
}
