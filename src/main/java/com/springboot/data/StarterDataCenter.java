package com.springboot.data;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.springboot.data.mapper")
@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
public class StarterDataCenter {
	public static void main(String[] args) {
		SpringApplication.run(StarterDataCenter.class, args);
	}


//	@Autowired
//	private DataService dataService;
//  // 此处的 cron 表达式意为每 30 分钟执行一次任务
//	@Scheduled(cron ="0 */30 * * * ?")
//	public void migrateDataOptimizedOnSchedule() throws Exception {
//		dataService.pageMigrateDataOptimized(dataService.getParams());
//	}
}
