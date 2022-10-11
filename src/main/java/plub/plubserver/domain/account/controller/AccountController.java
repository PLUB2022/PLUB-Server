package plub.plubserver.domain.account.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import plub.plubserver.common.dto.ApiResponse;
import plub.plubserver.domain.account.dto.AccountDto;
import plub.plubserver.domain.account.dto.AuthDto;
import plub.plubserver.domain.account.service.AccountService;

import javax.validation.Valid;

import java.io.IOException;

import static plub.plubserver.common.dto.ApiResponse.success;
import static plub.plubserver.domain.account.dto.AccountDto.AccountInfo;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
@Api(tags = "회원 API", hidden = true)
public class AccountController {

    private final AccountService accountService;

    @ApiOperation(value = "회원 정보")
    @GetMapping("/me")
    public ApiResponse<AccountInfo> getMyAccountInfo() {
        return success(accountService.getMyAccount(), "내 정보 조회");
    }

    @ApiOperation(value = "닉네임으로 회원 조회")
    @GetMapping("/{nickname}")
    public ApiResponse<AccountInfo> getAccountInfo(@Valid @PathVariable String nickname) {
        return success(accountService.getAccount(nickname), "유저 정보 조회");
    }

    @ApiOperation(value = "닉네임 검증 API")
    @GetMapping("/check/nickname/{nickname}")
    public ApiResponse<Boolean> checkNickname(@Valid @PathVariable String nickname) {
        return success(accountService.checkNickname(nickname), "check nickname");
    }

    @PutMapping("/nickname")
    public ApiResponse<AccountInfo> updateNickname(@Valid @RequestBody AccountDto.AccountNicknameRequest request) {
        return success(accountService.updateNickname(request), "내 정보 조회");
    }

    @PutMapping("/introduce")
    public ApiResponse<AccountInfo> updateIntroduce(@Valid @RequestBody AccountDto.AccountIntroduceRequest request) {
        return success(accountService.updateIntroduce(request), "내 정보 조회");
    }

    @ApiOperation(value = "회원 탈퇴")
    @PostMapping("/revoke")
    public ApiResponse<?> revoke(@RequestBody AuthDto.RevokeRequest revokeDto) throws IOException {
        AuthDto.AuthMessage revoke = accountService.revoke(revokeDto);
        return success(revoke.detailData(), revoke.detailMessage());
    }
}
