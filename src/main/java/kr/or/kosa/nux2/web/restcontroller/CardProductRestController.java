package kr.or.kosa.nux2.web.restcontroller;

import kr.or.kosa.nux2.domain.cardproduct.dto.CardProductDto;
import kr.or.kosa.nux2.domain.cardproduct.service.CardProductServiceImpl;
import kr.or.kosa.nux2.web.common.code.SuccessCode;
import kr.or.kosa.nux2.web.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cardproduct")
public class CardProductRestController {
    private final CardProductServiceImpl cardProductService;

    @GetMapping("/detail")
    public ResponseEntity<ApiResponse<CardProductDto.DetailsResponse>> cardProductDetail(@RequestBody CardProductDto.DetailRequest request) {
        cardProductService.showCardProductDetail(request);
        return null;
    }
    @PostMapping("/list")
    public ResponseEntity<ApiResponse<List<CardProductDto.Response>>> cardProductDetail(@RequestBody CardProductDto.ListRequest request) {
        int startNum = request.getStartNum();
        int endNum = request.getEndNum();
        Map<String, Object> map = new HashMap<>();

        map.put("startnum", startNum);
        map.put("endnum", endNum);
        List<CardProductDto.Response> response = cardProductService.showCardProductList(map);

        return new ResponseEntity<>(new ApiResponse<>(response, SuccessCode.SELECT_SUCCESS), HttpStatus.OK);
    }
}
