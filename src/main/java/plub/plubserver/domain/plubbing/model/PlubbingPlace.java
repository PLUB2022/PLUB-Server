package plub.plubserver.domain.plubbing.model;

import lombok.*;

import javax.persistence.Embeddable;

@Embeddable
@AllArgsConstructor
@Builder
@Getter
public class PlubbingPlace {
    private String address;
    private Double placePositionX;
    private Double placePositionY;

    public PlubbingPlace() {
        this.address = "";
        this.placePositionX = 0.0;
        this.placePositionY = 0.0;
    }
}
