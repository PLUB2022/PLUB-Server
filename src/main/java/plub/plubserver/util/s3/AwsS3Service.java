package plub.plubserver.util.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import plub.plubserver.domain.account.config.AccountCode;
import plub.plubserver.domain.account.exception.AccountException;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.repository.AccountRepository;
import plub.plubserver.util.s3.AwsS3Dto.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static plub.plubserver.config.security.SecurityUtils.getCurrentAccountEmail;

@Slf4j
@Component
@RequiredArgsConstructor
public class AwsS3Service {
    private final AmazonS3Client amazonS3Client;
    private final AccountRepository accountRepository;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private static final String TEMP_SAVE_PATH =
            System.getProperty("user.dir") + "/temp/";

    private Account getCurrentAccount() {
        return accountRepository.findByEmail(getCurrentAccountEmail())
                .orElseThrow(() -> new AccountException(AccountCode.NOT_FOUND_ACCOUNT));
    }

    private String generateFileName(MultipartFile multipartFile, Account owner) {
        String ownerPrefix = "";
        if (owner != null) ownerPrefix = owner.getEmail();
        return ownerPrefix + "_" + UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();
    }

    private File tempSave(MultipartFile multipartFile, Account owner) {
        String fileName = TEMP_SAVE_PATH + generateFileName(multipartFile, owner);
        File tempFile = new File(fileName);
        try {
            multipartFile.transferTo(tempFile);
        } catch (IOException e) {
            log.warn("파일 임시 저장 실패 = {}", e.getMessage());
        }
        return tempFile;
    }

    private S3SaveDir getS3SaveDir(String type) {
        return switch (type) {
            case "profile" -> S3SaveDir.ACCOUNT_PROFILE;
            case "plubbing-main" -> S3SaveDir.PLUBBING_MAIN_IMAGE;
            // 추후 더 추가될 것
            default -> S3SaveDir.ACCOUNT_PROFILE;
        };
    }

    private String getFilename(String url) {
        String[] parsedUrl = url.split("/");
        return parsedUrl[parsedUrl.length - 1];
    }

    public FileDto upload(MultipartFile multipartFile, String type, Account owner) {
        // 로컬에 임시 저장
        File tempFile = tempSave(multipartFile, owner);

        S3SaveDir savePath = getS3SaveDir(type);

        String bucketPath = bucket + savePath.path;

        // S3 업로드
        amazonS3Client.putObject(
                new PutObjectRequest(bucketPath, tempFile.getName(), tempFile)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
        );

        // 로컬에 저장했던 임시 파일 삭제
        if (!tempFile.delete()) log.warn("임시 파일 삭제 실패");
        String fileUrl = amazonS3Client.getUrl(bucketPath, tempFile.getName()).toString();
        return new FileDto(tempFile.getName(), fileUrl);
    }

    public FileListDto uploadFiles(UploadFileRequest uploadFileRequest) {
        Account loginUser = getCurrentAccount();
        List<FileDto> result = uploadFileRequest.files().stream()
                .map(file -> upload(file, uploadFileRequest.type(), loginUser))
                .toList();
        return new FileListDto(result);
    }

    public void delete(String type, String url) {
        String filename = getFilename(url);
        try {
            amazonS3Client.deleteObject(
                    new DeleteObjectRequest(bucket + getS3SaveDir(type).path, filename)
            );
        } catch (Exception e) {
            log.warn("S3 파일 삭제 실패 = {}", e.getMessage());
            throw new AwsS3Exception();
        }
    }

    public void deleteFiles(DeleteFileRequest deleteFileRequest) {
        String type = deleteFileRequest.type();
        deleteFileRequest.urls().forEach(url -> delete(type, url));
    }

    public FileListDto updateFiles(UpdateFileRequest updateFileRequest) {
        String type = updateFileRequest.type();
        Account loginUser = getCurrentAccount();
        // 기존꺼 삭제
        updateFileRequest.toDeleteUrls().forEach(file -> delete(type, file));

        // 새로운거 업로드
        return new FileListDto(updateFileRequest.newFiles().stream()
                .map(file -> upload(file, type, loginUser))
                .toList());
    }


}
