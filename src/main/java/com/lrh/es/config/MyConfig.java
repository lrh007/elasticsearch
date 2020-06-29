package com.lrh.es.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyConfig {

    /**
     * 初始化es客户端
     * @return
     */
    @Bean
    public RestHighLevelClient restHighLevelClient(){
        HttpHost httpHost = new HttpHost("localhost",9200,"http");
        RestClientBuilder restClientBuilder = RestClient.builder(httpHost);
        RestHighLevelClient client = new RestHighLevelClient(restClientBuilder);
        return client;
    }
}
