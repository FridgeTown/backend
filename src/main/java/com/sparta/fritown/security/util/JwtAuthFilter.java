package com.sparta.fritown.security.util;


import com.sparta.fritown.security.dto.SecurityUserDto;
import com.sparta.fritown.user.entity.User;
import com.sparta.fritown.user.repository.UserRepository;
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
        // request Header에서 AccessToken을 가져온다.
        String atc = request.getHeader("Authorization");

        // 토큰 검사 생략(모두 허용 URL의 경우 토큰 검사 통과) /signup /login 처럼 애초부터 토큰이 없는 요청의 경우 다음 filter로 에러 없이 바로 넘겨줘야 하기 때문에, return
        if (!StringUtils.hasText(atc)) {
            doFilter(request, response, filterChain);
            return;
        }

        // AccessToken을 검증하고, 만료되었을경우 예외를 발생시킨다.
        if (!jwtUtil.verifyToken(atc)) {
            throw new JwtException("Access Token 만료!");
        }

        // AccessToken의 값이 있고, 유효한 경우에 진행한다.
        if (jwtUtil.verifyToken(atc)) {

            // AccessToken 내부의 payload에 있는 email로 user를 조회한다. 없다면 예외를 발생시킨다 -> 정상 케이스가 아님
            User user = userRepository.findByEmail(jwtUtil.getUid(atc))
                    .orElseThrow(IllegalStateException::new);

            // SecurityContext에 등록할 User 객체를 만들어준다.
            SecurityUserDto userDto = SecurityUserDto.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .role("ROLE_".concat(user.getUserRole()))
                    .nickname(user.getNickname())
                    .build();

            // SecurityContext에 인증 객체를 등록해준다.
            Authentication auth = getAuthentication(userDto); // authentication 객체 생성 : 인증된 사용자 정보와 권한을 포함, 이후 어플리케이션 내에서 인증 상태를 나타냄.
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }


    public Authentication getAuthentication(SecurityUserDto member) {
        return new UsernamePasswordAuthenticationToken(member, "",
                List.of(new SimpleGrantedAuthority(member.getRole())));
    }

}