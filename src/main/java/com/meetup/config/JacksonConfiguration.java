/*
 *
 *  * *************************************************************************|
 *  *  Copyright (c) 2015. Yaana Technologies. All rights reserved.           *|
 *  *  Yaana Technologies PROPRIETARY/CONFIDENTIAL                            *|
 *  * *************************************************************************|
 *
 */

package com.meetup.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The type Jackson configuration.
 */
@Configuration
public class JacksonConfiguration {

  /**
   * Java time module module.
   *
   * @return the module
   */
  @Bean
  public Module javaTimeModule() {
    return new JavaTimeModule();
  }
}
