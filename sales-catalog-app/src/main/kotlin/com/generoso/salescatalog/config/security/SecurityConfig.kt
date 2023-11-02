package com.generoso.salescatalog.config.security

import com.generoso.salescatalog.auth.UserInfo
import com.generoso.salescatalog.auth.UserRole
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig @Autowired constructor(private val securityEntryPoint: SecurityEntryPoint) {

    private val roleClient: String = UserRole.CLIENT.role
    private val roleSales: String = UserRole.SALES.role

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain? {
        http.csrf { csrf -> csrf.disable() }

        http.authorizeHttpRequests { authorizeExchange ->
            //@formatter:off
            authorizeExchange.requestMatchers("/hello-world-public").permitAll()
                .requestMatchers("/hello-world").hasAnyRole(roleClient, roleSales)
                .requestMatchers("/hello-world-client").hasRole(roleClient)
                .requestMatchers("/hello-world-sales").hasRole(roleSales)
                .requestMatchers(HttpMethod.POST, "/v1/products").hasAnyRole(roleSales)
                .requestMatchers(HttpMethod.GET, "/v1/products/**").hasAnyRole(roleSales, roleClient)
                .requestMatchers("/private/**").permitAll()
                .anyRequest().authenticated()
            //@formatter:on
        }

        http.exceptionHandling { exceptionHandling ->
            exceptionHandling.authenticationEntryPoint(securityEntryPoint)
        }

        http.sessionManagement { session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        }
        http.oauth2ResourceServer { config: OAuth2ResourceServerConfigurer<HttpSecurity> ->
            config.jwt(Customizer.withDefaults())
        }
        return http.build()
    }

    @Bean
    fun jwtAuthenticationConverter(): JwtAuthenticationConverter {
        val jwtAuthenticationConverter = JwtAuthenticationConverter()
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(CustomJwtGrantedAuthoritiesConverter())
        return jwtAuthenticationConverter
    }

    @Bean
    fun authenticationThreadLocal(): InheritableThreadLocal<UserInfo> {
        return object : InheritableThreadLocal<UserInfo>() {
            override fun initialValue(): UserInfo {
                return UserInfo()
            }
        }
    }
}