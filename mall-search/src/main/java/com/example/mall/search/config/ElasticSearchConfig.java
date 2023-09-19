package com.example.mall.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ElasticSearchConfig {
    @Bean
    public RestHighLevelClient restHighLevelClient(ElasticSearchProperties properties) {
        String[] hosts = properties.getHosts();
        String schema = properties.getSchema();
        HttpHost[] httpHosts = new HttpHost[hosts.length];
        String hostName = null;
        Integer port = null;
        for (int i = 0; i < hosts.length; i++) {
            hostName = hosts[i].split(":")[0];
            port = Integer.valueOf(hosts[i].split(":")[1]);
            httpHosts[i] = new HttpHost(hostName, port, schema);
        }
        RestClientBuilder builder = RestClient.builder(httpHosts);
        return new RestHighLevelClient(builder);
    }
}
