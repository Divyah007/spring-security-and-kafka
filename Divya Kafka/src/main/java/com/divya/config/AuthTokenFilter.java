package com.divya.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.divya.model.User;
import com.divya.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AuthTokenFilter extends OncePerRequestFilter {

  // OncePerRequestFilter
  // When a request goes through the filter chain, we might want some of
  // the authentication actions to happen only once for the request.

  // We can extend the OncePerRequestFilter in such situations.
  // Spring guarantees that the OncePerRequestFilter is executed only once for a
  // given request.
  private static final Integer BEARER_SIZE = 7;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private JwtUtils jwtUtils;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    // here it checks for every incoming request if the token is there and
    // whether it is valid token or not
    String jwttoken = getJwtTokenFromHTTPRequest(request);

    try {

      if (StringUtils.hasText(jwttoken)) {

        if (jwttoken != null && jwtUtils.validateJwtToken(jwttoken)) {
          String phone = jwtUtils.getUserNameFromJwtToken(jwttoken);
          User fetchedUser = userRepository.findByPhone(phone).get();
          // UsernamePasswordAuthenticationToken is an implementation of the
          // Authentication interface
          // which specifies that the user wants to authenticate using a
          // username and password and token
          UsernamePasswordAuthenticationToken authentication =
              new UsernamePasswordAuthenticationToken(fetchedUser, jwttoken, null);
          authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

          // The SecurityContextHolder is where Spring
          // Security stores the details of who is authenticated
          SecurityContextHolder.getContext().setAuthentication(authentication);

        }

      }
    } catch (BadCredentialsException ex) {
      request.setAttribute("BadCredentialsException", "BadCredentialsException");
    }

    catch (Exception e) {
      log.error(e.getMessage());
    }

    //
    // The doFilter() is called each time when a
    // request/response is passed through the chain.
    // The FilterChain.doFilter() method invokes the next
    // filter in the chain and calls resource at the end of the chain if the
    // filter
    // is the last filter in the chain.
    //
    // This is just a normal method and after it's execution the control will
    // come to the next line.
    filterChain.doFilter(request, response);
  }

  private String getJwtTokenFromHTTPRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(BEARER_SIZE, bearerToken.length());
    }
    return null;
  }

}
