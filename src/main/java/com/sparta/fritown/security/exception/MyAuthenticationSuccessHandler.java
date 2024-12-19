package com.sparta.fritown.security.exception;

import com.sparta.fritown.security.auth.GeneratedToken;
import com.sparta.fritown.security.util.JwtUtil;
import com.sparta.fritown.user.dto.RegisterRequestDto;
import com.sparta.fritown.user.entity.User;
import com.sparta.fritown.user.repository.UserRepository;
import com.sparta.fritown.user.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class MyAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        log.info("sucess handler started");
        // OAuth2User로 캐스팅하여 인증된 사용자 정보를 가져온다.
        // OAuth2User는 OAuth 서버에서 회원정보를 가져와서, 우리가 만든 틀에 저장해둔 dto라고 할 수 있겠다.
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        // 사용자 이메일을 가져온다.
        String email = oAuth2User.getAttribute("email");
        // 서비스 제공 플랫폼(GOOGLE, KAKAO, NAVER)이 어디인지 가져온다.
        String provider = oAuth2User.getAttribute("provider");

        // CustomOAuth2UserService에서 셋팅한 로그인한 회원 존재 여부를 가져온다.
        boolean isExist = oAuth2User.getAttribute("exist");
        // OAuth2User로 부터 Role을 얻어온다.
        String role = oAuth2User.getAuthorities().stream().
                findFirst() // 첫번째 Role을 찾아온다.
                .orElseThrow(IllegalAccessError::new) // 존재하지 않을 시 예외를 던진다.
                .getAuthority(); // Role을 가져온다.

        // 회원이 존재할경우
        if (isExist) {
            log.info("successHandler_isExist started");
            // 회원이 존재하면 jwt token 발행을 시작한다.
            GeneratedToken token = jwtUtil.generateToken(email, role); // generateToken에서 AccessToken과 RefreshToken을 발급해 준다.
            log.info("jwtToken = {}", token.getAccessToken());

            // accessToken을 쿼리스트링에 담는 url을 만들어준다.
            String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:8080/health/login/success")
                    .queryParam("accessToken", token.getAccessToken())
                    .build()
                    .encode(StandardCharsets.UTF_8)
                    .toUriString();

            log.info("redirect 준비");
            // 로그인 확인 페이지로 리다이렉트 시킨다.
            getRedirectStrategy().sendRedirect(request, response, targetUrl);


        } else {
            log.info("successHandler_not isExist started");


            // 프론트 서버 연결 코드. -> 프론트에서 회원가입을 위한 추가적인 정보를 받고, 이를 /api/auth/register에 보내서, 회원 정보 저장하고, 다시 로그인 시도로, isExist 가 true인 것을 실행하도록
            // 회원이 존재하지 않을경우, 서비스 제공자와 email을 쿼리스트링으로 전달하는 url을 만들어준다.// 데이터를 프론트엔드에 보내주고, 프론트엔드에서 이를 가지고 회원가입 폼 작성.
            String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:8080/api/auth/register")
                    .queryParam("email", (String) oAuth2User.getAttribute("email"))
                    .queryParam("provider", provider)
                    .build()
                    .encode(StandardCharsets.UTF_8)
                    .toUriString();
            // 회원가입 페이지로 리다이렉트 시킨다.
            getRedirectStrategy().sendRedirect(request, response, targetUrl);

            log.info("email : {}", email);
            log.info("provider: {}", provider);


        }
    }

}