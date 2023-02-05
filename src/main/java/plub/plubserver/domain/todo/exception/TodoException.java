package plub.plubserver.domain.todo.exception;

import plub.plubserver.domain.todo.config.TodoCode;

public class TodoException extends RuntimeException {
    TodoCode todoCode;

    public TodoException(TodoCode todoCode) {
        super(todoCode.getMessage());
        this.todoCode = todoCode;
    }
}
