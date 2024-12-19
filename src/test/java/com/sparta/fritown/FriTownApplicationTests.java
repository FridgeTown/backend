package com.sparta.fritown;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class FriTownApplicationTests {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    void testRedisConnection() {
        String key = "testKey";
        String value = "testValue";

        redisTemplate.opsForValue().set(key, value);

        String retrievedValue = redisTemplate.opsForValue().get(key);
        assertEquals(value, retrievedValue);
    }

}
