package com.yhao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * function
 * Author: yang.hao
 * Date: 2017/2/9
 */

@Configuration
public class TransportConfig {
    @Bean(initMethod = "initClient")
    public TransportClientBean transportClientBean(){
        return new TransportClientBean();
    }

}
