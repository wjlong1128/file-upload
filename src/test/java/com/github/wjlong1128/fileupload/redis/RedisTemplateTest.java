package com.github.wjlong1128.fileupload.redis;

import com.github.wjlong1128.fileupload.entity.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;

@SpringBootTest
public class RedisTemplateTest {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedisTemplate<Object,Object> redisTemplate;

    @Test
    void setObject() {
        redisTemplate.opsForValue().set("user",new Customer().setId(1).setUsername("wjlong1128"));
    }

}
