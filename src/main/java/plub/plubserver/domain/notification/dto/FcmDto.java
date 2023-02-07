package plub.plubserver.domain.notification.dto;

public class FcmDto {
    public record FcmMessage(
            Boolean validateOnly,
            Message message

    ) {}

    public record Message(
            String token,
            Notification notification
    ) {}

    public record Notification(
            String title,
            String body
    ) {}

    public record PushMessage(
            String targetToken,
            String title,
            String body
    ) {}
}
