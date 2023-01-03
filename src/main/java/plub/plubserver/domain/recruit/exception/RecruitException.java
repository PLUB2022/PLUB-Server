package plub.plubserver.domain.recruit.exception;

import plub.plubserver.domain.recruit.config.RecruitCode;

public class RecruitException extends RuntimeException {
    RecruitCode recruitCode;

    public RecruitException(RecruitCode recruitCode) {
        super(recruitCode.getMessage());
        this.recruitCode = recruitCode;
    }
}
