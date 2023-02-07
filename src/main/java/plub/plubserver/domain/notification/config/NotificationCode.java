package plub.plubserver.domain.notification.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationCode {
    // 3030 starts
    GET_FCM_ACCESS_TOKEN_ERROR(400, 3030, "fcm access token get failed."),
    SEND_FCM_PUSH_ERROR(400, 3040, "send fcm push message failed."),
    FCM_MESSAGE_JSON_PARSING_ERROR(400, 3050, "fcm message json parsing failed."),;

    private final int HttpCode;
    private final int statusCode;
    private final String message;
}
