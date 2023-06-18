package plub.plubserver.domain.account.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class RevokeAccount {
    @Id
    @GeneratedValue
    private Long id;

    private String email;
    private SocialType socialType;
    private String phoneNumber;
    private String nickname;

    private LocalDateTime revokedAt;

}
