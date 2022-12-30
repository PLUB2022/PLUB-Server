package plub.plubserver.util.s3.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class AwsS3Dto {
    public record UploadFileRequest(
            String type,
            List<MultipartFile> files
    ) {}
    public record UpdateFileRequest(
            String type,
            List<String> toDeleteUrls,
            List<MultipartFile> newFiles
    ) {}
    public record FileDto(
            String filename,
            String fileUrl
    ) {}
    public record FileListDto(
            List<FileDto> files
    ) {}
}
