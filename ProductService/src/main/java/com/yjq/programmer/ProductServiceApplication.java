package com.yjq.programmer;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.yjq.programmer.dao")
@EnableFeignClients
public class ProductServiceApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}
