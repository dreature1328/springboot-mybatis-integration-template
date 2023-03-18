package com.springboot.data;

import com.springboot.data.common.utils.SpringContextUtils;
import com.springboot.data.service.DataService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@MapperScan("com.springboot.data.mapper")
@Import(SpringContextUtils.class)
@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
public class StarterDataCenter {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(StarterDataCenter.class, args);
	}

//	@Autowired
//	private DataService dataService;
//
//	@Scheduled(cron ="0 */5 * * * ?")
//	public void pageInsertOrUpdateDataOnSchedule() throws Exception {
//		dataService.pageInsertOrUpdateData();
//	}
}
