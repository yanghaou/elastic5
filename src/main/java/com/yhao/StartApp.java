package com.yhao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * function
 * Author: yang.hao
 * Date: 2017/2/9
 */
@SpringBootApplication
public class StartApp {
    public static void main(String arg[]){
        SpringApplication.run(StartApp.class,arg);
    }
}
