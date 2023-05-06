package plub.plubserver.domain.notification.dto;

import lombok.Builder;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.calendar.model.Calendar;
import plub.plubserver.domain.feed.model.Feed;
import plub.plubserver.domain.feed.model.FeedComment;
import plub.plubserver.domain.notification.model.Notification;
import plub.plubserver.domain.notification.model.NotificationType;
import plub.plubserver.domain.plubbing.model.Plubbing;

import java.util.List;

public class NotificationDto {
    public record NotifyParams(
            Account receiver,
            NotificationType type,
            Long redirectTargetId,
            String title,
            String content
    ) {
        @Builder
        public NotifyParams {
        }

        /**
         * 일정 (Calendar)
         */
        public static NotifyParams ofCreateCalendar(
                Account receiver,
                Plubbing plubbing,
                Calendar calendar
        ) {
            String content = """
                    새로운 일정이 등록되었어요! 모이는 시간과 장소를 확인하고 참여해 보세요! : %s, %s ~ %s, %s
                    """.formatted(
                    calendar.getTitle(),
                    calendar.getStartedAt(),
                    calendar.getEndedAt(),
                    calendar.getPlaceName()
            );
            return NotifyParams.builder()
                    .receiver(receiver)
                    .type(NotificationType.CREATE_UPDATE_CALENDAR)
                    .redirectTargetId(plubbing.getId())
                    .title(plubbing.getName())
                    .content(content)
                    .build();
        }

        public static NotifyParams ofUpdateCalendar(
                Account receiver,
                Plubbing plubbing,
                Calendar calendar
        ) {
            String content = """
                    모임 일정이 수정되었어요. 어떻게 변경되었는지 확인해 볼까요? : %s, %s ~ %s ,%s
                    """.formatted(
                    calendar.getTitle(),
                    calendar.getStartedAt(),
                    calendar.getEndedAt(),
                    calendar.getPlaceName()
            );
            return NotifyParams.builder()
                    .receiver(receiver)
                    .type(NotificationType.CREATE_UPDATE_CALENDAR)
                    .redirectTargetId(plubbing.getId())
                    .title(plubbing.getName())
                    .content(content)
                    .build();
        }

        /**
         * 게시글-댓글 (Feed)
         */
        public static NotifyParams ofCreateFeedComment(
                Account feedAuthor,
                Account commentAuthor,
                Feed feed,
                FeedComment comment
        ) {
            String content = """
                    '%s'님이 '%s'님의 게시글에 댓글을 남겼어요 : %s
                    """.formatted(
                    commentAuthor.getNickname(),
                    feedAuthor.getNickname(),
                    comment.getContent()
            );
            return NotifyParams.builder()
                    .receiver(feedAuthor)
                    .type(NotificationType.CREATE_FEED_COMMENT)
                    .redirectTargetId(feed.getId())
                    .title(feed.getPlubbing().getName())
                    .content(content)
                    .build();
        }

        public static NotifyParams ofCreateFeedCommentComment(
                Account commentAuthor,
                Feed feed,
                FeedComment parentComment,
                FeedComment comment
        ) {
            String content = """
                    '%s'님이 '%s'님의 댓글에 대댓글을 남겼어요 : %s
                    """.formatted(
                    commentAuthor.getNickname(),
                    parentComment.getAccount().getNickname(),
                    comment.getContent()
            );
            return NotifyParams.builder()
                    .receiver(parentComment.getAccount())
                    .type(NotificationType.CREATE_FEED_COMMENT_COMMENT)
                    .redirectTargetId(feed.getId())
                    .title(feed.getPlubbing().getName())
                    .content(content)
                    .build();
        }

        public static NotifyParams ofPinFeed(Feed feed) {
            String content = """
                    호스트가 %s을(를) 클립보드에 고정했어요.\uD83D\uDE03
                    """.formatted(feed.getTitle()); // 웃는 이모지
            return NotifyParams.builder()
                    .receiver(feed.getAccount())
                    .type(NotificationType.PINNED_FEED)
                    .redirectTargetId(feed.getId())
                    .title(feed.getPlubbing().getName())
                    .content(content)
                    .build();
        }
    }

    /**
     * Response
     */
    public record NotificationResponse(
            Long notificationId,
            NotificationType notificationType,
            String targetEntity,
            Long redirectTargetId,
            String title,
            String body,
            String createdAt,
            boolean isRead
    ) {
        @Builder
        public NotificationResponse {
        }

        public static NotificationResponse of(Notification notification) {
            NotificationType notificationType = notification.getType();
            String targetClassName = notificationType.redirectTargetClass().getSimpleName();
            return NotificationResponse.builder()
                    .notificationId(notification.getId())
                    .notificationType(notificationType)
                    .targetEntity(targetClassName)
                    .redirectTargetId(notification.getRedirectTargetId())
                    .title(notification.getTitle())
                    .body(notification.getContent())
                    .createdAt(notification.getCreatedAt())
                    .isRead(notification.isRead())
                    .build();
        }
    }

    public record NotificationListResponse(
            List<NotificationResponse> notifications
    ) {
        @Builder
        public NotificationListResponse {
        }

        public static NotificationListResponse of(List<NotificationResponse> notifications) {
            return NotificationListResponse.builder()
                    .notifications(notifications)
                    .build();
        }
    }
}
