package plub.plubserver.domain.plubbing.model;

import lombok.*;

import javax.persistence.Embeddable;

@Embeddable
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PlubbingPlace {
    private Double placePositionX;
    private Double placePositionY;
}
