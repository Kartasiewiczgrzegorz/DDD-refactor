package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.app.ExternalSenderException;
import com.grzegorzkartasiewicz.app.NotificationSender;
import com.grzegorzkartasiewicz.domain.Channel;
import com.grzegorzkartasiewicz.domain.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class SmtpNotificationSender implements NotificationSender {

  private final JavaMailSender javaMailSender;

  @Override
  public void send(Notification notification) {
    if (notification.getChannel() != Channel.EMAIL) {
      log.info("SmtpNotificationSender only handles EMAIL channel, skipping {}",
          notification.getId());
      return;
    }

    String recipientEmail = notification.getParams()
        .getOrDefault("email", notification.getRecipientId().id() + "@example.com");

    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(recipientEmail);
    message.setSubject("New Notification: " + notification.getType());
    message.setText(buildMessageBody(notification));
    message.setFrom("noreply@facebook-clone.com");

    try {
      javaMailSender.send(message);
      log.info("Sent email notification {} to {}", notification.getId(), recipientEmail);
    } catch (MailException e) {
      log.error("Failed to send email for notification {}", notification.getId(), e);
      throw new ExternalSenderException("Mail sending failed: " + e.getMessage());
    }
  }

  private String buildMessageBody(Notification notification) {
    StringBuilder body = new StringBuilder("You have a new notification of type: ");
    body.append(notification.getType()).append("\n\nDetails:\n");
    if (notification.getParams() != null) {
      notification.getParams()
          .forEach((k, v) -> body.append(k).append(": ").append(v).append("\n"));
    }
    return body.toString();
  }
}
