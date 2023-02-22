package plub.plubserver.domain.announcement.model;

import lombok.*;
import plub.plubserver.common.model.BaseEntity;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Announcement extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "announcement_id")
    private Long id;

    private String title;
    private String content;

    public void updateAnnouncement(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
