package plub.plubserver.domain.account.model;

public enum AccountStatus {
    // 정상, 일시 정지, 정지, 영구 정지, 삭제됨, 비활성화, 휴면
    NORMAL, PAUSED, BANNED, PERMANENTLY_BANNED, DELETED, INACTIVE, DORMANT
}
