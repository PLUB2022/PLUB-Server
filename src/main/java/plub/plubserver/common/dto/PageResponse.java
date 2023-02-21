package plub.plubserver.common.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
@Builder
@Getter
@Slf4j
public class PageResponse<T> {
    int totalPages;
    Long totalElements;

    boolean last;
    List<T> content;

    public static <T> PageResponse<T> of(Page<T> page) {
        return PageResponse.<T>builder()
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .last(page.isLast())
                .content(page.getContent())
                .build();
    }

    // 리스트를 페이징 자체 처리
    public static <T> PageResponse<T> of(Pageable pageable, List<T> list) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), list.size());
        List<T> content = new ArrayList<>();
        if (start > end) list.subList(end, list.size());
        else content = list.subList(start, end);
        Page<T> newPage = new PageImpl<>(
                content,
                pageable,
                list.size()
        );
        return PageResponse.of(newPage);

    }

    public static <T, O> PageResponse<T> of(Page<O> origin, List<T> list) {
        return PageResponse.<T>builder()
                .totalPages(origin.getTotalPages())
                .totalElements(origin.getTotalElements())
                .last(origin.isLast())
                .content(list)
                .build();
    }
}
