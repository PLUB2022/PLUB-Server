package plub.plubserver.util.s3.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import plub.plubserver.common.dto.ApiResponse;
import plub.plubserver.util.s3.dto.AwsS3Dto.FileListDto;
import plub.plubserver.util.s3.dto.AwsS3Dto.UpdateFileRequest;
import plub.plubserver.util.s3.dto.AwsS3Dto.UploadFileRequest;
import plub.plubserver.util.s3.service.AwsS3Service;

import static plub.plubserver.common.dto.ApiResponse.success;

@RestController
@RequiredArgsConstructor
public class AwsS3Controller {

    private final AwsS3Service awsS3Service;

    @PostMapping("/api/files")
    public ApiResponse<FileListDto> uploadFiles(@ModelAttribute UploadFileRequest uploadFileRequest) {
        return success(awsS3Service.uploadFiles(uploadFileRequest));
    }

    @PostMapping("/api/files/change")
    public ApiResponse<FileListDto> updateFiles(@ModelAttribute UpdateFileRequest updateFileRequest) {
        return success(awsS3Service.updateFiles(updateFileRequest));
    }

    @DeleteMapping("/api/files/{type}")
    public ApiResponse<?> deleteFiles(@PathVariable String type, @RequestParam String fileUrl) {
        awsS3Service.deleteFiles(type, fileUrl);
        return success("DELETE SUCCESS");
    }

}
