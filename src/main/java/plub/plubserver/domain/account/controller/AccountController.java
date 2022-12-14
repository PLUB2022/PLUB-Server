package plub.plubserver.domain.account.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import plub.plubserver.common.dto.ApiResponse;
import plub.plubserver.domain.account.dto.AccountDto.*;
import plub.plubserver.domain.account.service.AccountService;

import javax.validation.Valid;

import static plub.plubserver.common.dto.ApiResponse.success;
import static plub.plubserver.domain.account.dto.AccountDto.AccountInfoResponse;
import static plub.plubserver.domain.account.dto.AccountDto.NicknameResponse;
import static plub.plubserver.domain.account.dto.AuthDto.AuthMessage;


@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Api(tags = "회원 API", hidden = true)
public class AccountController {
    private final AccountService accountService;

    @ApiOperation(value = "회원 정보")
    @GetMapping("/me")
    public ApiResponse<AccountInfoResponse> getMyAccountInfo() {
        return success(accountService.getMyAccount());
    }

    @ApiOperation(value = "닉네임으로 회원 조회")
    @GetMapping("/{nickname}")
    public ApiResponse<AccountInfoResponse> getAccountInfo(@PathVariable String nickname) {
        return success(accountService.getAccount(nickname));
    }

    @ApiOperation(value = "닉네임 검증 API")
    @GetMapping("/check/nickname/{nickname}")
    public ApiResponse<NicknameResponse> isDuplicateNickname(@PathVariable String nickname) {
        return success(accountService.isDuplicateNickname(nickname));
    }

    @ApiOperation(value = "회원 프로필 수정 (프로필 사진, 인사말, 닉네임)")
    @PostMapping("/profile")
    public ApiResponse<AccountInfoResponse> updateProfile(
            @Valid @RequestBody AccountProfileRequest accountProfileRequest
    ) {
        return success(accountService.updateProfile(accountProfileRequest));
    }

    @ApiOperation(value = "회원 탈퇴")
    @PostMapping("/revoke")
    public ApiResponse<?> revoke() {
        AuthMessage revoke = accountService.revoke();
        return success(revoke.detailData());
    }

    @ApiOperation(value = "회원 관심사 등록")
    @PostMapping("/interest")
    public ApiResponse<AccountCategoryResponse> createAccountCategory(
            @Valid @RequestBody AccountCategoryRequest accountCategoryRequest) {
        return success(accountService.createAccountCategory(accountCategoryRequest));
    }

    @ApiOperation(value = "회원 관심사 조회")
    @GetMapping("/interest")
    public ApiResponse<AccountCategoryResponse> getAccountCategory() {
        return success(accountService.getAccountCategory());
    }
}
