package kr.or.kosa.nux2.web.auth.principal;

import kr.or.kosa.nux2.domain.member.dto.MemberDto;
import kr.or.kosa.nux2.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
        log.info("method = {}", "loadUserByUsername");
        System.out.println("memberId" + memberId);
        MemberDto.UserDto userDto = memberRepository.findById(memberId);
        System.out.println("userdto:" + userDto.toString());
        if (userDto != null) {
            return new CustomUserDetails(userDto);
        }
        return null;
    }
}
