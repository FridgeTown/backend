package com.sparta.fritown.global.security.util;


import com.sparta.fritown.global.security.dto.UserDetailsImpl;
import com.sparta.fritown.domain.entity.User;
import com.sparta.fritown.domain.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtException;
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

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getRequestURI().contains("/token/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, IOException {
        log.info("doFilterInternal 시작 됨");

        // request Header에서 AccessToken을 가져온다.
        String atc = request.getHeader("Authorization");

        // 토큰 검사 생략(모두 허용 URL의 경우 토큰 검사 통과) /signup /login 처럼 애초부터 토큰이 없는 요청의 경우 다음 filter로 에러 없이 바로 넘겨줘야 하기 때문에, return
        if (!StringUtils.hasText(atc)) {
            doFilter(request, response, filterChain);
            return;
        }

        log.info("StringUtils.hasText가 true임.");

        atc = atc.replace("Bearer", "").trim(); // to remove bearer

        log.info("토큰 : {}", atc);

        // AccessToken의 값이 있고, 유효한 경우에 진행한다.
        if (!jwtUtil.verifyToken(atc)) {
            log.info("jwtUtil.verifyToken 부분을 통과하지 못함.");
            throw new JwtException("Access Token 만료");
        }

        log.info("Access Token 만료 부분 지남.");

        User user = userRepository.findByEmail(jwtUtil.getUid(atc)).orElseThrow(
                () -> new JwtException("유저를 찾을 수 없습니다.")
        );

        log.info("유저 이메일 정보 출력 : {}", user.getEmail());


            // SecurityContext에 등록할 User 객체를 만들어준다.
        UserDetailsImpl userDto = UserDetailsImpl.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role("ROLE_USER")
                .nickname(user.getNickname())
                .build();

            // SecurityContext에 인증 객체를 등록해준다.
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDto, "", List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        log.info("Authentication set: {}", SecurityContextHolder.getContext().getAuthentication());

        SecurityContextHolder.getContext().setAuthentication(auth);
        log.info("SecurityContext에 Authentication 설정 완료");
        log.info("Authentication set: {}", SecurityContextHolder.getContext().getAuthentication());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication != null) {
            Object principal = authentication.getPrincipal();
//            log.info("Principal type: {}", principal.getClass().getName());
//            log.info("Principal details: {}", principal);
        }

        filterChain.doFilter(request, response);
    }

}