package plub.plubserver.domain.category.config;

import lombok.Getter;

@Getter
public enum CategoryCode {
    /**
     * success
     */
    CATEGORY_SUCCESS(200, 3001, "category request complete."),

    /**
     * fail
     */
    NOT_FOUND_CATEGORY(404, 3000, "not found category error.");


    private final int HttpCode;
    private final int statusCode;
    private final String message;

    CategoryCode(int HttpCode, int statusCode, String message) {
        this.HttpCode = HttpCode;
        this.statusCode = statusCode;
        this.message = message;
    }
}
