package koda.dto.request;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
public class DonationStoryCreateRequestDto {

    private String areaCode;
    private String storyTitle;

    private String storyPassword;
    private String storyWriter;
    private String storyContents;

    private String captchaInput; //캡차 번호
    private MultipartFile file;
}
