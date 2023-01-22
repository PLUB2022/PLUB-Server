package plub.plubserver.common.dto;

import lombok.Builder;

import javax.validation.constraints.Size;

public class CommentDto {
    public record CreateCommentRequest(
            @Size(max = 100)
            String content
    ) {
        @Builder
        public CreateCommentRequest {
        }
    }

    public record UpdateCommentRequest(
            @Size(max = 100)
            String content
    ) {
        @Builder
        public UpdateCommentRequest {
        }
    }

    public record CommentResponse(
            long commentId,
            String content,
            String profileImage,
            String nickname,
            String createdAt
    ) {
        @Builder
        public CommentResponse {
        }
    }

    public record CommentIdResponse(Long commentId) {
    }

    public record CommentMessage(Object result) {
    }
}
