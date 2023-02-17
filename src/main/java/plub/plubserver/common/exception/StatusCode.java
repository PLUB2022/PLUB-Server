package plub.plubserver.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StatusCode {
    /**
     * Common
     */
    COMMON_BAD_REQUEST(400, 9000, ""),
    INVALID_INPUT_VALUE(400, 9010, "invalid input value."),
    METHOD_NOT_ALLOWED(405, 9020, "method not allowed."),
    HTTP_CLIENT_ERROR(400, 9040, "http client error."),
    AWS_S3_UPLOAD_FAIL(400, 9050, "AWS S3 upload fail."),
    AWS_S3_DELETE_FAIL(400, 9070, "AWS S3 delete fail."),
    AWS_S3_FILE_SIZE_EXCEEDED(400, 9060, "exceeded file size."),

    /**
     * Account
     */
    NOT_FOUND_ACCOUNT(404, 2050, "not found account error."),
    NICKNAME_DUPLICATION(400, 2060, "duplicated nickname error."),
    EMAIL_DUPLICATION(400, 2070, "duplicated email error."),
    SOCIAL_TYPE_ERROR(400, 2090, "invalid social type error."),
    ROLE_ACCESS_ERROR(400, 2120, "role access error."),
    NICKNAME_ERROR(400, 2130, "invalid nickname error."),

    /**
     * Auth
     */
    // success
    LOGIN(200, 1000, "account exist, process login."),
    SIGNUP_COMPLETE(200, 1010, "signup complete, access token is issued."),
    ADMIN_LOGIN(200, 1020, "admin check, process login."),

    // fail
    NEED_TO_SIGNUP(404, 2050, "need to signup, X-ACCESS-TOKEN is issued."),
    FILTER_ACCESS_DENIED(401, 2000, "access denied."),
    FILTER_ROLE_FORBIDDEN(403, 2010, "role forbidden."),

    APPLE_LOGIN_ERROR(400, 2020, "apple login error."),
    SIGNUP_TOKEN_ERROR(400, 2030, "invalid sign up token error."),

    NOT_FOUND_REFRESH_TOKEN(404, 2040, "not found refresh token."),
    ENCRYPTION_FAILURE(400, 2100, "encryption failure"),
    DECRYPTION_FAILURE(400, 2110, "decryption failed."),

    IS_NOT_REFRESH(400, 2120, "this token is not refresh token."),
    EXPIRED_REFRESH(400, 2130, "expired refresh token."),

    /**
     * Archive
     */
    NOT_FOUND_ARCHIVE(404, 3010, "not found archive error."),
    NOT_ARCHIVE_AUTHOR(403, 3020, "this account is not this archive author."),

    /**
     * Calender
     */
    NOT_FOUNT_CALENDAR(404, 7050, "not fount calendar."),
    NOT_FOUNT_CALENDAR_ATTEND(404, 7060, "not fount calendar attend."),


    /**
     * Category
     */
    NOT_FOUND_CATEGORY(404, 3000, "not found category error."),

    /**
     * Feed
     */
    NOT_FOUND_FEED(404, 8010, "not found feed error."),
    NOT_FOUND_FEED_COMMENT(404, 8020, "not found feed comment error."),
    NOT_FEED_AUTHOR_ERROR(403, 8030, "not feed author error."),
    DELETED_STATUS_FEED(400, 8040, "deleted status feed error."),
    CANNOT_DELETED_FEED(400, 8050, "system feed cannot be deleted."),
    DELETED_STATUS_COMMENT(400, 8060, "deleted status feed comment error."),
    MAX_FEED_PIN(400, 8070, "max feed pin error."),

    /**
     * Notice
     */
    NOT_FOUND_NOTICE(404, 8510, "not found notice error."),
    NOT_FOUND_NOTICE_COMMENT(404, 8520, "not found notice comment error."),
    NOT_NOTICE_AUTHOR_ERROR(403, 8530, "not notice author error."),
    DELETED_STATUS_NOTICE(400, 8540, "deleted status notice error."),
    DELETED_STATUS_NOTICE_COMMENT(400, 8550, "deleted status notice comment error."),

    /**
     * Notification
     */
    GET_FCM_ACCESS_TOKEN_ERROR(400, 3030, "fcm access token get failed."),
    SEND_FCM_PUSH_ERROR(400, 3040, "send fcm push message failed."),
    FCM_MESSAGE_JSON_PARSING_ERROR(400, 3050, "fcm message json parsing failed."),

    /**
     * Plubbing
     */
    NOT_FOUND_PLUBBING(404, 6010, "not found plubbing error."),
    FORBIDDEN_ACCESS_PLUBBING(403, 6020, "this account is not joined this plubbing."),
    NOT_HOST_ERROR(403, 6030, "not host error."),
    DELETED_STATUS_PLUBBING(404, 6040, "deleted/ended status error."),
    NOT_MEMBER_ERROR(403, 6100, "this account is not a member of this plubbing."),
    NOT_FOUND_SUB_CATEGORY(404, 6110, "not found sub category error."),


    /**
     * Recruit
     */
    HOST_RECRUIT_ERROR(400, 6050, "host cannot apply it's own plubbings."),
    NOT_FOUND_QUESTION(404, 6060, "not found question error."),
    ALREADY_APPLIED_RECRUIT(400, 6070, "this applicant is already applied."),

    ALREADY_ACCEPTED(400, 6080, "this applicant is already accepted."),

    ALREADY_REJECTED(400, 6090, "this applicant is already rejected."),


    /**
     * Todo
     */
    NOT_FOUNT_TODO(404, 7010, "not fount todo."),
    NOT_COMPLETE_TODO(400, 7020, "not complete todo."),
    ALREADY_CHECKED_TODO(400, 7030, "already checked todo."),
    ALREADY_PROOF_TODO(400, 7040, "already proof todo."),

    ;

    private final int HttpCode;
    private final int statusCode;
    private final String message;
}
