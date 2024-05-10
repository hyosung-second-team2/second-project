package kr.or.kosa.nux2.domain.registrationcard.repository;

import kr.or.kosa.nux2.domain.registrationcard.dto.RegistrationCardDto;
import kr.or.kosa.nux2.domain.registrationcard.mapper.RegistrationCardMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class RegistrationCardRepository {
    private final RegistrationCardMapper registrationCardMapper;
    public List<RegistrationCardDto.Response> findAllRegistrationCardByMemberId(String memberId){
        return registrationCardMapper.findAllRegistrationCardByMemberId(memberId);
    };
    public int deleteRegistrationCard(int registrationId){
        return registrationCardMapper.deleteRegistrationCard(registrationId);
    };
    public int insertRegistrationCard(RegistrationCardDto.InsertRequest registrationCard){
        return registrationCardMapper.insertRegistrationCard(registrationCard);
    };
}
