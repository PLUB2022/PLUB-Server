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
import plub.plubserver.config.jwt.RefreshTokenRepository;
import plub.plubserver.config.security.SecurityUtils;
import plub.plubserver.domain.account.dto.AccountDto;
import plub.plubserver.domain.account.dto.AuthDto;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.repository.AccountRepository;
import plub.plubserver.exception.account.NickNameDuplicateException;
import plub.plubserver.exception.account.NicknameRuleException;
import plub.plubserver.exception.account.NotFoundAccountException;

import java.io.IOException;
import java.util.Collections;
import java.util.regex.Pattern;

import static plub.plubserver.domain.account.dto.AccountDto.AccountInfo;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AppleService appleService;
    private final RestTemplate restTemplate;

    @Value("${value.appAdminKey}")
    private String appAdminKey;

    // 회원 정보 조회
    @Transactional(readOnly = true)
    public AccountInfo getMyAccount() {
        return accountRepository.findByEmail(SecurityUtils.getCurrentAccountEmail())
                .map(AccountInfo::of).orElseThrow(NotFoundAccountException::new);
    }

    @Transactional(readOnly = true)
    public AccountInfo getAccount(String nickname) {
        return accountRepository.findByNickname(nickname)
                .map(AccountInfo::of).orElseThrow(NotFoundAccountException::new);
    }

    @Transactional(readOnly = true)
    public boolean checkNickname(String nickname) {
        String pattern = "^[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힣]*$";
        if(!Pattern.matches(pattern, nickname)){
            throw new NicknameRuleException();
        }
        return accountRepository.existsByNickname(nickname);
    }

    // 회원 정보 수정
    @Transactional
    public AccountInfo updateNickname(AccountDto.AccountNicknameRequest request) {
        Account myAccount = accountRepository.findByEmail(SecurityUtils.getCurrentAccountEmail())
                .orElseThrow(NotFoundAccountException::new);
        if (checkNickname(request.nickname())) {
            throw new NickNameDuplicateException();
        }
        myAccount.updateNickname(request.nickname());
        return AccountInfo.of(myAccount);
    }


    @Transactional
    public AccountInfo updateIntroduce(AccountDto.AccountIntroduceRequest request) {
        Account myAccount = accountRepository.findByEmail(SecurityUtils.getCurrentAccountEmail())
                .orElseThrow(NotFoundAccountException::new);
        myAccount.updateIntroduce(request.introduce());
        return AccountInfo.of(myAccount);
    }

    @Transactional
    public AuthDto.AuthMessage revoke(AuthDto.RevokeRequest revokeAccount) throws IOException {
        Account myAccount = accountRepository.findByEmail(SecurityUtils.getCurrentAccountEmail())
                .orElseThrow(NotFoundAccountException::new);
        String socialName = myAccount.getSocialType().getSocialName();

        if (socialName.equalsIgnoreCase("Google")) {
            revokeGoogle(revokeAccount);
        } else if (socialName.equalsIgnoreCase("Kakao")) {
            revokeKakao(revokeAccount);
        } else {
            appleService.revokeApple(myAccount, revokeAccount.authorizationCode());
        }
        return new AuthDto.AuthMessage("d", "탈퇴완료");
    }

    private void revokeGoogle(AuthDto.RevokeRequest revokeAccount) {
        String accessToken = revokeAccount.accessToken();
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("token", accessToken);
        restTemplate.postForEntity("https://oauth2.googleapis.com/revoke", parameters, String.class);
    }

    private void revokeKakao(AuthDto.RevokeRequest revokeAccount) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("target_id_type", "user_id");
        params.add("target_id", revokeAccount.userId());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + appAdminKey);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);
        restTemplate.postForEntity("https://kapi.kakao.com/v1/user/unlink", httpEntity, AuthDto.RevokeResponseKakao.class);
    }

//    @Transactional
//    public AuthDto.AuthMessage logout() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String loginAccount = authentication.getName();
//        if (accountRepository.findByEmail(loginAccount).isPresent()){
//            // 리프레시 토큰 삭제
//            RefreshToken refreshToken = refreshTokenRepository.findByAccount(authentication.getName()).get();
//            refreshTokenRepository.delete(refreshToken);
//        }else{
//            throw new NotFoundUserInformationException();
//        }
//        return Message.of("로그아웃 성공");
//    }

}
