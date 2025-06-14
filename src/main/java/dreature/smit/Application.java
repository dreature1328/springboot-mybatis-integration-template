package dreature.smit;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("dreature.smit.mapper")
public class Application {
	public static void main(String[] args) {
		org.springframework.boot.SpringApplication.run(Application.class, args);
	}
}
