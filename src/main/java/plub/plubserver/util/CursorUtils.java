package plub.plubserver.util;

import com.querydsl.core.types.dsl.BooleanExpression;

import static plub.plubserver.domain.feed.model.QFeed.feed;

public class CursorUtils {

    public BooleanExpression getCursorId(Long cursorId) {
        return cursorId == null ? null : feed.id.gt(cursorId);
    }

    public static Long getNextCursorId(Long currentCursorId, int amount, Long totalElements) {
        if (currentCursorId == null) {
            return (long) amount;
        }
        long nextCursorId = currentCursorId + amount;
        if (totalElements < nextCursorId) {
            return null;
        }
        return nextCursorId;
    }

    public static Long getTotalElements(Long totalElements, Long cursorElements) {
        if(cursorElements == null) {
            return totalElements;
        }
        Long total = totalElements + cursorElements;
        if (total < totalElements) {
            return totalElements;
        }
        return total;
    }
}
