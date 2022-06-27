package com.hotelbeds.api;

import com.hotelbeds.activities.api.ApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:application.properties")
public class HotelBedsApiTestConfiguration {

    @Autowired
    private Environment environment;

//    @Bean
//    public ApiClient apiClient() {
//        ApiClient client = new ApiClient();
//        client.setScheme("https");
//        client.setHost("api.test.hotelbeds.com");
//        client.setBasePath("/activity-content-api/3.0");
//        return client;
//    }

    public static String APIKEY = "xd2szxam4chh25z8cqsft44q";
    public static String SECRET = "XTKC7zXUyT";
    public static String ACCEPT = "application/json";
    public static String ACCEPTENCODING = "gzip";
}
