package kr.or.kosa.nux2.domain.member.service;

import kr.or.kosa.nux2.domain.member.dto.MemberConsCategoryDto;
import kr.or.kosa.nux2.domain.member.dto.MemberDto;
import kr.or.kosa.nux2.domain.member.dto.Role;
import kr.or.kosa.nux2.domain.member.repository.EmailAuthenticationRepository;
import kr.or.kosa.nux2.domain.member.repository.MemberExpenditureCategoryRepository;
import kr.or.kosa.nux2.domain.member.repository.MemberRepository;
import kr.or.kosa.nux2.web.auth.principal.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
    private static final String URL = "https://accounts.google.com/o/oauth2/revoke?token=";
    private RestTemplate restTemplate = new RestTemplate();
    private final MemberRepository memberRepository;
    private final EmailAuthenticationRepository emailAuthenticationRepository;
    private final MemberExpenditureCategoryRepository memberExpenditureCategoryRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JavaMailSender javaMailSender;



    @Override
    public String logout(CustomUserDetails customUserDetails) {
        log.info("method = {}","logout");
        if(customUserDetails.getUserDto().getProvider()!=null) {
            if (customUserDetails.getUserDto().getProvider().equals("google")) {
                String socialAccessToken = customUserDetails.getUserDto().getSocialToken();
                restTemplate.getForObject(URL + socialAccessToken, String.class);
            }
        }
        return "logout";

    }

    @Transactional
    @Override
    public String signUp(MemberDto.SignUpRequest request) {
        request.setMemberPassword(bCryptPasswordEncoder.encode(request.getMemberPassword()));
        request.setRole(Role.USER.getRoles());
        System.out.println("count:"+memberRepository.insertMember(request));
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("memberId",request.getMemberId());
        paramMap.put("MemberConsCategoryDtoList",request.getMemberConsCategoryDtoList());
        //memberExpenditureCategoryRepository.insertMemberConsCategory(request.getMemberConsCategoryDtoList());
        memberExpenditureCategoryRepository.insertMemberConsCategory(paramMap);
        return "main";
    }

    @Override
    public boolean checkMemberId(MemberDto.MemberIdRequest request) {
        if(memberRepository.isExistMemberId(request)){
            return false;
        }else{
            sendEmail(request);
            return true;
        }
    }
    @Transactional
    @Override
    public void sendEmail(MemberDto.MemberIdRequest request) {
        String authenticationNumber = generateAuthenticationNumber();
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setSubject("what's in my wallet 인증메일");
        mailMessage.setText("인증번호: "+ authenticationNumber);
        mailMessage.setTo(request.getMemberId());
        javaMailSender.send(mailMessage);

        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("memberId",request.getMemberId());
        paramMap.put("authenticationNumber",authenticationNumber);
        insertOrUpdateAuthenticationInfo(paramMap);
    }

    @Override
    public String generateAuthenticationNumber() {
        Random random = new Random();
        int randomNumber = random.nextInt(900000) + 100000;
        return String.valueOf(randomNumber);
    }

    @Override
    public int insertOrUpdateAuthenticationInfo(Map<String,Object> paramMap) {
       return emailAuthenticationRepository.insertOrUpdateAuthenticationInfo(paramMap);
    }
    @Transactional
    @Override
    public boolean validateAuthenticationNumber(MemberDto.AuthenticationDto request) {
        CustomUserDetails customUserDetails = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        MemberDto.MemberIdRequest memberIdRequest = new MemberDto.MemberIdRequest(customUserDetails.getUserDto().getMemberId());

        MemberDto.AuthenticationDto response = emailAuthenticationRepository.findAuthenticationNumberByMemberIdAndTimeDiffLessThanFiveMinute(memberIdRequest);

        if(response!=null){
            if(response.getAuthenticationNumber().equals(request.getAuthenticationNumber())){
                emailAuthenticationRepository.deleteAuthenticationInfoByMemberId(memberIdRequest);
                return true;
            }
        }
        emailAuthenticationRepository.deleteAuthenticationInfoByMemberId(memberIdRequest);
        return false;
    }

    @Transactional
    @Override
    public MemberDto.ProfileResponse showMemberProfile() {
        CustomUserDetails customUserDetails = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("memberID"+customUserDetails.getUserDto().getMemberId());
        MemberDto.MemberIdRequest request = new MemberDto.MemberIdRequest(customUserDetails.getUserDto().getMemberId());
        List<MemberConsCategoryDto.MemberConsCategoryResponse> memberConsCategoryDtoList = memberExpenditureCategoryRepository.selectMemberConsCategoryNames(request);
        System.out.println("memconstcate"+memberConsCategoryDtoList.toString());
        MemberDto.ProfileResponse response = memberRepository.selectMemberDetail(request);
        response.setMemberConsCategoryDtoList(memberConsCategoryDtoList);
        return response;
    }
    @Transactional
    @Override
    public MemberDto.ProfileResponse updateMemberInfo(MemberDto.UpdateMemberInfoRequest request) {
        CustomUserDetails customUserDetails = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("memberId",customUserDetails.getUserDto().getMemberId());
        paramMap.put("targetExpenditure",request.getTargetExpenditure());
        System.out.println("memberConsCategoryIdDtoList"+request.getMemberConsCategoryIdDtoList());
        paramMap.put("memberConsCategoryIdDtoList",request.getMemberConsCategoryIdDtoList());

        MemberDto.ProfileResponse response = updateTargetExpenditure(paramMap);
        response.setMemberId(customUserDetails.getUserDto().getMemberId());
        response.setMemberConsCategoryDtoList(updateMemberConsCategoryList(paramMap));

        return response;
    }

    @Override
    public boolean updatePassword(MemberDto.UpdatePasswordRequest request) {
        if(request.getChangePassword().equals(request.getCheckPassword())){
            CustomUserDetails customUserDetails = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Map<String,Object> paramMap = new HashMap<>();
            paramMap.put("memberId",customUserDetails.getUserDto().getMemberId());
            paramMap.put("memberPassword",bCryptPasswordEncoder.encode(request.getChangePassword()));
            int count = memberRepository.updatePassword(paramMap);
            if(count==1){
                return true;
            }
        }
        return false;
    }

    @Transactional
    @Override
    public MemberDto.ProfileResponse updateTargetExpenditure(Map<String,Object> paramMap){
        System.out.println("updateTarget:"+paramMap.get("memberConsCategoryIdDtoList"));
        System.out.println("updatetarget"+memberRepository.updateTargetExpenditure(paramMap));
        return memberRepository.findMemberNameAndTargetExpenditureByMemberId(paramMap);
    }

    @Transactional
    @Override
    public List<MemberConsCategoryDto.MemberConsCategoryResponse> updateMemberConsCategoryList(Map<String,Object> paramMap){
        List<MemberConsCategoryDto.MemberConsCategoryIdDto> savedList =memberExpenditureCategoryRepository.selectMemberConsCategoryIds(paramMap);
        List<MemberConsCategoryDto.MemberConsCategoryIdDto> changeList = (List<MemberConsCategoryDto.MemberConsCategoryIdDto>) paramMap.get("memberConsCategoryIdDtoList");

        System.out.println("saved"+savedList.toString());
        System.out.println("changed"+changeList.toString());
        for(MemberConsCategoryDto.MemberConsCategoryIdDto item : changeList){
            if(savedList.contains(item)){
                savedList.remove(item);
                changeList.remove(item);
            }
        }
        paramMap.put("list",savedList);
        System.out.println("saved"+paramMap.get("list"));
        memberExpenditureCategoryRepository.deleteMemberConsCategory(paramMap);

        paramMap.put("list",changeList);
        System.out.println("changes"+paramMap.get("list"));
        memberExpenditureCategoryRepository.insertMemberConsCategory(paramMap);
        return memberExpenditureCategoryRepository.selectMemberConsCategoryNames(new MemberDto.MemberIdRequest(paramMap.get("memberId").toString()));
    }





}
