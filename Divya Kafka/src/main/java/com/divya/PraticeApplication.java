package com.divya;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = { "com.divya" })
@SpringBootApplication

public class PraticeApplication {

  public static void main(String[] args) {
    SpringApplication.run(PraticeApplication.class, args);
  }

}
