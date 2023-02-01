package plub.plubserver.common.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
@Builder
@Getter
public class PageResponse<T> {
    int totalPages;
    int totalElements;
    boolean last;
    List<T> content;

    public static <T> PageResponse<T> of(Page<T> page) {
        return PageResponse.<T>builder()
                .totalPages(page.getTotalPages())
                .totalElements((int) page.getTotalElements())
                .last(page.isLast())
                .content(page.getContent())
                .build();
    }

    public static <T> PageResponse<T> of(Pageable pageable, List<T> list) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), list.size());
        Page<T> recruitCardPage = new PageImpl<>(
                list.subList(start, end),
                pageable,
                list.size()
        );
        return PageResponse.of(recruitCardPage);
    }
}
