package plub.plubserver.domain.account.model;

public enum AccountStatus {
    // 정상, 일시 정지, 정지, 영구 정지
    NORMAL, PAUSED, BANNED, PERMANENTLY_BANNED, DELETED
}
