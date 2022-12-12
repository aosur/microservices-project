package com.nttdata.bootcoinservice.config;

import com.nttdata.bootcoinservice.model.Transaction;
import com.nttdata.bootcoinservice.model.Wallet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfiguration {

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean(name = "redisTransactionTemplate")
    public RedisTemplate<String, Transaction> redisTransactionTemplate(
            JedisConnectionFactory jedisConnectionFactory) {
        final RedisTemplate<String, Transaction> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        return redisTemplate;
    }

    @Bean(name = "redisWalletTemplate")
    public RedisTemplate<String, Wallet> redisWalletTemplate(
            JedisConnectionFactory jedisConnectionFactory) {
        final RedisTemplate<String, Wallet> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        return redisTemplate;
    }

}
