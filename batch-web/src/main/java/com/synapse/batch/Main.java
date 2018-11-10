package com.synapse.batch;

import com.synapse.common.sso.interceptor.UserInfoInterceptor;
import com.synapse.common.sso.rest.RestTemplateProxy;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by wangyifan on 2017/3/1.
 */
@SpringBootApplication
@ComponentScan({"com.synapse.batch", "com.synapse.common.sso"})
@MapperScan("com.synapse.batch.mapper")
//@EnableEurekaClient
public class Main extends WebMvcConfigurerAdapter {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
//    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplateProxy();
    }

    /**
     * 配置拦截器
     *
     * @param registry
     * @author lance
     */
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserInfoInterceptor()).addPathPatterns("/**");
    }
}
