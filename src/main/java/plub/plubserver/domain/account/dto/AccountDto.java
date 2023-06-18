package plub.plubserver.domain.account.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import org.springframework.data.domain.Page;
import plub.plubserver.common.dto.PageResponse;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.model.SocialType;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

// TODO : 검증 로직 추가할 것 (길이제한 등등)
public class AccountDto {

    public record AccountInfoResponse(
            @ApiModelProperty(value = "이메일", example = "plub@example.com")
            String email,
            @ApiModelProperty(value = "닉네임", example = "플럽")
            String nickname,
            @ApiModelProperty(value = "소셜타입", example = "GOOGLE/KAKAO/APPLE")
            SocialType socialType,
            @ApiModelProperty(value = "생년월일", example = "19971012")
            String birthday,
            @ApiModelProperty(value = "성별정보", example = "M/F")
            String gender,
            @ApiModelProperty(value = "자기소개", example = "안녕하세요! 저는 플럽이에요")
            String introduce,
            @ApiModelProperty(value = "프로필 이미지", example = "https://s3.ap-northeast-2.amazonaws.com/plub/account/profile/test_profile.jpg")
            String profileImage,
            @ApiModelProperty(value = "푸시알림 수신여부", example = "true/false")
            boolean isReceivedPushNotification
    ) {
        @Builder
        public AccountInfoResponse {
        }

        public static AccountInfoResponse of(Account account) {
            return AccountInfoResponse.builder()
                    .email(account.getEmail())
                    .nickname(account.getNickname())
                    .socialType(account.getSocialType())
                    .gender(account.getGender())
                    .birthday(account.getBirthday())
                    .introduce(account.getIntroduce())
                    .profileImage(account.getProfileImage())
                    .isReceivedPushNotification(account.isReceivedPushNotification())
                    .build();
        }
    }

    public record AccountProfileRequest(
            @ApiModelProperty(value = "새로운 닉네임", example = "변경닉네임")
            String nickname,
            @ApiModelProperty(value = "새로운 자기소개", example = "변경자기소개")
            String introduce,
            @ApiModelProperty(value = "새로운 프로필 이미지", example = "디바이스 사진첩에서 가져온 이미지 파일")
            String profileImageUrl
    ) {
    }

    public record PlubbingAccountInfoResponse(
            Long accountId,
            String nickname,
            String profileImage
    ) {
        @Builder
        public PlubbingAccountInfoResponse {
        }

        public static PlubbingAccountInfoResponse of(Account account) {
            return PlubbingAccountInfoResponse.builder()
                    .accountId(account.getId())
                    .nickname(account.getNickname())
                    .profileImage(account.getProfileImage())
                    .build();
        }
    }

    public record NicknameResponse(
            boolean isAvailableNickname
    ) {
    }

    public record AccountCategoryResponse(
            Long accountId,
            List<Long> subCategories

    ) {
        @Builder
        public AccountCategoryResponse {
        }

        public static AccountCategoryResponse of(Account account) {
            return AccountCategoryResponse.builder()
                    .accountId(account.getId())
                    .subCategories(account.getAccountCategories().stream()
                            .map(a -> a.getCategorySub().getId()).toList())
                    .build();
        }
    }

    public record AccountCategoryRequest(
            @Size(max = 5)
            List<Long> subCategories
    ) {
    }

    public record AccountInfo(
            Long accountId,
            String nickname,
            String profileImage
    ) {
        @Builder
        public AccountInfo {
        }

        public static AccountInfo of(Account account) {
            return AccountInfo.builder()
                    .accountId(account.getId())
                    .nickname(account.getNickname())
                    .profileImage(account.getProfileImage())
                    .build();
        }
    }

    public record AccountListResponse(
            PageResponse<AccountInfoWeb> accountList
    ) {
        @Builder
        public AccountListResponse {
        }

        public static AccountListResponse of(Page<AccountInfoWeb> accountList) {
            return AccountListResponse.builder()
                    .accountList(PageResponse.of(accountList))
                    .build();
        }
    }

    public record AccountInfoWeb(
            Long accountId,
            String email,
            String nickname,
            String role,
            String status,
            String joinDate
    ) {
        @Builder
        public AccountInfoWeb {
        }

        public static AccountInfoWeb of(Account account) {
            return AccountInfoWeb.builder()
                    .accountId(account.getId())
                    .email(account.getEmail())
                    .nickname(account.getNickname())
                    .role(account.getRole().toString())
                    .status(account.getAccountStatus().toString())
                    .joinDate(account.getJoinDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    .build();
        }
    }

    // 앱 푸시 알림 변경
    public record AccountPushNotificationStatusResponse(
            boolean isReceivedPushNotification
    ) {
    }

    public record AccountIdResponse(
            Long accountId
    ) {
        public static AccountIdResponse of(Account account) {
            return new AccountIdResponse(account.getId());
        }
    }


    public record SmsRequest(
        String to
    ) {
    }

    public record SmsResponse(
            String requestId,
            LocalDateTime requestTime,
            String statusCode,
            String statusName
    ) {
    }

    public record SmsRequestDTO (
        String type,
        String contentType,
        String countryCode,
        String from,
        String content,
        List<SmsRequest> messages
    ) {
        @Builder
        public SmsRequestDTO {
        }
        public static SmsRequestDTO of(String from, String content, List<SmsRequest> messages) {
            return SmsRequestDTO.builder()
                    .type("SMS")
                    .contentType("COMM")
                    .countryCode("82")
                    .from(from)
                    .content(content)
                    .messages(messages)
                    .build();
        }
    }

    public record CertifySmsRequest(
            String phone,
            String certificationNum
    ) {
    }

    public record SmsMessage(Object result) {
    }
}

