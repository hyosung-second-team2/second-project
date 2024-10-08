package kr.or.kosa.nux2.web.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.kosa.nux2.web.auth.JwtUtils;
import kr.or.kosa.nux2.web.auth.authentication.JwtAuthenticationToken;
import kr.or.kosa.nux2.web.auth.principal.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("method = {}", "attemptAuthentication");
        String username = obtainUsername(request);
        String password = obtainPassword(request);
        JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(username, password);
        return authenticationManager.authenticate(jwtAuthenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("method = {}", "successfulAuthentication");
        String token = jwtUtils.createAccessToken(authResult);
        response.addHeader(HttpHeaders.AUTHORIZATION, token);

        CustomUserDetails customUserDetails = (CustomUserDetails) authResult.getPrincipal();
        customUserDetails.getUserDto().setMemberPassword(null);

        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(customUserDetails.getUserDto());

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(userJson);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("method = {}", "unsuccessfulAuthentication");
        response.setStatus(401);
    }
}
