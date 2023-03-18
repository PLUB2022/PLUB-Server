package plub.plubserver.domain.notification.model;

import lombok.RequiredArgsConstructor;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.calendar.model.Calendar;
import plub.plubserver.domain.feed.model.Feed;
import plub.plubserver.domain.notice.model.Notice;
import plub.plubserver.domain.plubbing.model.Plubbing;

@RequiredArgsConstructor
public enum NotificationType {
    CREATE_UPDATE_CALENDAR(ReceiverType.MEMBERS, Calendar.class),
    CREATE_FEED_COMMENT(ReceiverType.AUTHOR, Feed.class),
    CREATE_NOTICE(ReceiverType.MEMBERS, Notice.class),
    CREATE_NOTICE_COMMENT(ReceiverType.AUTHOR, Notice.class),
    LEAVE_PLUBBING(ReceiverType.HOST, Plubbing.class),
    APPLY_RECRUIT(ReceiverType.HOST, Plubbing.class),
    APPROVE_RECRUIT(ReceiverType.AUTHOR, Plubbing.class),
    PLUBBING_PERMANENTLY_PAUSED(ReceiverType.HOST, Plubbing.class),
    PLUBBING_RECEIVED_MANY_REPORTS(ReceiverType.HOST, Plubbing.class),
    TEST_ACCOUNT_ITSELF(ReceiverType.AUTHOR, Account.class)
    ;

    private enum ReceiverType {
        HOST, MEMBERS, AUTHOR
    }
    private final ReceiverType receiverType;
    private final Class<?> redirectTargetClass;

    public ReceiverType receiverType() {
        return receiverType;
    }

    public Class<?> redirectTargetClass() {
        return redirectTargetClass;
    }

}
