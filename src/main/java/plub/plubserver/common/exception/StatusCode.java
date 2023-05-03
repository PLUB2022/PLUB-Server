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
    HTTP_CLIENT_ERROR(400, 9030, "http client error."),
    AWS_S3_UPLOAD_FAIL(400, 9040, "AWS S3 upload fail."),
    AWS_S3_DELETE_FAIL(400, 9050, "AWS S3 delete fail."),
    AWS_S3_FILE_SIZE_EXCEEDED(400, 9060, "exceeded file size."),
    PAUSED_ACCOUNT(400, 9070, "paused account error."),
    BANNED_ACCOUNT(400, 9080, "banned account error."),
    PERMANENTLY_BANNED_ACCOUNT(400, 9090, "permanently banned account error."),
    INACTIVE_ACCOUNT(400, 9100, "inactive account error."),
    DORMANT_ACCOUNT(400, 9110, "dormant account error."),

    /**
     * Account
     */
    NOT_FOUND_ACCOUNT(404, 2000, "not found account error."),
    NICKNAME_DUPLICATION(400, 2010, "duplicated nickname error."),
    EMAIL_DUPLICATION(400, 2020, "duplicated email error."),
    SOCIAL_TYPE_ERROR(400, 2030, "invalid social type error."),
    ROLE_ACCESS_ERROR(400, 2040, "role access error."),
    NICKNAME_ERROR(400, 2050, "invalid nickname error."),
    SELF_REPORT_ERROR(400, 2060, "self report error."),
    SUSPENDED_ACCOUNT(400, 2070, "suspended account error."),
    NICKNAME_CHANGE_LIMIT(400, 2080, "nickname change limit error."),
    ALREADY_INACTIVE_ACCOUNT(400, 2090, "already inactive account error."),

    /**
     * Auth
     */
    // success
    LOGIN(200, 1001, "account exist, process login."),
    SIGNUP_COMPLETE(200, 1011, "signup complete, access token is issued."),
    ADMIN_LOGIN(200, 1021, "admin check, process login."),

    // fail
    FILTER_ACCESS_DENIED(401, 1000, "access denied."),
    FILTER_ROLE_FORBIDDEN(403, 1010, "role forbidden."),
    APPLE_LOGIN_ERROR(400, 1020, "apple login error."),
    SIGNUP_TOKEN_ERROR(400, 1030, "invalid sign up token error."),
    NOT_FOUND_REFRESH_TOKEN(404, 1040, "not found refresh token."),
    NEED_TO_SIGNUP(404, 1050, "need to signup, X-ACCESS-TOKEN is issued."),
    ENCRYPTION_FAILURE(400, 1060, "encryption failure"),
    DECRYPTION_FAILURE(400, 1070, "decryption failed."),
    IS_NOT_REFRESH(400, 1080, "this token is not refresh token."),
    EXPIRED_REFRESH(400, 1090, "expired refresh token."),

    /**
     * Category
     */
    NOT_FOUND_CATEGORY(404, 3000, "not found category error."),

    /**
     * Archive
     */
    NOT_FOUND_ARCHIVE(404, 3500, "not found archive error."),
    NOT_ARCHIVE_AUTHOR(403, 3510, "this account is not this archive author."),

    /**
     * Policy, Announcement
     */
    POLICY_NOT_FOUND(404, 4000, "not found policy error.."),
    NOT_FOUND_ANNOUNCEMENT(404, 4010, "not found announcement error."),

    /**
     * Notification
     */
    GET_FCM_ACCESS_TOKEN_ERROR(400, 4500, "fcm access token get failed."),
    SEND_FCM_PUSH_ERROR(400, 4510, "send fcm push message failed."),
    FCM_MESSAGE_JSON_PARSING_ERROR(400, 4520, "fcm message json parsing failed."),
    NOT_FOUND_NOTIFICATION(404, 4530, "not found notification error."),

    /**
     * Report
     */
    NOT_FOUND_REPORT(404, 5000, "not found report error."),
    NOT_FOUND_SUSPEND_ACCOUNT(404, 5010, "not found suspend account error."),
    CANNOT_CHANGE_PERMANENTLY_BANNED_ACCOUNT(400, 5020, "cannot change permanently banned account."),
    REPORT_TARGET_NOT_FOUND(404, 5030, "report target not found."),
    DUPLICATE_REPORT(400, 5040, "duplicate report."),
    INVALID_ACCOUNT_STATUS(400, 5050, "invalid account status."),
    TOO_MANY_REPORTS(400, 5060, "too many reports."),
    ALREADY_REVOKE_SUSPEND_ACCOUNT(400, 5070, "already revoke suspend account."),

    /**
     * Plubbing
     */
    NOT_FOUND_PLUBBING(404, 6000, "not found plubbing error."),
    FORBIDDEN_ACCESS_PLUBBING(403, 6010, "this account is not joined this plubbing."),
    NOT_HOST_ERROR(403, 6020, "not host error."),
    DELETED_STATUS_PLUBBING(404, 6030, "deleted/ended status error."),
    NOT_MEMBER_ERROR(403, 6040, "this account is not a member of this plubbing."),
    NOT_FOUND_SUB_CATEGORY(404, 6050, "not found sub category error."),

    /**
     * Recruit
     */
    HOST_RECRUIT_ERROR(400, 6060, "host cannot apply it's own plubbings."),
    NOT_FOUND_QUESTION(404, 6070, "not found question error."),
    NOT_APPLIED_RECRUIT(400, 6080, "this applicant is not applied."),
    ALREADY_APPLIED_RECRUIT(400, 6090, "this applicant is already applied."),
    ALREADY_ACCEPTED(400, 6100, "this applicant is already accepted."),
    ALREADY_REJECTED(400, 6110, "this applicant is already rejected."),
    PLUBBING_MEMBER_IS_FULL(400, 6120, "plubbing member is full."),
    NOT_FOUND_RECRUIT(404, 6130, "not found recruit error."),
    ALREADY_DONE_RECRUIT(400, 6140, "this recruit is already done."),
    MAX_PLUBBING_LIMIT_OVER(400, 6150, "max active plubbing limit is 3."),

    /**
     * Todo
     */
    NOT_FOUNT_TODO(404, 7000, "not found todo error."),
    NOT_COMPLETE_TODO(400, 7010, "not complete todo."),
    ALREADY_CHECKED_TODO(400, 7020, "already checked todo."),
    ALREADY_PROOF_TODO(400, 7030, "already proof todo."),
    NOT_FOUNT_TODO_TIMELINE(404, 7040, "not found todo timeline error."),
    TOO_MANY_TODO(400, 7050, "too many todo error."),

    /**
     * Calender
     */
    NOT_FOUNT_CALENDAR(404, 7500, "not found calendar error."),
    NOT_FOUNT_CALENDAR_ATTEND(404, 7510, "not fount calendar attend."),
    NOT_AUTHORITY_CALENDAR(403, 7520, "not authority calendar error."),

    /**
     * Feed
     */
    NOT_FOUND_FEED(404, 8000, "not found feed error."),
    NOT_FOUND_COMMENT(404, 8010, "not found comment error."),
    NOT_FEED_AUTHOR_ERROR(403, 8020, "not feed author error."),
    DELETED_STATUS_FEED(400, 8030, "deleted status feed error."),
    CANNOT_DELETED_FEED(400, 8040, "system feed cannot be deleted."),
    DELETED_STATUS_COMMENT(400, 8050, "deleted status comment error."),
    MAX_FEED_PIN(400, 8060, "max feed pin error."),

    /**
     * Notice
     */
    NOT_FOUND_NOTICE(404, 8500, "not found notice error."),
    NOT_NOTICE_AUTHOR_ERROR(403, 8510, "not notice author error."),
    DELETED_STATUS_NOTICE(400, 8520, "deleted status notice error."),

    ;

    private final int HttpCode;
    private final int statusCode;
    private final String message;
}