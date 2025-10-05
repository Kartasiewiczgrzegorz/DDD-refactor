package com.grzegorzkartasiewicz.domain;

import org.springframework.security.crypto.password.PasswordEncoder;

public class TestPasswordEncoder implements PasswordEncoder {

  @Override
  public String encode(CharSequence rawPassword) {
    return rawPassword.toString();
  }

  @Override
  public boolean matches(CharSequence rawPassword, String encodedPassword) {
    return rawPassword.equals(encodedPassword);
  }

  @Override
  public boolean upgradeEncoding(String encodedPassword) {
    return PasswordEncoder.super.upgradeEncoding(encodedPassword);
  }
}
