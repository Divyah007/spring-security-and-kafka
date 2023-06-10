package com.divya.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

  @Autowired
  private AuthTokenFilter tokenFilter;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf().disable().cors().and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
      // HERE endpoints "/actuator/prometheus", "/actuator/info",
      // "/actuator/health", "/user/register","/user/login" does not require
      // token to access the
      // resourse
      .authorizeHttpRequests()
      .antMatchers("/actuator/prometheus", "/actuator/info", "/actuator/health", "/user/register", "/user/login")
      .permitAll().anyRequest().authenticated();
    http.addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

}
