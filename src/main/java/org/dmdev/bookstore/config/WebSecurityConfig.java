package org.dmdev.bookstore.config;

import lombok.extern.slf4j.Slf4j;
import org.dmdev.bookstore.security.AuthenticationManage;
import org.dmdev.bookstore.security.BearerTokenServerAuthenticationConverter;
import org.dmdev.bookstore.security.JwtHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

@Slf4j
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class WebSecurityConfig {

    @Value("${jwt.secret}")
    private String secret;

    private final String [] publicRoutes = {"/api/auth/register", "/api/auth/login"};

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         AuthenticationManage authenticationManage,
                                                         ServerAccessDeniedHandler accessDeniedHandler,
                                                         ServerAuthenticationEntryPoint authenticationEntryPoint) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .exceptionHandling(ex -> {
                    ex.accessDeniedHandler(accessDeniedHandler);
                    ex.authenticationEntryPoint(authenticationEntryPoint);
                })
                .authorizeExchange(auth -> auth
                        .pathMatchers(publicRoutes).permitAll()
                        .pathMatchers(HttpMethod.OPTIONS).permitAll()
                        .anyExchange().authenticated()
                )
                .addFilterAt(bearerAuthenticationWebFilter(authenticationManage), SecurityWebFiltersOrder.AUTHENTICATION)
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    public ServerAccessDeniedHandler accessDeniedHandler() {
        return (exchange, ex) -> {
            log.error("Access denied: {}", ex.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        };
    }

    @Bean
    public ServerAuthenticationEntryPoint authenticationEntryPoint() {
        return (exchange, ex) -> {
            log.error("Unauthorized: {}", ex.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        };
    }

    private AuthenticationWebFilter bearerAuthenticationWebFilter(AuthenticationManage authenticationManage) {
        AuthenticationWebFilter bearerAuthenticationWebFilter = new AuthenticationWebFilter(authenticationManage);
        bearerAuthenticationWebFilter.setServerAuthenticationConverter(new BearerTokenServerAuthenticationConverter(new JwtHandler(secret)));
        bearerAuthenticationWebFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/**"));
        return bearerAuthenticationWebFilter;
    }
}
