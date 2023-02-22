package plub.plubserver.domain.policy.model;

import lombok.*;
import plub.plubserver.common.model.BaseEntity;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Policy extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "policy_id")
    private Long id;

    private String name;
    private String title;

    @Column(columnDefinition = "TEXT", length = 30000)
    private String content;
}
