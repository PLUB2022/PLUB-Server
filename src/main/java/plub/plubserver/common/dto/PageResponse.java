package plub.plubserver.common.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;
@Builder
@Getter
public class PageResponse<T> {
    int totalPages;
    int totalElements;
    boolean isLast;
    List<T> content;

    public static <T> PageResponse<T> of(Page<T> data) {
        return PageResponse.<T>builder()
                .totalPages(data.getTotalPages())
                .totalElements((int) data.getTotalElements())
                .isLast(data.isLast())
                .content(data.getContent())
                .build();
    }
}
