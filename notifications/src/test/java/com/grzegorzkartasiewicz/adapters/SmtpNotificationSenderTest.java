package com.grzegorzkartasiewicz.adapters;

import static org.assertj.core.api.Assertions.assertThat;

import com.grzegorzkartasiewicz.app.SocialGraphPort;
import com.grzegorzkartasiewicz.domain.Channel;
import com.grzegorzkartasiewicz.domain.Notification;
import com.grzegorzkartasiewicz.domain.NotificationType;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(classes = {
    SmtpNotificationSender.class,
    org.springframework.boot.mail.autoconfigure.MailSenderAutoConfiguration.class
}, properties = {
    "spring.mail.host=localhost",
    "spring.mail.port=3025",
    "spring.mail.username=test",
    "spring.mail.password=test",
    "spring.docker.compose.enabled=false",
    "jwt.secret=thisisaverysecuresecretkeyforjwttesting123456",
    "jwt.expiration.ms=86400000"
})
class SmtpNotificationSenderTest {

  @MockitoBean
  private SocialGraphPort socialGraphPort;

  @RegisterExtension
  static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
      .withConfiguration(GreenMailConfiguration.aConfig().withUser("test", "test"))
      .withPerMethodLifecycle(false);

  @Autowired
  private SmtpNotificationSender notificationSender;

  @Test
  @DisplayName("Should send email notification successfully")
  void shouldSendEmail() throws Exception {
    // given
    UUID recipientId = UUID.randomUUID();
    String targetEmail = "user@example.com";
    Map<String, String> params = Map.of("email", targetEmail, "message", "Hello!");
    Notification notification = Notification.create(recipientId, NotificationType.FRIEND_REQUEST,
        Channel.EMAIL, params);

    // when
    notificationSender.send(notification);

    // then
    MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
    assertThat(receivedMessages).hasSize(1);

    MimeMessage message = receivedMessages[0];
    assertThat(message.getAllRecipients()[0].toString()).isEqualTo(targetEmail);
    assertThat(message.getSubject()).isEqualTo("New Notification: FRIEND_REQUEST");
    assertThat(message.getContent().toString()).contains("message: Hello!");
  }
}
