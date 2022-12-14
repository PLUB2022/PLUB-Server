package plub.plubserver.util.s3.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import plub.plubserver.common.exception.CommonErrorCode;
import plub.plubserver.util.s3.S3SaveDir;
import plub.plubserver.util.s3.dto.AwsS3Dto.FileDto;
import plub.plubserver.util.s3.dto.AwsS3Dto.FileListDto;
import plub.plubserver.util.s3.dto.AwsS3Dto.UpdateFileRequest;
import plub.plubserver.util.s3.dto.AwsS3Dto.UploadFileRequest;
import plub.plubserver.util.s3.exception.AwsS3Exception;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static java.text.MessageFormat.format;
import static plub.plubserver.config.security.SecurityUtils.getCurrentAccountEmail;

@Slf4j
@Component
@RequiredArgsConstructor
public class AwsS3Service {
    private static final String FILE_EXTENSION_SEPARATOR = ".";
    private final AmazonS3Client amazonS3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public FileListDto uploadFiles(UploadFileRequest uploadFileRequest) {
        String currentAccountEmail = getCurrentAccountEmail();

        List<MultipartFile> files = uploadFileRequest.files();
        if (CollectionUtils.isEmpty(files)) {
            throw new AwsS3Exception(CommonErrorCode.INVALID_INPUT_VALUE);
        }
        List<FileDto> result = uploadFileRequest.files().stream()
                .map(file -> uploadV2(file, uploadFileRequest.type(), currentAccountEmail))
                .toList();
        return new FileListDto(result);
    }

    public FileDto uploadV2(MultipartFile multipartFile, String type, String owner) {
        validateFileExists(multipartFile);

        String fileName = buildFileName(Objects.requireNonNull(multipartFile.getOriginalFilename()), owner);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());

        S3SaveDir savePath = getS3SaveDir(type);
        String bucketPath = bucket + savePath.path;

        try (InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(bucketPath, fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (RuntimeException | IOException e) {
            throw new AwsS3Exception(CommonErrorCode.AWS_S3_ERROR);
        }

        String fileUrl = amazonS3Client.getUrl(bucketPath, fileName).toString();
        return new FileDto(multipartFile.getOriginalFilename(), fileUrl);
    }

    private void validateFileExists(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            throw new AwsS3Exception(CommonErrorCode.AWS_S3_ERROR);
        }
    }

    public String buildFileName(String originalFileName, String owner) {
        int fileExtensionIndex = originalFileName.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        String fileExtension = originalFileName.substring(fileExtensionIndex);
        return format("{0}_{1}_{2}", owner, UUID.randomUUID(), fileExtension);
    }

    private S3SaveDir getS3SaveDir(String type) {
        return switch (type) {
            case "profile" -> S3SaveDir.ACCOUNT_PROFILE;
            case "plubbing-main" -> S3SaveDir.PLUBBING_MAIN_IMAGE;
            // ?????? ??? ????????? ???
            default -> S3SaveDir.ACCOUNT_PROFILE;
        };
    }

    public FileListDto updateFiles(UpdateFileRequest updateFileRequest) {
        String type = updateFileRequest.type();
        String loginUser = getCurrentAccountEmail();

        // ????????? ??????

        List<String> urls = updateFileRequest.toDeleteUrls();
        if (CollectionUtils.isEmpty(urls)) {
            throw new AwsS3Exception(CommonErrorCode.AWS_S3_ERROR);
        }

        updateFileRequest.toDeleteUrls().forEach(file -> delete(type, file));

        // ???????????? ?????????
        return new FileListDto(updateFileRequest.newFiles().stream()
                .map(file -> uploadV2(file, type, loginUser))
                .toList());
    }

    public void deleteFiles(String type, String url) {
        delete(type, url);
    }

    public void delete(String type, String url) {
        String filename = getFilename(url);
        System.out.println("filename = " + filename);

        S3SaveDir savePath = getS3SaveDir(type);
        String bucketPath = bucket + savePath.path;

        try {
            amazonS3Client.deleteObject(new DeleteObjectRequest(bucketPath, filename));
        } catch (Exception e) {
            log.warn("S3 ?????? ?????? ?????? = {}", e.getMessage());
            throw new AwsS3Exception(CommonErrorCode.AWS_S3_ERROR);
        }
    }

    private String getFilename(String url) {
        String[] parsedUrl = url.split("/");
        String string = parsedUrl[parsedUrl.length - 1];
        return URLDecoder.decode(string, StandardCharsets.UTF_8);
    }
}
