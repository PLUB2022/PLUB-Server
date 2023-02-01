package plub.plubserver.domain.archive.dto;

import lombok.Builder;
import plub.plubserver.domain.archive.model.Archive;
import plub.plubserver.domain.archive.model.ArchiveImage;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;


public class ArchiveDto {

    /**
     * Request
     */
    public record ArchiveRequest(
            @NotBlank @Size(max = 12)
            String title,

            @Size(max = 10)
            List<String> images
    ) {
    }


    /**
     * Response
     */
    public record ArchiveIdResponse(
            Long archiveId
    ) {
        @Builder
        public ArchiveIdResponse {
        }

        public static ArchiveIdResponse of(Archive archive) {
            return ArchiveIdResponse.builder()
                    .archiveId(archive.getId())
                    .build();
        }
    }

    public record ArchiveCardResponse(
            Long archiveId,
            String title,
            String image,
            int imageCount,
            int sequence,
            String createdAt
    ) {
        @Builder
        public ArchiveCardResponse {
        }

        public static ArchiveCardResponse of(Archive archive) {
            return ArchiveCardResponse.builder()
                    .archiveId(archive.getId())
                    .title(archive.getTitle())
                    .image(archive.getImages().stream()
                            .map(ArchiveImage::getImage)
                            .findFirst()
                            .orElse(""))
                    .imageCount(archive.getImages().size())
                    .sequence(archive.getSequence())
                    .createdAt(archive.getCreatedAt())
                    .build();
        }
    }
}
