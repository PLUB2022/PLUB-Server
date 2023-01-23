package plub.plubserver.common.model;

public enum SortType {
    NEW, POPULAR;

    public static SortType of(String sort) {
        if (sort.equals("popular")) return SortType.POPULAR;
        else return SortType.NEW;
    }
}
