package plub.plubserver.util;

public class CursorUtils {

    public static final int TEN_AMOUNT = 10;
    public static final int TWENTY_AMOUNT = 20;

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
}
