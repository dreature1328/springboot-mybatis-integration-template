package com.springboot.data;

import com.springboot.data.common.utils.SpringContextUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@MapperScan("com.springboot.data.mapper")
@Import(SpringContextUtils.class)
public class StarterDataCenter {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(StarterDataCenter.class, args);
	}

}
