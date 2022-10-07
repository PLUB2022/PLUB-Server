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
import plub.plubserver.domain.account.model.Account;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AwsS3Service {
    private final AmazonS3Client amazonS3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private static final String TEMP_SAVE_PATH =
            System.getProperty("user.dir") + "/temp/";

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

    public record S3FileDto(String fileName, String savedPath){}

    public S3FileDto upload(MultipartFile multipartFile, S3SaveDir savePath, Account owner) {
        // 로컬에 임시 저장
        File tempFile = tempSave(multipartFile, owner);

        String bucketPath = bucket + savePath.path;

        // S3 업로드
        amazonS3Client.putObject(
                new PutObjectRequest(bucketPath, tempFile.getName(), tempFile)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
        );

        // 로컬에 저장했던 임시 파일 삭제
        if (!tempFile.delete()) log.warn("임시 파일 삭제 실패");
        String savedPath = amazonS3Client.getUrl(bucketPath, tempFile.getName()).toString();
        return new S3FileDto(tempFile.getName(), savedPath);
    }

    public boolean delete(S3SaveDir savePath, String s3SavedFileName) {
        try {
            amazonS3Client.deleteObject(
                    new DeleteObjectRequest(bucket + savePath.path, s3SavedFileName)
            );
        } catch (Exception e) {
            log.warn("S3 파일 삭제 실패 = {}", e.getMessage());
            return false;
        }
        return true;
    }

    public String getFile(S3SaveDir savePath, String fileName) {
        return amazonS3Client.getUrl(bucket + savePath.path, fileName).toString();
    }

    private String generateFileName(MultipartFile multipartFile, Account owner) {
        String ownerPrefix = "";
        if (owner != null) ownerPrefix = owner.getEmail();
        return ownerPrefix + "_" + UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();
    }
}
