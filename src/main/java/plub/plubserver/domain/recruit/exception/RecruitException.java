package plub.plubserver.domain.recruit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import plub.plubserver.domain.recruit.config.RecruitCode;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class RecruitException extends RuntimeException {
    RecruitCode recruitCode;

    public RecruitException(RecruitCode recruitCode) {
        super(recruitCode.getMessage());
        this.recruitCode = recruitCode;
    }
}
