package plub.plubserver.domain.account.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.model.SocialType;

import static plub.plubserver.domain.account.dto.AuthDto.LoginRequest;

public class AccountDto {
    public record AccountRequest(
            @ApiModelProperty(value = "이메일",example = "plub@example.com")
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

    public record AccountInfo(
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
            String introduce
    ){
        @Builder public AccountInfo{}
        public static AccountInfo of(Account account) {
            return AccountInfo.builder()
                    .email(account.getEmail())
                    .nickname(account.getNickname())
                    .socialType(account.getSocialType())
                    .gender(account.getGender())
                    .birthday(account.getBirthday())
                    .introduce(account.getIntroduce())
                    .build();
        }
    }

    public record AccountNicknameRequest(
            @ApiModelProperty(value = "닉네임", example = "플럽")
            String nickname
    ) {}

    public record AccountIntroduceRequest(
            @ApiModelProperty(value = "자기소개", example = "안녕하세요! 저는 플럽이에요")
            String introduce
    ) {}

}
