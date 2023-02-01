package plub.plubserver.domain.archive.dto;

import lombok.Builder;
import org.springframework.data.domain.Page;
import plub.plubserver.common.dto.PageResponse;
import plub.plubserver.domain.archive.model.Archive;
import plub.plubserver.domain.archive.model.ArchiveImage;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Optional;


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
            String image = "";
            int sequence = -1;
            Optional<ArchiveImage> archiveImage = archive.getImages().stream().findFirst();
            if (archiveImage.isPresent()) {
                image = archiveImage.get().getImage();
                sequence = archive.getImages().indexOf(archiveImage.get());
            }

            return ArchiveCardResponse.builder()
                    .archiveId(archive.getId())
                    .title(archive.getTitle())
                    .image(image)
                    .imageCount(archive.getImages().size())
                    .sequence(sequence)
                    .createdAt(archive.getCreatedAt())
                    .build();
        }
    }
    public record ArchiveCardListResponse(
            PageResponse<ArchiveCardResponse> archives
    ) {
        @Builder
        public ArchiveCardListResponse {
        }

        public static ArchiveCardListResponse of(Page<ArchiveCardResponse> archiveCardPage) {
            return ArchiveCardListResponse.builder()
                    .archives(PageResponse.of(archiveCardPage))
                    .build();
        }
    }
}
