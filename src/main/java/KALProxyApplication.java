import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.util.Arrays;

@SpringBootApplication(exclude={MongoAutoConfiguration.class})
@ComponentScan("com.yesmail")
public class KALProxyApplication {

    public static void main(String[] args) {
        SpringApplication.run(KALProxyApplication.class, args);
    }

}
