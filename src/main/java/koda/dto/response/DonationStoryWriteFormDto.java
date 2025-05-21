package koda.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/*
글쓰기 페이지 응답용 데이터
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DonationStoryWriteFormDto {

    private List<String> areaOptions; //권역
    private String captchaImageUrl;
}
