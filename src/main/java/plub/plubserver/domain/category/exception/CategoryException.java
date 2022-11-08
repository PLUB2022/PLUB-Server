package plub.plubserver.domain.category.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import plub.plubserver.domain.category.config.CategoryCode;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class CategoryException extends RuntimeException{
    CategoryCode categoryError;

    public CategoryException(CategoryCode categoryError) {
        super(categoryError.getMessage());
        this.categoryError = categoryError;
    }
}
