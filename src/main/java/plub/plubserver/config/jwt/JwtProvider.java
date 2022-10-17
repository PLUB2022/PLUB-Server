package plub.plubserver.config.jwt;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import plub.plubserver.config.security.PrincipalDetailService;
import plub.plubserver.domain.account.dto.AuthDto;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.model.Role;
import plub.plubserver.domain.account.exception.SignTokenException;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class JwtProvider {

    private final PrincipalDetailService principalDetailService;
    private final RefreshTokenRepository refreshTokenRepository;

    private static final String secret = "plubingplubingplubingplubingplubingplubingplubingplubingplubingplubingplubingplubingplubingplubingplubingplubingplubingplubingplubingplubingplubingplubing";
    
    public static final long ACCESS_VALID_DURATION = 3000 * 1000L; // 60초 (mile sec)
    public static final long REFRESH_VALID_DURATION = 6000 * 1000L; // 60초

    // Request 헤더에서 토큰을 파싱한다
    public String resolveToken(HttpServletRequest request) {
        String rawToken = request.getHeader("Authorization");
        if (rawToken != null && rawToken.startsWith("Bearer "))
            return rawToken.replace("Bearer ", "");
        else return null;
    }

    public String resolveSignToken(String rawToken) {
        if (rawToken != null && rawToken.startsWith("Bearer "))
            return rawToken.replace("Bearer ", "");
        else throw new SignTokenException(rawToken);
    }

    // Sign Token 생성
    public String createSignToken(String email) {
        Date now = new Date(System.currentTimeMillis());
        return Jwts.builder()
                .setSubject(email)
                .claim("sign", Role.ROLE_USER)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ACCESS_VALID_DURATION))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    // Access Token 생성
    private String createAccessToken(Account account) {
        Date now = new Date(System.currentTimeMillis());
        return Jwts.builder()
                .setSubject(account.getEmail())
                .claim("role", account.getRole())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ACCESS_VALID_DURATION))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    // Refresh Token 생성
    private String createRefreshToken() {
        Date now = new Date(System.currentTimeMillis());
        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + REFRESH_VALID_DURATION))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    // Access, Refresh Token 검증 (만료 여부 검사)
    public boolean validate(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.warn("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 입니다.");
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 JWT 입니다.");
        } catch (IllegalArgumentException e) {
            log.warn("JWT 잘못 되었습니다.");
        }
        return false;
    }

    /**
     * Authentication 객체 가져오기
     */
    public Authentication getAuthentication(String accessToken) {
        Claims body = Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(accessToken)
                .getBody();
        String email = body.getSubject();
        UserDetails userDetails = principalDetailService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public AuthDto.SigningAccount getSignKey(String signToken) {
        Claims body = Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(signToken)
                .getBody();
        String email = body.getSubject();
        String[] split = email.split("@");
        return new AuthDto.SigningAccount(email, split[1]);
    }

    /**
     * Access, Refresh 최초 발행
     */
    public JwtDto issue(Account account) {
        String access = createAccessToken(account);
        String refresh = createRefreshToken();
        Optional<RefreshToken> findToken = refreshTokenRepository.findByAccount(account);

        if (findToken.isEmpty()) {
            refreshTokenRepository.save(new RefreshToken(null, account, refresh));
        } else {
            findToken.get().replace(refresh);
        }

        return new JwtDto(access, refresh);
    }

    /**
     * Refresh Token 으로 Access Token 재발급 (Access, Refresh 둘 다 재발급)
     */
    public JwtDto reIssue(String refreshToken) {
        RefreshToken findRefreshToken = refreshTokenRepository
                .findByRefreshToken(refreshToken)
                .orElseThrow(
                        () -> new JwtException("Refresh Token 을 찾을 수 없습니다.")
                );

        Account account = findRefreshToken.getAccount();
        String newAccessToken = createAccessToken(account);
        String newRefreshToken = createRefreshToken();

        // 기존 refresh 토큰 값 변경
        findRefreshToken.replace(newRefreshToken);
        return new JwtDto(newAccessToken, newRefreshToken);
    }

}