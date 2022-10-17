package plub.plubserver.domain.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import plub.plubserver.domain.account.exception.*;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.repository.AccountRepository;
import plub.plubserver.util.s3.AwsS3Uploader;
import plub.plubserver.util.s3.S3SaveDir;

import java.io.IOException;
import java.util.Collections;
import java.util.regex.Pattern;

import static plub.plubserver.config.security.SecurityUtils.getCurrentAccountEmail;
import static plub.plubserver.domain.account.dto.AccountDto.AccountInfoResponse;
import static plub.plubserver.domain.account.dto.AccountDto.AccountProfileRequest;
import static plub.plubserver.domain.account.dto.AuthDto.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {

    private final AccountRepository accountRepository;
    private final AppleService appleService;
    private final RestTemplate restTemplate;
    private final AwsS3Uploader awsS3Uploader;

    @Value("${kakao.appAdminKey}")
    private String appAdminKey;

    // 회원 정보 조회
    public AccountInfoResponse getMyAccount() {
        return accountRepository.findByEmail(getCurrentAccountEmail())
                .map(AccountInfoResponse::of).orElseThrow(AccountNotFoundException::new);
    }

    public AccountInfoResponse getAccount(String nickname) {
        return accountRepository.findByNickname(nickname)
                .map(AccountInfoResponse::of).orElseThrow(AccountNotFoundException::new);
    }

    public boolean isDuplicateNickname(String nickname) {
        String pattern = "^[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힣]*$";
        if (!Pattern.matches(pattern, nickname)) {
            throw new InvalidNicknameRuleException(nickname);
        }
        return accountRepository.existsByNickname(nickname);
    }

    // 회원 정보 수정
    @Transactional
    public AccountInfoResponse updateProfile(AccountProfileRequest profileRequest) {
        Account myAccount = accountRepository.findByEmail(getCurrentAccountEmail())
                .orElseThrow(AccountNotFoundException::new);
        if (isDuplicateNickname(profileRequest.nickname())) throw new DuplicateNicknameException();

        AwsS3Uploader.S3FileDto newProfileImage =
                awsS3Uploader.upload(profileRequest.profileImage(), S3SaveDir.ACCOUNT_PROFILE, myAccount);

        myAccount.updateProfile(profileRequest.nickname(), profileRequest.introduce(), newProfileImage.savedPath());
        return AccountInfoResponse.of(myAccount);
    }

    @Transactional
    public AuthMessage revoke(RevokeRequest revokeAccount) throws IOException {
        Account myAccount = accountRepository.findByEmail(getCurrentAccountEmail())
                .orElseThrow(AccountNotFoundException::new);
        String socialName = myAccount.getSocialType().getSocialName();

        if (socialName.equalsIgnoreCase("Google")) {
            revokeGoogle(revokeAccount);
        } else if (socialName.equalsIgnoreCase("Kakao")) {
            revokeKakao(revokeAccount);
        } else if (socialName.equalsIgnoreCase("Apple")) {
            appleService.revokeApple(revokeAccount.authorizationCode());
        } else {
            throw new InvalidSocialTypeException(socialName);
        }
        return new AuthMessage("", "탈퇴완료");
    }

    private void revokeGoogle(RevokeRequest revokeRequest) {
        String accessToken = revokeRequest.accessToken();
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("token", accessToken);
        restTemplate.postForEntity("https://oauth2.googleapis.com/revoke", parameters, String.class);
    }

    private void revokeKakao(RevokeRequest revokeRequest) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("target_id_type", "user_id");
        params.add("target_id", revokeRequest.userId());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + appAdminKey);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);
        restTemplate.postForEntity("https://kapi.kakao.com/v1/user/unlink", httpEntity, RevokeKakaoResponse.class);
    }
}
