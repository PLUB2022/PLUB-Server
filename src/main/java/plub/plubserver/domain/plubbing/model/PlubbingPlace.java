package plub.plubserver.domain.plubbing.model;

import lombok.*;

import javax.persistence.Embeddable;

@Embeddable
@AllArgsConstructor
@Builder
@Getter
public class PlubbingPlace {
    private String address;
    private String roadAddress;
    private String placeName;
    private Double placePositionX;
    private Double placePositionY;

    public PlubbingPlace() {
        address = "";
        roadAddress = "";
        placeName = "";
        placePositionX = 0.0;
        placePositionY = 0.0;
    }
}
