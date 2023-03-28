package plub.plubserver.domain.notice.dto;

import lombok.Builder;
import plub.plubserver.domain.account.dto.AccountDto.*;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.notice.model.Notice;
import plub.plubserver.domain.plubbing.model.Plubbing;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class NoticeDto {
    public record CreateNoticeRequest(
            @NotBlank @Size(max = 15)
            String title,

            @NotBlank @Size(max = 400)
            String content
    ) {
        @Builder
        public CreateNoticeRequest {
        }

        public Notice toEntity(Plubbing plubbing, Account account) {
            return Notice.builder()
                    .title(this.title)
                    .content(this.content)
                    .plubbing(plubbing)
                    .account(account)
                    .build();
        }
    }

    public record UpdateNoticeRequest(
            @NotBlank @Size(max = 15)
            String title,

            @NotBlank @Size(max = 400)
            String content
    ) {
    }

    public record NoticeCardResponse(
            Long noticeId,
            String title,
            String content,
            String createdAt,
            Boolean isHost
    ) {
        @Builder
        public NoticeCardResponse {
        }

        public static NoticeCardResponse of(Notice notice, Boolean isHost) {
            return NoticeCardResponse.builder()
                    .noticeId(notice.getId())
                    .title(notice.getTitle())
                    .content(notice.getContent())
                    .createdAt(notice.getCreatedAt())
                    .isHost(isHost)
                    .build();
        }
    }

    public record NoticeResponse(
            Long noticeId,
            String title,
            String content,
            String createdAt,
            Long likeCount,
            Long commentCount,
            Boolean isHost,
            AccountInfo accountInfo

    ) {
        @Builder
        public NoticeResponse {
        }

        public static NoticeResponse of(Notice notice, Boolean isHost, Long likeCount, Long commentCount) {
            return NoticeResponse.builder()
                    .noticeId(notice.getId())
                    .title(notice.getTitle())
                    .content(notice.getContent())
                    .createdAt(notice.getCreatedAt())
                    .likeCount(likeCount)
                    .commentCount(commentCount)
                    .isHost(isHost)
                    .accountInfo(AccountInfo.of(notice.getAccount()))
                    .build();
        }
    }

    public record NoticeIdResponse(Long noticeId) {
    }

    public record NoticeMessage(Object result) {
    }
}
