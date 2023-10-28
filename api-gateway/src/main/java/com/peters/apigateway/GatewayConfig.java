package com.peters.apigateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator gatewayRouter(RouteLocatorBuilder builder){
        return builder.routes()
                .route(predicate -> predicate.path("get", "post", "put", "delete")
                        .filters(f->f.addRequestHeader("MyHeader", "URI")
                                .addRequestParameter("MyParams", "ParamValue"))
                        .uri("http://httpbin.org:80"))
                .route(predicate->predicate.path("/api/v1/book/**")
                        .uri("lb://book-service"))
                .route(predicate->predicate.path("/api/v1/user/**")
                        .uri("lb://user-service"))
                .route(predicate->predicate.path("/api/v1/auth/user/**")
                        .uri("lb://user-service"))
                .route(predicate->predicate.path("/api/v1/orders/**")
                        .uri("lb://order-service"))

                .build();
    }
}
