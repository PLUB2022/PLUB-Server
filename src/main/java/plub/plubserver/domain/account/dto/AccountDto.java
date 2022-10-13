package plub.plubserver.domain.account.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.model.SocialType;

import static plub.plubserver.domain.account.dto.AuthDto.LoginRequest;

// TODO : 검증 로직 추가할 것 (길이제한 등등)
public class AccountDto {
    public record AccountRequest(
            @ApiModelProperty(value = "이메일", example = "plub@example.com")
            String email,
            @ApiModelProperty(value = "비밀번호", example = "비밀번호")
            String password,
            @ApiModelProperty(value = "닉네임", example = "플럽")
            String nickname,
            @ApiModelProperty(value = "소셜타입", example = "GOOGLE/KAKAO/APPLE")
            String socialType
    ) {
        public LoginRequest toLoginRequest() {
            return LoginRequest.builder()
                    .email(email)
                    .password(password)
                    .build();
        }
    }

    public record AccountInfoResponse(
            @ApiModelProperty(value = "이메일",example = "plub@example.com")
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
            String profileImage
    ){
        @Builder public AccountInfoResponse {}
        public static AccountInfoResponse of(Account account) {
            return AccountInfoResponse.builder()
                    .email(account.getEmail())
                    .nickname(account.getNickname())
                    .socialType(account.getSocialType())
                    .gender(account.getGender())
                    .birthday(account.getBirthday())
                    .introduce(account.getIntroduce())
                    .profileImage(account.getProfileImage())
                    .build();
        }
    }

    public record AccountProfileRequest(
            @ApiModelProperty(value = "새로운 닉네임", example = "변경닉네임")
            String nickname,
            @ApiModelProperty(value = "새로운 자기소개", example = "변경자기소개")
            String introduce,
            @ApiModelProperty(value = "새로운 프로필 이미지", example = "디바이스 사진첩에서 가져온 이미지 파일")
            MultipartFile profileImage
    ) {}

}
