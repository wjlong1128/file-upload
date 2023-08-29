package com.github.wjlong1128.fileupload.redis;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;

/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/8/26
 * @desc
 */
@SpringBootTest
public class RedisTest {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void isMember() {
        Boolean member = stringRedisTemplate.opsForSet().isMember("wjl", "n");
        System.out.println(member);
    }
}
