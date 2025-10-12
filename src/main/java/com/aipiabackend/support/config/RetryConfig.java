package com.aipiabackend.support.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

/**
 * Spring-Retry 기능을 활성화하는 설정 클래스
 */
@EnableRetry
@Configuration
public class RetryConfig {
}
