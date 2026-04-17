package com.grzegorzkartasiewicz.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.grzegorzkartasiewicz.domain.vo.MessageContent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MessageContentTest {

  @Test
  void shouldCreateMessageContentWhenValid() {
    String validText = "Hello, friend!";
    MessageContent content = new MessageContent(validText);
    assertThat(content.value()).isEqualTo(validText);
  }

  @ParameterizedTest
  @ValueSource(strings = {"", " ", "   ", "\t", "\n"})
  void shouldThrowExceptionWhenContentIsBlank(String blankText) {
    assertThatThrownBy(() -> new MessageContent(blankText))
        .isInstanceOf(ValidationException.class);
  }

  @Test
  void shouldThrowExceptionWhenContentIsNull() {
    assertThatThrownBy(() -> new MessageContent(null))
        .isInstanceOf(ValidationException.class);
  }

  @Test
  void shouldThrowExceptionWhenContentIsTooLong() {
    String tooLongText = "a".repeat(1001);
    assertThatThrownBy(() -> new MessageContent(tooLongText))
        .isInstanceOf(ValidationException.class);
  }

  @Test
  void shouldCreateMessageContentWhenItHasMaxLength() {
    String maxLengthText = "a".repeat(1000);
    MessageContent content = new MessageContent(maxLengthText);
    assertThat(content.value()).isEqualTo(maxLengthText);
  }
}
