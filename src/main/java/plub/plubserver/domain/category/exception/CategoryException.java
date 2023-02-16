package plub.plubserver.domain.category.exception;

import plub.plubserver.domain.category.config.CategoryCode;

public class CategoryException extends RuntimeException {
    public CategoryCode categoryError;

    public CategoryException(CategoryCode categoryError) {
        super(categoryError.getMessage());
        this.categoryError = categoryError;
    }
}
