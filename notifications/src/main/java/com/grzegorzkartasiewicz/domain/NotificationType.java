package com.grzegorzkartasiewicz.domain;

public enum NotificationType {
  PASSWORD_RESET(true),
  EMAIL_VERIFICATION(true),
  COMMENT_LIKED(false),
  POST_LIKED(false),
  COMMENT_UNLIKED(false),
  POST_UNLIKED(false),
  COMMENT_EDITED(false),
  POST_EDITED(false),
  MESSAGE_RECEIVED(false),
  FRIEND_REQUEST(false);

  final boolean critical;

  NotificationType(boolean critical) {
    this.critical = critical;
  }
}
