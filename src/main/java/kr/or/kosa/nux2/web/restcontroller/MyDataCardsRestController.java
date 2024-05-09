package kr.or.kosa.nux2.web.restcontroller;

import kr.or.kosa.nux2.domain.cardproduct.dto.CardProductDto;
import kr.or.kosa.nux2.domain.cardproduct.repository.CardProductRepository;
import kr.or.kosa.nux2.domain.memberexpendituretend.dto.MemberExpenditureTendDto;
import kr.or.kosa.nux2.domain.memberexpendituretend.repository.MemberExpenditureTendRepository;
import kr.or.kosa.nux2.domain.registrationcard.dto.RegistrationCardDto;
import kr.or.kosa.nux2.domain.registrationcard.repository.RegistrationCardRepository;
import kr.or.kosa.nux2.domain.virtualmydata.dto.MyDataCardDto;
import kr.or.kosa.nux2.domain.virtualmydata.dto.MyDataTransanctionHistoryDto;
import kr.or.kosa.nux2.domain.virtualmydata.repository.MyDataCardRepository;
import kr.or.kosa.nux2.domain.virtualmydata.repository.MyDataTransHistoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@AllArgsConstructor
public class MyDataCardsRestController {
    private final MyDataCardRepository myDataCardRepository;
    private final MyDataTransHistoryRepository myDataTransHistoryRepository;
    private final RegistrationCardRepository registrationCardRepository;
    private final MemberExpenditureTendRepository memberExpenditureTendRepository;
    private final CardProductRepository cardProductRepository;

    @GetMapping("/mydatatest")
    public ResponseEntity<?> test(){

//        MyDataCardDto.AuthenticationRequest auth = new MyDataCardDto.AuthenticationRequest("김우재", "01089387607");
//          //List<MyDataCardDto.Response> response = myDataCardRepository.findAllMyDataCard(auth);
//          //myDataCardRepository.insertMyDataCard(new MyDataCardDto.InsertRequest("1111111111111111","111", "정혜미", "8", "30", "01099999999","10"));
//
//        List<MyDataCardDto.Response> response = myDataCardRepository.findAllMyDataCard();
//        List<MyDataTransanctionHistoryDto.Response> response = myDataTransHistoryRepository.findAllTransHistory(0L, "1223567899992222");
//        myDataTransHistoryRepository.insertTransHistory(new MyDataTransanctionHistoryDto.InsertRequest(3L, "거래승인",new Timestamp(2024, 5, 1, 23, 44, 11, 10), 99999L, "하나카드" , "1111111111111111", "김가네", "창경궁로254", "EX2"));
       //List<RegistrationCardDto.Response> response = registrationCardRepository.findAllRegistrationCardByMemberId("dnwo1111");
        //registrationCardRepository.insertRegistrationCard(new RegistrationCardDto.InsertRequest(2L, "dnwo1111", 8L, "12331111****1111", "차애카드"));
        //registrationCardRepository.deleteRegistrationCard(2);
//        Map<String, Object> map = new HashMap<>();
//        map.put("updateCode", "202405");
//        map.put("memberId", "dnwo1111");
//        List<MemberExpenditureTendDto.ServiceResponse> response =  memberExpenditureTendRepository.findMemberExpenditureTendMapper(map);
        List<CardProductDto.Response> response = cardProductRepository.findAllCardList(new HashMap<>());
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
    }
}
