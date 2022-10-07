package plub.plubserver.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import plub.plubserver.domain.account.AccountTemplate;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.util.s3.AwsS3Service;
import plub.plubserver.util.s3.S3SaveDir;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
class AwsS3ServiceTest {

    @Autowired
    AwsS3Service awsS3Service;

    MockMultipartFile mockFile = new MockMultipartFile(
            "test",
            "test.png",
            MediaType.IMAGE_JPEG_VALUE,
            "test".getBytes()
    );

    Account mockUser = AccountTemplate.makeAccount1();

    @Test
    @DisplayName("업로드 성공")
    void upload() {
        AwsS3Service.S3FileDto uploadedFile = awsS3Service.upload(mockFile, S3SaveDir.ACCOUNT_PROFILE, mockUser);
        assertThat(uploadedFile.fileName().endsWith(mockFile.getOriginalFilename())).isTrue();

        boolean expected = awsS3Service.delete(S3SaveDir.ACCOUNT_PROFILE, uploadedFile.fileName());
        assertThat(expected).isTrue();
    }

    /**
     * 결론 : AwsS3Service를 합리적으로 테스트하는 방법을 찾지 못 함
     * # 1 S3Mock 도입 실패
     * -> 계속 Failed to load context 에러가 뜨는데 정확한 이유는 모르겠지만,
     * 아마 mock한 s3를 AwsS3Service에서 가져다가 쓸 수 없기 때문이라 생각됨
     * 통합테스트로 빈을 다 띄우는데, 그 과정에서 실제 AWS 계정과 연동되는 로직이 서비스에 있어서...
     * 그래서 S3Mock은 일단 사용을 포기!
     *
     * # 2 업로드, 삭제 통합 코드
     * 업로드 되었던 파일들을 AfterAll로 모두 삭제하고 싶지만,
     * 업로드 성공 테스트때 올린 파일 이름을 가져올 수 없음
     * 그래서 하나에 통합되서 올림
     *
     * -> 그러면 임시 테스트 폴더를 만들고 거기에 업로드하고 비우는 식으로 하면 어떤가?
     * -> AwsS3Service에 테스트를 위한 버킷 폴더 생성, 삭제 로직이 들어가게 됨
     * -> 비즈니스 로직을 테스트 하기 위해 테코를 짜는데, 테코를 위해 비즈니스 로직을 추가한다? (주객 전도되는 느낌)
     *
     * # 3 실패하는 케이스들은 테스트 하기가 애매함
     * ex1. S3 연결 실패 -> 액세스, 프빗키가 잘못 되거나 S3 권한이 잘못된건데 테스트 코드로 테스트하기 애매함
     * ex2. 없는 버킷이름으로 접근 -> S3SaveDir 이라는 Enum 타입을 쓰기 때문에 잘못 들어갈 일이 없음
     */

}
