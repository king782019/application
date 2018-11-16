package com.cloudsync.cloud;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration.class})
public class CloudApplication {


	private static final Logger logger = LogManager.getLogger(CloudApplication.class);
	
    public static void main(String[] args) {
        SpringApplication.run(CloudApplication.class, args);
    }
}
