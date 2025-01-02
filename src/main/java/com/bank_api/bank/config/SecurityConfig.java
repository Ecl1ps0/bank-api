        package com.bank_api.bank.config;

        import org.springframework.context.annotation.Bean;
        import org.springframework.context.annotation.Configuration;
        import org.springframework.security.authentication.AuthenticationManager;
        import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
        import org.springframework.security.config.annotation.web.builders.HttpSecurity;
        import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
        import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
        import org.springframework.security.crypto.password.PasswordEncoder;
        import org.springframework.security.web.SecurityFilterChain;
        import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
        
        import com.bank_api.bank.utils.AuthTokenFilter;

        @Configuration
        public class SecurityConfig {
            
            @Bean
            public SecurityFilterChain webFilterChain(HttpSecurity http, AuthTokenFilter authTokenFilter) throws Exception {
                http.csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/auth/**").permitAll()
                        .anyRequest().authenticated()
                    )
                    .addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
            }

            @Bean
            public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
                return authenticationConfiguration.getAuthenticationManager();
            }

            @Bean
            public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
            }
            
        }
