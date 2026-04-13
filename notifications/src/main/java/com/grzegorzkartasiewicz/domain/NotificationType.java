package com.grzegorzkartasiewicz.domain;

public enum NotificationType {
  PASSWORD_RESET(true, Audience.DIRECT),
  EMAIL_VERIFICATION(true, Audience.DIRECT),
  COMMENT_LIKED(false, Audience.DIRECT),
  POST_LIKED(false, Audience.DIRECT),
  COMMENT_UNLIKED(false, Audience.DIRECT),
  POST_UNLIKED(false, Audience.DIRECT),
  COMMENT_EDITED(false, Audience.DIRECT),
  POST_EDITED(false, Audience.DIRECT),
  POST_CREATED(false, Audience.NETWORK),
  MESSAGE_RECEIVED(false, Audience.DIRECT),
  FRIEND_REQUEST(false, Audience.DIRECT);

  final boolean critical;
  final Audience audience;

  NotificationType(boolean critical, Audience audience) {
    this.critical = critical;
    this.audience = audience;
  }

  boolean isCritical() {
    return this.critical;
  }

  public Audience getAudience() {
    return this.audience;
  }
}
