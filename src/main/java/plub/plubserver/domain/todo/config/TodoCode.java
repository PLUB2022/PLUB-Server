package plub.plubserver.domain.todo.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TodoCode {
    NOT_FOUNT_TODO(404, 7010, "not fount todo"),
    NOT_COMPLETE_TODO(400, 7020, "not complete todo"),
    NOT_FOUNT_TODO_TIMELINE(404, 7030, "not fount todo timeline"),
    ALREADY_CHECKED_TODO(400, 7040, "already checked todo"),
    ALREADY_PROOF_TODO(400, 7050, "already proof todo"),
    NOT_CHECKED_TODO(400, 7060, "not checked todo"),
    ;


    private final int HttpCode;
    private final int statusCode;
    private final String message;
}
