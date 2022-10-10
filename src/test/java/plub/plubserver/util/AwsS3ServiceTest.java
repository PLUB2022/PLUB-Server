package plub.plubserver.util;

import io.findify.s3mock.S3Mock;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import plub.plubserver.domain.account.AccountTemplate;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.util.s3.AwsS3Uploader;
import plub.plubserver.util.s3.S3SaveDir;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Import(AwsS3MockConfig.class)
@SpringBootTest
class AwsS3ServiceTest {

    @Autowired
    private S3Mock s3Mock;
    @Autowired
    private AwsS3Uploader awsS3Service;

    MockMultipartFile mockFile = new MockMultipartFile(
            "test",
            "test.png",
            MediaType.IMAGE_JPEG_VALUE,
            "test".getBytes()
    );

    Account mockUser = AccountTemplate.makeAccount1();

    @AfterEach
    public void tearDown() {
        s3Mock.stop();
    }

    @Test
    @DisplayName("Account Owner 존재하는 업로드 성공")
    void uploadWithOwner() {
        AwsS3Uploader.S3FileDto uploadedFile = awsS3Service.upload(mockFile, S3SaveDir.ACCOUNT_PROFILE, mockUser);
        assertThat(uploadedFile.fileName()).contains(mockFile.getOriginalFilename());
        assertThat(uploadedFile.savedPath()).contains(mockFile.getOriginalFilename());
    }

    @Test
    @DisplayName("Account Owner 없는 업로드 성공")
    void uploadWithNoOwner() {
        AwsS3Uploader.S3FileDto uploadedFile = awsS3Service.upload(mockFile, S3SaveDir.ACCOUNT_PROFILE, null);
        assertThat(uploadedFile.fileName()).contains(mockFile.getOriginalFilename());
        assertThat(uploadedFile.savedPath()).contains(mockFile.getOriginalFilename());
    }

    @Test
    @DisplayName("파일 삭제 성공")
    void delete() {
        // given
        AwsS3Uploader.S3FileDto uploadedFile = awsS3Service.upload(mockFile, S3SaveDir.ACCOUNT_PROFILE, mockUser);

        // when
        boolean expected = awsS3Service.delete(S3SaveDir.ACCOUNT_PROFILE, uploadedFile.fileName());

        // then
        assertTrue(expected);
    }

    @Test
    @DisplayName("S3에 업로드된 파일 조회 성공")
    void getS3File() {
        // given
        AwsS3Uploader.S3FileDto uploadedFile = awsS3Service.upload(mockFile, S3SaveDir.ACCOUNT_PROFILE, mockUser);

        // when
        String expectedPath = awsS3Service.getFile(S3SaveDir.ACCOUNT_PROFILE, uploadedFile.fileName());

        // then
        assertEquals(expectedPath, uploadedFile.savedPath());
    }

}
