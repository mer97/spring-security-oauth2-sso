package top.leemer.clientorder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ClientOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientOrderApplication.class, args);
    }

}
