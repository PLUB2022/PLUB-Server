package plub.plubserver.common.model;

import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class BaseEntity {

    protected String createdAt;
    protected String modifiedAt;
    protected boolean visibility = true;

    private String customFormat() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @PrePersist
    protected void onPrePersist() {
        createdAt = customFormat();
        modifiedAt = createdAt;
    }

    @PreUpdate
    protected void onPreUpdate() {
        modifiedAt = customFormat();
    }

    public void softDelete() {
        visibility = false;
    }

    public void setVisible() {
        visibility = true;
    }

}