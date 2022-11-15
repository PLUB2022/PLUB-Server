package plub.plubserver.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import plub.plubserver.config.jwt.JwtAccessDeniedHandler;
import plub.plubserver.config.jwt.JwtAuthenticationEntryPoint;
import plub.plubserver.config.jwt.JwtProvider;
import plub.plubserver.config.jwt.JwtSecurityConfig;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) // secured 어노테이션 활성화, preAuthorize 어노테이션 활성화
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtProvider jwtProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()

                // exception handling 할 때 우리가 만든 클래스를 추가
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
                .and()

                // 세션 사용 안 함
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .formLogin().disable()
                .httpBasic().disable() // bearer 방식을 쓸 거다
                .authorizeRequests()
                .antMatchers("/api/category/**", "/api/auth/**", "/api/account/check/nickname/**", "/api/test/**").permitAll()
                .antMatchers("/docs/**", "/favicon.ico", "/v2/api-docs","/configuration/ui","/swagger-resources/**",
                        "/configuration/security","/swagger-ui.html","/swagger-ui/#", "/webjars/**","/swagger/**", "/swagger-ui/**", "/", "/csrf", "/error").permitAll()
                .anyRequest()
                .authenticated()

                // JwtFilter 를 addFilterBefore 로 등록했던 JwtSecurityConfig 클래스를 적용
                .and()
                .apply(new JwtSecurityConfig(jwtProvider));
    }
}
