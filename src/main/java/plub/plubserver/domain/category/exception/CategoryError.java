package plub.plubserver.domain.category.exception;

import lombok.Getter;

@Getter
public enum CategoryError {
    NOT_FOUND_CATEGORY(404, "CATEGORY-001", "not found category error");

    private final int status;
    private final String code;
    private final String message;

    CategoryError (int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
