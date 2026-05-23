package com.magicmeme;

import com.magicmeme.config.OpenRouterProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(OpenRouterProperties.class)
public class MagicMemeApplication {

    public static void main(String[] args) {
        SpringApplication.run(MagicMemeApplication.class, args);
    }
}
