package plub.plubserver.util.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import plub.plubserver.common.dto.ApiResponse;
import plub.plubserver.util.s3.AwsS3Dto.DeleteFileRequest;
import plub.plubserver.util.s3.AwsS3Dto.FileListDto;
import plub.plubserver.util.s3.AwsS3Dto.UpdateFileRequest;
import plub.plubserver.util.s3.AwsS3Dto.UploadFileRequest;

import static plub.plubserver.common.dto.ApiResponse.success;

@RestController
@RequiredArgsConstructor
public class AwsS3Controller {

    private final AwsS3Service awsS3Service;

    @PostMapping("/api/files")
    public ApiResponse<FileListDto> uploadFiles(@ModelAttribute UploadFileRequest uploadFileRequest) {
        return success(awsS3Service.uploadFiles(uploadFileRequest), "files are successfully uploaded.");
    }

    @PostMapping("/api/files/change")
    public ApiResponse<FileListDto> updateFiles(@ModelAttribute UpdateFileRequest updateFileRequest) {
        return success(awsS3Service.updateFiles(updateFileRequest), "files are successfully updated.");
    }

    @DeleteMapping("/api/files")
    public ApiResponse<?> deleteFiles(@RequestBody DeleteFileRequest deleteFileRequest) {
        awsS3Service.deleteFiles(deleteFileRequest);
        return success(null, "files are successfully deleted.");
    }


}
