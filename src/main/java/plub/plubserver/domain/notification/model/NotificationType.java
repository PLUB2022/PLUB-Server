package plub.plubserver.domain.notification.model;

import lombok.RequiredArgsConstructor;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.calendar.model.Calendar;
import plub.plubserver.domain.feed.model.Feed;
import plub.plubserver.domain.notice.model.Notice;
import plub.plubserver.domain.plubbing.model.Plubbing;

@RequiredArgsConstructor
public enum NotificationType {

    /**
     * 알림명 : 신고 1회 접수
     * 받는 사람 : 신고 대상자
     * 리다이렉트 : 신고 1회 고지 화면
     */
    REPORTED_ONCE(ReceiverType.ACCOUNT, Object.class),

    /**
     * 알림명 : 정지해제
     * 조건 : 신고 차감 또는 정지기간 이후 정지 해제
     * 받는 사람 : 정지 해제 대상자
     * 리다이렉트 : 플러빙 내 모임 페이지
     */
    UNBAN(ReceiverType.ACCOUNT, Object.class),

    /**
     * 알림명 : 한 달 정지
     * 조건 : 신고 3회 누적시 자동 한달 정지 (중간에 관리자가 정지해제 가능)
     * 받는 사람 : 한 달 정지 대상자
     * 리다이렉트 : 한 달 정지 안내 화면, 뒤로가기 및 앱 종료 후 재실행 시 앱 이용 불가 모달 계속 출력
     */
    BAN_ONE_MONTH(ReceiverType.ACCOUNT, Object.class),

    /**
     * 알림명 : 영구정지
     * 조건 : 신고 6회 누적, 검토 후 불량 이용자 확인 시
     * 받는 사람 : 영구정지 대상자
     * 리다이렉트 : 영구정지 안내 화면, 뒤로가기 및 앱 종료 후 재실행 시 앱 이용 불가 모달 계속 출력
     */
    BAN_PERMANENTLY(ReceiverType.ACCOUNT, Object.class),

    /**
     * 알림명 : 내 게시글에 댓글
     * 받는 사람 : 게시글 작성자
     * 리다이렉트 : 해당 댓글 화면
     */
    CREATE_FEED_COMMENT(ReceiverType.AUTHOR, Feed.class),

    /**
     * 알림명 : 내 댓글에 답글 (대댓글) - 알림 포맷 동일
     * 받는 사람 : 댓글 작성자
     * 리다이렉트 : 해당 댓글 화면
     */
    CREATE_FEED_COMMENT_COMMENT(ReceiverType.AUTHOR, Feed.class),


    /**
     * 알림명 : 모임 공지 등록
     * 받는 사람 : 멤버 전체
     * 리다이렉트 : 등록된 공지 상세페이지
     */
    CREATE_NOTICE(ReceiverType.MEMBERS, Notice.class),

    /**
     * 알림명 : 모임 일정 등록, 수정
     * 받는 사람 : 멤버 전체
     * 리다이렉트 : 일정 상세페이지
     */
    CREATE_UPDATE_CALENDAR(ReceiverType.MEMBERS, Calendar.class),


    /**
     * 알림명 : 지원 수락
     * 받는 사람 : 지원자
     * 리다이렉트 : 해당 플러빙 메인
     */
    APPROVE_RECRUIT(ReceiverType.ACCOUNT, Plubbing.class),

    /**
     * 알림명 : 참여신청 (모집 지원)
     * 받는 사람 : 호스트
     * 리다이렉트 : 마이페이지 모집 중인 모임 지원자 내역 페이지 (해당 지원자 내역 드롭다운)
     */
    APPLY_RECRUIT(ReceiverType.HOST, Object.class),

    /**
     * 알림명 : 모임 나가기, 탈퇴
     * 받는 사람 : 호스트
     * 리다이렉트 : 해당 플러빙 메인
     */
    LEAVE_PLUBBING(ReceiverType.HOST, Plubbing.class),

    /**
     * 알림명 : 호스트가 내 게시글 클립보드에 고정
     * 조건 : 호스트가 내 게시글을 클립보드에 고정한 경우
     * 받는 사람 : 게시글 작성자 (호스트X)
     * 리다이렉트 : 해당 클립보드 상세페이지 (호스트가 작성자일 경우 알림X)
     */
    PINNED_FEED(ReceiverType.AUTHOR, Feed.class),

    /**
     * 알림명 : 모임 강퇴
     * 조건 : 호스트가 특정 사용자 강퇴
     * 받는 사람 : 강퇴된 사용자
     * 리다이렉트 : 강퇴된 모임이 사라진 내 모임 화면
     */
    KICK_MEMBER(ReceiverType.ACCOUNT, Object.class),

    TEST_ACCOUNT_ITSELF(ReceiverType.ACCOUNT, Account.class)
    ;

    private enum ReceiverType {
        HOST, MEMBERS, AUTHOR, ACCOUNT
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
