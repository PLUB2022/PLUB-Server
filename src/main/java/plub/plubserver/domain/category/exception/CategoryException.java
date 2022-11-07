package plub.plubserver.domain.category.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class CategoryException extends RuntimeException{
    CategoryError categoryError;

    public CategoryException(CategoryError categoryError) {
        super(categoryError.getMessage());
        this.categoryError = categoryError;
    }
}
