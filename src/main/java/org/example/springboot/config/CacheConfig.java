package org.example.springboot.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration defaults = RedisCacheConfiguration.defaultCacheConfig()
                                                                  .entryTtl(Duration.ofMinutes(10))
                                                                  .disableCachingNullValues()
                                                                  .serializeValuesWith(
                                                                      RedisSerializationContext.SerializationPair
                                                                          .fromSerializer(
                                                                              new GenericJacksonJsonRedisSerializer(
                                                                                  new ObjectMapper())));

        Map<String, RedisCacheConfiguration> perCache = Map.of(
            "allProduct", defaults.entryTtl(Duration.ofSeconds(30)),
            "product", defaults.entryTtl(Duration.ofMinutes(5))
                                                              );

        return RedisCacheManager.builder(connectionFactory)
                                .cacheDefaults(defaults)
                                .withInitialCacheConfigurations(perCache)
                                .enableStatistics()
                                .build();
    }
}
