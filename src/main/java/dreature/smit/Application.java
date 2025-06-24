package dreature.smit;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("dreature.smit.mapper")
@EnableRabbit
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
