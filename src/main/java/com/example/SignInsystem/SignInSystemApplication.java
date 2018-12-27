package com.example.SignInsystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 签到系统
 * @author q
 */
@EnableScheduling
@SpringBootApplication
public class SignInSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(SignInSystemApplication.class, args);
	}
}
