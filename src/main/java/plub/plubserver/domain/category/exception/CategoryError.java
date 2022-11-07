package plub.plubserver.domain.category.exception;

import lombok.Getter;

@Getter
public enum CategoryError {
    NOT_FOUND_CATEGORY(404, "04010", "not found category error.");

    private final int HttpCode;
    private final String statusCode;
    private final String message;

    CategoryError (int HttpCode, String statusCode, String message) {
        this.HttpCode = HttpCode;
        this.statusCode = statusCode;
        this.message = message;
    }
}
