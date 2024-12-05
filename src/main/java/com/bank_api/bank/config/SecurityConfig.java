    package com.bank_api.bank.config;

    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.authentication.ReactiveAuthenticationManager;
    import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
    import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
    import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
    import org.springframework.security.config.web.server.ServerHttpSecurity;
    import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.security.web.server.SecurityWebFilterChain;

    @Configuration
    @EnableWebFluxSecurity
    @EnableReactiveMethodSecurity
    public class SecurityConfig {
        
        @Bean
        public SecurityWebFilterChain webFilterChain(ServerHttpSecurity http, ReactiveAuthenticationManager manager) {
            return http.httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                    .csrf(ServerHttpSecurity.CsrfSpec::disable)
                    .authenticationManager(manager)
                    .authorizeExchange(exchange -> exchange
                            .pathMatchers("/auth/**").permitAll()
                            .anyExchange().authenticated()
                    ).build();
        }

        @Bean
        public ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
            UserDetailsRepositoryReactiveAuthenticationManager authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
            authenticationManager.setPasswordEncoder(passwordEncoder);
            return authenticationManager;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
        
    }
