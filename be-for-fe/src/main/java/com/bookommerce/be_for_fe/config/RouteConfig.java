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
                .POST("/protected/api/books", HandlerFunctions.http())
                .before(BeforeFilterFunctions.setPath("/api/books"))
                .before(BeforeFilterFunctions.uri(RESOURCE_SERVER_BASE_URL))
                .filter(TokenRelayFilterFunctions.tokenRelay())
                .build())
            .and(GatewayRouterFunctions.route()
                .PUT("/protected/api/books/{id}", HandlerFunctions.http())
                .before(BeforeFilterFunctions.setPath("/api/books/{id}"))
                .before(BeforeFilterFunctions.uri(RESOURCE_SERVER_BASE_URL))
                .filter(TokenRelayFilterFunctions.tokenRelay())
                .build())
            .and(GatewayRouterFunctions.route()
                .DELETE("/protected/api/books/{id}", HandlerFunctions.http())
                .before(BeforeFilterFunctions.setPath("/api/books/{id}"))
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
                .GET("/protected/api/ratings", HandlerFunctions.http())
                .before(BeforeFilterFunctions.setPath("/api/ratings"))
                .before(BeforeFilterFunctions.uri(RESOURCE_SERVER_BASE_URL))
                .filter(TokenRelayFilterFunctions.tokenRelay())
                .build())
            .and(GatewayRouterFunctions.route()
                .POST("/protected/api/ratings", HandlerFunctions.http())
                .before(BeforeFilterFunctions.setPath("/api/ratings"))
                .before(BeforeFilterFunctions.uri(RESOURCE_SERVER_BASE_URL))
                .filter(TokenRelayFilterFunctions.tokenRelay())
                .build())
            .and(GatewayRouterFunctions.route()
                .PUT("/protected/api/ratings", HandlerFunctions.http())
                .before(BeforeFilterFunctions.setPath("/api/ratings"))
                .before(BeforeFilterFunctions.uri(RESOURCE_SERVER_BASE_URL))
                .filter(TokenRelayFilterFunctions.tokenRelay())
                .build())
            .and(GatewayRouterFunctions.route()
                .PATCH("/protected/api/ratings/{id}/**", HandlerFunctions.http())
                .before(BeforeFilterFunctions.setPath("/api/ratings/{id}/**"))
                .before(BeforeFilterFunctions.uri(RESOURCE_SERVER_BASE_URL))
                .filter(TokenRelayFilterFunctions.tokenRelay())
                .build())
            .and(GatewayRouterFunctions.route()
                .DELETE("/protected/api/ratings/{id}", HandlerFunctions.http())
                .before(BeforeFilterFunctions.setPath("/api/ratings/{id}"))
                .before(BeforeFilterFunctions.uri(RESOURCE_SERVER_BASE_URL))
                .filter(TokenRelayFilterFunctions.tokenRelay())
                .build());
    }

    @Bean
    public RouterFunction<ServerResponse> cartRouter() {
        return GatewayRouterFunctions.route()
            .GET("/protected/api/carts", HandlerFunctions.http())
            .before(BeforeFilterFunctions.setPath("/api/carts"))
            .before(BeforeFilterFunctions.uri(RESOURCE_SERVER_BASE_URL))
            .filter(TokenRelayFilterFunctions.tokenRelay())
            .build()
            .and(GatewayRouterFunctions.route()
                .POST("/protected/api/carts/items", HandlerFunctions.http())
                .before(BeforeFilterFunctions.setPath("/api/carts/items"))
                .before(BeforeFilterFunctions.uri(RESOURCE_SERVER_BASE_URL))
                .filter(TokenRelayFilterFunctions.tokenRelay())
                .build())
            .and(GatewayRouterFunctions.route()
                .PATCH("/protected/api/carts/items/{cartItemId}", HandlerFunctions.http())
                .before(BeforeFilterFunctions.setPath("/api/carts/items/{cartItemId}"))
                .before(BeforeFilterFunctions.uri(RESOURCE_SERVER_BASE_URL))
                .filter(TokenRelayFilterFunctions.tokenRelay())
                .build())
            .and(GatewayRouterFunctions.route()
                .DELETE("/protected/api/carts/items/{cartItemId}", HandlerFunctions.http())
                .before(BeforeFilterFunctions.setPath("/api/carts/items/{cartItemId}"))
                .before(BeforeFilterFunctions.uri(RESOURCE_SERVER_BASE_URL))
                .filter(TokenRelayFilterFunctions.tokenRelay())
                .build());
    }

    @Bean
    public RouterFunction<ServerResponse> resourceRouter() {
        return GatewayRouterFunctions.route()
            .GET("/images/books/**", HandlerFunctions.http())
            .before(BeforeFilterFunctions.uri(RESOURCE_SERVER_BASE_URL))
            .build()
            .and(GatewayRouterFunctions.route()
                .GET("/images/avatars/**", HandlerFunctions.http())
                .before(BeforeFilterFunctions.uri(RESOURCE_SERVER_BASE_URL))
                .build());
    }

    @Bean
    public RouterFunction<ServerResponse> profileRouter() {
        return GatewayRouterFunctions.route()
            .GET("/protected/api/me/profile", HandlerFunctions.http())
            .before(BeforeFilterFunctions.setPath("/api/me/profile"))
            .before(BeforeFilterFunctions.uri(RESOURCE_SERVER_BASE_URL))
            .filter(TokenRelayFilterFunctions.tokenRelay())
            .build()
            .and(GatewayRouterFunctions.route()
                .GET("/protected/api/me/profile/avatar", HandlerFunctions.http())
                .before(BeforeFilterFunctions.setPath("/api/me/profile/avatar"))
                .before(BeforeFilterFunctions.uri(RESOURCE_SERVER_BASE_URL))
                .filter(TokenRelayFilterFunctions.tokenRelay())
                .build())
            .and(GatewayRouterFunctions.route()
                .PATCH("/protected/api/me/profile", HandlerFunctions.http())
                .before(BeforeFilterFunctions.setPath("/api/me/profile"))
                .before(BeforeFilterFunctions.uri(RESOURCE_SERVER_BASE_URL))
                .filter(TokenRelayFilterFunctions.tokenRelay())
                .build());
    }


}
