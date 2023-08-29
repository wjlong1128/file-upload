package com.github.wjlong1128.fileupload.utils;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import org.junit.jupiter.api.Test;

/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/8/29
 * @desc desc.
 */
public class HuToolsHttp {
    @Test
    void get() {
        String result = HttpUtil.get("http://localhost:9000/videos/9/0/8/90819dde4a421d8d7179dc20f1c23100.mp4");
        System.out.println(JSONUtil.xmlToJson(result).toJSONString(2));
    }
}
