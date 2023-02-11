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
            List<String> images, // limit 3
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
                    .images(archive.getImages().stream()
                            .map(ArchiveImage::getImage)
                            .limit(3)
                            .toList())
                    .imageCount(archive.getImages().size())
                    .sequence(archive.getSequence())
                    .createdAt(archive.getCreatedAt().split(" ")[0])
                    .build();
        }
    }

    public record ArchiveResponse(
            String title,
            List<String> images,
            int imageCount,
            int sequence,
            String createdAt
    ) {
        @Builder
        public ArchiveResponse {
        }

        public static ArchiveResponse of(Archive archive) {
            return ArchiveResponse.builder()
                    .title(archive.getTitle())
                    .images(archive.getImages().stream()
                            .map(ArchiveImage::getImage)
                            .toList())
                    .imageCount(archive.getImages().size())
                    .sequence(archive.getSequence())
                    .createdAt(archive.getCreatedAt().split(" ")[0])
                    .build();
        }
    }
}
