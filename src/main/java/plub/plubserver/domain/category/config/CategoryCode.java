package plub.plubserver.domain.category.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CategoryCode {
    NOT_FOUND_CATEGORY(404, 3000, "not found category error.");

    private final int HttpCode;
    private final int statusCode;
    private final String message;
}
