package plub.plubserver.domain.todo.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import plub.plubserver.domain.plubbing.model.Plubbing;
import plub.plubserver.domain.todo.model.Todo;
import plub.plubserver.domain.todo.model.TodoTimeline;

import java.util.ArrayList;
import java.util.List;

import static plub.plubserver.domain.todo.model.QTodo.todo;

@RequiredArgsConstructor
@Slf4j
public class TodoRepositoryImpl implements TodoRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Todo> findAllByTodoTimelineAndPlubbing(
            TodoTimeline todoTimeline,
            Plubbing plubbing
    ) {
        List<Todo> uncheckedTodos = queryFactory
                .selectFrom(todo)
                .where(
                        todo.todoTimeline.eq(todoTimeline),
                        todo.todoTimeline.plubbing.eq(plubbing),
                        todo.isChecked.eq(false)
                )
                .orderBy(
                        todo.checkAt.asc()
                )
                .fetch();

        List<Todo> checkedTodos = queryFactory
                .selectFrom(todo)
                .where(
                        todo.todoTimeline.eq(todoTimeline),
                        todo.todoTimeline.plubbing.eq(plubbing),
                        todo.isChecked.eq(true)
                )
                .orderBy(
                        todo.modifiedAt.desc()
                )
                .fetch();

        List<Todo> todos = new ArrayList<>();
        todos.addAll(checkedTodos);
        todos.addAll(uncheckedTodos);

        return todos;
    }
}