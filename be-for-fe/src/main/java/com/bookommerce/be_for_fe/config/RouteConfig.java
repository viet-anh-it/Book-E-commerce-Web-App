package com.bookommerce.be_for_fe.config;

import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.filter.TokenRelayFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

// @formatter:off
@Configuration
@SuppressWarnings("null")
public class RouteConfig {

    private static final String RESOURCE_SERVER_BASE_URL = "https://api.bookommerce.com:8383";

    @Bean
    public RouterFunction<ServerResponse> bookRouter() {
        return GatewayRouterFunctions.route()
            .GET("/api/books", HandlerFunctions.http())
            .before(BeforeFilterFunctions.uri(RESOURCE_SERVER_BASE_URL))
            .build()
            .and(GatewayRouterFunctions.route()
                .GET("/api/books/{id}", HandlerFunctions.http())
                .before(BeforeFilterFunctions.uri(RESOURCE_SERVER_BASE_URL))
                .build())
            .and(GatewayRouterFunctions.route()
                .POST("/api/books", HandlerFunctions.http())
                .before(BeforeFilterFunctions.uri(RESOURCE_SERVER_BASE_URL))
                .filter(TokenRelayFilterFunctions.tokenRelay())
                .build())
            .and(GatewayRouterFunctions.route()
                .PUT("/api/books/{id}", HandlerFunctions.http())
                .before(BeforeFilterFunctions.uri(RESOURCE_SERVER_BASE_URL))
                .filter(TokenRelayFilterFunctions.tokenRelay())
                .build())
            .and(GatewayRouterFunctions.route()
                .DELETE("/api/books/{id}", HandlerFunctions.http())
                .before(BeforeFilterFunctions.uri(RESOURCE_SERVER_BASE_URL))
                .filter(TokenRelayFilterFunctions.tokenRelay())
                .build());
    }

    @Bean
    public RouterFunction<ServerResponse> genreRouter() {
        return GatewayRouterFunctions.route()
            .GET("/api/genres", HandlerFunctions.http())
            .before(BeforeFilterFunctions.uri(RESOURCE_SERVER_BASE_URL))
            .build()
            .and(GatewayRouterFunctions.route()
                .POST("/api/genres", HandlerFunctions.http())
                .before(BeforeFilterFunctions.uri(RESOURCE_SERVER_BASE_URL))
                .build());
    }

    @Bean
    public RouterFunction<ServerResponse> ratingRouter() {
        return GatewayRouterFunctions.route()
            .GET("/api/books/{bookId}/ratings", HandlerFunctions.http())
            .before(BeforeFilterFunctions.uri(RESOURCE_SERVER_BASE_URL))
            .build()
            .and(GatewayRouterFunctions.route()
                .GET("/api/ratings", HandlerFunctions.http())
                .before(BeforeFilterFunctions.uri(RESOURCE_SERVER_BASE_URL))
                .filter(TokenRelayFilterFunctions.tokenRelay())
                .build())
            .and(GatewayRouterFunctions.route()
                .POST("/api/ratings", HandlerFunctions.http())
                .before(BeforeFilterFunctions.uri(RESOURCE_SERVER_BASE_URL))
                .filter(TokenRelayFilterFunctions.tokenRelay())
                .build())
            .and(GatewayRouterFunctions.route()
                .PUT("/api/ratings", HandlerFunctions.http())
                .before(BeforeFilterFunctions.uri(RESOURCE_SERVER_BASE_URL))
                .filter(TokenRelayFilterFunctions.tokenRelay())
                .build())
            .and(GatewayRouterFunctions.route()
                .PATCH("/api/ratings/{id}/**", HandlerFunctions.http())
                .before(BeforeFilterFunctions.uri(RESOURCE_SERVER_BASE_URL))
                .filter(TokenRelayFilterFunctions.tokenRelay())
                .build())
            .and(GatewayRouterFunctions.route()
                .DELETE("/api/ratings/{id}", HandlerFunctions.http())
                .before(BeforeFilterFunctions.uri(RESOURCE_SERVER_BASE_URL))
                .filter(TokenRelayFilterFunctions.tokenRelay())
                .build());
    }

    @Bean
    public RouterFunction<ServerResponse> cartRouter() {
        return GatewayRouterFunctions.route()
            .GET("/api/carts", HandlerFunctions.http())
            .before(BeforeFilterFunctions.uri(RESOURCE_SERVER_BASE_URL))
            .filter(TokenRelayFilterFunctions.tokenRelay())
            .build()
            .and(GatewayRouterFunctions.route()
                .POST("/api/carts/items", HandlerFunctions.http())
                .before(BeforeFilterFunctions.uri(RESOURCE_SERVER_BASE_URL))
                .filter(TokenRelayFilterFunctions.tokenRelay())
                .build())
            .and(GatewayRouterFunctions.route()
                .PATCH("/api/carts/items/{cartItemId}", HandlerFunctions.http())
                .before(BeforeFilterFunctions.uri(RESOURCE_SERVER_BASE_URL))
                .filter(TokenRelayFilterFunctions.tokenRelay())
                .build())
            .and(GatewayRouterFunctions.route()
                .DELETE("/api/carts/items/{cartItemId}", HandlerFunctions.http())
                .before(BeforeFilterFunctions.uri(RESOURCE_SERVER_BASE_URL))
                .filter(TokenRelayFilterFunctions.tokenRelay())
                .build());
    }

    @Bean
    public RouterFunction<ServerResponse> resourceRouter() {
        return GatewayRouterFunctions.route()
            .GET("/images/books/**", HandlerFunctions.http())
            .before(BeforeFilterFunctions.uri(RESOURCE_SERVER_BASE_URL))
            .build();
    }
}
