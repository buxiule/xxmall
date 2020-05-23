package ltd.xx.mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@MapperScan("ltd.xx.mall.dao")
@SpringBootApplication
public class XxmallApplication {
    public static void main(String[] args) {
        SpringApplication.run(XxmallApplication.class, args);
    }
}
