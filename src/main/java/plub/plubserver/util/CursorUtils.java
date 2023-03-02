package plub.plubserver.util;

public class CursorUtils {

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
