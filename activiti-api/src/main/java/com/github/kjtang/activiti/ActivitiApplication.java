package com.github.kjtang.activiti;

import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by kjtang on 2018/10/15.
 */
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class ActivitiApplication {

    /**
     * @param args
     */
    public static void main(String[] args){
        SpringApplication.run(ActivitiApplication.class,args);
    }


}
