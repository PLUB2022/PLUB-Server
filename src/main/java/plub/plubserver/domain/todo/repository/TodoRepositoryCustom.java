package plub.plubserver.domain.todo.repository;

import plub.plubserver.domain.plubbing.model.Plubbing;
import plub.plubserver.domain.todo.model.Todo;
import plub.plubserver.domain.todo.model.TodoTimeline;

import java.util.List;

public interface TodoRepositoryCustom {

    List<Todo> findAllByTodoTimelineAndPlubbing(
            TodoTimeline todoTimeline,
            Plubbing plubbing
    );
}
