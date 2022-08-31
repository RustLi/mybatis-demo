package com.example.qy.mybatisdemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.qy.mybatisdemo.dal.**.mapper")
public class MybatisdemoApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(MybatisdemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Ok...");
    }
}
