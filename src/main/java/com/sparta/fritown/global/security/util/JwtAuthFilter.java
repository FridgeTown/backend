package com.sparta.fritown.global.security.util;


import com.sparta.fritown.global.security.dto.UserDetailsImpl;
import com.sparta.fritown.domain.entity.User;
import com.sparta.fritown.domain.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.Authentication;


import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    // OncePerRequestFilter이기 때문에, HTTP 요청마다 한번 씩, 실행 되는 메서드.

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getRequestURI().contains("/token/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, IOException {

        // request Header에서 AccessToken을 가져온다.
        String atc = request.getHeader("Authorization");

        /* 토큰 검사 생략(모두 허용 URL의 경우 토큰 검사 통과) /signup /login 처럼 애초부터 토큰이 없는 요청의 경우 다음 filter로 에러 없이 바로 넘겨줘야 하기 때문에, return
         * Authorization 토큰을 포함하지 않는 경우 doFilter로 넘기고 return. 왜냐?! -> 여기 아래는 토큰 검증 로직이기 때문
         */
        if (!StringUtils.hasText(atc)) {
            doFilter(request, response, filterChain);
            return;
        }

        atc = atc.replace("Bearer", "").trim(); // to remove bearer

        log.info("토큰 : {}", atc);

        // AccessToken의 값이 있고, 유효한 경우에 진행한다.
        if (!jwtUtil.verifyToken(atc)) {
            log.info("jwtUtil.verifyToken 부분을 통과하지 못함.");
            throw new JwtException("Access Token 만료");
        }

        User user = userRepository.findByEmail(jwtUtil.getUid(atc)).orElseThrow(
                () -> new JwtException("유저를 찾을 수 없습니다."));


        // SecurityContext에 등록할 User 객체를 만들어준다.
        UserDetailsImpl userDto = UserDetailsImpl.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role("ROLE_USER")
                .nickname(user.getNickname())
                .build();

        // SecurityContext에 인증 객체를 등록해준다. 이를 통해 흔히 사용하는 Authorization 어노테이션이 가능해지게 되는 것.
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDto, "", List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        SecurityContextHolder.getContext().setAuthentication(auth);
        filterChain.doFilter(request, response);
    }

}