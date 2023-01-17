package plub.plubserver.domain.recruit.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RecruitCode {
    HOST_RECRUIT_ERROR(400, 6050, "host cannot apply it's own plubbings."),
    NOT_FOUND_QUESTION(404, 6060, "not found question error."),
    ALREADY_APPLIED_RECRUIT(400, 6070, "this applicant is already applied."),

    ALREADY_ACCEPTED(400, 6080, "this applicant is already accepted."),

    ALREADY_REJECTED(400, 6090, "this applicant is already rejected.");


    private final int HttpCode;
    private final int statusCode;
    private final String message;
}
